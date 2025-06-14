package com.nexage.app.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.model.BidderConfig;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.SellerEligibleBidders;
import com.nexage.admin.core.repository.BidderConfigRepository;
import com.nexage.app.dto.publisher.PublisherRTBProfileGroupDTO;
import com.nexage.app.dto.publisher.PublisherRTBProfileLibraryDTO;
import com.nexage.app.security.LoginUserContext;
import com.nexage.app.services.CompanyRtbProfileLibraryService;
import com.nexage.app.services.CompanyService;
import com.nexage.app.services.RtbProfileLibraryService;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PublisherRtbProfileLibrarySelfServiceImplTest {

  @Mock private LoginUserContext userContext;
  @Mock private BidderConfigRepository bidderConfigRepository;
  @Mock private CompanyService companyService;
  @Mock private RtbProfileLibraryService rtbProfileLibraryService;
  @Mock private RtbProfileGroupServiceImpl rtbProfileGroupService;
  @Mock private CompanyRtbProfileLibraryService companyRtbProfileLibraryService;

  @InjectMocks private PublisherRtbProfileLibrarySelfServiceImpl selfService;

  @Test
  void shouldCallUpdateRTBProfileGroupWithCorrectParams() {
    // given
    PublisherRTBProfileGroupDTO group = new PublisherRTBProfileGroupDTO();
    // when
    selfService.updateRTBProfileGroup(2L, 3L, group);
    // then
    verify(rtbProfileGroupService).update(3L, group);
    verifyNoMoreInteractions(rtbProfileLibraryService);
  }

  @Test
  void shouldGetRtbProfileGroupByPublisherAndGroup() {
    final var publisherPid = 1L;
    final var groupPid = 1L;
    PublisherRTBProfileGroupDTO group = mock(PublisherRTBProfileGroupDTO.class);
    when(rtbProfileGroupService.get(groupPid)).thenReturn(group);

    // when
    PublisherRTBProfileGroupDTO result = selfService.getRTBProfileGroup(publisherPid, groupPid);

    // then
    assertEquals(result, group);
  }

  @Test
  void shouldCreateRtbProfileLibraryByPublisher() {
    final var publisherPid = 1L;
    PublisherRTBProfileLibraryDTO library = mock(PublisherRTBProfileLibraryDTO.class);
    when(rtbProfileLibraryService.create(publisherPid, library)).thenReturn(library);

    // when
    PublisherRTBProfileLibraryDTO result =
        selfService.createRTBProfileLibrary(publisherPid, library);

    // then
    assertEquals(result, library);
  }

  @Test
  void shouldGetRtbProfileLibraryByPublisher() {
    // given
    final var publisherPid = 1L;
    final var libraryPid = 1L;
    PublisherRTBProfileLibraryDTO library = mock(PublisherRTBProfileLibraryDTO.class);
    when(rtbProfileLibraryService.get(publisherPid, libraryPid)).thenReturn(library);

    // when
    PublisherRTBProfileLibraryDTO result =
        selfService.getRTBProfileLibrary(publisherPid, libraryPid);

    // then
    assertEquals(result, library);
  }

  @Test
  void shouldGetRtbProfileLibraryListByPublisher() {
    // given
    final var publisherPid = 1L;
    PublisherRTBProfileLibraryDTO library = mock(PublisherRTBProfileLibraryDTO.class);
    List<PublisherRTBProfileLibraryDTO> list = List.of(library);
    when(userContext.isNexageUser()).thenReturn(true);
    when(companyRtbProfileLibraryService.getRTBProfileLibrariesForCompany(publisherPid, true))
        .thenReturn(list);

    // when
    List<PublisherRTBProfileLibraryDTO> result =
        selfService.getRTBProfileLibrariesForCompany(publisherPid);

    // then
    assertEquals(result, list);
  }

  @Test
  void shouldFilterOutInactiveBiddersWhenGetEligibleRTBProfileLibrariesForCompany() {
    // given
    final var publisherPid = 1L;
    final var inactiveBidderPid = 2L;
    PublisherRTBProfileGroupDTO rtbProfileGroupDTO =
        new PublisherRTBProfileGroupDTO(
            1L, "name", 1, "none", String.valueOf(inactiveBidderPid), 2, 0, 1L, true);
    PublisherRTBProfileLibraryDTO library = new PublisherRTBProfileLibraryDTO();
    library.setGroups(Set.of(rtbProfileGroupDTO));
    List<PublisherRTBProfileLibraryDTO> libraryList = List.of(library);
    BidderConfig bidderConfig = new BidderConfig();
    bidderConfig.setPid(inactiveBidderPid);
    bidderConfig.setTrafficStatus(false);
    List<BidderConfig> bidderConfigList = List.of(bidderConfig);
    Company company = new Company();
    company.setEligibleBidders(createSellerEligibleBidders());

    given(bidderConfigRepository.findByTrafficStatus(false)).willReturn(bidderConfigList);
    given(companyService.getCompany(publisherPid)).willReturn(company);
    given(rtbProfileGroupService.get(anyLong())).willReturn(rtbProfileGroupDTO);
    given(userContext.isNexageUser()).willReturn(true);
    given(companyRtbProfileLibraryService.getRTBProfileLibrariesForCompany(publisherPid, true))
        .willReturn(libraryList);

    // when
    List<PublisherRTBProfileLibraryDTO> result =
        selfService.getEligibleRTBProfileLibrariesForCompany(publisherPid);

    // then
    assertTrue(result.isEmpty());
  }

  @Test
  void shouldGetEligibleBidders() {
    // given
    final var publisherPid = 1L;
    PublisherRTBProfileGroupDTO rtbProfileGroupDTO =
        new PublisherRTBProfileGroupDTO(1L, "name", 1, "none", "1", 0, 0, 1L, true);
    Company company = mock(Company.class);
    when(companyService.getCompany(publisherPid)).thenReturn(company);
    when(company.getEligibleBidders()).thenReturn(createSellerEligibleBidders());
    when(rtbProfileGroupService.get(anyLong())).thenReturn(rtbProfileGroupDTO);

    // when
    Set<Long> result = selfService.getEligibleBidders(publisherPid);

    // then
    assertEquals(Set.of(1L), result);
  }

  private Set<SellerEligibleBidders> createSellerEligibleBidders() {
    SellerEligibleBidders s = new SellerEligibleBidders();
    s.setPid(1L);
    s.setEligibleBidderGroups(Set.of(10L, 20L));
    return Set.of(s);
  }
}
