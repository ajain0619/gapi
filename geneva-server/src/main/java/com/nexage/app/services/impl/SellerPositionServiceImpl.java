package com.nexage.app.services.impl;

import com.nexage.admin.core.enums.PlacementCategory;
import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.model.Position;
import com.nexage.admin.core.model.RTBProfile;
import com.nexage.admin.core.model.Site;
import com.nexage.admin.core.model.Tag;
import com.nexage.admin.core.model.Tier;
import com.nexage.admin.core.repository.PositionRepository;
import com.nexage.admin.core.repository.RTBProfileRepository;
import com.nexage.admin.core.repository.SiteRepository;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.services.CampaignService;
import com.nexage.app.services.SellerPositionService;
import com.nexage.app.services.SellerSiteService;
import com.nexage.app.util.PositionValidator;
import com.nexage.app.util.RTBProfileUtil;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@Transactional
@PreAuthorize("@loginUserContext.isOcUserNexage() or @loginUserContext.isOcManagerSeller()")
public class SellerPositionServiceImpl implements SellerPositionService {

  private final SiteRepository siteRepository;
  private final PositionRepository positionRepository;
  private final CampaignService campaignService;
  private final RTBProfileRepository rtbProfileRepository;
  private final SellerSiteService sellerSiteService;
  private final PositionValidator positionValidator;
  private final RTBProfileUtil rtbProfileUtil;

  public SellerPositionServiceImpl(
      SiteRepository siteRepository,
      PositionRepository positionRepository,
      CampaignService campaignService,
      RTBProfileRepository rtbProfileRepository,
      SellerSiteService sellerSiteService,
      PositionValidator positionValidator,
      RTBProfileUtil rtbProfileUtil) {
    this.siteRepository = siteRepository;
    this.positionRepository = positionRepository;
    this.campaignService = campaignService;
    this.rtbProfileRepository = rtbProfileRepository;
    this.sellerSiteService = sellerSiteService;
    this.positionValidator = positionValidator;
    this.rtbProfileUtil = rtbProfileUtil;
  }

  /** {@inheritDoc} */
  @Override
  @PostAuthorize(
      "@loginUserContext.isOcAdminNexage() or @loginUserContext.isOcManagerNexage() "
          + "or ((@loginUserContext.isOcAdminSeller() or @loginUserContext.isOcManagerSeller()) "
          + "and (returnObject != null && returnObject.site != null and @loginUserContext.doSameOrNexageAffiliation(returnObject.site.companyPid)) )")
  public Position getPosition(Long pid) {
    return positionRepository
        .findById(pid)
        .orElseThrow(
            () -> new GenevaValidationException(ServerErrorCodes.SERVER_POSITION_NOT_EXISTS));
  }

