package com.nexage.app.services.impl.limit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.services.SellerLimitService;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreativeLimitInterceptorTest {

  @Mock private SellerLimitService sellerLimitService;

  @InjectMocks CreativeLimitInterceptor creativeLimitInterceptor;

  @Test
  void shouldThrowExceptionWhenCreateCampaignLimitReached() {
    var jointPoint = mock(JoinPoint.class);
    when(sellerLimitService.isLimitEnabled(anyLong())).thenReturn(true);
    when(sellerLimitService.canCreateCreativesInCampaign(anyLong(), anyLong())).thenReturn(false);

    var exception =
        assertThrows(
            GenevaValidationException.class,
            () -> creativeLimitInterceptor.canCreateCreativesInCampaign(jointPoint, 1L, 1L));
    assertEquals(
        ServerErrorCodes.SERVER_CREATIVES_PER_CAMPAIGN_LIMIT_REACHED, exception.getErrorCode());
  }
}
