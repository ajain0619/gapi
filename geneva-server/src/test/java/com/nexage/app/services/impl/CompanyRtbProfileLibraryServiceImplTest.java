package com.nexage.app.services.impl;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.model.RtbProfileLibrary;
import com.nexage.admin.core.repository.RTBProfileLibraryRepository;
import com.nexage.admin.core.sparta.jpa.model.RTBProfileLibraryPrivilegeLevel;
import com.nexage.app.dto.publisher.PublisherRTBProfileLibraryDTO;
import com.nexage.app.util.assemblers.PublisherRTBProfileLibraryAssembler;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CompanyRtbProfileLibraryServiceImplTest {

  @Mock private PublisherRTBProfileLibraryAssembler publisherRTBProfileLibraryAssembler;
  @Mock private RTBProfileLibraryRepository rtbProfileLibraryRepository;

  @InjectMocks private CompanyRtbProfileLibraryServiceImpl companyRtbProfileLibraryService;

  @Test
  void shouldReturnLibrariesByPublisherPid() {
    // given
    final long publisherPid = new Random().nextLong();

    RtbProfileLibrary rtbProfileLibrary = new RtbProfileLibrary();
    rtbProfileLibrary.setPrivilegeLevel(RTBProfileLibraryPrivilegeLevel.PUBLISHER);
    RtbProfileLibrary anotherRtbProfileLibrary = new RtbProfileLibrary();
    anotherRtbProfileLibrary.setPrivilegeLevel(RTBProfileLibraryPrivilegeLevel.GLOBAL);
    List<RtbProfileLibrary> libraries = List.of(rtbProfileLibrary);

    PublisherRTBProfileLibraryDTO publisherRTBProfileLibraryDTO =
        new PublisherRTBProfileLibraryDTO();
    publisherRTBProfileLibraryDTO.setPublisherPid(publisherPid);
    publisherRTBProfileLibraryDTO.setPrivilegeLevel("PUBLISHER");

    when(rtbProfileLibraryRepository.findAllByPublisherPidAndPrivilegeLevel(
            publisherPid, RTBProfileLibraryPrivilegeLevel.PUBLISHER))
        .thenReturn(libraries);
    when(publisherRTBProfileLibraryAssembler.make(rtbProfileLibrary))
        .thenReturn(publisherRTBProfileLibraryDTO);

    // when
    List<PublisherRTBProfileLibraryDTO> response =
        companyRtbProfileLibraryService.getRTBProfileLibrariesForCompany(publisherPid);

    // then
    assertNotNull(response);
    assertFalse(response.isEmpty());
    assertEquals(1, response.size());
    assertNotNull(response.get(0));
    assertEquals(response.get(0), publisherRTBProfileLibraryDTO);
  }

  @Test
  void shouldBeEmptyWhenLevelIsNotTheExpectedOneByPublisherPid() {
    // given
    final long publisherPid = new Random().nextLong();

    RtbProfileLibrary rtbProfileLibrary = new RtbProfileLibrary();
    rtbProfileLibrary.setPrivilegeLevel(RTBProfileLibraryPrivilegeLevel.NEXAGE_ONLY);
    RtbProfileLibrary anotherRtbProfileLibrary = new RtbProfileLibrary();
    anotherRtbProfileLibrary.setPrivilegeLevel(RTBProfileLibraryPrivilegeLevel.GLOBAL);
    List<RtbProfileLibrary> libraries = List.of(rtbProfileLibrary, anotherRtbProfileLibrary);

    when(rtbProfileLibraryRepository.findAllByPublisherPidAndPrivilegeLevel(
            publisherPid, RTBProfileLibraryPrivilegeLevel.PUBLISHER))
        .thenReturn(Collections.emptyList());

    // when
    List<PublisherRTBProfileLibraryDTO> response =
        companyRtbProfileLibraryService.getRTBProfileLibrariesForCompany(publisherPid);

    // then
    assertNotNull(response);
    assertTrue(response.isEmpty());
  }

  @Test
  void shouldReturnLibrariesByPublisherPidByInternalUser() {
    // given
    final long publisherPid = new Random().nextLong();

    RtbProfileLibrary rtbProfileLibrary = new RtbProfileLibrary();
    rtbProfileLibrary.setPrivilegeLevel(RTBProfileLibraryPrivilegeLevel.PUBLISHER);
    RtbProfileLibrary globalLevelRtbProfileLibrary = new RtbProfileLibrary();
    globalLevelRtbProfileLibrary.setPrivilegeLevel(RTBProfileLibraryPrivilegeLevel.GLOBAL);
    RtbProfileLibrary internalLevelRtbProfileLibrary = new RtbProfileLibrary();
    internalLevelRtbProfileLibrary.setPrivilegeLevel(RTBProfileLibraryPrivilegeLevel.NEXAGE_ONLY);

    PublisherRTBProfileLibraryDTO publisherRTBProfileLibraryDTO =
        new PublisherRTBProfileLibraryDTO();
    publisherRTBProfileLibraryDTO.setPublisherPid(publisherPid);
    publisherRTBProfileLibraryDTO.setPrivilegeLevel("PUBLISHER");

    PublisherRTBProfileLibraryDTO globalRTBProfileLibraryDTO = new PublisherRTBProfileLibraryDTO();
    globalRTBProfileLibraryDTO.setPrivilegeLevel("GLOBAL");

    PublisherRTBProfileLibraryDTO nexageRTBProfileLibraryDTO = new PublisherRTBProfileLibraryDTO();
    nexageRTBProfileLibraryDTO.setPrivilegeLevel("NEXAGE_ONLY");

    when(rtbProfileLibraryRepository.findAllByPublisherPid(publisherPid))
        .thenReturn(List.of(rtbProfileLibrary));
    when(rtbProfileLibraryRepository.findAllByPrivilegeLevel(
            RTBProfileLibraryPrivilegeLevel.GLOBAL))
        .thenReturn(List.of(globalLevelRtbProfileLibrary));
    when(rtbProfileLibraryRepository.findAllByPrivilegeLevel(
            RTBProfileLibraryPrivilegeLevel.NEXAGE_ONLY))
        .thenReturn(List.of(internalLevelRtbProfileLibrary));

    when(publisherRTBProfileLibraryAssembler.make(rtbProfileLibrary))
        .thenReturn(publisherRTBProfileLibraryDTO);
    when(publisherRTBProfileLibraryAssembler.make(globalLevelRtbProfileLibrary))
        .thenReturn(globalRTBProfileLibraryDTO);
    when(publisherRTBProfileLibraryAssembler.make(internalLevelRtbProfileLibrary))
        .thenReturn(nexageRTBProfileLibraryDTO);

    // when
    List<PublisherRTBProfileLibraryDTO> response =
        companyRtbProfileLibraryService.getRTBProfileLibrariesForCompany(publisherPid, true);

    // then
    assertAll(
        () -> assertNotNull(response),
        () -> assertFalse(response.isEmpty()),
        () -> assertEquals(3, response.size()));
  }

  @Test
  void shouldReturnPublisherAndGlobalLibrariesByPublisherPidByInternalUser() {
    // given
    final long publisherPid = new Random().nextLong();

    RtbProfileLibrary rtbProfileLibrary = new RtbProfileLibrary();
    rtbProfileLibrary.setPrivilegeLevel(RTBProfileLibraryPrivilegeLevel.PUBLISHER);
    RtbProfileLibrary globalLevelRtbProfileLibrary = new RtbProfileLibrary();
    globalLevelRtbProfileLibrary.setPrivilegeLevel(RTBProfileLibraryPrivilegeLevel.GLOBAL);

    PublisherRTBProfileLibraryDTO publisherRTBProfileLibraryDTO =
        new PublisherRTBProfileLibraryDTO();
    publisherRTBProfileLibraryDTO.setPublisherPid(publisherPid);
    publisherRTBProfileLibraryDTO.setPrivilegeLevel("PUBLISHER");

    PublisherRTBProfileLibraryDTO globalRTBProfileLibraryDTO = new PublisherRTBProfileLibraryDTO();
    globalRTBProfileLibraryDTO.setPrivilegeLevel("GLOBAL");

    when(rtbProfileLibraryRepository.findAllByPublisherPid(publisherPid))
        .thenReturn(List.of(rtbProfileLibrary));
    when(rtbProfileLibraryRepository.findAllByPrivilegeLevel(
            RTBProfileLibraryPrivilegeLevel.GLOBAL))
        .thenReturn(List.of(globalLevelRtbProfileLibrary));
    when(rtbProfileLibraryRepository.findAllByPrivilegeLevel(
            RTBProfileLibraryPrivilegeLevel.NEXAGE_ONLY))
        .thenReturn(Collections.emptyList());

    when(publisherRTBProfileLibraryAssembler.make(rtbProfileLibrary))
        .thenReturn(publisherRTBProfileLibraryDTO);
    when(publisherRTBProfileLibraryAssembler.make(globalLevelRtbProfileLibrary))
        .thenReturn(globalRTBProfileLibraryDTO);

    // when
    List<PublisherRTBProfileLibraryDTO> response =
        companyRtbProfileLibraryService.getRTBProfileLibrariesForCompany(publisherPid, true);

    // then
    assertAll(
        () -> assertNotNull(response),
        () -> assertFalse(response.isEmpty()),
        () -> assertEquals(2, response.size()));
  }

  @Test
  void shouldReturnPublisherRTBProfileLibrariesWhenGettingLibrariesForCompany() {
    // given
    long publisherPid = 4L;
    long companyLibraryPid = 1L;
    RtbProfileLibrary companyRtbProfileLibrary = new RtbProfileLibrary();
    companyRtbProfileLibrary.setPid(companyLibraryPid);
    PublisherRTBProfileLibraryDTO companyRtbProfileLibraryDto = new PublisherRTBProfileLibraryDTO();
    companyRtbProfileLibraryDto.setPid(companyLibraryPid);

    given(
            rtbProfileLibraryRepository.findAllByPublisherPidAndPrivilegeLevel(
                publisherPid, RTBProfileLibraryPrivilegeLevel.PUBLISHER))
        .willReturn(List.of(companyRtbProfileLibrary));
    given(publisherRTBProfileLibraryAssembler.make(companyRtbProfileLibrary))
        .willReturn(companyRtbProfileLibraryDto);

    // when
    List<PublisherRTBProfileLibraryDTO> result =
        companyRtbProfileLibraryService.getRTBProfileLibrariesForCompany(publisherPid);

    // then
    assertEquals(1, result.size());
    assertTrue(result.contains(companyRtbProfileLibraryDto));
    verify(rtbProfileLibraryRepository)
        .findAllByPublisherPidAndPrivilegeLevel(
            publisherPid, RTBProfileLibraryPrivilegeLevel.PUBLISHER);
    verify(publisherRTBProfileLibraryAssembler).make(companyRtbProfileLibrary);
  }

  @Test
  void shouldReturnRTBProfileLibrariesWhenGettingLibrariesForCompanyAsInternalUser() {
    // given
    long publisherPid = 4L;
    long companyLibraryPid = 1L;
    long globalLibraryPid = 2L;
    long nexageLibraryPid = 3L;
    boolean isInternalUser = true;
    RtbProfileLibrary companyRtbProfileLibrary = new RtbProfileLibrary();
    companyRtbProfileLibrary.setPid(companyLibraryPid);
    PublisherRTBProfileLibraryDTO companyRtbProfileLibraryDto = new PublisherRTBProfileLibraryDTO();
    companyRtbProfileLibraryDto.setPid(companyLibraryPid);

    RtbProfileLibrary globalRtbProfileLibrary = new RtbProfileLibrary();
    globalRtbProfileLibrary.setPid(globalLibraryPid);
    PublisherRTBProfileLibraryDTO globalRtbProfileLibraryDto = new PublisherRTBProfileLibraryDTO();
    globalRtbProfileLibraryDto.setPid(globalLibraryPid);

    RtbProfileLibrary nexageRtbProfileLibrary = new RtbProfileLibrary();
    nexageRtbProfileLibrary.setPid(nexageLibraryPid);
    PublisherRTBProfileLibraryDTO nexageRtbProfileLibraryDto = new PublisherRTBProfileLibraryDTO();
    nexageRtbProfileLibraryDto.setPid(nexageLibraryPid);

    given(rtbProfileLibraryRepository.findAllByPublisherPid(publisherPid))
        .willReturn(List.of(companyRtbProfileLibrary));
    given(
            rtbProfileLibraryRepository.findAllByPrivilegeLevel(
                RTBProfileLibraryPrivilegeLevel.GLOBAL))
        .willReturn(List.of(globalRtbProfileLibrary));
    given(
            rtbProfileLibraryRepository.findAllByPrivilegeLevel(
                RTBProfileLibraryPrivilegeLevel.NEXAGE_ONLY))
        .willReturn(List.of(nexageRtbProfileLibrary));
    given(publisherRTBProfileLibraryAssembler.make(companyRtbProfileLibrary))
        .willReturn(companyRtbProfileLibraryDto);
    given(publisherRTBProfileLibraryAssembler.make(globalRtbProfileLibrary))
        .willReturn(globalRtbProfileLibraryDto);
    given(publisherRTBProfileLibraryAssembler.make(nexageRtbProfileLibrary))
        .willReturn(nexageRtbProfileLibraryDto);

    // when
    List<PublisherRTBProfileLibraryDTO> result =
        companyRtbProfileLibraryService.getRTBProfileLibrariesForCompany(
            publisherPid, isInternalUser);

    // then
    assertTrue(result.contains(companyRtbProfileLibraryDto));
    assertTrue(result.contains(globalRtbProfileLibraryDto));
    if (isInternalUser) {
      assertTrue(result.contains(nexageRtbProfileLibraryDto));
    } else {
      assertFalse(result.contains(nexageRtbProfileLibraryDto));
    }
    verify(rtbProfileLibraryRepository).findAllByPublisherPid(publisherPid);
    verify(rtbProfileLibraryRepository)
        .findAllByPrivilegeLevel(RTBProfileLibraryPrivilegeLevel.GLOBAL);
    if (isInternalUser) {
      verify(rtbProfileLibraryRepository)
          .findAllByPrivilegeLevel(RTBProfileLibraryPrivilegeLevel.NEXAGE_ONLY);
    } else {
      verify(rtbProfileLibraryRepository, times(0))
          .findAllByPrivilegeLevel(RTBProfileLibraryPrivilegeLevel.NEXAGE_ONLY);
    }
    verify(publisherRTBProfileLibraryAssembler).make(companyRtbProfileLibrary);
    verify(publisherRTBProfileLibraryAssembler).make(globalRtbProfileLibrary);
    if (isInternalUser) {
      verify(publisherRTBProfileLibraryAssembler).make(nexageRtbProfileLibrary);
    } else {
      verify(publisherRTBProfileLibraryAssembler, times(0)).make(nexageRtbProfileLibrary);
    }
  }

  @Test
  void shouldReturnRTBProfileLibrariesWhenGettingLibrariesForCompanyAsNonInternalUser() {
    // given
    long publisherPid = 4L;
    long companyLibraryPid = 1L;
    long globalLibraryPid = 2L;
    long nexageLibraryPid = 3L;
    boolean isInternalUser = false;
    RtbProfileLibrary companyRtbProfileLibrary = new RtbProfileLibrary();
    companyRtbProfileLibrary.setPid(companyLibraryPid);
    PublisherRTBProfileLibraryDTO companyRtbProfileLibraryDto = new PublisherRTBProfileLibraryDTO();
    companyRtbProfileLibraryDto.setPid(companyLibraryPid);

    RtbProfileLibrary globalRtbProfileLibrary = new RtbProfileLibrary();
    globalRtbProfileLibrary.setPid(globalLibraryPid);
    PublisherRTBProfileLibraryDTO globalRtbProfileLibraryDto = new PublisherRTBProfileLibraryDTO();
    globalRtbProfileLibraryDto.setPid(globalLibraryPid);

    RtbProfileLibrary nexageRtbProfileLibrary = new RtbProfileLibrary();
    nexageRtbProfileLibrary.setPid(nexageLibraryPid);
    PublisherRTBProfileLibraryDTO nexageRtbProfileLibraryDto = new PublisherRTBProfileLibraryDTO();
    nexageRtbProfileLibraryDto.setPid(nexageLibraryPid);

    given(rtbProfileLibraryRepository.findAllByPublisherPid(publisherPid))
        .willReturn(List.of(companyRtbProfileLibrary));
    given(
            rtbProfileLibraryRepository.findAllByPrivilegeLevel(
                RTBProfileLibraryPrivilegeLevel.GLOBAL))
        .willReturn(List.of(globalRtbProfileLibrary));
    given(publisherRTBProfileLibraryAssembler.make(companyRtbProfileLibrary))
        .willReturn(companyRtbProfileLibraryDto);
    given(publisherRTBProfileLibraryAssembler.make(globalRtbProfileLibrary))
        .willReturn(globalRtbProfileLibraryDto);

    // when
    List<PublisherRTBProfileLibraryDTO> result =
        companyRtbProfileLibraryService.getRTBProfileLibrariesForCompany(
            publisherPid, isInternalUser);

    // then
    assertTrue(result.contains(companyRtbProfileLibraryDto));
    assertTrue(result.contains(globalRtbProfileLibraryDto));
    if (isInternalUser) {
      assertTrue(result.contains(nexageRtbProfileLibraryDto));
    } else {
      assertFalse(result.contains(nexageRtbProfileLibraryDto));
    }
    verify(rtbProfileLibraryRepository).findAllByPublisherPid(publisherPid);
    verify(rtbProfileLibraryRepository)
        .findAllByPrivilegeLevel(RTBProfileLibraryPrivilegeLevel.GLOBAL);
    if (isInternalUser) {
      verify(rtbProfileLibraryRepository)
          .findAllByPrivilegeLevel(RTBProfileLibraryPrivilegeLevel.NEXAGE_ONLY);
    } else {
      verify(rtbProfileLibraryRepository, times(0))
          .findAllByPrivilegeLevel(RTBProfileLibraryPrivilegeLevel.NEXAGE_ONLY);
    }
    verify(publisherRTBProfileLibraryAssembler).make(companyRtbProfileLibrary);
    verify(publisherRTBProfileLibraryAssembler).make(globalRtbProfileLibrary);
    if (isInternalUser) {
      verify(publisherRTBProfileLibraryAssembler).make(nexageRtbProfileLibrary);
    } else {
      verify(publisherRTBProfileLibraryAssembler, times(0)).make(nexageRtbProfileLibrary);
    }
  }
}
