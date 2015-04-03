package com.cf.security.exception;

public class CryptoConfigMissingException extends SecurityException {

	/**
	 *
	 */
	private static final long serialVersionUID = -2554201485017660105L;

	private String missing;

	public String getMissing() {
		return missing;
	}

	public CryptoConfigMissingException(String missing, String exceptionCode) {
		super(exceptionCode);
		this.missing = missing;
	}

	public CryptoConfigMissingException(String missing, String exceptionCode,
			String msg, Throwable th) {
		super(exceptionCode, msg, th);
		this.missing = missing;
	}

}
