package com.nexage.app.services;

import com.nexage.admin.core.model.AdSource;
import com.nexage.admin.core.model.RTBProfile;
import com.nexage.admin.core.model.Site;
import com.nexage.admin.core.model.Tag;
import com.nexage.admin.core.sparta.jpa.model.SellerAdSource;
import com.nexage.app.dto.tag.TagPerformanceMetricsDTO;
import java.util.List;
import java.util.Optional;

/**
 * Methods used in reference to Sellers.
 *
 * <p>NOTE: Please do not add any new functionality to this class or its implementations. The
 * business concepts associated to the different operations handled by the following functions use
 * strategies, concepts, or frameworks considered deprecated. Be sure you reach to the core team
 * before considering designing or implementing new functionality under this class.
 */
public interface SellerService {

  /**
   * Updates the {@link RTBProfile}.
   *
   * @param rtbProfile The {@link RTBProfile} to be updated.
   * @return The {@link Site} that contains said {@link RTBProfile}.
   */
  Site updateRTBProfile(RTBProfile rtbProfile);

  /**
   * Generates the hash for archiving {@link Tag}s.
   *
   * @param performanceMetrics The {@link TagPerformanceMetricsDTO} that the hash is based on.
   * @return The hash that was generated.
   */
  String calcHashForTagArchive(TagPerformanceMetricsDTO performanceMetrics);

  /**
   * Gets all the {@link SellerAdSource}s for the provided sellerPid.
   *
   * @param sellerPid The Pid of the seller to get the {@link SellerAdSource}s for.
   * @return The {@link List} of {@link SellerAdSource}s for the provided seller.
   */
  List<SellerAdSource> getAllAdsourceDefaults(Long sellerPid);

  /**
   * Gets all of the default {@link AdSource}s.
   *
   * @return The {@link List} of {@link AdSource}s.
   */
  List<AdSource> getPublisherSelfServeDefaultAdsources();

  /**
   * Check if SellerAdSource exists
   *
   * @param publisherPid publisher pid
   * @param adsourceId adsource id
   * @return true if SellerAdSource exists
   */
  boolean existsSellerAdSource(Long publisherPid, Long adsourceId);

  /**
   * Save SellerAdSource
   *
   * @param sellerAdSource SellerAdSource
   * @return saved sellerAdSource
   */
  SellerAdSource saveSellerAdSource(SellerAdSource sellerAdSource);

  /**
   * Retrive SellerAdSource from database
   *
   * @param publisherPid publisher pid
   * @param adsourceId adsource id
   * @return Optional of SellerAdSource
   */
  Optional<SellerAdSource> getSellerAdSourceBySellerPidAndAdSourcePid(
      Long publisherPid, Long adsourceId);

  /**
   * Removes SellerAdSource from database
   *
   * @param publisherPid publisher pid
   * @param adsourceId adsource id
   */
  void deleteSellerAdSourceBySellerPidAndAdSourcePid(Long publisherPid, Long adsourceId);
}
