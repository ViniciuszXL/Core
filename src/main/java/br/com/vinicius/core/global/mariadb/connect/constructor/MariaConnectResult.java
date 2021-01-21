package br.com.vinicius.core.global.mariadb.connect.constructor;

import java.io.Closeable;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;

import br.com.vinicius.core.global.utilitaries.Util;

public class MariaConnectResult implements Closeable {

	private final int result;
	private boolean success;
	private boolean error;
	
	private int maxRows;
	private int queryTimeout;
	
	private SQLWarning warning;
	private SQLException exception;
	
	private ResultSet resultSet;
	
	public MariaConnectResult(int result) {
		this.result = result;
	}
	
	public static MariaConnectResult create(int result) {
		return new MariaConnectResult(result);
	}
	
	public final int getResult() {
		return result;
	}
	
	public final boolean isSuccess() {
		return success;
	}
	
	public final boolean isError() {
		return error;
	}
	
	public final int getMaxRows() {
		return maxRows;
	}
	
	public final int getQueryTimeout() {
		return queryTimeout;
	}
	
	public final SQLWarning getWarning() {
		return warning;
	}
	
	public final SQLException getException() {
		return exception;
	}
	
	public final ResultSet getResultSet() {
		return resultSet;
	}
	
	public final MariaConnectResult maxRows(int maxRows) {
		this.maxRows = maxRows;
		return this;
	}
	
	public final MariaConnectResult queryTimeout(int queryTimeout) {
		this.queryTimeout = queryTimeout;
		return this;
	}
	
	public final MariaConnectResult warning(SQLWarning warning) {
		this.warning = warning;
		return this;
	}
	
	public final MariaConnectResult resultSet(ResultSet resultSet) {
		this.resultSet = resultSet;
		return this;
	}
	
	public final MariaConnectResult exception(SQLException exception) {
		this.exception = exception;
		return this;
	}
	
	public final MariaConnectResult build() {
		this.success = this.result == 0 ? false : true;
		this.error = this.result == 1 ? false : true;
		return this;
	}

	@Override
	public void close() throws IOException {
		Util.closeResultSet(resultSet);
	}
	
}
