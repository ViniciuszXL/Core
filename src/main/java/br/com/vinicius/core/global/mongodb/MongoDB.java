package br.com.vinicius.core.global.mongodb;

import br.com.vinicius.core.global.logging.FormattedLogger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bson.Document;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import br.com.vinicius.core.Core;
import br.com.vinicius.core.global.mongodb.collections.MongoCollections;
import br.com.vinicius.core.global.mongodb.connect.MongoConnect;
import br.com.vinicius.core.global.mongodb.exception.MongoException;
import br.com.vinicius.core.global.mongodb.util.MongoDoc;
import br.com.vinicius.core.global.mongodb.util.MongoUtil;

public class MongoDB extends MongoUtil {

	private final MongoConnect connect;

	public MongoDB(Core plugin, FormattedLogger logger) {
		this.connect = new MongoConnect(plugin, logger);
	}

	public final boolean initialize() {
		if (!connect.loadConfiguration())
			return false;
		if (!connect.initializeConnection())
			return false;
		if (!this.initializeDocuments())
			return false;
		return true;
	}

	public final MongoConnect connection() {
		return connect;
	}

	public final MongoDatabase database() {
		return connection().getDatabase();
	}

	public final boolean initializeDocuments() {
		try {
			Arrays.asList(MongoCollections.values()).stream().filter(s -> !hasCollection(s)).forEach(d -> {
				try {
					createCollection(d);
				} catch (MongoException e) {
					e.printStackTrace();
				}
			});
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public final MongoCollection<Document> findCollection(MongoCollections mongoCollection) {
		return database().getCollection(mongoCollection.getName());
	}

	public boolean hasCollection(MongoCollections mongoCollection) {
		return database().listCollectionNames().into(new ArrayList<String>()).contains(mongoCollection.getName());
	}

	public void createCollection(MongoCollections mongoCollection) throws MongoException {
		if (hasCollection(mongoCollection))
			throw new MongoException("Already exists collection with name %s.", mongoCollection.getName());

		this.database().createCollection(mongoCollection.getName());
	}

	public List<MongoDoc> find(MongoCollections mongoCollection) throws MongoException {
		if (!hasCollection(mongoCollection))
			throw new MongoException("Collection name %s not exists.", mongoCollection.getName());
		return parseList(findCollection(mongoCollection).find());
	}

	public MongoDoc find(MongoCollections mongoCollection, String key, String value) throws MongoException {
		if (!hasCollection(mongoCollection))
			throw new MongoException("Collection name %s not exists.", mongoCollection.getName());
		FindIterable<Document> dor = findCollection(mongoCollection).find(new Document(key, value));
		if (!dor.iterator().hasNext())
			throw new MongoException("No result found for key '%s' and value '%s'.", key, value);
		return new MongoDoc(dor.iterator().next());
	}

	public void remove(MongoCollections mongoCollection, String key, String value) throws MongoException {
		remove(mongoCollection, new MongoDoc(new Document(key, value)));
	}

	public void remove(MongoCollections mongoCollection, MongoDoc doc) throws MongoException {
		remove(mongoCollection, doc.getDoc());
	}

	public void remove(MongoCollections mongoCollection, Document doc) throws MongoException {
		if (!hasCollection(mongoCollection))
			throw new MongoException("Collection name %s not exists.", mongoCollection.getName());
		findCollection(mongoCollection).deleteOne(doc);
	}

}
