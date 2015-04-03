package com.currencytrade.security;

import java.util.Date;

import com.currencytrade.domain.User;

public class SecurityContext {
	private User userDetails;
	private Date created;
	private String ticket;

	public SecurityContext(User userDetails, Date created, String ticket) {
		super();
		this.userDetails = userDetails;
		this.created = created;
		this.ticket = ticket;
	}

	// TODO reap and cull!
	// TODO ticket management... bored right now

	public User getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(User userDetails) {
		this.userDetails = userDetails;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public String getTicket() {
		return ticket;
	}

	public void setTicket(String ticket) {
		this.ticket = ticket;
	}
}
