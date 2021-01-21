package br.com.vinicius.core.global.http.connection;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import br.com.vinicius.core.global.http.result.HTTPResult;
import br.com.vinicius.core.global.http.type.HTTPType;

public class HTTPRequest {

	private final String url;
	private final StringBuilder builder;
	private final HTTPType type;

	private int HTTP_CODE;
	private String HTTP_ERROR_MESSAGE;

	private HTTPResult result;

	public HTTPRequest(String url, HTTPType type) {
		this.url = url;
		this.type = type;

		this.builder = new StringBuilder();
	}

	public final String getResult() {
		return builder.toString();
	}

	public final int getCode() {
		return HTTP_CODE;
	}

	public final boolean hasError() {
		return HTTP_CODE != 200 ? true : false;
	}

	public final String getErrorMessage() {
		return HTTP_ERROR_MESSAGE;
	}

	public final HTTPResult getHTTPResult() {
		return result;
	}

	public final HTTPType getHTTPType() {
		return type;
	}

	public final void request() throws Exception {
		HttpURLConnection connection = (HttpURLConnection) new URL(this.url).openConnection();

		// Method Request //
		connection.setRequestMethod(this.type.name().toUpperCase());

		// Request Property //
		connection.addRequestProperty("User-Agent",
				"Mozilla/5.0 (Macintosh; Intel Mac OS X 11_0_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36");
		connection.addRequestProperty("Cache-Control", "no-cache, no-store, must-revalidate");
		connection.addRequestProperty("Pragma", "no-cache");
		connection.addRequestProperty("Accept", "application/json, text/plain, */*");
		connection.addRequestProperty("Content-Type", "application/json");
		connection.addRequestProperty("Sec-Fetch-Mode", "cors");

		HTTP_CODE = connection.getResponseCode();
		if (HTTP_CODE == 404)
			throw new Exception("This url for request has returned error code 404. This url not exists.");
		if (HTTP_CODE == 502)
			throw new Exception("API is not initialized! Return error code: 502");
		if (HTTP_CODE == 500)
			throw new Exception("API error ocurred. Tell the administrators for check the API console"
					+ " in docker-compose. Return error code: 500");

		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String line;

		while ((line = reader.readLine()) != null) {
			builder.append(line);
		}

		reader.close();
	}

}
