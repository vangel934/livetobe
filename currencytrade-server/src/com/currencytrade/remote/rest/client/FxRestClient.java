package com.currencytrade.remote.rest.client;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.currencytrade.model.Fx;

public interface FxRestClient {
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("")
	public Response fx(String user, String pass, Fx fx);
}
