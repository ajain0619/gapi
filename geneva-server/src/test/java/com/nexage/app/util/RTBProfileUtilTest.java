package com.nexage.app.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.nexage.admin.core.enums.AlterReserve;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.RTBProfile;
import com.nexage.admin.core.model.RtbProfileLibrary;
import com.nexage.admin.core.model.SellerAttributes;
import com.nexage.admin.core.repository.RTBProfileLibraryRepository;
import com.nexage.admin.core.repository.RTBProfileRepository;
import com.nexage.admin.core.sparta.jpa.model.RTBProfileLibraryAssociation;
import com.nexage.app.util.validator.RTBProfileValidator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RTBProfileUtilTest {

  @Mock private RTBProfileValidator rtbProfileValidator;
  @Mock private RTBProfileLibraryRepository rtbProfileLibraryRepository;
  @Mock private RTBProfileRepository rtbProfileRepository;
  @InjectMocks private RTBProfileUtil rtbProfileUtil;

  private RTBProfile rtbProfile;

  @BeforeEach
  public void setup() {
    rtbProfile = new RTBProfile();
  }

  @Test
  void shouldPrepareRTBProfile() {
    // when
    rtbProfileUtil.prepareDefaultRtbProfile(rtbProfile);
    // then
    assertNotNull(rtbProfile.getExchangeSiteTagId());
  }

  @Test
  void shouldSetAlterReserveForTagWithoutSellerAttributes() {
    // given
    Company company = new Company();
    rtbProfile.setAlterReserve(null);
    // when
    rtbProfileUtil.setAlterReserveForTag(company, rtbProfile);
    // then
    assertEquals(AlterReserve.OFF, rtbProfile.getAlterReserve());
  }

  @Test
  void shouldSetAlterReserveForTagWithSellerAttributes() {
    // given
    Company company = new Company();
    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerAttributes.setPfoEnabled(true);
    company.setSellerAttributes(sellerAttributes);
    rtbProfile.setAlterReserve(null);
    // when
    rtbProfileUtil.setAlterReserveForTag(company, rtbProfile);
    // then
    assertEquals(AlterReserve.ONLY_IF_HIGHER, rtbProfile.getAlterReserve());
  }

  @Test
  void shouldGetAllRTBProfileLibraries() {
    // given
    Set<Long> pids = Set.of(1L);
    RtbProfileLibrary rtbProfileLibrary = new RtbProfileLibrary();
    given(rtbProfileLibraryRepository.findAllById(pids)).willReturn(List.of(rtbProfileLibrary));
    // when
    Set<RtbProfileLibrary> result = rtbProfileUtil.getRTBProfileLibraries(pids);
    // then
    assertTrue(result.contains(rtbProfileLibrary));
    verify(rtbProfileLibraryRepository).findAllById(pids);
  }

  @Test
  void shouldReturnEmptySetWhenGettingAllRTBProfileLibrariesWithNull() {
    // when
    Set<RtbProfileLibrary> result = rtbProfileUtil.getRTBProfileLibraries(null);
    // then
    assertTrue(result.isEmpty());
  }

  @Test
  void shouldAddNewLibraries() {
    RTBProfile rtbProfile = new RTBProfile();
    rtbProfile.setLibraryPids(Set.of(1L, 2L));
    Set<RtbProfileLibrary> rtbProfileLibraries = new HashSet<>();
    rtbProfileLibraries.add(createRtbProfileLibrary(1L));
    rtbProfileLibraries.add(createRtbProfileLibrary(2L));
    Set<RTBProfileLibraryAssociation> rtbProfileLibraryAssociation =
        createRtbProfileLibraryAssociations(rtbProfile, rtbProfileLibraries);
    rtbProfileUtil.syncRTBProfileLibraries(
        rtbProfile, rtbProfileLibraries, rtbProfileLibraryAssociation);

    assertEquals(2, rtbProfile.getLibraries().size());
  }

  @Test
  void shouldUpdateProfileForTag() {
    // Given
    Date unixEpoch = new Date(0);
    RTBProfile rtbProfile = new RTBProfile();
    rtbProfile.setLastUpdate(unixEpoch);

    given(rtbProfileRepository.findByTagPid(10L)).willReturn(Optional.of(rtbProfile));
    given(rtbProfileRepository.save(rtbProfile)).willReturn(mock(RTBProfile.class));

    // When
    rtbProfileUtil.updateRTBProfileForTag(10L);

    // Then
    assertNotEquals(unixEpoch, rtbProfile.getLastUpdate());
  }

  @Test
  void shouldUpdateProfileForTagWithNullProfile() {
    // Given
    given(rtbProfileRepository.findByTagPid(10L)).willReturn(Optional.empty());

    // When
    rtbProfileUtil.updateRTBProfileForTag(10L);

    // Then
    verify(rtbProfileRepository, times(0)).save(any(RTBProfile.class));
  }

  private RtbProfileLibrary createRtbProfileLibrary(Long pid) {
    RtbProfileLibrary rtbProfileLibrary = new RtbProfileLibrary();
    rtbProfileLibrary.setPid(pid);
    return rtbProfileLibrary;
  }

  private Set<RTBProfileLibraryAssociation> createRtbProfileLibraryAssociations(
      RTBProfile rtbProfile, Set<RtbProfileLibrary> rtbProfileLibraries) {
    Set<RTBProfileLibraryAssociation> rtbProfileLibraryAssociations = new HashSet<>();
    for (RtbProfileLibrary rtbProfileLibrary : rtbProfileLibraries) {
      RTBProfileLibraryAssociation rtbProfileLibraryAssociation =
          new RTBProfileLibraryAssociation();
      rtbProfileLibraryAssociation.setLibrary(rtbProfileLibrary);
      rtbProfileLibraryAssociation.setRtbprofile(rtbProfile);
      rtbProfileLibraryAssociations.add(rtbProfileLibraryAssociation);
    }
    return rtbProfileLibraryAssociations;
  }
}
