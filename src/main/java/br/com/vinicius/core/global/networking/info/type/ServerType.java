package br.com.vinicius.core.global.networking.info.type;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum ServerType {

	// Lobbies //
	LOBBY(0, "Lobby"),
	SKYWARS_LOBBY(1, "SkyWars Lobby"),
	BEDWARS_LOBBY(2, "BedWars Lobby"),
	
	// Login Servers //
	LOGIN(10, "Login"),
	
	// Servers //
	PVP(11, "PvP"),
	
	// SkyWars //
	SKY_WARS_SOLO(20, "SkyWars Solo"),
	SKY_WARS_DUO(21, "SkyWars Dupla"),
	
	// Bed Wars //
	BED_WARS_SOLO(31, "BedWars Solo"),
	BED_WARS_DUO(32, "BedWars Dupla"),
	BED_WARS_TRIO(33, "BedWars Trio"),
	BED_WARS_QUARTETO(34, "BedWars Quarteto"),

	private static final Map<Integer, ServerType> BY_ID = new HashMap<Integer, ServerType>();

	private final int id;
	private final String name;

	static {
		for (ServerType type : values()) {
			BY_ID.put(type.id, type);
		}
	}

	private ServerType(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public static ServerType getServerType(int id) {
		return BY_ID.get(id);
	}

	public static ServerType getServerType(String name) {
		return Arrays.asList(values()).stream().filter(s -> s.getName().equalsIgnoreCase(name)).findFirst()
				.orElse(null);
	}
}
