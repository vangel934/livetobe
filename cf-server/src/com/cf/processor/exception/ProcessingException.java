package com.cf.processor.exception;

public class ProcessingException extends Exception {

	private static final long serialVersionUID = -5169045955056077310L;

	private String exceptionCode;
	private String operation;

	public ProcessingException(String exceptionCode, String operation) {
		super();
		this.exceptionCode = exceptionCode;
		this.operation = operation;
	}

	public ProcessingException(String exceptionCode, String operation,
			String msg, Throwable th) {
		super(msg, th);
		this.exceptionCode = exceptionCode;
		this.operation = operation;
	}

	public String getExceptionCode() {
		return exceptionCode;
	}

	public String getOperation() {
		return operation;
	}

}
