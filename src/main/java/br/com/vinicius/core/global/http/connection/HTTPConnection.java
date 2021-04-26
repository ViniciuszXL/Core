package br.com.vinicius.core.global.http.connection;

import br.com.vinicius.core.global.http.type.HTTPType;
import br.com.vinicius.core.global.query.Query.Search;

public class HTTPConnection {
	
	public static HTTPConnection create() {
		return new HTTPConnection();
	}

	private String url;
	private HTTPType type;
	
	public HTTPConnection() {
		this(null, null);
	}
	
	public HTTPConnection(String url, HTTPType type) {
		this.url = url;
		this.type = type;
	}
	
	public HTTPConnection URL(String url) {
		this.url = url;
		return this;
	}
	
	public HTTPConnection Type(HTTPType type) {
		this.type = type;
		return this;
	}
	
	public HTTPConnection build() {
		return new HTTPConnection(url, type);
	}
	
	public Search<String> startConnection() {
		try {
			HTTPRequest request = new HTTPRequest(url, type);
			request.request();
			return Search.foundOne(request.getResult());
		} catch (Exception e) {
			return Search.error(e.getMessage());
		}
	}
	
}
