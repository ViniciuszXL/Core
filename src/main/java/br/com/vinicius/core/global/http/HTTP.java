package br.com.vinicius.core.global.http;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import br.com.vinicius.core.global.http.api.HTTPUrl;
import br.com.vinicius.core.global.http.api.HTTPAPI;
import br.com.vinicius.core.global.http.connection.HTTPConnection;
import br.com.vinicius.core.global.http.type.HTTPType;
import br.com.vinicius.core.global.query.Query.Result;
import br.com.vinicius.core.global.query.Query.Search;

public class HTTP {
	
	private final ThreadFactoryBuilder threadFactory;
	private final Executor asyncExecutor;
	
	public HTTP() {
		this.threadFactory = new ThreadFactoryBuilder();
		this.threadFactory.setNameFormat("Core HTTP Thread");
		this.threadFactory.setDaemon(false);
		
		this.asyncExecutor = Executors.newCachedThreadPool(threadFactory.build());
	}
	
	public final Search<String> request(String url, HTTPType type) {
		return HTTPConnection.create().URL(url).Type(type).startConnection();
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
		return HTTPConnection.create().URL(url).Type(type).startConnection();
	}
	
	public final Search<String> request(HTTPAPI api, HTTPAPI.Type APIType, HTTPType type) {
		String url = api.getURL() + APIType.name().toLowerCase();
		return HTTPConnection.create().URL(url).Type(type).startConnection();
	}
	
	// Update Method //
	
	public final void update(int id, String table, String value) {
		if (!value.contains(":") && !value.contains(";")) {
			throw new Error("Argument for the update is incorrect. [1]");
		}
		
		String url = HTTPAPI.UPDATE.getURL() + "id/" + id + "/" + table + "/" + value;
		attemptToUpdate(new HTTPConstructor() {
			public Search<String> run() {
				return HTTPConnection.create().URL(url).Type(HTTPType.GET).startConnection();
			}
		});
	}
	
	private final void attemptToUpdate(HTTPConstructor constructor) {
		asyncExecutor.execute(() -> {
			Search<String> result = constructor.run();
			if (result.result() == Result.ERROR) {
				throw new Error("An error ocurred for the execute the request: " + result.first());
			}
		});
	}
}
