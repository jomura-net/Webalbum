package jomora.picture;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ReferFilter implements Filter {

	private static final Log log = LogFactory.getLog(ReferFilter.class);

	public void init(FilterConfig config) throws ServletException {
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		this.doFilter((HttpServletRequest) request,
				(HttpServletResponse) response, chain);
	}

	private void doFilter(HttpServletRequest request,
			HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		String referer = request.getHeader("referer");
		String host = request.getHeader("host");
		if (referer == null || referer.indexOf(host) < 0) {
			log.warn("Illegal access from " + request.getRemoteAddr());
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
		} else {
			chain.doFilter(request, response);
		}
	}

	public void destroy() {
	}

}
