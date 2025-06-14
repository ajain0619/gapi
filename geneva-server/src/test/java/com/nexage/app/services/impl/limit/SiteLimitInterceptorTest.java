package com.nexage.app.services.impl.limit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.services.SellerLimitService;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SiteLimitInterceptorTest {

  @Mock private SellerLimitService sellerLimitService;

  private SiteLimitInterceptor siteLimitInterceptor;

  @BeforeEach
  void setUp() {
    siteLimitInterceptor = new SiteLimitInterceptor(sellerLimitService);
  }

  @Test
  void shouldThrowExceptionWhenCheckingCreateLimitsForSites() {
    // given
    var jointPoint = mock(JoinPoint.class);
    var publisherPid = 1L;

    when(sellerLimitService.canCreateSites(publisherPid)).thenReturn(false);

    // when
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> siteLimitInterceptor.canCreateSites(jointPoint, publisherPid));

    // then
    assertEquals(ServerErrorCodes.SERVER_LIMIT_REACHED, exception.getErrorCode());
    verify(sellerLimitService).canCreateSites(publisherPid);
  }

  @Test
  void shouldDoNothingWhenCheckingCreateLimitsForSites() {

    // given
    var jointPoint = mock(JoinPoint.class);
    var publisherPid = 1L;

    when(sellerLimitService.canCreateSites(publisherPid)).thenReturn(true);

    // when
    siteLimitInterceptor.canCreateSites(jointPoint, publisherPid);

    // then
    verify(sellerLimitService).canCreateSites(publisherPid);
  }
}
