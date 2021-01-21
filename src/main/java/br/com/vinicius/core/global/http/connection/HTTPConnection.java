package br.com.vinicius.core.global.http.connection;

import br.com.vinicius.core.global.http.type.HTTPType;
import br.com.vinicius.core.global.query.Query.Search;

public class HTTPConnection {

	private final String url;
	private final HTTPType type;
	
	public HTTPConnection(String url, HTTPType type) {
		this.url = url;
		this.type = type;
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
