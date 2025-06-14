package com.nexage.app.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.model.RTBProfile;
import com.nexage.admin.core.model.RtbProfileLibrary;
import com.nexage.admin.core.repository.RTBProfileLibraryRepository;
import com.nexage.admin.core.repository.RTBProfileRepository;
import com.nexage.admin.core.sparta.jpa.model.RTBProfileLibraryAssociation;
import com.nexage.app.dto.RTBProfileLibraryCloneDataDTO;
import com.nexage.app.dto.publisher.PublisherRTBProfileGroupDTO;
import com.nexage.app.dto.publisher.PublisherRTBProfileLibraryDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.services.impl.limit.RtbProfileLimitChecker;
import com.nexage.app.util.assemblers.PublisherRTBProfileLibraryAssembler;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.error.model.CommonErrorCodes;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class RtbProfileLibraryServiceImplTest {

  @Mock private PublisherRTBProfileLibraryAssembler publisherRTBProfileLibraryAssembler;
  @Mock private RTBProfileLibraryRepository rtbProfileLibraryRepository;
  @Mock private RTBProfileRepository rtbProfileRepository;
  @Mock private RtbProfileLimitChecker rtbProfileLimitChecker;
  @InjectMocks private RtbProfileLibraryServiceImpl rtbProfileLibraryService;

  @Test
  void shouldCreateAndReturnRTBProfileLibraryDto() {
    // given
    long libraryPid = 1L;
    long publisherPid = 2L;

    RtbProfileLibrary rtbProfileLibrary = new RtbProfileLibrary();
    rtbProfileLibrary.setPid(libraryPid);

    PublisherRTBProfileLibraryDTO rtbProfileLibraryDto = new PublisherRTBProfileLibraryDTO();
    rtbProfileLibraryDto.setPid(libraryPid);

    given(publisherRTBProfileLibraryAssembler.apply(any(), eq(rtbProfileLibraryDto)))
        .willReturn(rtbProfileLibrary);
    given(publisherRTBProfileLibraryAssembler.applyGroups(rtbProfileLibrary, rtbProfileLibraryDto))
        .willReturn(rtbProfileLibrary);
    given(rtbProfileLibraryRepository.save(rtbProfileLibrary)).willReturn(rtbProfileLibrary);
    given(publisherRTBProfileLibraryAssembler.make(rtbProfileLibrary))
        .willReturn(rtbProfileLibraryDto);

    // when
    PublisherRTBProfileLibraryDTO result =
        rtbProfileLibraryService.create(publisherPid, rtbProfileLibraryDto);

    // then
    assertEquals(rtbProfileLibraryDto, result);
    verify(publisherRTBProfileLibraryAssembler).apply(any(), eq(rtbProfileLibraryDto));
    verify(publisherRTBProfileLibraryAssembler)
        .applyGroups(rtbProfileLibrary, rtbProfileLibraryDto);
    verify(rtbProfileLibraryRepository, times(2)).save(rtbProfileLibrary);
    verify(publisherRTBProfileLibraryAssembler).make(rtbProfileLibrary);
  }

  @Test
  void shouldThrowNotFoundExceptionWhenGettingNonExistingRTBProfileLibraryWithPublisherPid() {
    // given
    long libraryPid = 1L;
    long publisherPid = 2L;
    given(rtbProfileLibraryRepository.findByPidAndPublisherPid(libraryPid, publisherPid))
        .willReturn(Optional.empty());

    // when
    var ex =
        assertThrows(
            GenevaValidationException.class,
            () -> rtbProfileLibraryService.get(publisherPid, libraryPid));

    // then
    assertEquals(ServerErrorCodes.SERVER_RTB_PROFILE_LIBRARY_NOT_FOUND, ex.getErrorCode());
  }

  @Test
  void shouldThrowNotFoundExceptionWhenGettingNonExistingRTBProfileLibrary() {
    // given
    long libraryPid = 1L;
    given(rtbProfileLibraryRepository.findById(libraryPid)).willReturn(Optional.empty());

    // when
    var ex =
        assertThrows(
            GenevaValidationException.class, () -> rtbProfileLibraryService.get(libraryPid));

    // then
    assertEquals(ServerErrorCodes.SERVER_RTB_PROFILE_LIBRARY_NOT_FOUND, ex.getErrorCode());
  }

  @Test
  void shouldThrowNotFoundExceptionWhenDeletingNonExistingRTBProfileLibraryByPidAndPublisherPid() {
    // given
    long libraryPid = 1L;
    long publisherPid = 2L;
    given(rtbProfileLibraryRepository.findByPidAndPublisherPid(libraryPid, publisherPid))
        .willReturn(Optional.empty());

    // when
    var ex =
        assertThrows(
            GenevaValidationException.class,
            () -> rtbProfileLibraryService.delete(publisherPid, libraryPid));

    // then
    assertEquals(ServerErrorCodes.SERVER_RTB_PROFILE_LIBRARY_NOT_FOUND, ex.getErrorCode());
  }

  @Test
  void shouldDeleteRTBProfileLibraryByPidAndPublisherPid() {
    // given
    long libraryPid = 1L;
    long publisherPid = 2L;
    Set<RTBProfileLibraryAssociation> rtbProfileLibraryAssociations = new HashSet<>();
    RTBProfileLibraryAssociation rtbProfileLibraryAssociation = new RTBProfileLibraryAssociation();
    RTBProfile rtbProfile = new RTBProfile();
    rtbProfileLibraryAssociation.setRtbprofile(rtbProfile);
    rtbProfileLibraryAssociations.add(rtbProfileLibraryAssociation);
    RtbProfileLibrary rtbProfileLibrary = new RtbProfileLibrary();
    rtbProfileLibrary.setPid(libraryPid);
    rtbProfileLibrary.setProfileLibraryAssociations(rtbProfileLibraryAssociations);
    given(rtbProfileLibraryRepository.findByPidAndPublisherPid(libraryPid, publisherPid))
        .willReturn(Optional.of(rtbProfileLibrary));
    given(rtbProfileRepository.save(any(RTBProfile.class))).willReturn(new RTBProfile());

    // when
    rtbProfileLibraryService.delete(publisherPid, libraryPid);

    // then
    verify(rtbProfileLibraryRepository, times(2))
        .findByPidAndPublisherPid(libraryPid, publisherPid);
    verify(rtbProfileLibraryRepository).save(rtbProfileLibrary);
    verify(rtbProfileLibraryRepository).delete(rtbProfileLibrary);
  }

  @Test
  void shouldThrowNotFoundExceptionWhenDeletingNonExistingRTBProfileLibraryByPid() {
    // given
    long libraryPid = 1L;
    given(rtbProfileLibraryRepository.findById(libraryPid)).willReturn(Optional.empty());

    // when
    var ex =
        assertThrows(
            GenevaValidationException.class, () -> rtbProfileLibraryService.delete(libraryPid));

    // then
    assertEquals(ServerErrorCodes.SERVER_RTB_PROFILE_LIBRARY_NOT_FOUND, ex.getErrorCode());
  }

  @Test
  void shouldDeleteRTBProfileLibraryByPid() {
    // given
    long libraryPid = 1L;
    RtbProfileLibrary rtbProfileLibrary = new RtbProfileLibrary();
    rtbProfileLibrary.setPid(libraryPid);
    given(rtbProfileLibraryRepository.findById(libraryPid))
        .willReturn(Optional.of(rtbProfileLibrary));

    // when
    rtbProfileLibraryService.delete(libraryPid);

    // then
    verify(rtbProfileLibraryRepository, times(2)).findById(libraryPid);
    verify(rtbProfileLibraryRepository).save(rtbProfileLibrary);
    verify(rtbProfileLibraryRepository).delete(rtbProfileLibrary);
  }

  @Test
  void shouldReturnRTBProfileLibraryDtoWhenGettingByValidLibraryPidAndPublisherPid() {
    // given
    long libraryPid = 1L;
    long publisherPid = 2L;
    RtbProfileLibrary rtbProfileLibrary = new RtbProfileLibrary();
    rtbProfileLibrary.setPid(libraryPid);
    rtbProfileLibrary.setPublisherPid(publisherPid);
    PublisherRTBProfileLibraryDTO rtbProfileLibraryDto = new PublisherRTBProfileLibraryDTO();
    rtbProfileLibraryDto.setPid(libraryPid);
    rtbProfileLibraryDto.setPublisherPid(publisherPid);
    given(rtbProfileLibraryRepository.findByPidAndPublisherPid(libraryPid, publisherPid))
        .willReturn(Optional.of(rtbProfileLibrary));
    given(publisherRTBProfileLibraryAssembler.make(rtbProfileLibrary))
        .willReturn(rtbProfileLibraryDto);

    // when
    PublisherRTBProfileLibraryDTO result = rtbProfileLibraryService.get(publisherPid, libraryPid);

    // then
    assertEquals(libraryPid, result.getPid());
    assertEquals(publisherPid, result.getPublisherPid());
    verify(rtbProfileLibraryRepository).findByPidAndPublisherPid(libraryPid, publisherPid);
    verify(publisherRTBProfileLibraryAssembler).make(rtbProfileLibrary);
  }

  @Test
  void shouldReturnRTBProfileLibraryDtoWhenGettingByValidLibraryPid() {
    // given
    long libraryPid = 1L;
    RtbProfileLibrary rtbProfileLibrary = new RtbProfileLibrary();
    rtbProfileLibrary.setPid(libraryPid);
    PublisherRTBProfileLibraryDTO rtbProfileLibraryDto = new PublisherRTBProfileLibraryDTO();
    rtbProfileLibraryDto.setPid(libraryPid);
    given(rtbProfileLibraryRepository.findById(libraryPid))
        .willReturn(Optional.of(rtbProfileLibrary));
    given(publisherRTBProfileLibraryAssembler.make(rtbProfileLibrary))
        .willReturn(rtbProfileLibraryDto);

    // when
    PublisherRTBProfileLibraryDTO result = rtbProfileLibraryService.get(libraryPid);

    // then
    assertEquals(libraryPid, result.getPid());
    verify(rtbProfileLibraryRepository).findById(libraryPid);
    verify(publisherRTBProfileLibraryAssembler).make(rtbProfileLibrary);
  }

  @Test
  void shouldReturnAllRTBProfileLibraries() {
    // given
    long libraryPid = 1L;
    RtbProfileLibrary rtbProfileLibrary = new RtbProfileLibrary();
    rtbProfileLibrary.setPid(libraryPid);
    PublisherRTBProfileLibraryDTO rtbProfileLibraryDto = new PublisherRTBProfileLibraryDTO();
    rtbProfileLibraryDto.setPid(libraryPid);

    given(rtbProfileLibraryRepository.findAll()).willReturn(List.of(rtbProfileLibrary));
    given(publisherRTBProfileLibraryAssembler.make(rtbProfileLibrary))
        .willReturn(rtbProfileLibraryDto);

    // when
    List<PublisherRTBProfileLibraryDTO> result = rtbProfileLibraryService.getAll();

    // then
    assertEquals(1, result.size());
    assertTrue(result.contains(rtbProfileLibraryDto));
    verify(rtbProfileLibraryRepository).findAll();
    verify(publisherRTBProfileLibraryAssembler).make(rtbProfileLibrary);
  }

  @Test
  void shouldUpdateAndReturnRTBProfileLibraryDtoWithPublisherPid() {
    // given
    long libraryPid = 1L;
    long publisherPid = 2L;
    String nameBeforeUpdate = "name before update";
    String nameAfterUpdate = "name after update";

    RtbProfileLibrary rtbProfileLibraryBeforeUpdate = new RtbProfileLibrary();
    rtbProfileLibraryBeforeUpdate.setPid(libraryPid);
    rtbProfileLibraryBeforeUpdate.setPublisherPid(publisherPid);
    rtbProfileLibraryBeforeUpdate.setName(nameBeforeUpdate);

    PublisherRTBProfileLibraryDTO rtbProfileLibraryDto = new PublisherRTBProfileLibraryDTO();
    rtbProfileLibraryDto.setPid(libraryPid);
    rtbProfileLibraryDto.setPublisherPid(publisherPid);
    rtbProfileLibraryBeforeUpdate.setName(nameAfterUpdate);

    RtbProfileLibrary rtbProfileLibraryAfterUpdate = new RtbProfileLibrary();
    rtbProfileLibraryAfterUpdate.setPid(libraryPid);
    rtbProfileLibraryAfterUpdate.setPublisherPid(publisherPid);
    rtbProfileLibraryAfterUpdate.setName(nameAfterUpdate);

    given(rtbProfileLibraryRepository.findByPidAndPublisherPid(libraryPid, publisherPid))
        .willReturn(Optional.of(rtbProfileLibraryBeforeUpdate));
    given(
            publisherRTBProfileLibraryAssembler.apply(
                rtbProfileLibraryBeforeUpdate, rtbProfileLibraryDto))
        .willReturn(rtbProfileLibraryAfterUpdate);
    given(
            publisherRTBProfileLibraryAssembler.applyGroups(
                rtbProfileLibraryAfterUpdate, rtbProfileLibraryDto))
        .willReturn(rtbProfileLibraryAfterUpdate);
    given(rtbProfileLibraryRepository.save(rtbProfileLibraryAfterUpdate))
        .willReturn(rtbProfileLibraryAfterUpdate);
    given(publisherRTBProfileLibraryAssembler.make(rtbProfileLibraryAfterUpdate))
        .willReturn(rtbProfileLibraryDto);

    // when
    PublisherRTBProfileLibraryDTO result =
        rtbProfileLibraryService.update(publisherPid, libraryPid, rtbProfileLibraryDto);

    // then
    assertEquals(rtbProfileLibraryDto, result);
    verify(rtbProfileLibraryRepository).findByPidAndPublisherPid(libraryPid, publisherPid);
    verify(publisherRTBProfileLibraryAssembler)
        .apply(rtbProfileLibraryBeforeUpdate, rtbProfileLibraryDto);
    verify(publisherRTBProfileLibraryAssembler)
        .applyGroups(rtbProfileLibraryAfterUpdate, rtbProfileLibraryDto);
    verify(rtbProfileLibraryRepository).save(rtbProfileLibraryAfterUpdate);
    verify(publisherRTBProfileLibraryAssembler).make(rtbProfileLibraryAfterUpdate);
  }

  @Test
  void shouldThrowNotFoundWhenRtbProfileLibraryDoesNotExist() {
    // when
    when(rtbProfileLibraryRepository.findById(anyLong())).thenReturn(Optional.empty());

    // then
    var exception =
        assertThrows(
            GenevaValidationException.class, () -> rtbProfileLibraryService.update(1L, null));
    assertEquals(ServerErrorCodes.SERVER_RTB_PROFILE_LIBRARY_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void shouldThrowNotFoundWhenRtbProfileLibraryOfPublisherDoesNotExist() {
    // when
    when(rtbProfileLibraryRepository.findByPidAndPublisherPid(anyLong(), anyLong()))
        .thenReturn(Optional.empty());

    // then
    var exception =
        assertThrows(
            GenevaValidationException.class, () -> rtbProfileLibraryService.update(1L, 1L, null));
    assertEquals(ServerErrorCodes.SERVER_RTB_PROFILE_LIBRARY_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void shouldUpdateAndReturnRTBProfileLibraryDtoWithoutPublisherPid() {
    // given
    long libraryPid = 1L;
    String nameBeforeUpdate = "name before update";
    String nameAfterUpdate = "name after update";

    RtbProfileLibrary rtbProfileLibraryBeforeUpdate = new RtbProfileLibrary();
    rtbProfileLibraryBeforeUpdate.setPid(libraryPid);
    rtbProfileLibraryBeforeUpdate.setName(nameBeforeUpdate);

    PublisherRTBProfileLibraryDTO rtbProfileLibraryDto = new PublisherRTBProfileLibraryDTO();
    rtbProfileLibraryDto.setPid(libraryPid);
    rtbProfileLibraryBeforeUpdate.setName(nameAfterUpdate);

    RtbProfileLibrary rtbProfileLibraryAfterUpdate = new RtbProfileLibrary();
    rtbProfileLibraryAfterUpdate.setPid(libraryPid);
    rtbProfileLibraryAfterUpdate.setName(nameAfterUpdate);

    given(rtbProfileLibraryRepository.findById(libraryPid))
        .willReturn(Optional.of(rtbProfileLibraryBeforeUpdate));
    given(
            publisherRTBProfileLibraryAssembler.apply(
                rtbProfileLibraryBeforeUpdate, rtbProfileLibraryDto))
        .willReturn(rtbProfileLibraryAfterUpdate);
    given(
            publisherRTBProfileLibraryAssembler.applyGroups(
                rtbProfileLibraryAfterUpdate, rtbProfileLibraryDto))
        .willReturn(rtbProfileLibraryAfterUpdate);
    given(rtbProfileLibraryRepository.save(rtbProfileLibraryAfterUpdate))
        .willReturn(rtbProfileLibraryAfterUpdate);
    given(publisherRTBProfileLibraryAssembler.make(rtbProfileLibraryAfterUpdate))
        .willReturn(rtbProfileLibraryDto);

    // when
    PublisherRTBProfileLibraryDTO result =
        rtbProfileLibraryService.update(libraryPid, rtbProfileLibraryDto);

    // then
    assertEquals(rtbProfileLibraryDto, result);
    verify(rtbProfileLibraryRepository).findById(libraryPid);
    verify(publisherRTBProfileLibraryAssembler)
        .apply(rtbProfileLibraryBeforeUpdate, rtbProfileLibraryDto);
    verify(publisherRTBProfileLibraryAssembler)
        .applyGroups(rtbProfileLibraryAfterUpdate, rtbProfileLibraryDto);
    verify(rtbProfileLibraryRepository).save(rtbProfileLibraryAfterUpdate);
    verify(publisherRTBProfileLibraryAssembler).make(rtbProfileLibraryAfterUpdate);
  }

  @Test
  void shouldReturnClonedRTBProfileLibraryDto() {
    // Given
    PublisherRTBProfileLibraryDTO rtbProfileLibraryDto =
        new PublisherRTBProfileLibraryDTO(1L, "rtbProfileLibraryDto", 1, "GLOBAL", 100L, true);
    PublisherRTBProfileGroupDTO rtbProfileGroupDto =
        new PublisherRTBProfileGroupDTO(
            1L, "rtbProfileGroupDto", 1, "GLOBAL", "Data", 2, 3, 100L, true);
    Set<PublisherRTBProfileGroupDTO> groups = Set.of(rtbProfileGroupDto);
    rtbProfileLibraryDto.setGroups(groups);
    List<PublisherRTBProfileLibraryDTO> libraries = List.of(rtbProfileLibraryDto);

    RTBProfileLibraryCloneDataDTO rtbProfileLibraryCloneDataDto =
        new RTBProfileLibraryCloneDataDTO();
    rtbProfileLibraryCloneDataDto.setPublisherPid(100L);
    rtbProfileLibraryCloneDataDto.setPublisherOwned();
    ReflectionTestUtils.setField(rtbProfileLibraryCloneDataDto, "name", "rtbProfileLibraryDto");
    ReflectionTestUtils.setField(rtbProfileLibraryCloneDataDto, "libraries", libraries);
    ReflectionTestUtils.setField(rtbProfileLibraryCloneDataDto, "bidderPids", List.of("123"));
    ReflectionTestUtils.setField(rtbProfileLibraryCloneDataDto, "categories", List.of("category"));
    ReflectionTestUtils.setField(rtbProfileLibraryCloneDataDto, "adomains", List.of("adomain"));

    RtbProfileLibrary rtbProfileLibrary = new RtbProfileLibrary();

    given(rtbProfileLibraryRepository.save(any())).willReturn(rtbProfileLibrary);
    given(publisherRTBProfileLibraryAssembler.apply(any(), any())).willReturn(rtbProfileLibrary);
    given(publisherRTBProfileLibraryAssembler.applyGroups(any(), any()))
        .willReturn(rtbProfileLibrary);
    given(rtbProfileLibraryRepository.save(any())).willReturn(rtbProfileLibrary);
    given(publisherRTBProfileLibraryAssembler.make(any())).willReturn(rtbProfileLibraryDto);

    // When
    PublisherRTBProfileLibraryDTO rtbProfileLibraryDtoClone =
        rtbProfileLibraryService.clone(100L, rtbProfileLibraryCloneDataDto);

    // Then
    assertNotNull(rtbProfileLibraryDtoClone);
    assertEquals(100L, rtbProfileLibraryCloneDataDto.getPublisherPid());
    assertEquals("rtbProfileLibraryDto", rtbProfileLibraryCloneDataDto.getName());
    assertEquals("GLOBAL", rtbProfileLibraryDtoClone.getPrivilegeLevel());
    assertNotNull(rtbProfileLibraryDtoClone.getGroups());
  }

  @Test
  void shouldThrowExceptionOnCloneWithDataNull() {
    var exception =
        assertThrows(
            GenevaValidationException.class, () -> rtbProfileLibraryService.clone(100L, null));
    assertEquals(CommonErrorCodes.COMMON_BAD_REQUEST, exception.getErrorCode());
  }
}
