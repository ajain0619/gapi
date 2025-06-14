package com.ssp.geneva.common.security.filter;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

@ExtendWith(MockitoExtension.class)
class FilterChainExceptionHandlerFilterTest {

  @Mock private HandlerExceptionResolver handlerExceptionResolver;

  @InjectMocks private FilterChainExceptionHandlerFilter filterChainExceptionHandlerFilter;

  @Test
  void shouldPassFilterInternalSuccessfully() throws ServletException, IOException {
    // given
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    FilterChain filterChain = mock(FilterChain.class);
    // when
    filterChainExceptionHandlerFilter.doFilterInternal(request, response, filterChain);
    // then
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void shouldHandleExceptionInFilterChain() throws ServletException, IOException {
    // given
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    FilterChain filterChain = mock(FilterChain.class);
    RuntimeException ex = new RuntimeException("Dummy error");
    ModelAndView modalAndView = mock(ModelAndView.class);

    doThrow(ex).when(filterChain).doFilter(request, response);
    when(handlerExceptionResolver.resolveException(request, response, null, ex))
        .thenReturn(modalAndView);

    // when
    filterChainExceptionHandlerFilter.doFilterInternal(request, response, filterChain);
    // then
    verify(filterChain).doFilter(request, response);
    verify(handlerExceptionResolver).resolveException(request, response, null, ex);
  }
}
