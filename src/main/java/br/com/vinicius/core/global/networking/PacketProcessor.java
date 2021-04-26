package br.com.vinicius.core.global.networking;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import br.com.vinicius.core.Core;
import br.com.vinicius.core.data.BasePlayerData;
import br.com.vinicius.core.management.clan.constructor.Clan;
import br.com.vinicius.core.networking.info.CompleteServerInfo;
import br.com.vinicius.core.networking.packet.in.*;
import br.com.vinicius.core.networking.packet.in.clan.*;
import br.com.vinicius.core.networking.packet.in.message.*;
import br.com.vinicius.core.networking.packet.in.permission.*;
import br.com.vinicius.core.networking.packet.in.proxy.*;
import br.com.vinicius.core.networking.packet.out.message.PacketOutMessage;
import br.com.vinicius.core.player.BasePlayer;
import br.com.vinicius.core.player.group.Group;
import br.com.vinicius.core.player.messages.PlayerToggles.PlayerToggle;
import br.com.vinicius.core.player.punishment.entry.MuteEntry;
import br.com.vinicius.core.player.punishment.type.PunishmentType;
import br.com.vinicius.core.utilitaries.Prefix;
import br.com.vinicius.core.utilitaries.Util;
import br.com.vinicius.core.utilitaries.announceament.Broadcast;
import br.com.vinicius.core.utilitaries.color.ColorUtil;
import br.com.vinicius.core.utilitaries.logging.FormattedLogger;
import br.com.vinicius.core.utilitaries.messages.base.BaseMessages;

public final class PacketProcessor<TPlayer extends BasePlayer<TPlayer, TData>, TData extends BasePlayerData> {

	private static final String CLAN_REMOVE_PLAYER = Prefix.CLAN + ColorUtil.GRAY + "Você foi " + ColorUtil.RED
			+ "expulso" + ColorUtil.GRAY + " do clan " + ColorUtil.WHITE + "%s" + ColorUtil.GRAY + "!";

	private final Core<TPlayer, TData> plugin;
	private final FormattedLogger logger;

	public PacketProcessor(Core<TPlayer, TData> plugin, FormattedLogger parentLogger) {
		this.plugin = plugin;
		this.logger = new FormattedLogger(parentLogger, "Packet");
	}

	public void processSetPermission(PacketInSetPermission packet) {
		plugin.getPermissionHandler().setPermission(packet.getGroup(), packet.getPermission());
	}

	public void processUnsetPermission(PacketInUnsetPermission packet) {
		plugin.getPermissionHandler().unsetPermission(packet.getGroup(), packet.getPermission());
	}

	public void processDoubleCoin(PacketInDoubleCoin packet) {
		plugin.setDoubleCoinEnabled(packet.getValue());
	}

	public final void processFakeUpdate(PacketInFakeUpdate packet) {
		Player p = Bukkit.getPlayer(packet.getPlayerUniqueId());
		if (p == null) {
			logger.log("Jogador '%s' recebeu uma atualização de nome mas está offline!", packet.getPlayerUniqueId());
			return;
		}

		TPlayer tPlayer = plugin.getPlayerList().getPlayer(p);
		String fakeName = packet.getFakeName();

		try {
			tPlayer.getData().setFakeName(fakeName.equalsIgnoreCase("Nenhum") ? null : fakeName);
			tPlayer.setGroupTag(Group.JOGADOR);
		} finally {
			plugin.getProtocolAPI().getFake().apply(p,
					fakeName.equalsIgnoreCase("Nenhum") ? tPlayer.getRealName() : fakeName, true);
		}
	}

	public final void processSkinUpdate(PacketInSkinUpdate packet) {
		Player p = Bukkit.getPlayer(packet.getPlayerUniqueId());
		if (p == null) {
			logger.log("Jogador '%s' recebeu uma atualização de skin, mas está offline!", packet.getPlayerUniqueId());
			return;
		}

		TPlayer tPlayer = plugin.getPlayerList().getPlayer(p);
		String skinName = packet.getSkinName();

		plugin.getSkinManager().attempSkinUpdateWithoutCheck(p,
				skinName.equalsIgnoreCase("Nenhuma") ? tPlayer.getRealName() : skinName);
	}

