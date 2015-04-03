package com.cf.domain;

public class UserData implements User {

	private String username;
	private String encPass;
	private String cryptConfig;

	public UserData(String user, String encPass, String cryptConfig) {
		this.username = user;
		this.encPass = encPass;
		this.cryptConfig = cryptConfig;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public String getPass() {
		return encPass;
	}

	@Override
	public String getCryptConfig() {
		return cryptConfig;
	}

}
