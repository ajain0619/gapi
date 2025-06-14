package com.nexage.app.services;

import com.nexage.admin.core.model.AdSource;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.Position;
import com.nexage.admin.core.model.RTBProfile;
import com.nexage.admin.core.model.Site;
import com.nexage.admin.core.model.Tag;
import com.nexage.app.dto.tag.TagCleanupResultsDTO;
import com.nexage.app.dto.tag.TagDeploymentInfoDTO;
import com.nexage.app.dto.tag.TagPerformanceMetricsDTO;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

/**
 * Methods used in reference to Seller's tags.
 *
 * <p>NOTE: Please do not add any new functionality to this class or its implementations. The
 * business concepts associated to the different operations handled by the following functions use
 * strategies, concepts, or frameworks considered deprecated. Be sure you reach to the core team
 * before considering designing or implementing new functionality under this class.
 */
public interface SellerTagService {
  /**
   * Updates the {@link Tag}.
   *
   * @param tag The updated {@link Tag}.
   * @return The {@link Site} with the updated {@link Tag}.
   */
  Site updateTag(Tag tag);

  /**
   * Creates an Exchange {@link Tag}.
   *
   * @param sitePid The pid correlating to the {@link Site} that the {@link Tag} will belong to.
   * @param tag The {@link Tag} that is being created.
   * @param rtbProfile The {@link RTBProfile} the tag will belong to.
   * @param fromSellerAdmin Whether or not this request is coming from Seller Admin or not.
   * @return The {@link Site} with the newly created {@link Tag}.
   */
  Site createExchangeTag(Long sitePid, Tag tag, RTBProfile rtbProfile, boolean fromSellerAdmin);

  /**
   * Updates an Exchange {@link Tag}.
   *
   * @param tag The {@link Tag} being updated.
   * @param rtbProfile The {@link RTBProfile} the tag will belong to.
   * @return The {@link Site} with the updated {@link Tag}.
   */
  Site updateExchangeTag(Tag tag, RTBProfile rtbProfile);

  /**
   * Creates a {@link Tag}.
   *
   * @param sitePid The pid correlating to the {@link Site} that the {@link Tag} will belong to.
   * @param tag The {@link Tag} that is being created.
   * @param fromSellerAdmin Whether or not this request is coming from Seller Admin or not.
   * @return The {@link Site} with the newly created {@link Tag}.
   */
  Site createTag(Long sitePid, Tag tag, boolean fromSellerAdmin);

  /**
   * Creates a {@link Tag}.
   *
   * @param site The {@link Site} that the {@link Tag} will belong to.
   * @param tag The {@link Tag} that is being created.
   * @param fromSellerAdmin Whether or not this request is coming from Seller Admin or not.
   * @return The {@link Site} with the newly created {@link Tag}.
   */
  Site createTag(Site site, Tag tag, boolean fromSellerAdmin);

  /**
   * Deletes a {@link Tag}.
   *
   * @param sitePid The Pid of the {@link Site} that the {@link Tag} being deleted belongs to.
   * @param tagPid The Pid of the {@link Tag} being deleted.
   * @return The {@link Site} without the deleted {@link Tag}.
   */
  Site deleteTag(Long sitePid, Long tagPid);

  /**
   * Gets the {@link TagDeploymentInfoDTO}.
   *
   * @param sitePid The {@link Site} that the {@link Tag} belongs to.
   * @param tagPid The {@link Tag} whose {@link TagDeploymentInfoDTO} is being retrieved.
   * @return The {@link TagDeploymentInfoDTO} for the {@link Tag} that matches the tagPid.
   */
  TagDeploymentInfoDTO getTagDeploymentInfo(Long sitePid, Long tagPid);

  /**
   * Undeploys the {@link Tag}.
   *
   * @param sitePid The Pid of the {@link Site} that the {@link Tag} belongs to.
   * @param positionPid The Pid of the {@link Position}.
   * @param tagPid The Pid of the {@link Tag} being undeployed.
   * @return The {@link Site} with the {@link Tag} having an INACTIVE status.
   */
  Site undeployTag(Long sitePid, Long positionPid, Long tagPid);

  /**
   * Cleans up the Tag Deployments.
   *
   * @param sitePid The Pid that matches the {@link Site} whose {@link Tag}s' deployments are being
   *     cleaned up.
   * @return The {@link TagCleanupResultsDTO}.
   */
  TagCleanupResultsDTO cleanupTagDeployments(Long sitePid);

  /**
   * Deletes an Exchange {@link Tag}.
   *
   * @param sitePid The Pid of the {@link Site} that the {@link Tag} being deleted belongs to.
   * @param tagPid The Pid of the {@link Tag} being deleted.
   * @return The {@link Site} without the deleted {@link Tag}.
   */
  Site deleteExchangeTag(Long sitePid, Long tagPid);

  /**
   * Gets the {@link TagPerformanceMetricsDTO}.
   *
   * @param sitePid The Pid of the {@link Site} the the {@link Tag} belongs to.
   * @param tagPid The Pid of the {@link Tag} to get the {@link TagPerformanceMetricsDTO} from.
   * @return The {@link TagPerformanceMetricsDTO.Builder} for the {@link Tag}.
   */
  TagPerformanceMetricsDTO.Builder getTagPerformanceMetrics(final Long sitePid, final Long tagPid);

  /**
   * Gets a {@link List} of {@link TagPerformanceMetricsDTO}s.
   *
   * @param sitePid The Pid of the {@link Site} that the {@link Tag}s belong to.
   * @param tagPids A {@link Collection} of {@link Long}s that correlate to tag pids within the
   *     {@link Site}.
   * @return The {@link List} of {@link TagPerformanceMetricsDTO}s for the {@link Tag}s.
   */
  List<TagPerformanceMetricsDTO> getTagPerformanceMetrics(
      final Long sitePid, final Collection<Long> tagPids);

  /**
   * Gets the Tag Revenue for a specified {@link Tag}.
   *
   * @param tagPid The Pid of the {@link Tag}.
   * @param publisher The Pid of the {@link Company} for Authorization.
   * @return The Tag Revenue.
   */
  BigDecimal getTagRevenue(Long tagPid, Long publisher);

  /**
   * Gets the {@link TagPerformanceMetricsDTO} for all the {@link Tag}s passed in.
   *
   * @param tags The {@link List} of {@link Tag}s to get the {@link TagPerformanceMetricsDTO}s for.
   * @return The {@link List} of {@link TagPerformanceMetricsDTO}s.
   */
  List<TagPerformanceMetricsDTO> getTagPerformanceMetrics(final List<Tag> tags);

  /**
   * Gets the Publisher {@link AdSource} {@link Tag}s.
   *
   * @param publisherId The Id of the Publisher.
   * @param adsourceId The Id of the {@link AdSource}.
   * @return A {@link List} of {@link Tag}s.
   */
  List<Tag> getPubAdsourceTags(final Long publisherId, final Long adsourceId);

  /**
   * Archives the {@link Tag}.
   *
   * @param site The {@link Site} the {@link Tag} being archived belongs to.
   * @param positionPid The Pid of the {@link Position} of the {@link Tag}.
   * @param tagPid The Pid of the {@link Tag} being archived
   * @return The {@link Site} with the {@link Tag} now archived.
   */
  Site archiveTag(Site site, Long positionPid, Long tagPid);
}
