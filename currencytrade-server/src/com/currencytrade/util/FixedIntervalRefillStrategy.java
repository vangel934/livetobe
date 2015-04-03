package com.currencytrade.util;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * http://crackingjavainterviews.blogspot.com/2013/06/what-do-you-understand-by-
 * token-bucket.html
 */
public class FixedIntervalRefillStrategy implements TokenBucket.RefillStrategy {
	private final long numTokens;
	private final long intervalInMillis;
	private AtomicLong nextRefillTime;

	public FixedIntervalRefillStrategy(long numTokens, long interval,
			TimeUnit unit) {
		this.numTokens = numTokens;
		intervalInMillis = unit.toMillis(interval);
		nextRefillTime = new AtomicLong(-1L);
	}

	@Override
	public long refill() {
		final long now = System.currentTimeMillis();
		final long refillTime = nextRefillTime.get();
		if (now < refillTime) {
			return 0;
		}

		return nextRefillTime.compareAndSet(refillTime, now + intervalInMillis) ? numTokens
				: 0;
	}

	@Override
	public long getIntervalInMillis() {
		return intervalInMillis;
	}

}
