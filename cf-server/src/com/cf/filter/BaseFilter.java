package com.cf.filter;

import java.io.IOException;

import javax.servlet.FilterConfig;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseFilter {
	protected Logger log = LoggerFactory.getLogger(getClass());

	protected void sentUnavailableResponse(ServletResponse response)
			throws IOException {
		if (log.isTraceEnabled()) {
			log.trace("IP exceeded token limit");
		}
		((HttpServletResponse) response)
		.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
		((HttpServletResponse) response)
		.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
	}

	protected Long loadLongParam(String paramName, Long defaultValue,
			FilterConfig config) {
		String param = config.getInitParameter(paramName);
		Long paramValue = null;
		if (param != null) {
			try {
				paramValue = Long.valueOf(param);
			} catch (NumberFormatException e) {
				log.warn(
						"problem reading filter param '{}'! Setting default value:{}",
						paramName, defaultValue);
				paramValue = defaultValue;
			}
		}

		if (log.isDebugEnabled()) {
			log.debug("loaded param rate: {}", paramValue);
		}

		return paramValue;
	}

	protected Integer loadIntParam(String paramName, Integer defaultValue,
			FilterConfig config) {
		String param = config.getInitParameter(paramName);
		Integer paramValue = null;
		if (param != null) {
			try {
				paramValue = Integer.valueOf(param);
			} catch (NumberFormatException e) {
				log.warn(
						"problem reading filter param '{}'! Setting default value:{}",
						paramName, defaultValue);
				paramValue = defaultValue;
			}
		}

		if (log.isDebugEnabled()) {
			log.debug("loaded param rate: {}", paramValue);
		}

		return paramValue;
	}

	protected String loadStringParam(String paramName, String defaultValue,
			FilterConfig config) {
		String param = config.getInitParameter(paramName);
		if (param == null) {
			log.warn(
					"problem reading filter param '{}'! Setting default value:{}",
					paramName, defaultValue);
			param = defaultValue;
		}

		if (log.isDebugEnabled()) {
			log.debug("loaded param rate: {}", param);
		}

		return param;
	}
}
