package com.cf.security.impl;

import java.math.BigInteger;
import java.nio.charset.CharacterCodingException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import com.cf.security.exception.CryptoConfigMissingException;

public class ShaCrypt {

	private static final int SALT_SIZE_MIN = 24;
	private static final int HASH_SIZE_MIN = 24;
	private static final int PBKDF2_ITERATIONS = 1000;
	private static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA1";
	private static final int INDEX_ITERATIONS = 0;
	private static final int INDEX_SALT = 1;

	private ShaCrypt() {
		//
	}

	private static final String PATTERN_CONFIG = "{0}:{1}";

	public static String generateConfig() {
		byte[] salt = generateSalt(SALT_SIZE_MIN);

		List<String> params = new ArrayList<>(2);
		params.add(INDEX_ITERATIONS, String.valueOf(PBKDF2_ITERATIONS));
		params.add(INDEX_SALT, CharHelper.toHex(salt));

		return MessageFormat.format(PATTERN_CONFIG, params.toArray());
	}

	private static byte[] getSaltFromConfig(String config)
			throws CharacterCodingException, CryptoConfigMissingException {
		if ((config == null) || config.isEmpty()) {
			throw new CryptoConfigMissingException(
					"getSaltFromConfig.config.missing", "config");
		}

		String[] splitConfig = config.split(":");
		if (splitConfig.length != 2) {
			throw new CryptoConfigMissingException(
					"getSaltFromConfig.config.invalid", "config");
		}

		String saltHash = splitConfig[INDEX_SALT];
		if (saltHash.isEmpty()) {
			throw new CryptoConfigMissingException(
					"getSaltFromConfig.salt.missing", "salt");
		}

		return CharHelper.bytesToStringUTFNIO(CharHelper.fromHex(saltHash))
				.getBytes();
	}

	public static void main(String[] args) {

	}

	private static int getIterationsFromConfig(String config)
			throws CryptoConfigMissingException {
		if ((config == null) || config.isEmpty()) {
			throw new CryptoConfigMissingException(
					"getIterationsFromConfig.config.missing", "config");
		}

		String[] splitConfig = config.split(":");
		if (splitConfig.length != 2) {
			throw new CryptoConfigMissingException(
					"getIterationsFromConfig.config.invalid", "config");
		}

		String iterationString = splitConfig[INDEX_ITERATIONS];
		if (iterationString.isEmpty()) {
			throw new CryptoConfigMissingException(
					"getIterationsFromConfig.config.invalid", "iterations");
		}

		return Integer.parseInt(iterationString);
	}

	private static byte[] generateSalt(int saltSize) {
		if (saltSize < SALT_SIZE_MIN) {
			saltSize = SALT_SIZE_MIN;
		}
		// Generate a random salt
		SecureRandom random = new SecureRandom();
		String salt = new BigInteger(130, random).toString(32);
		return salt.getBytes();

		// SecureRandom random = new SecureRandom();
		// byte[] salt = new byte[saltSize];
		// random.nextBytes(salt);
		// return salt;
	}

	public static String generateUUID(int size) {
		if (size < SALT_SIZE_MIN) {
			size = SALT_SIZE_MIN;
		}

		UUID uuid = UUID.nameUUIDFromBytes(generateSalt(size));
		return uuid.toString();
	}

	private static byte[] pbkdf2(char[] password, byte[] salt, int iterations,
			int bytes) throws NoSuchAlgorithmException, InvalidKeySpecException {
		PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, bytes * 8);
		SecretKeyFactory skf = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
		return skf.generateSecret(spec).getEncoded();
	}

	public static String encrypt(char[] password, String cryptoConfig)
			throws NoSuchAlgorithmException, InvalidKeySpecException,
			CharacterCodingException, CryptoConfigMissingException {
		if (password.length == 0) {
			throw new IllegalArgumentException("encrypt.password.missing");
		}

		if ((cryptoConfig == null) || cryptoConfig.isEmpty()) {
			throw new IllegalArgumentException("encrypt.cryptoConfig.missing");
		}

		// Hash the password
		byte[] hash = pbkdf2(password, getSaltFromConfig(cryptoConfig),
				getIterationsFromConfig(cryptoConfig), HASH_SIZE_MIN);

		return CharHelper.convertToHex(hash);
	}

}
