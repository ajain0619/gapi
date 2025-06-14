package com.nexage.app.services;

import com.nexage.app.dto.seller.SiteDTO;
import com.nexage.app.util.validator.site.SiteQueryParams;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SiteService {

  /**
   * Find all {@link SiteDTO} under request criteria, returning a paginated response.
   *
   * @param sellerId sellerId
   * @param qt The term to be found.
   * @param siteType siteType
   * @param status status
   * @return {@link Page} of {@link SiteDTO} instances based on parameters.
   */
  Page<SiteDTO> getSites(
      Long sellerId,
      Pageable pageable,
      Optional<String> qt,
      Optional<List<String>> siteType,
      Optional<List<String>> status,
      Optional<String> fetch);

  /**
   * Find all {@link SiteDTO} under request criteria, returning a paginated response.
   *
   * @param sellerSeatPid Seller seat pid
   * @param qt The term to be found.
   * @param siteType Site type
   * @param status Status
   * @return {@link Page} of {@link SiteDTO} instances based on parameters.
   */
  Page<SiteDTO> getSitesForSellerSeat(
      Long sellerSeatPid,
      Pageable pageable,
      Optional<String> qt,
      Optional<List<String>> siteType,
      Optional<List<String>> status,
      Optional<String> fetch);

  /**
   * Find all {@link SiteDTO} under request criteria, returning a paginated response.
   *
   * @param sellerId sellerId
   * @param qt qt
   * @param pageable Pagination based on {@link Pageable}
   * @return {@link Page} of {@link SiteDTO} instances based on parameters.
   */
  Page<SiteDTO> searchSitesAndPositionsForSeller(
      Long sellerId, Optional<String> qt, Pageable pageable);

  /**
   * Find {@link Page} of {@link SiteDTO} base on query field and list of query terms
   *
   * @param qf query field
   * @param qt list of query terms
   * @param pageable Pagination based on {@link Pageable}
   * @return {@link Page} of {@link SiteDTO} instances based on parameters.
   */
  Page<SiteDTO> getSites(String qf, Set<Long> qt, Pageable pageable);

  /**
   * Find {@link Page} of {@link SiteDTO} base on query field and list of query terms
   *
   * @param queryParams query field
   * @param pageable Pagination based on {@link Pageable}
   * @return {@link Page} of {@link SiteDTO} instances based on parameters.
   */
  Page<SiteDTO> getSites(SiteQueryParams queryParams, Pageable pageable);

  /**
   * Returns paged list of sites with minimal data. Method supports search by site name.
   *
   * @param sellerPid seller PID
   * @param qt name search term
   * @param pageable
   * @return paged list of sites
   */
  Page<SiteDTO> getSiteMinimalData(Long sellerPid, String qt, Pageable pageable);

  /**
   * Returns paged list of sites with minimal data. Method supports search by site name.
   *
   * @param sellerSeatPid seller seat PID
   * @param qt name search term
   * @param pageable
   * @return paged list of sites
   */
  Page<SiteDTO> getSiteMinimalDataForSellerSeat(Long sellerSeatPid, String qt, Pageable pageable);
}
