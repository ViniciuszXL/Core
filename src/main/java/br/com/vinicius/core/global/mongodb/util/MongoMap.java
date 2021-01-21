package br.com.vinicius.core.global.mongodb.util;

import java.util.HashMap;
import java.util.Map;

public class MongoMap {

	private Map<String, Object> mapList;
	
	public MongoMap() {
		this.mapList = new HashMap<>();
	}
	
	public static MongoMap create() {
		return new MongoMap();
	}
	
	public MongoMap append(String key, Object value) {
		this.mapList.put(key, value);
		return this;
	}
	
	public MongoMap remove(String key) {
		this.mapList.remove(key);
		return this;
	}

	public Map<String, Object> build() {
		return this.mapList;
	}
	
}
