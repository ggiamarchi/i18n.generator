package com.github.ggiamarchi.i18n;

public class GeneratorException extends RuntimeException {

	private static final long serialVersionUID = 3889520282869349654L;

	public GeneratorException(String message) {
		super(message);
	}

	public GeneratorException(String message, Throwable cause) {
		super(message, cause);
	}

	public GeneratorException(Throwable cause) {
		super(cause);
	}

}
