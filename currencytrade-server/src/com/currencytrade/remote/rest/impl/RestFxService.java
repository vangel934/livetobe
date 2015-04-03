package com.currencytrade.remote.rest.impl;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.currencytrade.model.Fx;
import com.currencytrade.processor.FxProcessor;
import com.currencytrade.processor.exception.ProcessingException;
import com.currencytrade.processor.impl.DefaultFxProcessor;
import com.currencytrade.processor.impl.ResourceHandler;
import com.currencytrade.remote.rest.exception.ExceededRateLimitException;
import com.currencytrade.remote.rest.exception.FxFailedException;
import com.currencytrade.remote.rest.exception.NotAuthorizedException;
import com.currencytrade.security.AuthenticationManager;
import com.currencytrade.security.SecurityContext;
import com.currencytrade.security.TicketManager;
import com.currencytrade.security.exception.SecurityException;
import com.currencytrade.security.impl.DaoUserDetailService;
import com.currencytrade.security.impl.ShaPassEncoder;
import com.currencytrade.util.TokenBucket;
import com.currencytrade.util.TokenBuckets;

@Path("fx-service")
public class RestFxService implements com.currencytrade.remote.rest.FxService {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	private FxProcessor processor = new DefaultFxProcessor();
	TokenBucket bucket = TokenBuckets.newFixedIntervalRefill(null);
	// private RateLimiterHandler rtHandler = new RateLimiterHandler();

	// TODO - it's ugly
	private AuthenticationManager authentication = new AuthenticationManager.Builder()
			.provider(DaoUserDetailService.getInstance(),
					ShaPassEncoder.getInstance(), TicketManager.getInstance())
			.build();

	public FxProcessor getProcessor() {
		return processor;
	}

	public void setProcessor(FxProcessor processor) {
		this.processor = processor;
	}

	public AuthenticationManager getAuthentication() {
		return authentication;
	}

	public void setAuthentication(AuthenticationManager authentication) {
		this.authentication = authentication;
	}

	@Override
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response fx(@HeaderParam("auth-user") String user,
			@HeaderParam("auth-pass") String pass, Fx fx) {

		/* check if rate limit is exceeded */
		checkRateLimit();

		/* authenticate user */
		authenticate(user, pass);

		try {
			/* process FX data */
			getProcessor().processFx(fx);
		} catch (ProcessingException e) {
			log.error(e.getMessage(), e);
			throw new FxFailedException(e.getExceptionCode());
		}

		return Response.created(null).build();
	}

	private void checkRateLimit() {
		System.out.println("checking rate limit..");
		if (!bucket.consume()) {
			// if (!rtHandler.acquire()) {
			System.out.println("rate limit exceeded");
			throw new ExceededRateLimitException();
		}
	}

	private void authenticate(String user, String pass) {
		try {
			SecurityContext context = getAuthentication().authenticate(user,
					pass);
			if (context == null) {
				throw new NotAuthorizedException("fx.rest.fault.authenticate");
			}
		} catch (SecurityException e) {
			log.error(e.getMessage(), e);
			throw new NotAuthorizedException(e.getExceptionCode());
		}
	}

	@Override
	@GET
	@Path("test")
	public Response test() {
		return Response.ok("TEST OK", MediaType.TEXT_PLAIN).build();
	}

	@Override
	@GET
	@Produces(MediaType.TEXT_XML)
	public List<Fx> fxs() {
		try {
			return ResourceHandler.INSTANCE.readFromCSV();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new FxFailedException("fx.rest.fault.readfxs");
		}
	}

}
