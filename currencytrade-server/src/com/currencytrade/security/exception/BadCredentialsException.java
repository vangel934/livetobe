package com.currencytrade.security.exception;

public class BadCredentialsException extends SecurityException {

	private static final long serialVersionUID = 5513994384369345841L;

	private String user;

	public String getUser() {
		return user;
	}

	public BadCredentialsException(String user, String exceptionCode) {
		super(exceptionCode);
		this.user = user;
	}

	public BadCredentialsException(String user, String exceptionCode,
			String msg, Throwable th) {
		super(exceptionCode, msg, th);
		this.user = user;
	}

}
