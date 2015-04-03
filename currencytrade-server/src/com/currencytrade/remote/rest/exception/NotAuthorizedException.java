package com.currencytrade.remote.rest.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * 401 Unauthorized
 */
public class NotAuthorizedException extends WebApplicationException {

	private static final long serialVersionUID = 8574145260300913731L;
	private static final Status STATUS = Status.UNAUTHORIZED;

	public NotAuthorizedException() {
		super(Response.status(STATUS).build());
	}

	public NotAuthorizedException(String msg) {
		super(Response.status(STATUS).entity(msg).type(MediaType.TEXT_PLAIN)
				.build());
	}
}
