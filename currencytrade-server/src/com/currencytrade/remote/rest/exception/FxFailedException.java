package com.currencytrade.remote.rest.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * 500 Internal Server Error
 */
public class FxFailedException extends WebApplicationException {

	private static final long serialVersionUID = -6725339919236560370L;
	private static final Status STATUS = Status.INTERNAL_SERVER_ERROR;

	public FxFailedException() {
		super(Response.status(STATUS).build());
	}

	public FxFailedException(String msg) {
		super(Response.status(STATUS).entity(msg).type(MediaType.TEXT_PLAIN)
				.build());
	}
}
