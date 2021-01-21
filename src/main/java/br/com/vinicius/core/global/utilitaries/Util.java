package br.com.vinicius.core.global.utilitaries;

import java.net.HttpURLConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class Util {
	
	public static final String formatMillis(long millis) {
		return (System.currentTimeMillis() - millis) + "ms";
	}
	
	public static final String formatUniqueId(UUID uniqueId, int length) {
		String formatedUniqueId = uniqueId.toString().replace("-", "");
		formatedUniqueId = formatedUniqueId.substring(0, length);
		return formatedUniqueId;
	}

	public static void closeStatement(Statement statement) {
		if (statement == null)
			return;

		try {
			if (!statement.isClosed())
				statement.close();
		} catch (SQLException e) {
		}
	}

	public static void closeResultSet(ResultSet resultSet) {
		if (resultSet == null)
			return;

		try {
			if (!resultSet.isClosed())
				resultSet.close();
		} catch (SQLException e) {
		}
	}

	public static void closeConnection(Connection connection) {
		if (connection == null)
			return;

		try {
			if (!connection.isClosed())
				connection.close();
		} catch (Exception e) {
		}
	}

	public static void closeConnection(Jedis connection) {
		if (connection == null)
			return;

		try {
			if (connection.isConnected())
				connection.close();
		} catch (Exception e) {
		}
	}

	public static void closeConnection(JedisPool connection) {
		if (connection == null)
			return;

		try {
			if (!connection.isClosed())
				connection.close();
		} catch (Exception e) {
		}
	}

	public static void closeConnection(HttpURLConnection connection) {
		if (connection == null)
			return;

		try {
			connection.disconnect();
		} catch (Exception e) {
		}
	}

}
