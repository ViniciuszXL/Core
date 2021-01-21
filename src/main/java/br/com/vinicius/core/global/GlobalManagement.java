package br.com.vinicius.core.global;

import br.com.vinicius.core.Core;
import br.com.vinicius.core.global.group.GroupManagement;
import br.com.vinicius.core.global.http.HTTP;
import br.com.vinicius.core.global.logging.FormattedLogger;
import br.com.vinicius.core.global.mariadb.MariaDB;
import br.com.vinicius.core.global.mongodb.MongoDB;
import br.com.vinicius.core.global.redis.Redis;

public class GlobalManagement {

	private final GroupManagement groupManagement;

	private final HTTP http;
	private final MariaDB mariaDB;
	private final Redis redis;
	private final MongoDB mongoDB;

	public GlobalManagement(Core plugin, FormattedLogger logger) {
		this.groupManagement = new GroupManagement();

		this.http = new HTTP();
		this.mariaDB = new MariaDB(plugin, logger);
		this.redis = new Redis(plugin, logger);
		this.mongoDB = new MongoDB(plugin, logger);
	}

	public final boolean initialize() {
		if (!mariaDB.initialize())
			return false;
		if (!redis.initialize())
			return false;
		if (!mongoDB.initialize())
			return false;
		return true;
	}

	public final GroupManagement group() {
		return groupManagement;
	}

	public final HTTP http() {
		return http;
	}

	public final MariaDB maria() {
		return mariaDB;
	}

	public final Redis redis() {
		return redis;
	}

	public final MongoDB mongo() {
		return mongoDB;
	}

}
