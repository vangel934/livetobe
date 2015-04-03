package com.cf.security;

import java.util.Date;

import com.cf.domain.User;
import com.cf.security.exception.SecurityException;

public class AuthenticationProvider implements Authentication {

	// KISS
	private UserDetailService userService;
	private PassEncoder passEncoder;
	private TicketManager ticketManager;

	public AuthenticationProvider(UserDetailService userService,
			PassEncoder passEncoder, TicketManager ticketManager) {
		this.userService = userService;
		this.passEncoder = passEncoder;
		this.ticketManager = ticketManager;
	}

	@Override
	public SecurityContext authenticate(String user, String pass)
			throws SecurityException {
		if ((user == null) || (pass == null)) {
			throw new SecurityException("authenticate.userpass.missing");
		}

		User loadedUser = userService.loadUser(user);
		if (loadedUser == null) {
			return null;
		}

		SecurityContext context = null;
		if (passEncoder.isPassValid(pass, loadedUser.getPass(),
				loadedUser.getCryptConfig())) {
			// generate new ticket
			String ticket = TicketManager.generateTicket();

			// create security context
			context = new SecurityContext(loadedUser, new Date(), ticket);

			// register ticket
			ticketManager.registerTicket(ticket, context);
		}

		return context;
	}

}
