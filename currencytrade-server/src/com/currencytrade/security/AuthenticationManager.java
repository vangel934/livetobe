package com.currencytrade.security;

import java.util.ArrayList;
import java.util.List;

import com.currencytrade.security.exception.BadCredentialsException;

public class AuthenticationManager implements Authentication {
	private List<Authentication> providers;

	public AuthenticationManager(List<Authentication> providers) {
		super();
		this.providers = providers;
	}

	public AuthenticationManager(Builder builder) {
		providers = builder.providers;
	}

	public List<Authentication> getProviders() {
		return providers;
	}

	@Override
	public SecurityContext authenticate(String user, String pass)
			throws com.currencytrade.security.exception.SecurityException,
			BadCredentialsException {
		if ((providers == null) || (providers.size() == 0)) {
			throw new SecurityException("authenticate.providers.missing");
		}
		SecurityContext context = null;

		for (Authentication auth : providers) {
			if (context != null) {
				break;
			}
			context = auth.authenticate(user, pass);
		}

		if (context == null) {
			throw new BadCredentialsException(user,
					"authenticate.badcredentials");
		}

		return context;

	}

	public static class Builder {
		private List<Authentication> providers = new ArrayList<>();

		public Builder() {

		}

		public Builder provider(Authentication provider) {
			providers.add(provider);
			return this;
		}

		public Builder provider(UserDetailService userDetailsService,
				PassEncoder encoder, TicketManager ticketManager) {
			providers.add(new AuthenticationProvider(userDetailsService,
					encoder, ticketManager));
			return this;
		}

		public Builder providers(List<Authentication> providers) {
			this.providers = providers;
			return this;
		}

		public AuthenticationManager build() {
			return new AuthenticationManager(this);
		}
	}
}
