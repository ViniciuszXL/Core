package br.com.vinicius.core.global.mongodb.exception;

public class MongoException extends Throwable {

	private static final long serialVersionUID = -100359791885779166L;

	public MongoException() {
		super();
	}

	public MongoException(String message) {
		super(message);
	}
	
	public MongoException(String message, Object... args) {
		super(String.format(message, args));
	}

	public MongoException(String message, Throwable cause) {
		super(message, cause);
	}

	public MongoException(Throwable cause) {
		super(cause);
	}

	public MongoException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
