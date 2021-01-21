package br.com.vinicius.core.global.mongodb.util;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.client.FindIterable;

public class MongoUtil {

	public List<MongoDoc> parseList(FindIterable<Document> list) {
		return parseList(list.into(new ArrayList<Document>()));
	}

	public List<MongoDoc> parseList(List<Document> list) {
		List<MongoDoc> mongoList = new ArrayList<MongoDoc>();
		list.forEach(x -> mongoList.add(new MongoDoc(x)));
		return mongoList;
	}

}