	public void processPunishment(PacketInPunishment packet) {
		Player player = Bukkit.getPlayer(packet.getPunishedUniqueId());
		if (player == null) {
			logger.log("Jogador '%s' recebeu punição, mas está offline!", packet.getPunishedUniqueId());
			return;
		}

		TPlayer tPlayer = plugin.getPlayerList().getPlayer(player);
		PunishmentType punishmentType = packet.getPunishmentType();
		logger.log("Recebido '%s' para '%s'/'%s'!", punishmentType, player.getUniqueId(), player.getName());
		if (punishmentType == PunishmentType.BAN) {
			if (packet.isPermanent())
				player.kickPlayer(
						String.format(BaseMessages.YOU_HAVE_BEEN_BANNED, packet.getReason(), Util.CONSOLE_ID));
			else if (packet.isValid())
				player.kickPlayer(String.format(BaseMessages.YOU_HAVE_BEEN_TEMP_BANNED,
						Util.time(packet.getExpiry() - packet.getPunishDate(), false), packet.getReason(),
						Util.CONSOLE_ID));
			Broadcast.all(ColorUtil.RED + "Um jogador conectado nesta sala foi banido.");
		} else if (punishmentType == PunishmentType.MUTE) {
			MuteEntry muteEntry = new MuteEntry(packet.getPunishmentId(), packet.getPunisherId(),
					packet.getPunishDate(), packet.getExpiry(), packet.getReason());

			tPlayer.setCurrentMute(muteEntry);
			if (packet.isPermanent())
				player.sendMessage(String.format(BaseMessages.YOU_HAVE_BEEN_MUTED, packet.getReason()));
			else if (packet.isValid())
				player.sendMessage(String.format(BaseMessages.YOU_HAVE_BEEN_TEMP_MUTED,
						Util.getLongTimeString(packet.getExpiry() - packet.getPunishDate()), packet.getReason()));

			plugin.getPermissionHandler().updatePlayer(tPlayer, false, false, true);
		}
	}

	public void processUndoPunishment(PacketInUndoPunishment packet) {
		Player player = Bukkit.getPlayer(packet.getPunishedUniqueId());
		if (player == null) {
			logger.log("Jogador '%s' recebeu a anulação da punição, mas está offline!", packet.getPunishedUniqueId());
			return;
		}

		TPlayer tPlayer = plugin.getPlayerList().getPlayer(player);
		PunishmentType punishmentType = packet.getPunishmentType();
		logger.log("Recebido anulação de punicão '%s' para '%s'/'%s'!", punishmentType, player.getUniqueId(),
				player.getName());
		if (punishmentType == PunishmentType.MUTE) {
			player.sendMessage(BaseMessages.YOU_HAVE_BEEN_UNMUTED);
			tPlayer.setCurrentMute(null);
			plugin.getPermissionHandler().updatePlayer(tPlayer, false, false, true);
		}
	}

	public void processClanRemove(PacketInClanRemove packet) {
		Player p = Bukkit.getPlayer(packet.getUniqueId());
		if (p == null) {
			logger.log("Jogador '%s' recebeu remoção de clan, mas está offline", packet.getUniqueId());
			return;
		}

		TPlayer tPlayer = plugin.getPlayerList().getPlayer(p);
		Clan clan = tPlayer.getClan();
		tPlayer.setClan(null);
		tPlayer.updateGroupTag(tPlayer.getDisplayGroup());

		BaseMessages.prefix(p, String.format(CLAN_REMOVE_PLAYER, clan.getName()));
	}

