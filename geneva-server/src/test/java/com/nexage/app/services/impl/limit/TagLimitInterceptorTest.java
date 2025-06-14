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
class TagLimitInterceptorTest {

  @Mock private SellerLimitService sellerLimitService;

  private TagLimitInterceptor tagLimitInterceptor;

  @BeforeEach
  void setUp() {
    tagLimitInterceptor = new TagLimitInterceptor(sellerLimitService);
  }

  @Test
  void shouldThrowExceptionWhenCheckingCreateLimitsForTagsInPosition() {

    // given
    var jointPoint = mock(JoinPoint.class);
    var publisherPid = 1L;
    var sitePid = 1L;
    var positionPid = 1L;

    when(sellerLimitService.canCreateTagsInPosition(publisherPid, sitePid, positionPid))
        .thenReturn(false);

    // when
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () ->
                tagLimitInterceptor.canCreateTagsInPosition(
                    jointPoint, publisherPid, sitePid, positionPid));

    // then
    assertEquals(ServerErrorCodes.SERVER_TAGS_PER_POSITION_LIMIT_REACHED, exception.getErrorCode());
    verify(sellerLimitService).canCreateTagsInPosition(publisherPid, sitePid, positionPid);
  }

  @Test
  void shouldDoNothingWhenCheckingCreateLimitsForTagsInPosition() {

    // given
    var jointPoint = mock(JoinPoint.class);
    var publisherPid = 1L;
    var sitePid = 1L;
    var positionPid = 1L;

    when(sellerLimitService.canCreateTagsInPosition(publisherPid, sitePid, positionPid))
        .thenReturn(true);

    // when
    tagLimitInterceptor.canCreateTagsInPosition(jointPoint, publisherPid, sitePid, positionPid);

    // then
    verify(sellerLimitService).canCreateTagsInPosition(publisherPid, sitePid, positionPid);
  }
}
