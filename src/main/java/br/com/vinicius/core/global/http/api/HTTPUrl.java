package br.com.vinicius.core.global.http.api;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class HTTPUrl {

	public static HTTPUrl create() {
		return new HTTPUrl();
	}

	private final Map<String, Object> mapList;

	public HTTPUrl() {
		this.mapList = new HashMap<>();
	}

	public HTTPUrl add(String key, Object value) {
		mapList.put(key, value);
		return this;
	}

	public String build() {
		String build = "";
		boolean isQuery = mapList.size() > 1;

		for (Entry<String, Object> entry : mapList.entrySet()) {
			if (isQuery)
				build = build + (build.contains("?") ? "&" : "?") + entry.getKey() + "=" + entry.getValue();
			else
				build = "/" + entry.getKey() + "/" + entry.getValue();
		}

		return build;
	}

}
