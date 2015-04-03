package com.cf.security;

import com.cf.security.exception.SecurityException;

public interface Authentication {

	public SecurityContext authenticate(String user, String pass)
			throws SecurityException;
}
