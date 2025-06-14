package com.nexage.app.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.model.SellerAttributes;
import com.nexage.admin.core.repository.CampaignRepository;
import com.nexage.admin.core.repository.CreativeRepository;
import com.nexage.admin.core.repository.PositionRepository;
import com.nexage.admin.core.repository.SellerAttributesRepository;
import com.nexage.admin.core.repository.SiteRepository;
import com.nexage.admin.core.repository.TagRepository;
import com.nexage.admin.core.repository.UserRepository;
import com.nexage.app.services.CampaignCreativeService;
import com.nexage.app.services.LimitService;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class SellerLimitServiceImplTest {

  private static final long PUBLISHER_PID = 123L;
  private static final long SITE_PID = 456L;
  private static final long POSITION_PID = 789L;
  private static final long CAMPAIGN_PID = 101112L;

  @Mock UserRepository userRepository;
  @Mock SellerAttributesRepository sellerAttributesRepository;
  @Mock CampaignRepository campaignRepository;
  @Mock CampaignCreativeService campaignCreativeService;
  @Mock LimitService limitService;
  @Mock SiteRepository siteRepository;
  @Mock PositionRepository positionRepository;
  @Mock TagRepository tagRepository;
  @Mock CreativeRepository creativeRepository;

  @InjectMocks SellerLimitServiceImpl sellerLimitService;

  @Test
  void shouldCheckSpecificSiteLimitCorrectly() {
    // given
    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerAttributes.setSiteLimit(3);
    given(sellerAttributesRepository.findById(PUBLISHER_PID))
        .willReturn(Optional.of(sellerAttributes));
    given(siteRepository.countByCompanyPidAndStatusNot(PUBLISHER_PID, Status.DELETED))
        .willReturn(2L);

    // when & then
    assertEquals(1, sellerLimitService.checkSitesLimit(PUBLISHER_PID));
  }

  @Test
  void shouldCheckGlobalSiteLimitCorrectly() {
    // given
    given(limitService.getGlobalSiteLimit()).willReturn(4);
    given(siteRepository.countByCompanyPidAndStatusNot(PUBLISHER_PID, Status.DELETED))
        .willReturn(2L);

    // when & then
    assertEquals(2, sellerLimitService.checkSitesLimit(PUBLISHER_PID));
  }

  @Test
  void shouldCheckSpecificPositionsInSiteLimitCorrectly() {
    // given
    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerAttributes.setPositionsPerSiteLimit(3);
    given(sellerAttributesRepository.findById(PUBLISHER_PID))
        .willReturn(Optional.of(sellerAttributes));
    given(positionRepository.countBySitePidAndStatusNot(SITE_PID, Status.DELETED)).willReturn(2L);

    // when & then
    assertEquals(1, sellerLimitService.checkPositionsInSiteLimit(PUBLISHER_PID, SITE_PID));
  }

  @Test
  void shouldCheckGlobalPositionsInSiteLimitCorrectly() {
    // given
    given(limitService.getGlobalPositionsPerSiteLimit()).willReturn(4);
    given(positionRepository.countBySitePidAndStatusNot(SITE_PID, Status.DELETED)).willReturn(2L);

    // when & then
    assertEquals(2, sellerLimitService.checkPositionsInSiteLimit(PUBLISHER_PID, SITE_PID));
  }

  @Test
  void shouldCheckSpecificTagsInPositionLimitCorrectly() {
    // given
    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerAttributes.setTagsPerPositionLimit(3);
    given(sellerAttributesRepository.findById(PUBLISHER_PID))
        .willReturn(Optional.of(sellerAttributes));
    given(
            tagRepository.countBySitePidAndPositionPidAndStatusNot(
                SITE_PID, POSITION_PID, Status.DELETED))
        .willReturn(2L);

    // when & then
    assertEquals(
        1, sellerLimitService.checkTagsInPositionLimit(PUBLISHER_PID, SITE_PID, POSITION_PID));
  }

  @Test
  void shouldCheckGlobalTagsInPositionLimitCorrectly() {
    // given
    given(limitService.getGlobalTagsPerPositionLimit()).willReturn(4);
    given(
            tagRepository.countBySitePidAndPositionPidAndStatusNot(
                SITE_PID, POSITION_PID, Status.DELETED))
        .willReturn(2L);

    // when & then
    assertEquals(
        2, sellerLimitService.checkTagsInPositionLimit(PUBLISHER_PID, SITE_PID, POSITION_PID));
  }

  @Test
  void shouldCheckSpecificCampaignLimitCorrectly() {
    // given
    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerAttributes.setCampaignsLimit(2);
    given(sellerAttributesRepository.findById(PUBLISHER_PID))
        .willReturn(Optional.of(sellerAttributes));
    given(campaignRepository.count(any(Specification.class))).willReturn(2L);

    // when & then
    assertEquals(0, sellerLimitService.checkCampaignsLimit(PUBLISHER_PID));
  }

  @Test
  void shouldCheckGlobalCampaignLimitCorrectly() {
    // given
    given(limitService.getGlobalCampaignsLimit()).willReturn(4);
    given(campaignRepository.count(any(Specification.class))).willReturn(2L);

    // when & then
    assertEquals(2, sellerLimitService.checkCampaignsLimit(PUBLISHER_PID));
  }

  @Test
  void shouldCheckSpecificCreativesPerCampaignLimitCorrectly() {
    // given
    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerAttributes.setCreativesPerCampaignLimit(2);
    given(sellerAttributesRepository.findById(PUBLISHER_PID))
        .willReturn(Optional.of(sellerAttributes));
    given(creativeRepository.countAllNonDeletedByCampaignPid(CAMPAIGN_PID)).willReturn(2L);
    given(campaignRepository.existsByPidAndSellerId(CAMPAIGN_PID, PUBLISHER_PID)).willReturn(true);

    // when & then
    assertEquals(0, sellerLimitService.checkCreativesInCampaignLimit(PUBLISHER_PID, CAMPAIGN_PID));
  }

  @Test
  void shouldCheckGlobalCreativesPerCampaignLimitCorrectly() {
    // given
    given(limitService.getGlobalCreativesPerCampaignsLimit()).willReturn(3);
    given(creativeRepository.countAllNonDeletedByCampaignPid(CAMPAIGN_PID)).willReturn(2L);
    given(campaignRepository.existsByPidAndSellerId(CAMPAIGN_PID, PUBLISHER_PID)).willReturn(true);

    // when & then
    assertEquals(1, sellerLimitService.checkCreativesInCampaignLimit(PUBLISHER_PID, CAMPAIGN_PID));
  }

  @Test
  void shouldThrowOnCheckCreativesPerCampaignLimitWhenCampaignDoesNotExistForPublisher() {
    // when & then
    assertThrows(
        GenevaValidationException.class,
        () -> sellerLimitService.checkCreativesInCampaignLimit(PUBLISHER_PID, CAMPAIGN_PID));
  }

  @Test
  void shouldCheckSpecificUsersLimitCorrectly() {
    // given
    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerAttributes.setUserLimit(2);
    given(sellerAttributesRepository.findById(PUBLISHER_PID))
        .willReturn(Optional.of(sellerAttributes));
    given(userRepository.count(any(Specification.class))).willReturn(1L);

    // when & then
    assertEquals(1, sellerLimitService.checkUsersLimit(PUBLISHER_PID));
  }

  @Test
  void shouldCheckGlobalUsersLimitCorrectly() {
    // given
    given(limitService.getGlobalUsersLimit()).willReturn(3);
    given(userRepository.count(any(Specification.class))).willReturn(1L);

    // when & then
    assertEquals(2, sellerLimitService.checkUsersLimit(PUBLISHER_PID));
  }

  @Test
  void shouldAllowAllBooleanActionsWhenLimitIsNotEnabled() {
    // given
    long publisher = 1L;
    long campaignPid = 2L;
    long site = 3L;
    long position = 4L;
    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerAttributes.setLimitEnabled(false);
    given(sellerAttributesRepository.findById(publisher)).willReturn(Optional.of(sellerAttributes));

    // when & then
    assertTrue(sellerLimitService.canCreateCampaigns(publisher));
    assertTrue(sellerLimitService.canCreateCreativesInCampaign(publisher, campaignPid));
    assertTrue(sellerLimitService.canCreatePositionsInSite(publisher, site));
    assertTrue(sellerLimitService.canCreateSites(publisher));
    assertTrue(sellerLimitService.canCreateTagsInPosition(publisher, site, position));
    assertTrue(sellerLimitService.canCreateUsers(publisher));
    assertFalse(sellerLimitService.isLimitEnabled(publisher));
  }
}
