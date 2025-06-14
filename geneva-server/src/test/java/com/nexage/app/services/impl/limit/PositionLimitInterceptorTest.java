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
class PositionLimitInterceptorTest {

  @Mock private SellerLimitService sellerLimitService;

  private PositionLimitInterceptor positionLimitInterceptor;

  @BeforeEach
  void setUp() {
    positionLimitInterceptor = new PositionLimitInterceptor(sellerLimitService);
  }

  @Test
  void shouldThrowExceptionWhenCheckingCreateLimitsForPositionInSite() {
    // given
    var jointPoint = mock(JoinPoint.class);
    var publisherPid = 1L;
    var sitePid = 1L;

    when(sellerLimitService.canCreatePositionsInSite(publisherPid, sitePid)).thenReturn(false);

    // when
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () ->
                positionLimitInterceptor.canCreatePositionsInSite(
                    jointPoint, publisherPid, sitePid));

    // then
    assertEquals(ServerErrorCodes.SERVER_LIMIT_REACHED, exception.getErrorCode());
    verify(sellerLimitService).canCreatePositionsInSite(publisherPid, sitePid);
  }

  @Test
  void shouldDoNothingWhenCheckingCreateLimitsForPositionInSite() {

    // given
    var jointPoint = mock(JoinPoint.class);
    var publisherPid = 1L;
    var sitePid = 1L;

    when(sellerLimitService.canCreatePositionsInSite(publisherPid, sitePid)).thenReturn(true);

    // when
    positionLimitInterceptor.canCreatePositionsInSite(jointPoint, publisherPid, sitePid);

    // then
    verify(sellerLimitService).canCreatePositionsInSite(publisherPid, sitePid);
  }
}
