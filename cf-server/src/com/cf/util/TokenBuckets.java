package com.cf.util;

import java.util.concurrent.TimeUnit;

/**
 * http://crackingjavainterviews.blogspot.com/2013/06/what-do-you-understand-by-
 * token-bucket.html
 */
public final class TokenBuckets {

	private static final String PARAM_RATE = "limit.rate";
	private static final long DEFAULT_RATE = 10;
	private static final long DEFAULT_PERIOD = 1 * 1000; // 1 second

	private TokenBuckets() {
	}

	private static long getRefillRate(Long refillRate) {
		if (refillRate == null) {
			refillRate = Long.valueOf(System.getProperty(PARAM_RATE,
					String.valueOf(DEFAULT_RATE)));
		}
		return refillRate;
	}

	// private static long getCapacity(Long capacityTokens) {
	// if (capacityTokens == null) {
	// capacityTokens = Long.valueOf(System.getProperty(PARAM_RATE,
	// String.valueOf(DEFAULT_RATE)));
	// }
	//
	// return capacityTokens;
	// }

	public static TokenBucket newFixedIntervalRefill(Long refillTokens) {
		TokenBucket.RefillStrategy strategy = new FixedIntervalRefillStrategy(
				getRefillRate(refillTokens), DEFAULT_PERIOD,
				TimeUnit.MILLISECONDS);
		return new TokenBucket(strategy);
	}

}
