package br.com.vinicius.core.teste;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bson.Document;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import br.com.vinicius.core.global.http.HTTP;
import br.com.vinicius.core.global.http.api.HTTPUrl;
import br.com.vinicius.core.global.http.api.HTTPAPI;
import br.com.vinicius.core.global.http.api.HTTPAPI.Type;
import br.com.vinicius.core.global.http.type.HTTPType;
import br.com.vinicius.core.global.query.Query.Result;
import br.com.vinicius.core.global.query.Query.Search;

public class ClassTeste {

	public static void main12(String[] args) {
		HTTP http = new HTTP();
		long millis = System.currentTimeMillis();

		try {
			Search<String> get = http.request(HTTPAPI.PLAYERS, HTTPAPI.Type.GET, HTTPType.GET);
			if (get.result() == Result.ERROR) {
				System.out.println(get.first());
				return;
			}

			System.out.println(get.first());
		} finally {
			System.out.println("getPlayers: " + (System.currentTimeMillis() - millis) + "ms");
		}
	}

	public static void main(String[] args) {
		HTTP http = new HTTP();
		boolean error = false;
		int requestId = 0;
		long mediaMS = 0, max = 0, min = -1;

		do {
			long millis = System.currentTimeMillis();
			String msg = "";

			try {
				Search<String> get = http.request(HTTPAPI.PLAYERS, HTTPAPI.Type.GET, HTTPType.GET);
				if (get.result() == Result.ERROR) {
					error = true;
					System.out.println(get.first());
					return;
				}

				msg = get.first();
			} finally {
				long formatted = System.currentTimeMillis() - millis;
				if (min == -1)
					min = formatted;

				if (formatted < min)
					min = formatted;

				if (formatted > max)
					max = formatted;

				mediaMS += formatted;
				System.out.println((requestId++) + " (" + formatted + "ms, MIN: " + min + "ms, MAX: " + max
						+ "ms, AVG: " + (mediaMS / requestId) + "ms): " + msg);
			}
		} while (!error);
	}

	public static URLConnection setupConnection(URL url) throws IOException {
		URLConnection connection = url.openConnection();
		connection.setConnectTimeout(1000);
		connection.setReadTimeout(1000);
		connection.setUseCaches(false);
		connection.setDoInput(true);
		connection.setDoOutput(true);
		return connection;
	}

	public static void main3(String[] args) {
		String urlString = "http://localhost/api/players/create?name=ViniciuszX&uuid=8ad49e67-9acc-4363-9370-eccc62f0bfa4";
		long millis = System.currentTimeMillis();

		try {
			StringBuilder result = new StringBuilder();
			URL url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.addRequestProperty("User-Agent",
					"Mozilla/5.0 (Macintosh; Intel Mac OS X 11_0_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36");
			conn.addRequestProperty("Cache-Control", "no-cache, no-store, must-revalidate");
			conn.addRequestProperty("Pragma", "no-cache");
			conn.addRequestProperty("Accept", "application/json, text/plain, */*");
			conn.addRequestProperty("Content-Type", "application/json");
			conn.addRequestProperty("Sec-Fetch-Mode", "cors");
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			rd.close();

			System.out.println(result.toString());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			System.out.println("registerNewPlayer: " + ((System.currentTimeMillis() - millis) + "ms"));
		}
	}

