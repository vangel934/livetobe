package com.currencytrade.security.impl;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.security.SecureRandom;

public class CharHelper {

	private CharHelper() {
		//
	}

	public static String bytesToStringUTFNIO(byte[] bytes)
			throws CharacterCodingException {
		ByteBuffer buffer = ByteBuffer.wrap(bytes);
		return Charset.forName("UTF-8").newDecoder().decode(buffer).toString();

	}

	public static void main(String[] args) throws CharacterCodingException {
		String s = "user1";
		SecureRandom random = new SecureRandom();
		String s1 = new BigInteger(130, random).toString(32);
		String h1 = toHex(s1.getBytes());
		// String h1 = "fe540048702e0da5503b3f976125ebf8bf1c8f39c8011d19";
		byte[] h2 = fromHex(h1);
		System.out.println(h1);
		System.out.println(h2);
		System.out.println(bytesToStringUTFNIO(h2));
	}

	public static byte[] fromHex(String hex) {
		byte[] binary = new byte[hex.length() / 2];
		for (int i = 0; i < binary.length; i++) {
			binary[i] = (byte) Integer.parseInt(
					hex.substring(2 * i, (2 * i) + 2), 16);
		}
		return binary;
	}

	public static String toHex(byte[] array) {
		BigInteger bi = new BigInteger(1, array);
		String hex = bi.toString(16);
		int paddingLength = (array.length * 2) - hex.length();
		if (paddingLength > 0) {
			return String.format("%0" + paddingLength + "d", 0) + hex;
		} else {
			return hex;
		}
	}

	public static String convertToHex(byte[] byteData) {
		StringBuilder finalHex = new StringBuilder();
		for (int i = 0; i < byteData.length; i++) {
			String hex = Integer.toHexString(0xff & byteData[i]);
			if (hex.length() == 1) {
				finalHex.append('0');
			}
			finalHex.append(hex);
		}

		return finalHex.toString();
	}
}
