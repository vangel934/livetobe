package com.currencytrade.security.exception;

public class SecurityException extends Exception {

	private static final long serialVersionUID = -3914353762320690744L;

	private String exceptionCode;

	public String getExceptionCode() {
		return exceptionCode;
	}

	public SecurityException(String exceptionCode) {
		super();
		this.exceptionCode = exceptionCode;
	}

	public SecurityException(String exceptionCode, String msg, Throwable th) {
		super(msg, th);
		this.exceptionCode = exceptionCode;
	}

}
