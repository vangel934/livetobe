package com.currencytrade.security;

import java.util.Map;
import java.util.WeakHashMap;

import com.currencytrade.domain.User;
import com.currencytrade.security.impl.ShaCrypt;

public class TicketManager {

	// should be configurable
	private static final long REAP_TIMEOUT = 10 * 60 * 1000;
	private static final long REAP_INTERVAL = 1 * 60 * 1000;

	private static final TicketManager INSTANCE = new TicketManager();

	public static TicketManager getInstance() {
		return INSTANCE;
	}

	private Map<String, SecurityContext> userTickets = new WeakHashMap<>();

	private TicketManager() {
	}

	public static String generateTicket() {
		return ShaCrypt.generateUUID(30);
	}

	public void registerTicket(String ticket, SecurityContext context) {
		userTickets.put(ticket, context);
	}

	public boolean isTicketValid(String ticket) {
		// ugly
		return userTickets.containsKey(ticket);
	}

	public User getUser(String ticket) {
		SecurityContext context = userTickets.get(ticket);
		if (context != null) {
			return context.getUserDetails();
		}
		return null;
	}

	private void startReapAndCull() {
		// TODO - reap & cull. Reap on Timeout. Cull in next iteration.
	}

	private void isReapAndCullRunning() {
		// TODO isReapAndCullRunning
	}
}
