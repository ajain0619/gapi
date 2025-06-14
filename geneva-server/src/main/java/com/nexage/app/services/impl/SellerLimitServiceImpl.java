package com.nexage.app.services.impl;

import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.model.SellerAttributes;
import com.nexage.admin.core.repository.CampaignRepository;
import com.nexage.admin.core.repository.CreativeRepository;
import com.nexage.admin.core.repository.PositionRepository;
import com.nexage.admin.core.repository.SellerAttributesRepository;
import com.nexage.admin.core.repository.SiteRepository;
import com.nexage.admin.core.repository.TagRepository;
import com.nexage.admin.core.repository.UserRepository;
import com.nexage.admin.core.specification.CampaignSpecification;
import com.nexage.admin.core.specification.UserSpecification;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.services.LimitService;
import com.nexage.app.services.SellerLimitService;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@Qualifier("sellerLimitServiceImpl")
public class SellerLimitServiceImpl implements SellerLimitService {

  private final UserRepository userRepository;
  private final LimitService limitService;
  private final SellerAttributesRepository sellerAttributesRepository;
  private final CampaignRepository campaignRepository;
  private final SiteRepository siteRepository;
  private final PositionRepository positionRepository;
  private final TagRepository tagRepository;
  private final CreativeRepository creativeRepository;

  @Autowired
  public SellerLimitServiceImpl(
      UserRepository userRepository,
      LimitService limitService,
      SellerAttributesRepository sellerAttributesRepository,
      CampaignRepository campaignRepository,
      SiteRepository siteRepository,
      PositionRepository positionRepository,
      TagRepository tagRepository,
      CreativeRepository creativeRepository) {
    this.userRepository = userRepository;
    this.limitService = limitService;
    this.sellerAttributesRepository = sellerAttributesRepository;
    this.campaignRepository = campaignRepository;
    this.siteRepository = siteRepository;
    this.positionRepository = positionRepository;
    this.tagRepository = tagRepository;
    this.creativeRepository = creativeRepository;
  }

  @Override
  public boolean canCreateSites(long publisher) {
    return !isLimitEnabled(publisher) || checkSitesLimit(publisher) > 0;
  }

  @Override
  public boolean canCreatePositionsInSite(long publisher, long site) {
    return !isLimitEnabled(publisher) || checkPositionsInSiteLimit(publisher, site) > 0;
  }

  @Override
  public boolean canCreateTagsInPosition(long publisher, long site, long position) {
    return !isLimitEnabled(publisher) || checkTagsInPositionLimit(publisher, site, position) > 0;
  }

  @Override
  public boolean canCreateCampaigns(long publisher) {
    return !isLimitEnabled(publisher) || checkCampaignsLimit(publisher) > 0;
  }

  @Override
  public boolean canCreateCreativesInCampaign(long publisher, long campaignPid) {
    return !isLimitEnabled(publisher) || checkCreativesInCampaignLimit(publisher, campaignPid) > 0;
  }

  @Override
  public boolean canCreateUsers(long publisher) {
    return !isLimitEnabled(publisher) || checkUsersLimit(publisher) > 0;
  }

  @Override
  @PreAuthorize(
      "(@loginUserContext.isOcUserNexage() or @loginUserContext.isOcUserSeller() or @loginUserContext.isOcApiSeller())"
          + " and @loginUserContext.isPublisherSelfServeEnabled(#sellerPid)")
  public int checkSitesLimit(long sellerPid) {
    int sites = (int) siteRepository.countByCompanyPidAndStatusNot(sellerPid, Status.DELETED);
    int limit = getPubSiteLimit(sellerPid).orElseGet(limitService::getGlobalSiteLimit);

    return limit - sites;
  }

  @Override
  @PreAuthorize(
      "(@loginUserContext.isOcUserNexage() or @loginUserContext.isOcUserSeller() or @loginUserContext.isOcApiSeller())"
          + " and @loginUserContext.isPublisherSelfServeEnabled(#sellerPid)"
          + " and @loginUserContext.canAccessSite(#sitePid)")
  public int checkPositionsInSiteLimit(long sellerPid, long sitePid) {
    int positions = (int) positionRepository.countBySitePidAndStatusNot(sitePid, Status.DELETED);
    int limit =
        getPubPositionsPerSiteLimit(sellerPid)
            .orElseGet(limitService::getGlobalPositionsPerSiteLimit);

    return limit - positions;
  }

