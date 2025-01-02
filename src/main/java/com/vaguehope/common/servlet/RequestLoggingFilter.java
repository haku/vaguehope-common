package com.vaguehope.common.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestLoggingFilter implements Filter {

	private static final Logger LOG = LoggerFactory.getLogger(RequestLoggingFilter.class);

	@Override
	public void init(final FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
		final HttpServletRequest req = (HttpServletRequest) request;
		final HttpServletResponse resp = (HttpServletResponse) response;

		final String remoteAddr = req.getRemoteAddr();
		final String requestURI = req.getRequestURI();
		final String method = req.getMethod();
		final String ranges = StringUtils.join(req.getHeaders("Range"), ",");

		try {
			chain.doFilter(request, response);
		}
		finally {
			if (ranges != null) {
				LOG.info("{} {} {} (r:{}) {}", resp.getStatus(), method, requestURI, ranges, remoteAddr);
			}
			else {
				LOG.info("{} {} {} {}", resp.getStatus(), method, requestURI, remoteAddr);
			}
		}
	}

	@Override
	public void destroy() {

	}

	public static void addTo(ServletContextHandler handler) {
		handler.addFilter(new FilterHolder(new RequestLoggingFilter()), "/*", null);
	}

}