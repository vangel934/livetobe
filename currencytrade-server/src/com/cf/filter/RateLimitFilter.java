package com.cf.filter;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cf.util.TokenBucket;
import com.cf.util.TokenBuckets;

public class RateLimitFilter implements Filter {

	//
	private static final int DEFAULT_CLEANER_PERIOD = 60;
	private static final int DEFAULT_CLEANER_DELAY = 5;
	private static final String PARAM_RATE = "rate";
	private static final String PARAM_CLEANER_PERIOD = "cleaner-period";
	private static final String PARAM_CLEANER_DELAY = "cleaner-delay";

	private Long rate = null;
	private Integer cleanerPeriod = DEFAULT_CLEANER_PERIOD;
	private Integer cleanerDelay = DEFAULT_CLEANER_DELAY;
	private Logger log = LoggerFactory.getLogger(getClass());
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

		// Get the IP address of client machine.
		String ipAddress = request.getRemoteAddr();

		System.out.println("rate limiting " + ipAddress);
		if (log.isTraceEnabled()) {
			log.trace("rate limiting {}", ipAddress);
		}

		// TODO -> map IP with rate limiting leaky bucket (not token bucket) ->
		// when it gets full, remove IP mapping from map. map must be weak
		// reference
		if (!mapLimits.containsKey(ipAddress)) {
			mapLimits.put(ipAddress, TokenBuckets.newFixedIntervalRefill(rate));
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
			if (log.isTraceEnabled()) {
				log.trace("IP exceeded token limit");
			}
			((HttpServletResponse) response)
			.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
			((HttpServletResponse) response)
			.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
		}

	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		if (log.isTraceEnabled()) {
			log.trace("init::RateLimitFilter");
		}
		mapLimits = new WeakHashMap<>();

		// load rate
		loadRateParam(config);

		// load cleaner-delay
		loadCleanerDelayParam(config);

		// load cleaner-period
		loadCleanerPeriodParam(config);

		// quick fix for infinite IP map
		startPeriodicCleaner();
	}

	private void loadRateParam(FilterConfig config) {
		String rateParam = config.getInitParameter(PARAM_RATE);
		if (rateParam != null) {
			try {
				rate = Long.valueOf(rateParam);
			} catch (NumberFormatException e) {
				log.warn("problem reading filter param 'rate':{}", rateParam);
			}
		}

		if (log.isDebugEnabled()) {
			log.debug("loaded param rate: {}", rate);
		}
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
