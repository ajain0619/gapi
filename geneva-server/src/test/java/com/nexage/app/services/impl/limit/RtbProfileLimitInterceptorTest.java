package com.nexage.app.services.impl.limit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.nexage.app.dto.publisher.PublisherRTBProfileGroupDTO;
import com.nexage.app.dto.publisher.PublisherRTBProfileGroupDTO.ItemType;
import com.nexage.app.dto.publisher.PublisherRTBProfileLibraryDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.services.RtbProfileLibrarySellerLimitService;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.Collections;
import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RtbProfileLimitInterceptorTest {

  @Mock private RtbProfileLibrarySellerLimitService rtbProfileLibrarySellerLimitService;

  private RtbProfileLimitInterceptor rtbProfileLimitInterceptor;

  @BeforeEach
  void setUp() {
    rtbProfileLimitInterceptor =
        new RtbProfileLimitInterceptor(rtbProfileLibrarySellerLimitService);
  }

  @ParameterizedTest
  @EnumSource(
      value = ItemType.class,
      names = {"BIDDER"})
  void shouldThrowGenevaValidationExceptionWhenCreateRTBProfileGroupAndCheckLimits(
      ItemType itemType) {

    // given
    var jointPoint = mock(JoinPoint.class);
    var publisherPid = 1L;

    PublisherRTBProfileGroupDTO group = getGroup(itemType);

    when(rtbProfileLibrarySellerLimitService.canCreateBidderGroups(publisherPid)).thenReturn(false);

    // when
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> rtbProfileLimitInterceptor.checkLimitsGroup(jointPoint, publisherPid, group));

    // then
    assertEquals(ServerErrorCodes.SERVER_LIMIT_REACHED, exception.getErrorCode());
  }

  @ParameterizedTest
  @EnumSource(
      value = ItemType.class,
      names = {"CATEGORY", "ADOMAIN"})
  void shouldThrowGenevaValidationExceptionWhenCreateRTBProfileLibraryAndCheckLimits(
      ItemType itemType) {

    // given
    var jointPoint = mock(JoinPoint.class);
    var publisherPid = 1L;

    PublisherRTBProfileLibraryDTO library = new PublisherRTBProfileLibraryDTO();
    PublisherRTBProfileGroupDTO group = getGroup(itemType);
    library.setGroups(Collections.singleton(group));

    when(rtbProfileLibrarySellerLimitService.canCreateBlockGroups(publisherPid)).thenReturn(false);

    // when
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> rtbProfileLimitInterceptor.checkLimitsLibrary(jointPoint, publisherPid, library));

    // then
    assertEquals(ServerErrorCodes.SERVER_LIMIT_REACHED, exception.getErrorCode());
  }

  private static PublisherRTBProfileGroupDTO getGroup(ItemType itemType) {
    return new PublisherRTBProfileGroupDTO(
        0L, "name", 0, "privilegeLevel", "data", itemType.asInt(), 0, 100L, false);
  }
}
