package com.cf.remote.rest.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * 503 Service Unavailable
 */
public class ExceededRateLimitException extends WebApplicationException {

	private static final long serialVersionUID = 4689505250258023521L;
	private static final Status STATUS = Status.SERVICE_UNAVAILABLE;

	public ExceededRateLimitException() {
		super(Response.status(STATUS).build());
	}

	public ExceededRateLimitException(String msg) {
		super(Response.status(STATUS).entity(msg).type(MediaType.TEXT_PLAIN)
				.build());
	}
}