	public void processClanMessage(PacketInClanMessage packet) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			TPlayer tPlayer = plugin.getPlayerList().getPlayer(p);
			if (tPlayer.getClan() == null)
				continue;
			Clan clan = tPlayer.getClan();
			if (clan.getId() != packet.getClanId())
				continue;
			p.sendMessage(packet.getMessage());
		}
	}

	public void processClanDelete(PacketInClanDelete packet) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			TPlayer tPlayer = plugin.getPlayerList().getPlayer(p);
			if (tPlayer.getClan() == null)
				continue;
			Clan clan = tPlayer.getClan();
			if (clan.getId() != packet.getClanId())
				continue;
			String leader = clan.getLeader();
			BaseMessages.prefix(p, Prefix.CLAN + ColorUtil.WHITE + leader + ColorUtil.GRAY + " deletou o clan!");

			tPlayer.setClan(null);
			tPlayer.updateGroupTag(tPlayer.getDisplayGroup());
		}
	}

	public void processGroup(PacketInGroup packet) {
		Player player = Bukkit.getPlayer(packet.getTargetUniqueId());
		if (player == null) {
			logger.log("Jogador '%s' recebeu uma atualização de grupo, mas está offline!", packet.getTargetUniqueId());
			return;
		}

		TPlayer tPlayer = plugin.getPlayerList().getPlayer(player);
		Group oldGroup = packet.getOldGroup();
		Group newGroup = packet.getNewGroup();

		tPlayer.onGroupChange(oldGroup, newGroup);

		tPlayer.setGroup(newGroup);
		tPlayer.setGroupExpiry(packet.getExpiry());

		if (tPlayer.getTitlesTag() == null) {
			tPlayer.setGroupTag(newGroup);

			for (TPlayer tTarget : plugin.getPlayerList().getPlayers()) {
				tTarget.getScoreboardHandler().updateNametag(tPlayer);
				tPlayer.getScoreboardHandler().updateNametag(tTarget);
			}
		}

		player.setDisplayName(tPlayer.getDisplayGroup().getPrefixFormatted() + player.getName() + ColorUtil.RESET);
		plugin.getPermissionHandler().updatePlayer(tPlayer, true, true, false);
		player.sendMessage(String.format(BaseMessages.YOUR_GROUP_UPDATED, newGroup.getColoredName()));
	}

	public void processSendAlert(PacketInSendHaxAlert packet) {
		Broadcast.pluginSpecific(PlayerToggle.ANTI_CHEAT_ALERTS_ENABLED, Group.MOD, packet.getMessage());
	}

	public void processMessage(PacketInMessage packet) {
		Player player = Bukkit.getPlayer(packet.getTargetUniqueId());
		if (player == null) {
			logger.log("Jogador '%s' recebeu uma mensagem mas está offline!", packet.getTargetUniqueId());
			return;
		}

		BaseMessages.prefix(player, packet.getMessage());
	}

	public void processGlobalMessage(PacketInGlobalMessage packet) {
		if (packet.getMinimumGroup() == Group.getDefaultGroup()) {
			Broadcast.all(packet.getMessage());
			return;
		}

		Broadcast.pluginSpecific(packet.getMinimumGroup(), packet.getMessage());
	}

	public void processToggableAlert(PacketInTogglableAlert packet) {
		Broadcast.pluginSpecific(packet.getToggle(), packet.getMinimumGroup(), packet.getMessage());
	}

	public void processServerInfo(PacketInServerInfo packet) {
		CompleteServerInfo serverInfo = plugin.getServerInfoHandler().getServerInfo(packet.getName());
		if (serverInfo == null) {
			serverInfo = new CompleteServerInfo(packet.getName(), packet.getType());
			this.plugin.getServerInfoHandler().registerServerInfo(serverInfo);
		}

		serverInfo.setStatus(packet.getStatus());
		serverInfo.setPlayers(packet.getPlayers());
		serverInfo.setMaxPlayers(packet.getMaxPlayers());
		serverInfo.setMessage(packet.getMessage());
		serverInfo.setMotd(packet.getMotd());
		serverInfo.setMaintance(packet.isMaintance());
		serverInfo.setWorld(packet.getWorld());
	}

	public void processAddAutoMessage(PacketInAddAutoMessage packet) {
		plugin.getMessageHandler().addAutoMessage(packet.getId(), packet.getMessage());
	}

	public void processRemoveAutoMessage(PacketInRemoveAutoMessage packet) {
		plugin.getMessageHandler().removeAutoMessage(packet.getId());
	}

	public void processPlayerInfoRequest(PacketInPlayerInfoRequest packet) {
		Player target = Bukkit.getPlayer(packet.getTargetUniqueId());
		if (target == null)
			return;

		TPlayer tTarget = plugin.getPlayerList().getPlayer(target);
		List<String> playerInfo = plugin.getDataHandler().getPlayerInfo(tTarget);
		String serverInfoString = Util.stringJoin(playerInfo, "\n");

		StringBuilder builder = new StringBuilder();
		builder.append("§eInformações do servidor onde se encontra o jogador:\n");
		builder.append("§cSua latência no momento é de §f" + plugin.getProtocolAPI().getPing(target) + " ms\n");
		builder.append(
				"§cEstá com o modo fly " + (target.getAllowFlight() ? "ativado" : "desativado") + " no momento.\n");
		builder.append("§cEstá com o admin " + (tTarget.isModerating() ? "ativado" : "desativado") + " no momento.\n");
		builder.append(serverInfoString);

		plugin.getPacketHandler().sendPacketToUniqueId(packet.getPlayerUniqueId(),
				new PacketOutMessage(packet.getPlayerUniqueId(), builder.toString()));
	}
}
