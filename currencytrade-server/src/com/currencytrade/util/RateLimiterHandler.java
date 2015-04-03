package com.currencytrade.util;

import com.google.common.util.concurrent.RateLimiter;

public class RateLimiterHandler {
	private static final Object LOCK = new Object();
	private static final String PARAM_RATE = "limit.rate";
	private static final String PARAM_TIMEOUT = "limit.acquire.timeout";
	private static final double DEFAULT_RATE = 10;
	private Double rate;
	private Double timeout;
	private volatile RateLimiter rateLimiter;

	public RateLimiterHandler() {
	}

	public RateLimiterHandler(Double rate, Double timeout) {
		super();
		setRate(rate);
		setTimeout(timeout);
	}

	private RateLimiter getRateLimiter() {
		if (rateLimiter == null) {
			synchronized (LOCK) {
				if (rateLimiter == null) {
					rateLimiter = RateLimiter.create(getRate());
				}
			}
		}
		return rateLimiter;
	}

	public boolean acquire() {
		return getRateLimiter().tryAcquire();
	}

	public double getRate() {
		if (rate == null) {
			rate = Double.valueOf(System.getProperty(PARAM_RATE,
					String.valueOf(DEFAULT_RATE)));
		}
		return rate;
	}

	public void setRate(Double rate) {
		this.rate = rate;
	}

	public double getTimeout() {
		if (timeout == null) {
			timeout = Double.valueOf(System.getProperty(PARAM_TIMEOUT,
					String.valueOf(1000 / getRate())));
		}
		return timeout;
	}

	public void setTimeout(Double timeout) {
		this.timeout = timeout;
	}

}
