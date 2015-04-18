package com.cf.filter;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.cf.util.TokenBucket;
import com.cf.util.TokenBuckets;

public class RateLimitFilter extends BaseFilter implements Filter {

	/* SYSTEM PARAMS */
	private static final String SYSPARAM_CACHE_CAPACITY = "ratelimit.cache.capacity";
	private static final String SYSPARAM_CACHE_LOADFACTOR = "ratelimit.cache.loadfactor";
	private static final String SYSPARAM_CACHE_THREADS = "ratelimit.cache.threads";

	/* DEFAULTS */
	private static final int DEFAULT_CACHE_CAPACITY = 1000;
	private static final int DEFAULT_CACHE_THREADS = 5;
	private static final float DEFAULT_CACHE_LOADFACTOR = 0.75f;
	private static final long DEFAULT_RATE_USER = 20;
	private static final long DEFAULT_RATE_TOTAL = 400;
	private static final int DEFAULT_CLEANER_PERIOD = 60;
	private static final int DEFAULT_CLEANER_DELAY = 5;

	/* PARAMS */
	private static final String PARAM_RATE_USER = "rate-user";
	private static final String PARAM_RATE_TOTAL = "rate-total";
	private static final String PARAM_CLEANER_PERIOD = "cleaner-period";
	private static final String PARAM_CLEANER_DELAY = "cleaner-delay";

	/* MEMBERS */
	private int cacheCapacity = DEFAULT_CACHE_CAPACITY;
	private int cacheThreads = DEFAULT_CACHE_THREADS;
	private float cacheLoadFactor = DEFAULT_CACHE_LOADFACTOR;
	private Long rateUser = DEFAULT_RATE_USER;
	private Long rateTotal = DEFAULT_RATE_TOTAL;
	private Integer cleanerPeriod = DEFAULT_CLEANER_PERIOD;
	private Integer cleanerDelay = DEFAULT_CLEANER_DELAY;
	private TokenBucket totalBucket;
	private Map<String, TokenBucket> mapLimits;
	private ScheduledExecutorService executor;

	@Override
	public void destroy() {
		mapLimits.clear();
		mapLimits = null;
		executor.shutdownNow();
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		// check total number of requests..
		if (totalBucket.consume()) {

			// Get the IP address of client machine.
			String ipAddress = request.getRemoteAddr();

			System.out.println("rate limiting " + ipAddress);
			if (log.isTraceEnabled()) {
				log.trace("rate limiting {}", ipAddress);
			}

			// TODO -> map IP with rate limiting leaky bucket (not token bucket)
			if (!mapLimits.containsKey(ipAddress)) {
				mapLimits.put(ipAddress,
						TokenBuckets.newFixedIntervalRefill(rateUser));
			}

			// this is wrong... not leaky..no control of elements in bucket...IP
			// will remain for ever in map..
			TokenBucket bucket = mapLimits.get(ipAddress);
			if (bucket.consume()) {
				if (log.isTraceEnabled()) {
					log.trace("IP acquired token");
				}
				chain.doFilter(request, response);

			} else {
				sentUnavailableResponse(response);
			}
		} else {
			sentUnavailableResponse(response);
		}
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		if (log.isTraceEnabled()) {
			log.trace("init::RateLimitFilter");
		}

		// load init parameters
		loadInitParams(config);

		// init total bucket
		totalBucket = TokenBuckets.newFixedIntervalRefill(rateTotal);

		// init user cache
		initCache();

		// quick fix for infinite IP map
		startPeriodicCleaner();
	}

	private void initCache() {
		// init cache params from sys properties or use default values
		initCacheParams();

		// init user cache
		mapLimits = new ConcurrentHashMap<>(cacheCapacity, cacheLoadFactor,
				cacheThreads);
	}

	private void initCacheParams() {
		initCacheCapacity();

		initCacheThreads();

		initCacheLoadFactor();
	}

