package com.currencytrade.remote.rest;

import java.util.List;

import javax.ws.rs.core.Response;

import com.currencytrade.model.Fx;

public interface FxService {

	public Response fx(String user, String pass, Fx fx);

	public List<Fx> fxs();

	public Response test();
}
