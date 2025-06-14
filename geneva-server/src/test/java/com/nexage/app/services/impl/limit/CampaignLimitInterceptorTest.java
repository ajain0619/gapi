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
class CampaignLimitInterceptorTest {

  @Mock private SellerLimitService sellerLimitService;

  @InjectMocks CampaignLimitInterceptor campaignLimitInterceptor;

  @Test
  void shouldThrowExceptionWhenCreateCampaignLimitReached() {
    var jointPoint = mock(JoinPoint.class);
    when(sellerLimitService.isLimitEnabled(anyLong())).thenReturn(true);
    when(sellerLimitService.canCreateCampaigns(anyLong())).thenReturn(false);

    var exception =
        assertThrows(
            GenevaValidationException.class,
            () -> campaignLimitInterceptor.canCreateCampaigns(jointPoint, 1L));
    assertEquals(ServerErrorCodes.SERVER_LIMIT_REACHED, exception.getErrorCode());
  }
}