  @Override
  @PreAuthorize(
      "(@loginUserContext.isOcUserNexage() or @loginUserContext.isOcUserSeller() or @loginUserContext.isOcApiSeller())"
          + " and @loginUserContext.isPublisherSelfServeEnabled(#sellerPid)"
          + " and @loginUserContext.canAccessPlacement(#positionPid)")
  public int checkTagsInPositionLimit(long sellerPid, long sitePid, long positionPid) {
    int tags =
        (int)
            tagRepository.countBySitePidAndPositionPidAndStatusNot(
                sitePid, positionPid, Status.DELETED);
    int limit =
        getPubTagsPerPositionLimit(sellerPid)
            .orElseGet(limitService::getGlobalTagsPerPositionLimit);

    return limit - tags;
  }

  @Override
  @PreAuthorize(
      "(@loginUserContext.isOcUserNexage() or @loginUserContext.isOcUserSeller() or @loginUserContext.isOcApiSeller())"
          + " and @loginUserContext.isPublisherSelfServeEnabled(#sellerPid)")
  public int checkCampaignsLimit(long sellerPid) {
    int campaigns =
        (int)
            campaignRepository.count(
                CampaignSpecification.hasCompanyPid(sellerPid)
                    .and(CampaignSpecification.isNotDeleted()));
    int limit = getPubCampaignsLimit(sellerPid).orElseGet(limitService::getGlobalCampaignsLimit);

    return limit - campaigns;
  }

  @Override
  @PreAuthorize(
      "(@loginUserContext.isOcUserNexage() or @loginUserContext.isOcUserSeller() or @loginUserContext.isOcApiSeller())"
          + " and @loginUserContext.isPublisherSelfServeEnabled(#sellerPid)")
  public int checkCreativesInCampaignLimit(long sellerPid, long campaignPid) {
    validateCampaignForSeller(campaignPid, sellerPid);

    int creatives = (int) creativeRepository.countAllNonDeletedByCampaignPid(campaignPid);
    int limit =
        getPubCreativesPerCampaignsLimit(sellerPid)
            .orElseGet(limitService::getGlobalCreativesPerCampaignsLimit);

    return limit - creatives;
  }

  @Override
  @PreAuthorize(
      "(@loginUserContext.isOcUserNexage() or @loginUserContext.isOcUserSeller() or @loginUserContext.isOcApiSeller())"
          + " and @loginUserContext.isPublisherSelfServeEnabled(#sellerPid)")
  public int checkUsersLimit(long sellerPid) {
    int users =
        (int)
            userRepository.count(
                UserSpecification.withCompany(sellerPid).and(UserSpecification.isEnabled()));
    int limit = getPubUsersLimit(sellerPid).orElseGet(limitService::getGlobalUsersLimit);

    return limit - users;
  }

  private Optional<Integer> getPubSiteLimit(long sellerPid) {
    return sellerAttributesRepository.findById(sellerPid).map(SellerAttributes::getSiteLimit);
  }

  private Optional<Integer> getPubPositionsPerSiteLimit(long sellerPid) {
    return sellerAttributesRepository
        .findById(sellerPid)
        .map(SellerAttributes::getPositionsPerSiteLimit);
  }

  private Optional<Integer> getPubTagsPerPositionLimit(long sellerPid) {
    return sellerAttributesRepository
        .findById(sellerPid)
        .map(SellerAttributes::getTagsPerPositionLimit);
  }

  private Optional<Integer> getPubCampaignsLimit(long sellerPid) {
    return sellerAttributesRepository.findById(sellerPid).map(SellerAttributes::getCampaignsLimit);
  }

  private Optional<Integer> getPubCreativesPerCampaignsLimit(long sellerPid) {
    return sellerAttributesRepository
        .findById(sellerPid)
        .map(SellerAttributes::getCreativesPerCampaignLimit);
  }

  private Optional<Integer> getPubUsersLimit(long sellerPid) {
    return sellerAttributesRepository.findById(sellerPid).map(SellerAttributes::getUserLimit);
  }

  private void validateCampaignForSeller(long campaignPid, long sellerPid) {
    if (!campaignRepository.existsByPidAndSellerId(campaignPid, sellerPid)) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_CAMPAIGN_FOR_SELLER);
    }
  }

  @Override
  public boolean isLimitEnabled(long sellerPid) {
    return sellerAttributesRepository
        .findById(sellerPid)
        .map(SellerAttributes::isLimitEnabled)
        .orElse(true);
  }
}
