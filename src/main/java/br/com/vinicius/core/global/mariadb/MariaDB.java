package br.com.vinicius.core.global.mariadb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import br.com.vinicius.core.Core;
import br.com.vinicius.core.global.logging.FormattedLogger;
import br.com.vinicius.core.global.mariadb.connect.MariaConnect;
import br.com.vinicius.core.global.mariadb.connect.constructor.MariaConnectResult;
import br.com.vinicius.core.global.utilitaries.Util;

public class MariaDB {

	private final MariaConnect connect;

	public MariaDB(Core plugin, FormattedLogger logger) {
		this.connect = new MariaConnect(plugin, logger);
	}

	public final boolean initialize() {
		if (!connect.loadConfiguration())
			return false;
		if (!connect.initializeConnection())
			return false;
		if (!connect.initialize())
			return false;
		return true;
	}

	public final Connection connection() {
		return connect.getConnection();
	}

	public final MariaConnectResult query(String sql) throws SQLException {
		PreparedStatement preparedStatement = null;

		try {
			preparedStatement = connection().prepareStatement(sql);
			return MariaConnectResult.create(1).resultSet(preparedStatement.executeQuery()).build();
		} catch (SQLException e) {
			e.printStackTrace();
			return MariaConnectResult.create(0).exception(e).build();
		} finally {
			Util.closeStatement(preparedStatement);
		}
	}

	public final MariaConnectResult query(String sql, Object... objects) throws SQLException {
		PreparedStatement preparedStatement = null;

		try {
			int id = 1;
			preparedStatement = connection().prepareStatement(sql);
			for (Object object : objects) {
				preparedStatement.setObject(id, object);
				id += 1;
			}

			return MariaConnectResult.create(1).resultSet(preparedStatement.executeQuery()).build();
		} catch (SQLException e) {
			e.printStackTrace();
			return MariaConnectResult.create(0).exception(e).build();
		} finally {
			Util.closeStatement(preparedStatement);
		}
	}

	public final MariaConnectResult update(String sql) throws SQLException {
		PreparedStatement preparedStatement = null;

		try {
			preparedStatement = connection().prepareStatement(sql);
			int result = preparedStatement.executeUpdate();
			return MariaConnectResult.create(result).maxRows(preparedStatement.getMaxRows())
					.queryTimeout(preparedStatement.getQueryTimeout()).warning(preparedStatement.getWarnings()).build();
		} catch (SQLException e) {
			e.printStackTrace();
			return MariaConnectResult.create(0).exception(e).build();
		} finally {
			Util.closeStatement(preparedStatement);
		}
	}

	public final MariaConnectResult update(String sql, Object... objects) throws SQLException {
		PreparedStatement preparedStatement = null;

		try {
			int id = 1;
			preparedStatement = connection().prepareStatement(sql);
			if (objects != null) {
				for (Object object : objects) {
					preparedStatement.setObject(id, object);
					id += 1;
				}
			}

			int result = preparedStatement.executeUpdate();
			return MariaConnectResult.create(result).maxRows(preparedStatement.getMaxRows())
					.queryTimeout(preparedStatement.getQueryTimeout()).warning(preparedStatement.getWarnings()).build();
		} catch (SQLException e) {
			e.printStackTrace();
			return MariaConnectResult.create(0).exception(e).build();
		} finally {
			Util.closeStatement(preparedStatement);
		}
	}

}
