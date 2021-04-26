package br.com.vinicius.core.global.networking.info;

import br.com.vinicius.core.networking.info.status.ServerStatus;
import br.com.vinicius.core.networking.info.type.ServerType;

public final class CompleteServerInfo {

	private final String name;

	private final ServerType type;
	private ServerStatus status;

	private int players;
	private int maxPlayers;

	private String message;
	private String motd;
	private String world;

	private boolean maintance;

	public CompleteServerInfo(String name, ServerType type) {
		this.name = name;
		this.type = type;
		this.status = ServerStatus.STOPPED;

		this.players = 0;
		this.maxPlayers = 20;

		this.message = null;
		this.motd = null;
		this.world = null;

		this.maintance = false;
	}

	public String getName() {
		return name;
	}

	public ServerType getType() {
		return type;
	}

	public ServerStatus getStatus() {
		return status;
	}
	
	public boolean isStatus(ServerStatus status) {
		return this.status.equals(status);
	}

	public void setStatus(ServerStatus status) {
		this.status = status;
	}

	public int getPlayers() {
		return players;
	}

	public void setPlayers(int players) {
		this.players = players;
	}

	public int getMaxPlayers() {
		return maxPlayers;
	}

	public void setMaxPlayers(int maxPlayers) {
		this.maxPlayers = maxPlayers;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMotd() {
		return motd;
	}

	public void setMotd(String motd) {
		this.motd = motd;
	}

	public boolean isMaintance() {
		return maintance;
	}

	public void setMaintance(boolean maintance) {
		this.maintance = maintance;
	}

	public String getWorld() {
		return world;
	}

	public void setWorld(String world) {
		this.world = world;
	}

	@Override
	public String toString() {
		return String.format(
				"{name:%s, type:%s, status:%s, players:%d, maxPlayers:%d, message:%s, motd:%s, maintance:%s, world:%s}",
				name, type, status, players, maxPlayers, message, motd, maintance, world);
	}
}
