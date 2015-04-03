package com.currencytrade.security.impl;

import java.util.Map;

import com.currencytrade.domain.User;
import com.currencytrade.domain.UserDao;
import com.currencytrade.security.UserDetailService;

public class DaoUserDetailService implements
com.currencytrade.security.UserDetailService {

	private static final UserDetailService INSTANCE = new DaoUserDetailService();

	public static UserDetailService getInstance() {
		return INSTANCE;
	}

	private DaoUserDetailService() {
	}

	private Map<String, User> daoData = UserDao.INSTANCE.getModel();

	@Override
	public User loadUser(String user) {
		return daoData.get(user);
	}

}
