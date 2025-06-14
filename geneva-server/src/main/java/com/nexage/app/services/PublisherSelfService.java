package com.nexage.app.services;

import com.nexage.app.dto.PositionArchiveTransactionDTO;
import com.nexage.app.dto.RtbProfileLibsAndTagsDTO;
import com.nexage.app.dto.SiteUpdateInfoDTO;
import com.nexage.app.dto.publisher.PublisherAdSourceDefaultsDTO;
import com.nexage.app.dto.publisher.PublisherBuyerDTO;
import com.nexage.app.dto.publisher.PublisherDTO;
import com.nexage.app.dto.publisher.PublisherHierarchyDTO;
import com.nexage.app.dto.publisher.PublisherPositionDTO;
import com.nexage.app.dto.publisher.PublisherSiteDTO;
import com.nexage.app.dto.publisher.PublisherTagDTO;
import com.nexage.app.dto.publisher.PublisherTierDTO;
import com.nexage.app.dto.tag.TagArchiveTransactionDTO;
import com.nexage.app.dto.tag.TagDeploymentInfoDTO;
import com.nexage.app.dto.tag.TagPerformanceMetricsDTO;
import com.ssp.geneva.common.base.annotation.Legacy;
import com.ssp.geneva.server.report.performance.pss.model.BiddersPerformanceForPubSelfServe;
import com.ssp.geneva.server.report.performance.pss.model.EstimatedRevenueByAdNetworksForPubSelfServ;
import com.ssp.geneva.server.report.performance.pss.model.EstimatedRevenueByAdvertiserForPubSelfServ;
import com.ssp.geneva.server.report.performance.pss.model.EstimatedRevenueForPubSelfServe;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @deprecated Although the logic associated to Publisher Self-Serve is highly used and active
 *     within the core of the app, we want to avoid developers following same practices put in here.
 *     This is part of the old legacy pss context, our plan is to slowly migrate each business logic
 *     to its own separated self-serve to reduce class complexity and to follow single
 *     responsibility principle.
 */
@Legacy
@Deprecated
public interface PublisherSelfService {

  List<PublisherSiteDTO> getSites(long publisher, boolean detail);

  PublisherSiteDTO getSite(long site, boolean detail);

  PublisherSiteDTO createSite(long publisher, PublisherSiteDTO publisherSite, boolean detail);

  PublisherSiteDTO updateSite(long publisher, PublisherSiteDTO publisherSite, boolean detail);

  SiteUpdateInfoDTO siteUpdateInfo(long publisher, PublisherSiteDTO publisherSite, boolean detail);

  void deleteSite(long publisher, long site);

  /**
   * Fetch positions for a given site PID
   *
   * @param publisherPid publisher PID
   * @param sitePid site PID
   * @return positions pertaining to the site
   */
  List<PublisherPositionDTO> getPositions(long publisherPid, long sitePid, boolean detail);

  /**
   * Fetch a specific position
   *
   * @param publisherPid publisher PID
   * @param sitePid site PID
   * @param positionPid position PID
   * @return requested position
   */
  PublisherPositionDTO getPosition(
      long publisherPid, long sitePid, long positionPid, boolean detail);

  /**
   * Create a new publisher position within a site
   *
   * @param site site PID
   * @param publisherPosition state of the position
   * @return created position, as stored in the DB
   */
  PublisherPositionDTO createPosition(
      long site, PublisherPositionDTO publisherPosition, boolean detail);

  /**
   * Update a publisher position within a site
   *
   * @param publisherPid publisher PID
   * @param sitePid site PID
   * @param publisherPosition new state of the position
   * @return updated position, as stored in the DB
   */
  PublisherPositionDTO updatePosition(
      long publisherPid, long sitePid, PublisherPositionDTO publisherPosition, boolean detail);

  PublisherPositionDTO copyPosition(
      long publisherPid,
      long srcSitePid,
      long srcPositionPid,
      Long targetSitePid,
      PublisherPositionDTO publisherPosition);

  PublisherPositionDTO detailedPosition(long publisher, long site, long position);

  PositionArchiveTransactionDTO getPositionPerformanceMetricsForArchive(
      long publisher, long site, long position, boolean withTransaction);

  PositionArchiveTransactionDTO getPositionPerformanceMetrics(
      long publisher, long site, long position, boolean withTransaction);

  PublisherSiteDTO archivePosition(long publisher, long site, long position, String transactionId);

