package br.com.vinicius.core.global.mongodb.collections;

public enum MongoCollections {

	GROUP("groups"),
	PERMISSIONS("permissions");
	
	private final String name;
	
	private MongoCollections(String name) {
		this.name = name;
	}
	
	public final String getName() {
		return name;
	}
	
}
