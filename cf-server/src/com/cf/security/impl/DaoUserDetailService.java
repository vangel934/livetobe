package com.cf.security.impl;

import java.util.Map;

import com.cf.domain.User;
import com.cf.domain.UserDao;
import com.cf.security.UserDetailService;

public class DaoUserDetailService implements
com.cf.security.UserDetailService {

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
