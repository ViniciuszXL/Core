package br.com.vinicius.core.global.networking;

import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import br.com.vinicius.core.Core;
import br.com.vinicius.core.data.BasePlayerData;
import br.com.vinicius.core.networking.info.type.ServerType;
import br.com.vinicius.core.networking.packet.PacketDirection;
import br.com.vinicius.core.networking.packet.PacketIn;
import br.com.vinicius.core.networking.packet.PacketOut;
import br.com.vinicius.core.networking.packet.PacketType;
import br.com.vinicius.core.networking.packet.PacketType.PacketTypeIn;
import br.com.vinicius.core.player.BasePlayer;
import br.com.vinicius.core.utilitaries.Util;
import br.com.vinicius.core.utilitaries.logging.FormattedLogger;

public final class PacketHandler<TPlayer extends BasePlayer<TPlayer, TData>, TData extends BasePlayerData>
		implements PluginMessageListener {

	private static final String PACKET_CHANNEL = "Packet";
	private static final String REDIRECT_ALL_CHANNEL = "RedirectAll";
	private static final String REDIRECT_UNIQUE_ID_CHANNEL = "RedirectUUID";
	private static final String REDIRECT_NAME_CHANNEL = "RedirectName";
	private static final String PROXY_PACKET_CHANNEL = "ProxyPacket";

	private final Core<TPlayer, TData> plugin;
	private final PacketProcessor<TPlayer, TData> processor;
	private final FormattedLogger logger;

	public PacketHandler(Core<TPlayer, TData> plugin, FormattedLogger parentLogger) {
		this.plugin = plugin;
		this.processor = new PacketProcessor<TPlayer, TData>(plugin, parentLogger);
		this.logger = new FormattedLogger(parentLogger, "Net");
	}

	public void sendProxyPacket(PacketOut packet) {
		if (packet.getPacketType().getDirection() != PacketDirection.PROXY) {
			logger.log("%s e um %s packet, nao e possivel envia-lo como um pacote PROXY!", packet.getPacketType(),
					packet.getPacketType().getDirection());
			return;
		}

		Player proxyLink = Util.getFirstPlayer();
		if (proxyLink == null) {
			logger.log(Level.WARNING, "Nenhum jogador on-line para enviar pacote: %s", packet.getPacketType());
			return;
		}

		ByteArrayDataOutput data = ByteStreams.newDataOutput();
		data.writeByte(packet.getPacketType().getId());
		packet.writePacketData(data);

		proxyLink.sendPluginMessage(plugin, PROXY_PACKET_CHANNEL, data.toByteArray());
	}

	public void sendServerPacket(ServerType serverType, PacketOut packet) {
		if (packet.getPacketType().getDirection() != PacketDirection.SERVER) {
			logger.log("%s e um %s pacote, nao e possivel envia-lo como um pacote SERVER!", packet.getPacketType(),
					packet.getPacketType().getDirection());
			return;
		}

		Player proxyLink = Util.getFirstPlayer();
		if (proxyLink == null) {
			logger.log(Level.WARNING, "Nenhum jogador online para enviar pacotes: %s", packet.getPacketType());
			return;
		}

		ByteArrayDataOutput data = ByteStreams.newDataOutput();

		flushPacket(REDIRECT_ALL_CHANNEL, data, proxyLink, packet, serverType);
	}

	public void sendGlobalPacket(PacketOut packet) {
		sendServerPacket(null, packet);
	}

	public void sendPacketToUniqueId(UUID uniqueId, PacketOut packet) {
		if (packet.getPacketType().getDirection() != PacketDirection.SERVER) {
			logger.log("%s e um %s pacote, nao pode envia-lo como um pacote SERVER!", packet.getPacketType(),
					packet.getPacketType().getDirection());
			return;
		}

		Player proxyLink = Util.getFirstPlayer();
		if (proxyLink == null) {
			logger.log(Level.WARNING, "Nenhum jogador online para enviar pacotes: %s", packet.getPacketType());
			return;
		}

		ByteArrayDataOutput data = ByteStreams.newDataOutput();
		data.writeLong(uniqueId.getMostSignificantBits());
		data.writeLong(uniqueId.getLeastSignificantBits());

		flushPacket(REDIRECT_UNIQUE_ID_CHANNEL, data, proxyLink, packet, null);
	}

	public void sendPacketToName(String name, PacketOut packet) {
		if (packet.getPacketType().getDirection() != PacketDirection.SERVER) {
			logger.log("%s e um %s packet, nao pode envia-lo como um pacote SERVER!", packet.getPacketType(),
					packet.getPacketType().getDirection());
			return;
		}

		Player proxyLink = Util.getFirstPlayer();
		if (proxyLink == null) {
			logger.log(Level.WARNING, "Nenhum jogador online para enviar pacotes: %s", packet.getPacketType());
			return;
		}

		ByteArrayDataOutput data = ByteStreams.newDataOutput();
		data.writeUTF(name);

		flushPacket(REDIRECT_NAME_CHANNEL, data, proxyLink, packet, null);
	}

	private void flushPacket(String channel, ByteArrayDataOutput data, Player proxyLink, PacketOut packet,
			ServerType serverType) {
		ByteArrayDataOutput packetData = ByteStreams.newDataOutput();
		packetData.writeByte(serverType == null ? -1 : serverType.getId());
		packetData.writeByte(packet.getPacketType().getId());
		packet.writePacketData(packetData);
		byte[] packetRawData = packetData.toByteArray();

		data.writeShort(packetRawData.length);
		data.write(packetRawData);

		proxyLink.sendPluginMessage(plugin, channel, data.toByteArray());
	}

	@Override
	public void onPluginMessageReceived(String channel, Player dontMatter, byte[] rawData) {
		try {
			if (!channel.equals(PACKET_CHANNEL)) {
				return;
			}

			ByteArrayDataInput data = ByteStreams.newDataInput(rawData);
			int serverTypeId = data.readByte();
			if (serverTypeId != -1) {
				ServerType serverType = ServerType.getServerType(serverTypeId);
				if (serverType == null) {
					logger.log(Level.WARNING, "ID do tipo de servidor desconhecido recebido: %d", serverTypeId);
					return;
				}

				if (serverType != plugin.getServerType()) {
					return;
				}
			}

			int packetId = data.readByte();

			PacketTypeIn packetType = PacketType.In.getPacketType(packetId);
			if (packetType == null) {
				logger.log(Level.WARNING, "ID de pacote desconhecido recebido: %d", packetId);
				return;
			}

			PacketIn packet = packetType.createInstance();
			if (packet == null) {
				logger.log(Level.WARNING, "Falha ao instanciar o pacote: %s", packetType);
				return;
			}

			packet.readPacketData(data);
			packet.processPacketData(processor);
		} catch (Exception e) {
			logger.log(Level.SEVERE, e, "Ocorreu um erro ao manusear dados de pacotes recebidos! Detalhes abaixo:");
		}
	}

	public void registerChannels(Messenger messenger) {
		messenger.registerIncomingPluginChannel(plugin, PACKET_CHANNEL, this);
		messenger.registerOutgoingPluginChannel(plugin, REDIRECT_ALL_CHANNEL);
		messenger.registerOutgoingPluginChannel(plugin, REDIRECT_UNIQUE_ID_CHANNEL);
		messenger.registerOutgoingPluginChannel(plugin, REDIRECT_NAME_CHANNEL);
		messenger.registerOutgoingPluginChannel(plugin, PROXY_PACKET_CHANNEL);
	}
}
