package com.currencytrade.util;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Simplified:
 * http://crackingjavainterviews.blogspot.com/2013/06/what-do-you-understand-by-
 * token-bucket.html
 */
public class TokenBucket {
	private final RefillStrategy refillStrategy;
	private AtomicLong size;

	public TokenBucket(RefillStrategy refillStrategy) {
		this.refillStrategy = refillStrategy;
		size = new AtomicLong(0L);
	}

	public boolean consume() {

		long newTokens = Math.max(0, refillStrategy.refill());

		long existingSize = size.get();
		long newSize = existingSize + newTokens;
		if (--newSize > 0) {
			if (size.compareAndSet(existingSize, newSize)) {
				return true;
			}
		}
		return false;

	}

	@Override
	public String toString() {
		return "Size : " + size;
	}

	public interface RefillStrategy {
		long refill();

		long getIntervalInMillis();
	}
}