	private void initCacheLoadFactor() {
		String loadFactorSys = System.getProperty(SYSPARAM_CACHE_LOADFACTOR);
		if (loadFactorSys != null) {
			try {
				cacheLoadFactor = Float.valueOf(loadFactorSys);
			} catch (NumberFormatException e) {
				log.warn(
						"Load factor system property has invalid value: {}! Setting default value: {}",
						loadFactorSys, DEFAULT_CACHE_LOADFACTOR);
				cacheLoadFactor = DEFAULT_CACHE_LOADFACTOR;
			}
		}
	}

	private void initCacheThreads() {
		cacheThreads = Integer.getInteger(SYSPARAM_CACHE_THREADS,
				DEFAULT_CACHE_THREADS);
	}

	private void initCacheCapacity() {
		cacheCapacity = Integer.getInteger(SYSPARAM_CACHE_CAPACITY,
				DEFAULT_CACHE_CAPACITY);
	}

	private void loadInitParams(FilterConfig config) {
		// load rate user
		loadRateParam(config);

		// load rate total
		loadRateTotalParam(config);

		// load cleaner-delay
		loadCleanerDelayParam(config);

		// load cleaner-period
		loadCleanerPeriodParam(config);
	}

	private void loadRateParam(FilterConfig config) {
		String rateParam = config.getInitParameter(PARAM_RATE_USER);
		if (rateParam != null) {
			try {
				rateUser = Long.valueOf(rateParam);
			} catch (NumberFormatException e) {
				log.warn("problem reading filter param 'rate-user':{}",
						rateParam);
			}
		}

		if (log.isDebugEnabled()) {
			log.debug("loaded param rate: {}", rateUser);
		}
	}

	private void loadRateTotalParam(FilterConfig config) {
		rateTotal = loadLongParam(PARAM_RATE_TOTAL, DEFAULT_RATE_TOTAL, config);
	}

	private void loadCleanerPeriodParam(FilterConfig config) {
		String param = config.getInitParameter(PARAM_CLEANER_PERIOD);
		if (param != null) {
			try {
				cleanerPeriod = Integer.valueOf(param);
			} catch (NumberFormatException e) {
				log.warn("problem reading filter param 'cleaner-period':{}",
						param);
				cleanerPeriod = DEFAULT_CLEANER_PERIOD;
			}
		}

		if (log.isDebugEnabled()) {
			log.debug("loaded param cleaner-period: {}", cleanerPeriod);
		}
	}

	private void loadCleanerDelayParam(FilterConfig config) {
		String param = config.getInitParameter(PARAM_CLEANER_DELAY);
		if (param != null) {
			try {
				cleanerDelay = Integer.valueOf(param);
			} catch (NumberFormatException e) {
				log.warn("problem reading filter param 'cleaner-delay':{}",
						param);
				cleanerDelay = DEFAULT_CLEANER_DELAY;
			}
		}

		if (log.isDebugEnabled()) {
			log.debug("loaded param cleaner-delay: {}", cleanerDelay);
		}
	}

	private void startPeriodicCleaner() {
		executor = Executors.newScheduledThreadPool(1);
		executor.scheduleAtFixedRate(new CleanerThread(), cleanerDelay,
				cleanerPeriod, TimeUnit.MINUTES);

	}

	private class CleanerThread implements Runnable {

		private Set<String> blacklist = new HashSet<>();

		@Override
		public void run() {
			if (log.isDebugEnabled()) {
				log.debug("cleaning IP limiters");
			}
			for (String ip : mapLimits.keySet()) {
				// other iteration delete it
				if (blacklist.contains(ip)) {
					if (log.isTraceEnabled()) {
						log.trace("removing IP limiter:{}", ip);
					}
					blacklist.remove(ip);
				} else {
					if (log.isTraceEnabled()) {
						log.trace("setting IP in blacklist: {}", ip);
					}
					// every 60mins add IP to blacklist
					blacklist.add(ip);
				}
			}
		}

	}

}