	public static void main2(String[] args) {
		String urlString = "http://localhost/api/players/create?name=ViniciuszX&uuid=8ad49e67-9acc-4363-9370-eccc62f0bfa4";
		long millis = System.currentTimeMillis();

		try {
			URL url = new URL(urlString);
			HttpURLConnection http = (HttpURLConnection) url.openConnection();
			http.setUseCaches(false);
			http.setDefaultUseCaches(false);
			http.addRequestProperty("User-Agent", "Mozilla/5.0");
			http.addRequestProperty("Cache-Control", "no-cache, no-store, must-revalidate");
			http.addRequestProperty("Pragma", "no-cache");
			http.connect();

			try {
				System.out.println("Code: " + http.getResponseCode());

				BufferedReader buff = new BufferedReader(new InputStreamReader(http.getInputStream()));
				String output = "";
				String line = null;
				while ((line = buff.readLine()) != null)
					output += line;

				JsonParser parser = new JsonParser();
				JsonObject json = (JsonObject) parser.parse(output);

				System.out.println(json);
				System.out.println(json.get("response"));
			} finally {
				http.disconnect();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			System.out.println("registerNewPlayer: " + ((System.currentTimeMillis() - millis) + "ms"));
		}
	}

	public static void main1(String[] args) {
		try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")) {
			MongoDatabase database = mongoClient.getDatabase("wm");
			if (!containsCollection(database, "teste"))
				database.createCollection("teste");

			MongoCollection<Document> collection = database.getCollection("teste");
			List<Document> documents = new ArrayList<Document>();
			Map<String, Object> mapList = MapConstructor.create().append("name", "ViniciuszXL")
					.append("uniqueId", UUID.randomUUID().toString()).append("group", "Dono").append("sexy", true)
					.build();
			mapList.put("groups",
					Arrays.asList(MapConstructor.create().append("groupId", 1).append("group_expiry", -1).build(),
							MapConstructor.create().append("groupId", 2).append("group_expiry", -1).build()));

			documents.add(new Document(mapList));
			documents.add(new Document(
					MapConstructor.create().append("name", "LuizLyrio").append("uniqueId", UUID.randomUUID().toString())
							.append("group", "Dono").append("sexy", false).build()));

			collection.insertMany(documents);

			// printDocuments(collection);
			printDocument(collection, "name", "ViniciuszXL");

			documents.clear();
			documents.addAll(Arrays.asList(new Document("name", "ViniciuszXL"), new Document("name", "LuizLyrio")));
			documents.forEach(x -> collection.deleteOne(x));
			printDocuments(collection);
		}
	}

	public static boolean containsCollection(MongoDatabase database, String collectionName) {
		return database.listCollectionNames().into(new ArrayList<String>()).contains(collectionName);
	}

	public static void printDocuments(MongoCollection<Document> collection) {
		int size = collection.find().into(new ArrayList<Document>()).size();
		if (size < 1) {
			System.out.println("Não há valores setados!");
			return;
		}

		for (Document cur : collection.find()) {
			System.out.println(cur.toJson());
		}
	}

	public static void printDocument(MongoCollection<Document> collection, String key, Object value) {
		FindIterable<Document> dor = collection.find(new Document(key, value));
		if (!dor.iterator().hasNext()) {
			System.out.println("Não foi encontrado!");
			return;
		}

		Document doc = dor.iterator().next();
		List<Document> docList = doc.getList("groups", Document.class);

		System.out.println("\nFound: " + doc.toJson());
		System.out.println("\nGroups: " + doc.get("groups"));
		System.out.println("Group 1: " + docList.get(0).toJson());
		System.out.println("    groupId: " + docList.get(0).getInteger("groupId"));
		System.out.println("    group_expiry: " + docList.get(0).getInteger("group_expiry"));

		System.out.println("Group 2: " + docList.get(1).toJson());
		System.out.println("    groupId: " + docList.get(1).getInteger("groupId"));
		System.out.println("    group_expiry: " + docList.get(1).getInteger("group_expiry") + "\n");
	}

	public static class MapConstructor {

		private Map<String, Object> mapList;

		public MapConstructor() {
			this.mapList = new HashMap<String, Object>();
		}

		public static MapConstructor create() {
			return new MapConstructor();
		}

		public MapConstructor append(String key, Object value) {
			this.mapList.put(key, value);
			return this;
		}

		public Map<String, Object> build() {
			return this.mapList;
		}

	}

}
