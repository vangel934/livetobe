package com.currencytrade.remote.rest.client;

import javax.ws.rs.core.Response;

import com.currencytrade.model.Fx;

public class DefaultFxRestClient extends RestClient implements FxRestClient {

	public DefaultFxRestClient() {
		super(DefaultFxRestClient.class);
	}

	@Override
	public Response fx(String user, String pass, Fx fx) {
		return executeMethod("fx", user, pass, null, null, fx, Response.class);
	}

}
