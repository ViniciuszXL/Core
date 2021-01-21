package br.com.vinicius.core.global.mongodb.util;

import java.util.List;

import org.bson.Document;

public class MongoDoc extends MongoUtil {

	private final Document document;

	public MongoDoc(Document document) {
		this.document = document;
	}

	public final Document getDoc() {
		return document;
	}

	public final String getString(String key) {
		return document.getString(key);
	}

	public final List<MongoDoc> getList(String key) {
		return parseList(document.getList(key, Document.class));
	}

}
