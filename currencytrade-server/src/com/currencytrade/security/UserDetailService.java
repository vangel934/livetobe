package com.currencytrade.security;

import com.currencytrade.domain.User;

public interface UserDetailService {

	public User loadUser(String user);
}
