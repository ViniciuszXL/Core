package br.com.vinicius.core.global.utilitaries.lang.convert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum EnumLang {

	PT_BR("pt_br", new HashMap<String, String>());

	private static final Map<String, EnumLang> lookup = new HashMap<String, EnumLang>();

	static {
		for (EnumLang lang : EnumSet.allOf(EnumLang.class))
			lookup.put(lang.getLocale(), lang);
	}

	private final String locale;
	private final Map<String, String> map;

	EnumLang(String locale, Map<String, String> map) {
		this.locale = locale;
		this.map = map;
	}

	public static EnumLang get(String locale) {
		EnumLang result = lookup.get(locale);
		return result == null ? PT_BR : result;
	}

	public static void clean() {
		for (EnumLang enumLang : EnumLang.values()) {
			enumLang.getMap().clear();
		}
	}

	public static void init() {
		for (EnumLang enumLang : EnumLang.values()) {
			try {
				InputStreamReader stream = new InputStreamReader(EnumLang.class.getResourceAsStream("/pt_br.lang"),
						"UTF-8");
				BufferedReader reader = new BufferedReader(stream);

				readFile(enumLang, reader);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void readFile(EnumLang enumLang, BufferedReader reader) throws IOException {
		String temp;
		String[] tempStringArr;

		try {
			temp = reader.readLine();
			while (temp != null) {
				if (temp.startsWith("#"))
					continue;
				if (temp.contains("=")) {
					tempStringArr = temp.split("=");
					enumLang.map.put(tempStringArr[0], tempStringArr.length > 1 ? tempStringArr[1] : "");
				}

				temp = reader.readLine();
			}
		} finally {
			reader.close();
		}
	}

	public String getLocale() {
		return locale;
	}

	public Map<String, String> getMap() {
		return map;
	}
}
