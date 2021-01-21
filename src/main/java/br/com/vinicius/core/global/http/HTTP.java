package br.com.vinicius.core.global.http;

import java.util.Arrays;
import java.util.List;

import br.com.vinicius.core.global.http.api.HTTPUrl;
import br.com.vinicius.core.global.http.api.HTTPAPI;
import br.com.vinicius.core.global.http.api.HTTPAPI.Type;
import br.com.vinicius.core.global.http.connection.HTTPConnection;
import br.com.vinicius.core.global.http.type.HTTPType;
import br.com.vinicius.core.global.query.Query.Search;

public class HTTP {
	
	public final Search<String> request(String url, HTTPType type) {
		HTTPConnection connection = new HTTPConnection(url, type);
		return connection.startConnection();
	}
	
	public final Search<String> request(HTTPAPI api, HTTPAPI.Type APIType, HTTPType type, String... args){
		return request(api, APIType, type, Arrays.asList(args));
	}
	
	public final Search<String> request(HTTPAPI api, HTTPAPI.Type APIType, HTTPType type, List<String> args){
		HTTPUrl httpURL = HTTPUrl.create();
		for (String s : args) {
			if (!s.contains(":"))
				continue;
			String[] split = s.split(":");
			String key = split[0];
			String value = split[1];
			httpURL.add(key, value);
		}
		return request(api, APIType, type, httpURL);
	}

	public final Search<String> request(HTTPAPI api, HTTPAPI.Type APIType, HTTPType type, HTTPUrl httpURL) {
		String url = api.getURL() + APIType.name().toLowerCase() + httpURL.build();
		HTTPConnection connection = new HTTPConnection(url, type);
		return connection.startConnection();
	}
	
	public final Search<String> request(HTTPAPI api, HTTPAPI.Type APIType, HTTPType type) {
		String url = api.getURL() + APIType.name().toLowerCase();
		HTTPConnection connection = new HTTPConnection(url, type);
		return connection.startConnection();
	}
	
}
