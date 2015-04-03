package com.currencytrade.security;

import com.currencytrade.security.exception.CryptoConfigMissingException;
import com.currencytrade.security.exception.EncryptException;

public interface PassEncoder {
	// public String getEncoderConfig();

	public String encodePass(String pass, String config)
			throws EncryptException, CryptoConfigMissingException;

	public boolean isPassValid(String passIn, String passSer, String config)
			throws EncryptException, CryptoConfigMissingException;
}
