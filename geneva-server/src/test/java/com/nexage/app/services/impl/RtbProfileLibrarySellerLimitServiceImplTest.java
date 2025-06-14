package com.nexage.app.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.model.SellerAttributes;
import com.nexage.admin.core.repository.SellerAttributesRepository;
import com.nexage.app.dto.publisher.PublisherRTBProfileLibraryDTO;
import com.nexage.app.services.CompanyRtbProfileLibraryService;
import com.nexage.app.services.LimitService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RtbProfileLibrarySellerLimitServiceImplTest {

  @Mock SellerAttributesRepository sellerAttributesRepository;
  @Mock CompanyRtbProfileLibraryService rtbProfileLibraryService;
  @Mock LimitService limitService;

  @InjectMocks RtbProfileLibrarySellerLimitServiceImpl rtbProfileLibrarySellerLimitService;

  @Test
  void shouldAllowAllBooleanActionsWhenLimitIsNotEnabled() {
    // given
    long sellerPid = 1L;

    // when
    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerAttributes.setLimitEnabled(false);
    when(sellerAttributesRepository.findById(sellerPid)).thenReturn(Optional.of(sellerAttributes));

    // then
    assertTrue(rtbProfileLibrarySellerLimitService.canCreateBidderGroups(sellerPid));
    assertTrue(rtbProfileLibrarySellerLimitService.canCreateBlockGroups(sellerPid));
    assertFalse(rtbProfileLibrarySellerLimitService.isLimitEnabled(sellerPid));
  }

  @Test
  void shouldHaveLimitEnabledWhenNoAttributesAreFound() {
    // given
    final var sellerPid = 1L;

    // when
    when(sellerAttributesRepository.findById(sellerPid)).thenReturn(Optional.empty());

    // then
    assertTrue(rtbProfileLibrarySellerLimitService.isLimitEnabled(sellerPid));
  }

  @Test
  void shouldHaveLimitEnabledWhenAttributesAreFoundWithLimitEnabled() {
    // given
    final var sellerPid = 1L;
    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerAttributes.setLimitEnabled(true);

    // when
    when(sellerAttributesRepository.findById(sellerPid)).thenReturn(Optional.of(sellerAttributes));

    // then
    assertTrue(rtbProfileLibrarySellerLimitService.isLimitEnabled(sellerPid));
  }

  @Test
  void shouldBeZeroForCheckBidderWhenBidderLimitIsReached() {
    // given
    final var sellerPid = 1L;
    PublisherRTBProfileLibraryDTO publisherRTBProfileLibrary = new PublisherRTBProfileLibraryDTO();
    List<PublisherRTBProfileLibraryDTO> libraries = List.of(publisherRTBProfileLibrary);
    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerAttributes.setLimitEnabled(true);
    sellerAttributes.setBidderLibrariesLimit(1);

    // when
    when(sellerAttributesRepository.findById(sellerPid)).thenReturn(Optional.of(sellerAttributes));
    when(rtbProfileLibraryService.getRTBProfileLibrariesForCompany(sellerPid, true, true, true))
        .thenReturn(libraries);

    // then
    int result = rtbProfileLibrarySellerLimitService.checkBidderLibrariesLimit(sellerPid);
    assertEquals(0, result);
  }

  @Test
  void shouldBeZeroForCheckBidderWhenGlobalBidderLimitIsReached() {
    // given
    final var sellerPid = 1L;
    PublisherRTBProfileLibraryDTO publisherRTBProfileLibrary = new PublisherRTBProfileLibraryDTO();
    List<PublisherRTBProfileLibraryDTO> libraries = List.of(publisherRTBProfileLibrary);
    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerAttributes.setLimitEnabled(false);

    // when
    when(sellerAttributesRepository.findById(sellerPid)).thenReturn(Optional.of(sellerAttributes));
    when(rtbProfileLibraryService.getRTBProfileLibrariesForCompany(sellerPid, true, true, true))
        .thenReturn(libraries);
    when(limitService.getGlobalBidderLibrariesLimit()).thenReturn(1);

    // then
    int result = rtbProfileLibrarySellerLimitService.checkBidderLibrariesLimit(sellerPid);
    assertEquals(0, result);
  }

  @Test
  void shouldBeZeroForBlockLibrariesLimitWhenBlockLibrariesLimiIsReached() {
    // given
    final var sellerPid = 1L;
    PublisherRTBProfileLibraryDTO publisherRTBProfileLibrary = new PublisherRTBProfileLibraryDTO();
    List<PublisherRTBProfileLibraryDTO> libraries = List.of(publisherRTBProfileLibrary);
    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerAttributes.setLimitEnabled(true);
    sellerAttributes.setBlockLibrariesLimit(1);

    // when
    when(sellerAttributesRepository.findById(sellerPid)).thenReturn(Optional.of(sellerAttributes));
    when(rtbProfileLibraryService.getRTBProfileLibrariesForCompany(sellerPid, false, true, true))
        .thenReturn(libraries);

    // then
    int result = rtbProfileLibrarySellerLimitService.checkBlockLibrariesLimit(sellerPid);
    assertEquals(0, result);
  }

  @Test
  void shouldBeZeroForBlockLibrariesLimitWhenGlobalBlockLibrariesLimitIsReached() {
    // given
    final var sellerPid = 1L;
    PublisherRTBProfileLibraryDTO publisherRTBProfileLibrary = new PublisherRTBProfileLibraryDTO();
    List<PublisherRTBProfileLibraryDTO> libraries = List.of(publisherRTBProfileLibrary);
    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerAttributes.setLimitEnabled(false);

    // when
    when(sellerAttributesRepository.findById(sellerPid)).thenReturn(Optional.of(sellerAttributes));
    when(rtbProfileLibraryService.getRTBProfileLibrariesForCompany(sellerPid, false, true, true))
        .thenReturn(libraries);
    when(limitService.getGlobalBlockLibrariesLimit()).thenReturn(1);

    // then
    int result = rtbProfileLibrarySellerLimitService.checkBlockLibrariesLimit(sellerPid);
    assertEquals(0, result);
  }
}
