package br.com.vinicius.core.global.networking.info;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

import br.com.vinicius.core.Core;
import br.com.vinicius.core.data.BasePlayerData;
import br.com.vinicius.core.data.redis.BaseRedisExecutor;
import br.com.vinicius.core.networking.info.status.ServerStatus;
import br.com.vinicius.core.networking.info.type.ServerType;
import br.com.vinicius.core.networking.packet.out.PacketOutServerInfo;
import br.com.vinicius.core.player.BasePlayer;
import br.com.vinicius.core.utilitaries.logging.FormattedLogger;

public abstract class BaseServerInfoHandler<TPlayer extends BasePlayer<TPlayer, TData>, TData extends BasePlayerData> {

	private final Core<TPlayer, TData> plugin;
	protected final FormattedLogger logger;

	protected final Map<String, CompleteServerInfo> serverInfos;
	protected final Multimap<ServerType, CompleteServerInfo> serverInfosByType;
	public boolean shutdown = false;

	public BaseServerInfoHandler(Core<TPlayer, TData> plugin, FormattedLogger parentLogger) {
		this.plugin = plugin;
		this.logger = new FormattedLogger(parentLogger, "ServerInfo");

		this.serverInfos = new HashMap<String, CompleteServerInfo>();
		this.serverInfosByType = MultimapBuilder.enumKeys(ServerType.class).arrayListValues().build();
	}

	public final CompleteServerInfo getServerInfo(String serverName) {
		return serverInfos.get(serverName.toLowerCase());
	}

	public final List<CompleteServerInfo> getServerInfos() {
		return serverInfos.values().stream().collect(Collectors.toList());
	}

	public final Collection<CompleteServerInfo> getServerInfosByType(ServerType serverType) {
		return serverInfosByType.get(serverType);
	}

	public final void registerServerInfo(CompleteServerInfo serverInfo) {
		this.serverInfos.put(serverInfo.getName().toLowerCase(), serverInfo);
		this.serverInfosByType.put(serverInfo.getType(), serverInfo);
	}

	public final void unregisterServerInfo(CompleteServerInfo serverInfo) {
		this.serverInfos.remove(serverInfo.getName().toLowerCase());
		this.serverInfosByType.remove(serverInfo.getType(), serverInfo);
	}

	public final List<CompleteServerInfo> getServersInfosByType(ServerType serverType) {
		return getServerInfos().stream().filter(s -> s.getType().equals(serverType)).collect(Collectors.toList());
	}

	public final int getOnlinePlayersByType(ServerType type) {
		return getServerInfos().stream().filter(s -> s.getType().equals(type)).mapToInt(i -> i.getPlayers()).sum();
	}

	public void sendServerInfo() {
		CompleteServerInfo serverInfo = new CompleteServerInfo(plugin.getServerName(), plugin.getServerType());
		serverInfo.setStatus(!shutdown ? getStatus() : ServerStatus.STOPPED);
		serverInfo.setPlayers(!shutdown ? Bukkit.getOnlinePlayers().size() : 0);
		serverInfo.setMaxPlayers(Bukkit.getMaxPlayers());
		serverInfo.setMessage(!shutdown ? getMessage() : "Nada aqui");
		serverInfo.setMotd(!shutdown ? getMotd() : "Nada aqui");
		serverInfo.setMaintance(!shutdown ? isMaintance() : false);
		serverInfo.setWorld(!shutdown ? getWorld() : "world");

		try (BaseRedisExecutor<TPlayer, TData> redisExecutor = plugin.getRedisHandler().executor()){
			redisExecutor.sendServerInfo(new PacketOutServerInfo(serverInfo));
		}
	}

	public void shutdown() {
		this.shutdown = true;

		CompleteServerInfo currentServer = new CompleteServerInfo(plugin.getServerName(), plugin.getServerType());
		currentServer.setStatus(ServerStatus.STOPPED);
		currentServer.setPlayers(0);
		currentServer.setMaxPlayers(Bukkit.getMaxPlayers());
		currentServer.setMessage("");
		currentServer.setMotd("");
		currentServer.setMaintance(false);
		currentServer.setWorld("");
		
		try (BaseRedisExecutor<TPlayer, TData> redisExecutor = plugin.getRedisHandler().executor()){
			redisExecutor.sendServerInfo(new PacketOutServerInfo(currentServer));
		}
	}

	public abstract ServerStatus getStatus();

	public abstract String getMessage();

	public abstract String getMotd();

	public abstract String getWorld();

	public abstract boolean isMaintance();

}
