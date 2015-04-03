package com.cf.domain;

import java.util.HashMap;
import java.util.Map;

import com.cf.security.exception.CryptoConfigMissingException;
import com.cf.security.exception.EncryptException;
import com.cf.security.impl.ShaCrypt;
import com.cf.security.impl.ShaPassEncoder;

/**
 * Mock
 */
public enum UserDao {
	INSTANCE;

	private static final String USER_1 = "user1";
	private static final String USER_2 = "user2";

	private Map<String, User> contentProvider = new HashMap<String, User>();

	private UserDao() {

		try {
			String config = ShaCrypt.generateConfig();
			User user = new UserData(USER_1, ShaPassEncoder.getInstance()
					.encodePass(USER_1, config), config);
			contentProvider.put(USER_1, user);
			config = ShaCrypt.generateConfig();
			user = new UserData(USER_2, ShaPassEncoder.getInstance()
					.encodePass(USER_2, config), config);
			contentProvider.put(USER_2, user);
		} catch (EncryptException | CryptoConfigMissingException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage(), e);
		}

	}

	public Map<String, User> getModel() {
		return contentProvider;
	}
}
