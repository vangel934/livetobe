package com.cf.security;

import com.cf.domain.User;

public interface UserDetailService {

	public User loadUser(String user);
}
