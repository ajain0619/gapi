package com.ssp.geneva.common.security.filter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

/**
 * Custom filter which manages exceptions thrown in Spring security filter chain, by simply
 * redirecting them to default Spring mechanism for handling REST exceptions (a class annotated with
 * '@RestControllerAdvice' or '@ControllerAdvice' and method(s) annotated with '@ExceptionHandler').
 * This class is hooked in Spring security filter chain.
 */
@Component
@Log4j2
public class FilterChainExceptionHandlerFilter extends OncePerRequestFilter {

  private final HandlerExceptionResolver handlerExceptionResolver;

  public FilterChainExceptionHandlerFilter(HandlerExceptionResolver handlerExceptionResolver) {
    this.handlerExceptionResolver = handlerExceptionResolver;
  }

  /** {@inheritDoc} */
  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
    try {
      filterChain.doFilter(request, response);
    } catch (Exception ex) {
      log.error("Spring Security filter chain exception: {}", ex.getMessage());
      // pass the error to Spring's exception resolver
      handlerExceptionResolver.resolveException(request, response, null, ex);
    }
  }
}