  List<PublisherTagDTO> getTags(long publisherPid, long sitePid, long positionPid);

  PublisherTagDTO getTag(long publisherPid, long sitePid, long positionPid, long tagPid);

  PublisherTagDTO createTag(long publisher, long site, long position, PublisherTagDTO publisherTag);

  PublisherTagDTO cloneTag(
      long publisher,
      long site,
      long position,
      long tag,
      PublisherTagDTO publisherTag,
      long targetSite,
      long targetPosition);

  PublisherTagDTO updateTag(long publisher, long site, long position, PublisherTagDTO publisherTag);

  List<PublisherTierDTO> getTiers(long publisherPid, long sitePid, long positionPid);

  PublisherTierDTO getTier(long publisherPid, long sitePid, long positionPid, long tierPid);

  PublisherTierDTO createTier(
      Long publisher, long site, long position, PublisherTierDTO publisherTier);

  PublisherTierDTO updateTier(
      long publisher, long site, long position, PublisherTierDTO publisherTier);

  void deleteTier(Long companyPid, long site, long position, long tier);

  List<PublisherBuyerDTO> getBuyers(long publisher, String search);

  TagDeploymentInfoDTO getTagDeploymentInfo(long publisherPid, long sitePid, long tagPid);

  void undeployTag(long publisherPid, long sitePid, long positionPid, long tagPid);

  // RTB Profile Libraries

  Set<PublisherHierarchyDTO> getTagHierachy(long publisher, long rtbprofilegroup);

  TagArchiveTransactionDTO getTagPerformanceMetrics(
      long publisher, long site, long position, long tag, boolean withTransaction);

  PublisherPositionDTO archiveTag(
      long publisher, long site, long position, long tag, String transactionId);

  List<PublisherAdSourceDefaultsDTO> getAllAdsourceDefaultsForSeller(long publisher);

  PublisherAdSourceDefaultsDTO getAdsourceDefaultsForSeller(long publisher, long adsourceId);

  void deleteAdsourceDefaultsForSeller(long publisher, long adsourceId);

  PublisherAdSourceDefaultsDTO createAdsourceDefaultsForSeller(
      long publisher, long adsourceId, PublisherAdSourceDefaultsDTO defaults);

  PublisherAdSourceDefaultsDTO updateAdsourceDefaultsForSeller(
      long publisher, long adsourceId, PublisherAdSourceDefaultsDTO defaults);

  List<PublisherTagDTO> getPubAdsourceTags(long publisherId, long adsourceId);

  List<PublisherTagDTO> updatePubAdsourceTags(
      long publisherId, long adsourceId, List<PublisherTagDTO> tags);

  List<TagPerformanceMetricsDTO> getPubAdsourceTagPerformanceMetrics(
      long publisherId, long adsourceId);

  PublisherDTO getPublisher(long id);

  PublisherDTO updatePublisher(long publisherId, PublisherDTO inPublisher);

  Collection<PublisherAdSourceDefaultsDTO> getAvailableAdsources(long publisher);

  List<BiddersPerformanceForPubSelfServe> getBiddersPerformanceForPSS(
      long publisher, Date start, Date stop);

  EstimatedRevenueForPubSelfServe getEstimatedRevenue(long publisher, String start, String stop);

  EstimatedRevenueByAdNetworksForPubSelfServ getEstimatedRevenueByAdNetworks(
      long publisher, String start, String stop);

  EstimatedRevenueByAdvertiserForPubSelfServ getEstimatedRevenueByAdvertiser(
      long publisher, String start, String stop);

  void validateTag(
      long publisherPid,
      long adsourceId,
      String primaryId,
      String primaryName,
      String secondaryId,
      String secondaryName);

  void updateRTBProfileLibToRTBProfilesMap(
      long publisher, RtbProfileLibsAndTagsDTO rtbprofileLibAndTagList);

  PublisherTagDTO getDecisionMaker(long publisher, long site, long position);

  PublisherTagDTO createDecisionMaker(
      long publisher, long site, long position, PublisherTagDTO publisherTag);

  PublisherTagDTO updateDecisionMaker(
      long publisher, long site, long position, PublisherTagDTO publisherTag);

  List<PublisherTagDTO> generateSmartYieldDemandSourceTags(
      long publisher, long site, long position, List<PublisherTagDTO> publisherTags);
}
