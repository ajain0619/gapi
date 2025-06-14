package com.ssp.geneva.common.cache;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SinglePageCachingFilterNoQueryParamsTest {

  @Test
  void shouldReturnCorrectKey() {
    var request = mock(HttpServletRequest.class);
    when(request.getRequestURI()).thenReturn("/request/uri");
    when(request.getMethod()).thenReturn("GET");

    var filter = new SinglePageCachingFilterNoQueryParams();

    var out = filter.calculateKey(request);
    Assertions.assertEquals("GET/request/uri", out);
  }
}
