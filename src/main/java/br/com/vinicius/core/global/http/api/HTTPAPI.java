package br.com.vinicius.core.global.http.api;

public enum HTTPAPI {

	UPDATE("update"),
	
	INFO("infos"),
	PERMISSIONS("permissions"),
	PLAYERS("players"),
	GROUPS("groups");
	
	private final String url;
	
	private HTTPAPI(String url) {
		this.url = url;
	}
	
	public final String getURL() {
		return "http://localhost/api/" + url + "/";
	}

	public enum Type {
		
		DELETE, CREATE, GET;
		
	}
	
}
