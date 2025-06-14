package com.ssp.geneva.common.logging;

import static java.util.Objects.nonNull;

import java.io.IOException;
import java.util.UUID;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.apache.log4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/** Servler filter which logs request details, such as URL path, current user etc. */
@Log4j2
@Component
public class MDCFilter extends OncePerRequestFilter {

  private static final String REQUEST_ID = "Request-Id";

  @Override
  protected void doFilterInternal(
      HttpServletRequest httpServletRequest,
      HttpServletResponse httpServletResponse,
      FilterChain filterChain)
      throws ServletException, IOException {

    String requestId = httpServletRequest.getHeader(REQUEST_ID);

    if (!nonNull(requestId)) {
      requestId = UUID.randomUUID().toString();
    }

    httpServletRequest.getUserPrincipal();
    MDC.put("requestId", requestId);
    httpServletResponse.setHeader(REQUEST_ID, requestId);

    if (isUserPrincipalExisting(httpServletRequest)) {
      MDC.put("userName", httpServletRequest.getUserPrincipal().getName());
    }

    try {
      log.info(
          "Request [{}] - URI={}",
          httpServletRequest.getMethod(),
          httpServletRequest.getRequestURI());
      filterChain.doFilter(httpServletRequest, httpServletResponse);
    } finally {
      MDC.remove("requestId");
      MDC.remove("userName");
      MDC.clear();
    }
  }

  @Override
  public void destroy() {}

  private boolean isUserPrincipalExisting(final HttpServletRequest httpServletRequest) {
    return nonNull(httpServletRequest.getUserPrincipal())
        && nonNull(httpServletRequest.getUserPrincipal().getName());
  }
}
