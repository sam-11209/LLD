package com.system.lld.exception;

public class NotFoundException extends RuntimeException {

	private static final long serialVersionUID = 203347878474111339L;

	private final String statusCode;
	private final String errorMessage;

	public NotFoundException(String statusCode, String message, Throwable cause) {
		super(message, cause);
		this.statusCode = statusCode;
		this.errorMessage = message;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}
}
