package com.cf.security.impl;

import java.nio.charset.CharacterCodingException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import com.cf.security.PassEncoder;
import com.cf.security.exception.CryptoConfigMissingException;
import com.cf.security.exception.EncryptException;

public class ShaPassEncoder implements PassEncoder{

	private static final PassEncoder INSTANCE = new ShaPassEncoder();
	public static PassEncoder getInstance() {
		return INSTANCE;
	}
	//	public ShaPassEncoder(String config){
	//		this.config = config;
	//	}

	private ShaPassEncoder(){

	}

	@Override
	public String encodePass(String pass, String config) throws EncryptException, CryptoConfigMissingException {
		try {
			return ShaCrypt.encrypt(pass.toCharArray(), config);
		} catch (NoSuchAlgorithmException |InvalidKeySpecException | CharacterCodingException e) {
			throw new EncryptException(null, "encodePass.general");
		}
	}

	@Override
	public boolean isPassValid(String passIn, String passSer, String config) throws EncryptException, CryptoConfigMissingException  {
		String encodedPass = encodePass(passIn, config);
		return encodedPass.equals(passSer);
	}

	//	@Override
	//	public String getEncoderConfig() {
	//		return config;
	//	}

}
