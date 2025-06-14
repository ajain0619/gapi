package com.nexage.app.services;

import com.nexage.admin.core.dto.SiteSummaryDTO;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.Position;
import com.nexage.admin.core.model.RTBProfile;
import com.nexage.admin.core.model.Site;
import com.nexage.admin.core.model.Tag;
import com.nexage.admin.core.model.Tier;
import com.nexage.admin.core.sparta.jpa.model.SiteDealTerm;
import com.nexage.app.dto.SiteDealTermSummaryDTO;
import com.nexage.app.dto.SiteUpdateInfoDTO;
import com.nexage.app.dto.tag.TagUpdateInfoDTO;
import java.util.List;
import java.util.Set;

/**
 * Methods used in reference to Seller's sites.
 *
 * <p>NOTE: Please do not add any new functionality to this class or its implementations. The
 * business concepts associated to the different operations handled by the following functions use
 * strategies, concepts, or frameworks considered deprecated. Be sure you reach to the core team
 * before considering designing or implementing new functionality under this class.
 */
public interface SellerSiteService {

  /**
   * Gets the site based on the {@link Long} pid passed in.
   *
   * @param pid {@link Long} passed in that correlates to a Site's Pid.
   * @return The {@link Site} that has a matching Pid.
   */
  Site getSite(Long pid);

  /**
   * Retrieves {@link Site} by PID and validates it against the expected publisher PID
   *
   * @param sitePid site PID
   * @param publisherPid publisher PID
   * @return The {@link Site} that has a matching site PID and publisher PID.
   */
  Site getValidatedSiteForPublisher(long sitePid, long publisherPid);

  /**
   * Gets all {@link SiteSummaryDTO}'s.
   *
   * @return The {@link List} of {@link SiteSummaryDTO}.
   */
  List<SiteSummaryDTO> getAllSitesSummary();

  /**
   * Gets all {@link SiteDealTermSummaryDTO} for a given Seller.
   *
   * @param sellerPid The {@link Long} seller Pid to retrieve the {@link SiteDealTermSummaryDTO}
   *     for.
   * @return A {@link List} of {@link SiteDealTermSummaryDTO} for the seller that matches the {@link
   *     Long} Pid passed in.
   */
  List<SiteDealTermSummaryDTO> getAllSiteDealTerms(Long sellerPid);

  /**
   * Gets a {@link List} of {@link SiteSummaryDTO} based on company Pid.
   *
   * @param companyPid The {@link Long} that corresponds to a company Pid.
   * @return The {@link List} of {@link SiteSummaryDTO} for the company that matches the {@link
   *     Long} passed in.
   */
  List<SiteSummaryDTO> getAllSitesSummaryByCompanyPid(Long companyPid);

  /**
   * Updates the {@link SiteDealTerm}s to Publisher Default
   *
   * @param sellerPid The {@link Long} that correlates to a seller.
   * @param sitePids The {@link List} of {@link Long}s that correlate to sites to be updated.
   * @return The {@link List} of {@link SiteDealTermSummaryDTO}s after the {@link SiteDealTerm}s
   *     have been updated.
   */
  List<SiteDealTermSummaryDTO> updateSiteDealTermsToPubDefault(Long sellerPid, List<Long> sitePids);

  /**
   * Updates the {@link SiteDealTerm}s to Publisher default only when a user has the role of Nexage
   * Yield Manager
   *
   * @param sellerPid The {@link Long} that correlates to a seller.
   * @param sitePids The {@link List} of {@link Long}s that correlate to the sites associated with
   *     the seller to be updated.
   * @return The {@link List} of {@link SiteDealTermSummaryDTO}s after the {@link SiteDealTerm}s
   *     have been updated.
   */
  List<SiteDealTermSummaryDTO> updateSiteDealTermsToPubDefaultByYieldManager(
      Long sellerPid, List<Long> sitePids);

  /**
   * Gets the allowed sites based on the {@link Long} user Pid passed in.
   *
   * @param userPid The {@link Long} passed in.
   * @return A {@link List} of {@link SiteSummaryDTO} allowed for that user.
   */
  List<SiteSummaryDTO> getAllowedSitesForUser(Long userPid);

  /**
   * Processes the Update Request for the {@link Site} passed in.
   *
   * @param toBeUpdated {@link Site} that is being updated.
   * @return The {@link SiteUpdateInfoDTO} for the updated {@link Site}.
   */
  SiteUpdateInfoDTO processUpdateSiteRequest(Site toBeUpdated);

  /**
   * Performs update operations on the {@link Site} passed in.
   *
   * @param toBeUpdated The {@link Site} that is to be updated.
   * @return A {@link Set} of {@link TagUpdateInfoDTO}.
   */
  Set<TagUpdateInfoDTO> performUpdateSiteOperations(Site toBeUpdated);

  /**
   * Creates a {@link Site} for the {@link Company} that matches the Pid passed in.
   *
   * @param companyPid The {@link Long} Pid that correlates to a {@link Company}.
   * @param site The {@link Site} that is to be created under said {@link Company}.
   * @return The newly created {@link Site}.
   */
  Site createSite(Long companyPid, Site site);

  void validateSiteNameUniqueness(Long sitePid, Long companyPid, String siteName);

  /**
   * Deletes the {@link Site} that matches the Pid passed in.
   *
   * @param sitePid the {@link Long} that correlates to a {@link Site} to be deleted.
   */
  void deleteSite(Long sitePid);

  /**
   * Updates the {@link Site}
   *
   * @param site The {@link Site} with updated information to be added.
   * @return The updated {@link Site}.
   */
  Site updateSite(Site site);

  /**
   * Calculates a HashCode based on a {@link Set} of {@link TagUpdateInfoDTO}.
   *
   * @param tagUpdateInfo The {@link Set} of {@link TagUpdateInfoDTO} that the HashCode will be
   *     calculated from.
   * @return The HashCode as a {@link String}.
   */
  String calcHashForSiteUpdate(Set<TagUpdateInfoDTO> tagUpdateInfo);

  /**
   * Gets all {@link Site}s by the company pid passed in.
   *
   * @param companyId The {@link Long} that correlates to a {@link Company}.
   * @return A {@link List} of {@link Site}s that belong to that {@link Company}.
   */
  List<Site> getAllSitesByCompanyPid(Long companyId);

  /**
   * Assigns {@link RTBProfile} to {@link Site}.
   *
   * @param sitePid The Pid that correlates to the {@link Site} to add the {@link RTBProfile} to.
   * @param rtbProfilePid The Pid of the {@link RTBProfile} that's being added to the {@link Site}.
   * @param ownerRTBProfilePid The pid for the Owner of the {@link RTBProfile}.
   */
  void assignRTBProfileToSite(Long sitePid, Long rtbProfilePid, Long ownerRTBProfilePid);

  /**
   * Adjusts the Tag Reserves for a {@link Site} update.
   *
   * @param site The {@link Site} that will be updated.
   * @return A {@link Set} of {@link TagUpdateInfoDTO}.
   */
  Set<TagUpdateInfoDTO> adjustTagReservesForSiteUpdate(Site site);

  /**
   * Adds an undated {@link Tier} if the {@link Tag} is present.
   *
   * @param position position
   */
  void addUpdatedTierIfTagIsPresentProxy(Position position);
}
