package br.com.vinicius.core.global.networking.info.status;

public enum ServerStatus {
	
	STOPPED,
	WAITING,
	PLAYING;

	public static ServerStatus getStatus(int id) {
		return id < 0 || id >= values().length ? null : values()[id];
	}
}
