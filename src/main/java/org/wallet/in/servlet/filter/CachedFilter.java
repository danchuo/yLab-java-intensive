package org.wallet.in.servlet.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

/**
 * A filter that caches the body of HTTP requests, enabling multiple reads of the request body. It
 * wraps the original `HttpServletRequest` with a `CachedBodyHttpServletRequest`.
 */
@WebFilter("/*")
public class CachedFilter implements Filter {
  /**
   * Filters the request, wrapping the original request with a `CachedBodyHttpServletRequest`.
   *
   * @param request the original `ServletRequest`
   * @param response the `ServletResponse`
   * @param chain the filter chain for request processing
   * @throws IOException if an I/O error occurs during request processing
   * @throws ServletException if a servlet exception occurs during request processing
   */
  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    CachedBodyHttpServletRequest cachedBodyHttpServletRequest =
        new CachedBodyHttpServletRequest((HttpServletRequest) request);
    chain.doFilter(cachedBodyHttpServletRequest, response);
  }
}