  /** {@inheritDoc} */
  @Override
  @Transactional
  @PreAuthorize(
      "@loginUserContext.isOcAdminNexage() or @loginUserContext.isOcManagerNexage() "
          + "or (@loginUserContext.canAccessSite(#sitePid) "
          + "and (@loginUserContext.isOcAdminSeller() or @loginUserContext.isOcManagerSeller() or @loginUserContext.isOcApiSeller()) )")
  public Site createPosition(Long sitePid, Position position) {
    if (position.getSitePid() != null && !sitePid.equals(position.getSitePid())) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_REQUEST_PARAM_BODY_NOT_MATCH);
    }

    Site site = sellerSiteService.getSite(sitePid);
    position.setSite(site);

    positionValidator.validatePosition(position);

    if (position.getDefaultRtbProfile() != null) {
      position.setDefaultRtbProfile(
          rtbProfileUtil.prepareDefaultRtbProfile(position.getDefaultRtbProfile()));
    }

    PlacementCategory pc = position.getPlacementCategory();
    if ((pc.equals(PlacementCategory.BANNER) || pc.equals(PlacementCategory.MEDIUM_RECTANGLE))
        && position.isMraidAdvancedTracking()) {
      log.warn(
          "mraidAdvancedTracking is not allowed to be enabled for this placement category when creating new position for site : {} ",
          sitePid);

      position.setMraidAdvancedTracking(false);
    }

    site.addPosition(position);
    positionRepository.save(position);
    return siteRepository.save(site);
  }

  /** {@inheritDoc} */
  @Override
  @Transactional
  @PreAuthorize(
      "@loginUserContext.isOcAdminNexage() or @loginUserContext.isOcManagerNexage() "
          + "or (@loginUserContext.canAccessSite(#position.getSitePid()) "
          + "and (@loginUserContext.isOcAdminSeller() or @loginUserContext.isOcManagerSeller() or @loginUserContext.isOcApiSeller()) )")
  public Site updatePosition(Position position) {
    positionValidator.validatePosition(position);

    if (position.getDefaultRtbProfile() != null) {
      position.setDefaultRtbProfile(
          rtbProfileUtil.prepareDefaultRtbProfile(position.getDefaultRtbProfile()));
    }

    Site site = sellerSiteService.getSite(position.getSitePid());

    boolean matchFound = site.getPositions().removeIf(p -> position.getPid().equals(p.getPid()));

    if (!matchFound) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_POSITION_NOT_EXISTS);
    }

    // add the updated position
    site.getPositions().add(position);
    position.setSite(site);
    sellerSiteService.addUpdatedTierIfTagIsPresentProxy(position);
    return siteRepository.save(site);
  }

  /** {@inheritDoc} */
  @Override
  @Transactional
  @PreAuthorize(
      "@loginUserContext.isOcAdminNexage() or @loginUserContext.isOcManagerNexage() "
          + "or (@loginUserContext.canAccessSite(#sitePid) "
          + "and (@loginUserContext.isOcAdminSeller() or @loginUserContext.isOcManagerSeller() or @loginUserContext.isOcApiSeller()) )")
  public Site deletePosition(Long sitePid, Long positionPid) {
    Site site = sellerSiteService.getSite(sitePid);
    site.getTags();

    boolean matchFound = site.getPositions().removeIf(p -> p.getPid().longValue() == positionPid);

    if (!matchFound) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_POSITION_NOT_EXISTS);
    }
    return siteRepository.save(site);
  }

  /** {@inheritDoc} */
  @Override
  @Transactional
  @PreAuthorize(
      "(@loginUserContext.isOcManagerNexage() or @loginUserContext.isOcUserSeller()) and @loginUserContext.canAccessSite(#site.pid)")
  public Site archivePosition(Site site, Long positionPid) {
    Date now = Calendar.getInstance().getTime();

    // Position to archive
    Position positionBeingArchived = getPositionToArchive(site, positionPid, now);

    // Archive tags directly owned by the position being archived through
    // tag.position_pid (may or may not be deployed)
    archiveTagsDirectlyOwnedByPosition(site, positionPid, now);

    // Undeploy all tags from position being archived and remove all tiers
    undeployAllTagsFromPositionAndRemoveAllTiers(positionBeingArchived);

    // Undeploy archived tags from all other positions
    undeployArchivedTagsFromAllOtherPositions(site, positionPid, now);

    campaignService.removePositionFromCampaignTargets(
        site.getPid(), positionBeingArchived.getName());

    site.setLastUpdate(now);
    return siteRepository.saveAndFlush(site);
  }

  /** {@inheritDoc} */
  @PreAuthorize("@loginUserContext.isOcManagerNexage()")
  public void assignRTBProfileToPosition(
      Long positionPid, Long rtbProfilePid, Long ownerRTBProfilePid) {
    Position position = getPosition(positionPid);
    RTBProfile rtbProfile = null;
    if (rtbProfilePid != null) {
      rtbProfile =
          rtbProfileRepository
              .findByDefaultRtbProfileOwnerCompanyPidAndPid(ownerRTBProfilePid, rtbProfilePid)
              .orElse(null);
    }
    position.setDefaultRtbProfile(rtbProfile);
    positionRepository.saveAndFlush(position);
  }

  private Position getPositionToArchive(Site site, Long positionPid, Date now) {
    Position positionBeingArchived = null;
    for (Position position : site.getPositions()) {
      if (position.getPid().equals(positionPid)) {
        position.setStatus(Status.DELETED);
        position.setUpdatedOn(now);
        positionBeingArchived = position;
        break;
      }
    }
    if (null == positionBeingArchived) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_POSITION_NOT_FOUND_IN_SITE);
    }

    return positionBeingArchived;
  }

  private void archiveTagsDirectlyOwnedByPosition(Site site, Long positionPid, Date now) {
    for (Tag tag : site.getTags()) {
      if (tag.getPosition() != null && tag.getPosition().getPid().equals(positionPid)) {
        if (tag.getStatus() != Status.DELETED && (tag.isExchangeTag() && tag.getPid() > 0L)) {
          rtbProfileUtil.updateRTBProfileForTag(tag.getPid());
        } else {
          tag.setStatus(Status.DELETED);
          tag.setUpdatedOn(now);
        }
      }
    }
  }

  private void undeployAllTagsFromPositionAndRemoveAllTiers(Position positionBeingArchived) {
    for (Iterator<Tier> tierIterator = positionBeingArchived.getTiers().iterator();
        tierIterator.hasNext(); ) {
      Tier tier = tierIterator.next();
      List<Tag> tagsToUndeploy = new ArrayList<>();
      tagsToUndeploy.addAll(tier.getTags());
      for (Tag tag : tagsToUndeploy) {
        tier.removeTag(tag);
      }
      tierIterator.remove();
    }
  }

  private void undeployArchivedTagsFromAllOtherPositions(Site site, Long positionPid, Date now) {
    for (Position position : site.getPositions()) {
      for (Iterator<Tier> tierIterator = position.getTiers().iterator(); tierIterator.hasNext(); ) {
        Tier tier = tierIterator.next();
        List<Tag> tagsToUndeploy = new ArrayList<>();
        for (Tag tag : tier.getTags()) {
          if (tag.getPosition() != null && tag.getPosition().getPid().equals(positionPid)) {
            tagsToUndeploy.add(tag);
          }
        }
        removeTagsAndTiers(tagsToUndeploy, tier, tierIterator, position, now);
      }
    }
  }

  private void removeTagsAndTiers(
      List<Tag> tagsToUndeploy,
      Tier tier,
      Iterator<Tier> tierIterator,
      Position position,
      Date now) {

    for (Tag tag : tagsToUndeploy) {
      tier.removeTag(tag);
    }
    if (tier.getTags() == null || tier.getTags().isEmpty()) {
      // No tags left in tier.
      tierIterator.remove();
      position.renumberTiers();
    }
    if (!tagsToUndeploy.isEmpty()) {
      position.setUpdatedOn(now);
    }
  }
}
