package com.nexage.app.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.google.common.collect.Sets;
import com.nexage.admin.core.model.RtbProfileGroup;
import com.nexage.admin.core.repository.RtbProfileGroupRepository;
import com.nexage.app.dto.publisher.PublisherAttributes;
import com.nexage.app.dto.publisher.PublisherRTBProfileGroupDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.security.UserContext;
import com.nexage.app.services.RtbProfileLibrarySellerLimitService;
import com.nexage.app.services.impl.limit.RtbProfileLimitChecker;
import com.nexage.app.services.impl.limit.RtbProfileLimitInterceptor;
import com.nexage.app.util.assemblers.PublisherRTBProfileGroupAssembler;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.security.error.GenevaSecurityException;
import com.ssp.geneva.common.security.error.SecurityErrorCodes;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RtbProfileGroupServiceImplTest {

  @Mock private UserContext userContext;
  @Mock private RtbProfileGroupRepository rtbProfileGroupRepository;
  @Mock private PublisherRTBProfileGroupAssembler publisherRTBProfileGroupAssembler;
  @Mock private RtbProfileLimitChecker rtbProfileLimitChecker;
  @Mock private RtbProfileLibrarySellerLimitService rtbProfileLibrarySellerLimitService;
  @InjectMocks private RtbProfileLimitInterceptor rtbProfileLimitInterceptor;
  @InjectMocks private RtbProfileGroupServiceImpl rtbProfileGroupService;

  @Test
  void shouldCreateRTBProfileGroup() {
    // given
    PublisherRTBProfileGroupDTO rtbProfileGroupDTO =
        new PublisherRTBProfileGroupDTO(1L, "publisherGroup", 1, "level", "data", 2, 0, 1L, true);
    RtbProfileGroup rtbProfileGroup = new RtbProfileGroup();
    rtbProfileGroup.setPid(rtbProfileGroupDTO.getPid());
    doNothing().when(rtbProfileLimitChecker).checkLimitsGroup(1L, rtbProfileGroupDTO);
    given(publisherRTBProfileGroupAssembler.apply(any(), any())).willReturn(rtbProfileGroup);
    given(rtbProfileGroupRepository.save(any())).willAnswer(i -> i.getArgument(0));
    given(publisherRTBProfileGroupAssembler.make(any())).willReturn(rtbProfileGroupDTO);
    // when
    PublisherRTBProfileGroupDTO resultProfileGroupDTO =
        rtbProfileGroupService.create(1L, rtbProfileGroupDTO);
    // then
    assertEquals(rtbProfileGroupDTO, resultProfileGroupDTO);
    verify(rtbProfileGroupRepository).save(any());
  }

  @Test
  void shouldGetRTBProfileGroup() {
    // given
    PublisherRTBProfileGroupDTO rtbProfileGroupDTO =
        new PublisherRTBProfileGroupDTO(1L, "publisherGroup", 1, "level", "data", 2, 0, 1L, true);
    given(rtbProfileGroupRepository.findById(1L)).willReturn(Optional.of(new RtbProfileGroup()));
    given(publisherRTBProfileGroupAssembler.make(any())).willReturn(rtbProfileGroupDTO);
    // when
    PublisherRTBProfileGroupDTO resultProfileGroupDTO = rtbProfileGroupService.get(1L);
    // then
    assertEquals(rtbProfileGroupDTO, resultProfileGroupDTO);
  }

  @Test
  void shouldUpdateRTBProfileGroupWhenUserCanAccessPublisher() {
    // given
    PublisherRTBProfileGroupDTO groupDTO =
        aRTBProfileBuilderGroup(PublisherRTBProfileGroupDTO.ItemType.CATEGORY);
    RtbProfileGroup coreGroup = new RtbProfileGroup();
    when(userContext.doSameOrNexageAffiliation(100L)).thenReturn(true);
    when(rtbProfileGroupRepository.findById(123L)).thenReturn(Optional.of(coreGroup));
    when(publisherRTBProfileGroupAssembler.apply(coreGroup, groupDTO)).thenReturn(coreGroup);
    when(rtbProfileGroupRepository.save(coreGroup)).thenReturn(new RtbProfileGroup());
    when(publisherRTBProfileGroupAssembler.make(any(RtbProfileGroup.class)))
        .thenReturn(new PublisherRTBProfileGroupDTO());
    // when
    rtbProfileGroupService.update(123L, groupDTO);
    // then
    verify(userContext).doSameOrNexageAffiliation(100L);
    verify(rtbProfileGroupRepository).findById(123L);
    verify(rtbProfileGroupRepository).save(any(RtbProfileGroup.class));
    verify(publisherRTBProfileGroupAssembler)
        .apply(any(RtbProfileGroup.class), any(PublisherRTBProfileGroupDTO.class));
    verify(publisherRTBProfileGroupAssembler).make(any(RtbProfileGroup.class));
    verifyNoMoreInteractions(userContext);
    verifyNoMoreInteractions(rtbProfileGroupRepository);
    verifyNoMoreInteractions(publisherRTBProfileGroupAssembler);
  }

  @Test
  void shouldThrowNotFoundExceptionWhenGroupNotFound() {
    // given
    PublisherRTBProfileGroupDTO groupDTO =
        aRTBProfileBuilderGroup(PublisherRTBProfileGroupDTO.ItemType.CATEGORY);
    when(userContext.doSameOrNexageAffiliation(100L)).thenReturn(true);
    when(rtbProfileGroupRepository.findById(123L)).thenReturn(Optional.empty());
    // when
    var ex =
        assertThrows(
            GenevaValidationException.class, () -> rtbProfileGroupService.update(123L, groupDTO));
    // then
    assertEquals(ServerErrorCodes.SERVER_RTB_PROFILE_GROUP_NOT_FOUND, ex.getErrorCode());
    verify(rtbProfileGroupRepository).findById(123L);
    verify(userContext).doSameOrNexageAffiliation(100L);
    verifyNoMoreInteractions(userContext);
    verifyNoMoreInteractions(rtbProfileGroupRepository);
    verifyNoInteractions(publisherRTBProfileGroupAssembler);
  }

  @Test
  void shouldThrowUserNotAuthorizedExceptionWhenUserCannotAccessPublisher() {
    // given
    PublisherRTBProfileGroupDTO groupDTO =
        aRTBProfileBuilderGroup(PublisherRTBProfileGroupDTO.ItemType.CATEGORY);
    when(userContext.doSameOrNexageAffiliation(100L)).thenReturn(false);
    // when
    var ex =
        assertThrows(
            GenevaSecurityException.class, () -> rtbProfileGroupService.update(123L, groupDTO));
    // then
    assertEquals(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED, ex.getErrorCode());
    verify(userContext).doSameOrNexageAffiliation(100L);
    verifyNoMoreInteractions(userContext);
    verifyNoInteractions(rtbProfileGroupRepository);
    verifyNoInteractions(publisherRTBProfileGroupAssembler);
  }

  @Test
  void shouldSeparateGroupsCorrectly() {
    // Given
    PublisherAttributes publisherAttributes =
        PublisherAttributes.newBuilder()
            .withDefaultBlock(Sets.newHashSet(1L, 2L))
            .withDefaultBidderGroups(Sets.newHashSet(3L, 4L))
            .build();
    given(rtbProfileGroupRepository.existsByPidAndIsUICustomGroup(anyLong(), anyBoolean()))
        .willReturn(true)
        .willReturn(false)
        .willReturn(true)
        .willReturn(false);

    // When
    rtbProfileGroupService.separateIndividualsGroups(publisherAttributes);

    // Then
    assertEquals(
        Set.of(1L),
        publisherAttributes.getIndividualsDefaultBlock(),
        "Bidder groups not separated correctly, error in individualsDefaultBlock");
    assertEquals(
        Set.of(4L),
        publisherAttributes.getIndividualsDefaultBidderGroups(),
        "Bidder groups not separated correctly, error in individualsDefaultBidderGroups");
  }

  @Test
  void shouldSeparateGroupsCorrectlyWithNullGroups() {
    // Given
    PublisherAttributes publisherAttributes = PublisherAttributes.newBuilder().build();

    // When
    rtbProfileGroupService.separateIndividualsGroups(publisherAttributes);

    // Then
    assertNull(
        publisherAttributes.getIndividualsDefaultBlock(),
        "Bidder groups not separated correctly, error in individualsDefaultBlock");
    assertNull(
        publisherAttributes.getIndividualsDefaultBidderGroups(),
        "Bidder groups not separated correctly, error in individualsDefaultBidderGroups");
  }

  @Test
  void shouldMergeGroupsCorrectly() {
    // Given
    PublisherAttributes publisherAttributes =
        PublisherAttributes.newBuilder()
            .withDefaultBlock(new HashSet<>())
            .withDefaultBidderGroups(new HashSet<>())
            .withIndividualsDefaultBlock(Sets.newHashSet(1L))
            .withIndividualsDefaultBidderGroups(Sets.newHashSet(2L))
            .build();

    // When
    rtbProfileGroupService.mergeIndividualsGroups(publisherAttributes);

    // Then
    assertEquals(
        Set.of(),
        publisherAttributes.getIndividualsDefaultBlock(),
        "Bidder groups not merged correctly, individualsDefaultBlock not cleared");
    assertEquals(
        Set.of(),
        publisherAttributes.getIndividualsDefaultBidderGroups(),
        "Bidder groups not merged correctly, individualsDefaultBidderGroups not cleared");
    assertEquals(
        Set.of(1L),
        publisherAttributes.getDefaultBlock(),
        "Bidder groups not merged correctly, error in defaultBlock");
    assertEquals(
        Set.of(2L),
        publisherAttributes.getDefaultBidderGroups(),
        "Bidder groups not merged correctly, error in defaultBidderGroups");
  }

  @Test
  void shouldMergeGroupsCorrectlyWithNullIndividualGroups() {
    // Given
    PublisherAttributes publisherAttributes =
        PublisherAttributes.newBuilder()
            .withDefaultBlock(new HashSet<>())
            .withDefaultBidderGroups(new HashSet<>())
            .build();

    // When
    rtbProfileGroupService.mergeIndividualsGroups(publisherAttributes);

    // Then
    assertEquals(
        Set.of(),
        publisherAttributes.getDefaultBlock(),
        "Bidder groups not merged correctly, error in defaultBlock");
    assertEquals(
        Set.of(),
        publisherAttributes.getDefaultBidderGroups(),
        "Bidder groups not merged correctly, error in defaultBidderGroups");
  }

  @Test
  void shouldFailLimitReachedOnCreateForItemTypeBidder() {
    // given
    final var publisherPid = 1L;
    var jointPoint = mock(JoinPoint.class);
    PublisherRTBProfileGroupDTO group =
        aRTBProfileBuilderGroup(PublisherRTBProfileGroupDTO.ItemType.BIDDER);
    when(rtbProfileLibrarySellerLimitService.canCreateBidderGroups(publisherPid)).thenReturn(false);

    // when
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> rtbProfileLimitInterceptor.checkLimitsGroup(jointPoint, publisherPid, group));

    // then
    assertEquals(ServerErrorCodes.SERVER_LIMIT_REACHED, exception.getErrorCode());
  }

  @Test
  void shouldFailLimitReachedOnCreateForItemTypeCategory() {
    // given
    final var publisherPid = 1L;
    var jointPoint = mock(JoinPoint.class);
    PublisherRTBProfileGroupDTO group =
        aRTBProfileBuilderGroup(PublisherRTBProfileGroupDTO.ItemType.CATEGORY);

    // when
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> rtbProfileLimitInterceptor.checkLimitsGroup(jointPoint, publisherPid, group));

    // then
    assertEquals(ServerErrorCodes.SERVER_LIMIT_REACHED, exception.getErrorCode());
  }

  @Test
  void shouldFailLimitReachedOnCreateForItemTypeAdomain() {
    // given
    final var publisherPid = 1L;
    var jointPoint = mock(JoinPoint.class);
    PublisherRTBProfileGroupDTO group =
        aRTBProfileBuilderGroup(PublisherRTBProfileGroupDTO.ItemType.ADOMAIN);

    // when
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> rtbProfileLimitInterceptor.checkLimitsGroup(jointPoint, publisherPid, group));

    // then
    assertEquals(ServerErrorCodes.SERVER_LIMIT_REACHED, exception.getErrorCode());
  }

  private static PublisherRTBProfileGroupDTO aRTBProfileBuilderGroup(
      PublisherRTBProfileGroupDTO.ItemType itemType) {
    return new PublisherRTBProfileGroupDTO(
        0L, "name", 0, "privilegeLevel", "data", itemType.asInt(), 0, 100L, false);
  }
}
