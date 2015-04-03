package com.currencytrade.security;

import com.currencytrade.security.exception.SecurityException;

public interface Authentication {

	public SecurityContext authenticate(String user, String pass)
			throws SecurityException;
}
