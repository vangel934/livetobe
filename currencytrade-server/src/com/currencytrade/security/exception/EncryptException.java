package com.currencytrade.security.exception;

public class EncryptException extends SecurityException {

	private static final long serialVersionUID = -6689982337557925408L;

	private String source;

	public EncryptException(String source, String exceptionCode) {
		super(exceptionCode);
		this.source = source;
	}

	public EncryptException(String source, String exceptionCode, String msg,
			Throwable th) {
		super(exceptionCode, msg, th);
		this.source = source;
	}

	public String getSource() {
		return source;
	}
}
