package com.nexage.admin.core.repository;

import com.nexage.admin.core.dto.SeatHolderMetadataDTO;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.MdmId;
import com.nexage.admin.core.model.aggregation.CompanyMetricsAggregation;
import com.nexage.admin.core.projections.AllSeatHolderMetaDataReturnProjection;
import com.nexage.admin.core.projections.BuyerMetaDataForCompanyProjection;
import com.nexage.admin.core.projections.CompanySearchSummaryProjection;
import com.nexage.admin.core.projections.SearchSummaryProjection;
import com.nexage.admin.core.projections.SellerMetaDataForCompanyProjection;
import com.ssp.geneva.common.model.inventory.CompanyType;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository
    extends JpaRepository<Company, Long>, JpaSpecificationExecutor<Company> {

  String SELLER_METRICS_TABLE_ALIAS = "sm";
  String NEW_COMPANY_METRICS_AGGREGATION =
      "NEW com.nexage.admin.core.model.aggregation.CompanyMetricsAggregation("
          + "c.id, "
          + "c.pid, "
          + "c.name, "
          + "c.defaultRtbProfilesEnabled, "
          + "SUM(sm.adClicked) AS adClicked, "
          + "SUM(sm.adRequested) AS adRequested, "
          + "SUM(sm.adServed) AS adServed, "
          + "SUM(sm.adDelivered) AS adDelivered, "
          + "CASE WHEN (SUM(sm.adRequested) = 0) THEN 0.0 ELSE (100   * SUM(sm.adServed) / SUM(sm.adRequested)) END AS fillRate, "
          + "CASE WHEN (SUM(sm.adDelivered) = 0) THEN 0.0 ELSE (100   * SUM(sm.adClicked) / SUM(sm.adDelivered)) END AS ctr, "
          + "CASE WHEN (SUM(sm.adRequested) = 0) THEN 0.0 ELSE (1000  * SUM(sm.sellerRevenue) / SUM(sm.adRequested)) END AS rpm, "
          + "CASE WHEN (SUM(sm.adDelivered) = 0) THEN 0.0 ELSE (1000  * SUM(sm.sellerRevenue)  / SUM(sm.adDelivered)) END AS ecpm, "
          + "CASE WHEN (SUM(sm.adDelivered) = 0) THEN 0.0 ELSE (1000  * SUM(sm.totalRevenue) / SUM(sm.adDelivered)) END AS totalEcpm, "
          + "CASE WHEN (SUM(sm.adRequested) = 0) THEN 0.0 ELSE (1000  * SUM(sm.totalRevenue) / SUM(sm.adRequested)) END AS totalRpm, "
          + "SUM(sm.sellerRevenue) AS sellerRevenue, "
          + "SUM(sm.totalRevenue) AS totalRevenue, "
          + "SUM(sm.verizonRevenue) AS verizonRevenue)";

  String NEW_COMPANY_METRICS_AGGREGATION_FOR_NON_NEXAGE_USER =
      "NEW com.nexage.admin.core.model.aggregation.CompanyMetricsAggregation("
          + "c.id, "
          + "c.pid, "
          + "c.name, "
          + "c.defaultRtbProfilesEnabled, "
          + "SUM(sm.adClicked) AS adClicked, "
          + "SUM(sm.adRequested) AS adRequested, "
          + "SUM(sm.adServed) AS adServed, "
          + "SUM(sm.adDelivered) AS adDelivered, "
          + "CASE WHEN (SUM(sm.adRequested) = 0) THEN 0.0 ELSE (100   * SUM(sm.adServed) / SUM(sm.adRequested)) END AS fillRate, "
          + "CASE WHEN (SUM(sm.adDelivered) = 0) THEN 0.0 ELSE (100   * SUM(sm.adClicked) / SUM(sm.adDelivered)) END AS ctr, "
          + "CASE WHEN (SUM(sm.adRequested) = 0) THEN 0.0 ELSE (1000  * SUM(sm.sellerRevenue) / SUM(sm.adRequested)) END AS rpm, "
          + "CASE WHEN (SUM(sm.adDelivered) = 0) THEN 0.0 ELSE (1000  * SUM(sm.sellerRevenue)  / SUM(sm.adDelivered)) END AS ecpm, "
          + "SUM(sm.sellerRevenue) AS sellerRevenue)";

  /**
   * Aggregate {@link Company} and {@link com.nexage.admin.core.model.SellerMetrics} into a single
   * projection, based on request. This query does not accept search based in any query param.
   *
   * @param startDate Start period for aggregation.
   * @param stopDate Stop period for aggregation.
   * @param pageable Pagination based on {@link Pageable}
   * @return {@link Page} of {@link CompanyMetricsAggregation} instances based on parameters.
   */
  @Query(
      value =
          "SELECT "
              + NEW_COMPANY_METRICS_AGGREGATION
              + " FROM Company c LEFT JOIN c.sellerMetrics "
              + SELLER_METRICS_TABLE_ALIAS
              + " WITH sm.startDate >= :startDate AND sm.stopDate <= :stopDate "
              + "WHERE c.type = 'SELLER' "
              + "GROUP BY c")
  Page<CompanyMetricsAggregation> aggregateMetrics(
      @NotNull @Param("startDate") Date startDate,
      @NotNull @Param("stopDate") Date stopDate,
      @NotNull Pageable pageable);

  /**
   * Aggregate {@link Company} and {@link com.nexage.admin.core.model.SellerMetrics} into a single
   * projection, based on request. This query does not accept search based in any query param.
   *
   * @param startDate Start period for aggregation.
   * @param stopDate Stop period for aggregation.
   * @param companies filter with company pids
   * @param pageable Pagination based on {@link Pageable}
   * @return {@link Page} of {@link CompanyMetricsAggregation} instances based on parameters.
   */
  @Query(
      value =
          "SELECT "
              + NEW_COMPANY_METRICS_AGGREGATION_FOR_NON_NEXAGE_USER
              + " FROM Company c LEFT JOIN c.sellerMetrics "
              + SELLER_METRICS_TABLE_ALIAS
              + " WITH sm.startDate >= :startDate AND sm.stopDate <= :stopDate "
              + "WHERE c.type = 'SELLER' AND c.pid in :companies "
              + "GROUP BY c")
  Page<CompanyMetricsAggregation> aggregateNonNexageMetricsByCompanies(
      @NotNull @Param("startDate") Date startDate,
      @NotNull @Param("stopDate") Date stopDate,
      @NotNull @Param("companies") Set<Long> companies,
      @NotNull Pageable pageable);

  /**
   * Aggregate {@link Company} and {@link com.nexage.admin.core.model.SellerMetrics} into a single
   * projection, based on request. This query does not accept search based in any query param.
   *
   * @param startDate Start period for aggregation.
   * @param stopDate Stop period for aggregation.
   * @param sellerSeatPid filter with seller seat pid
   * @param pageable Pagination based on {@link Pageable}
   * @return {@link Page} of {@link CompanyMetricsAggregation} instances based on parameters.
   */
  @Query(
      value =
          "SELECT "
              + NEW_COMPANY_METRICS_AGGREGATION
              + " FROM Company c LEFT JOIN c.sellerMetrics "
              + SELLER_METRICS_TABLE_ALIAS
              + " WITH sm.startDate >= :startDate AND sm.stopDate <= :stopDate "
              + "WHERE c.type = 'SELLER' AND c.sellerSeat.pid = :sellerSeatPid "
              + "GROUP BY c")
  Page<CompanyMetricsAggregation> aggregateMetricsBySellerSeatPid(
      @NotNull @Param("startDate") Date startDate,
      @NotNull @Param("stopDate") Date stopDate,
      @NotNull @Param("sellerSeatPid") Long sellerSeatPid,
      @NotNull Pageable pageable);

  /**
   * Aggregate {@link Company} and {@link com.nexage.admin.core.model.SellerMetrics} into a single
   * projection, based on request. This query search companies only by name due to aggregation
   * restrictions in this specific version of Hibernate & Spring.
   *
   * @param startDate Start period for aggregation.
   * @param stopDate Stop period for aggregation.
   * @param qt qt The term to be found.
   * @param pageable Pagination based on {@link Pageable}
   * @return {@link Page} of {@link CompanyMetricsAggregation} instances based on parameters.
   */
  @Query(
      value =
          "SELECT "
              + NEW_COMPANY_METRICS_AGGREGATION
              + " FROM Company c LEFT JOIN c.sellerMetrics  "
              + SELLER_METRICS_TABLE_ALIAS
              + " WITH sm.startDate >= :startDate AND sm.stopDate <= :stopDate "
              + "WHERE (c.type = 'SELLER' AND c.name LIKE %:qt%) "
              + "GROUP BY c")
  Page<CompanyMetricsAggregation> aggregateMetricsByName(
      @NotNull @Param("startDate") Date startDate,
      @NotNull @Param("stopDate") Date stopDate,
      @NotNull @Param("qt") String qt,
      @NotNull Pageable pageable);

  /**
   * Aggregate {@link Company} and {@link com.nexage.admin.core.model.SellerMetrics} into a single
   * projection, based on request. This query search companies only by name due to aggregation
   * restrictions in this specific version of Hibernate & Spring.
   *
   * @param startDate Start period for aggregation.
   * @param stopDate Stop period for aggregation.
   * @param qt qt The term to be found.
   * @param companies filter with companies pids
   * @param pageable Pagination based on {@link Pageable}
   * @return {@link Page} of {@link CompanyMetricsAggregation} instances based on parameters.
   */
  @Query(
      value =
          "SELECT "
              + NEW_COMPANY_METRICS_AGGREGATION_FOR_NON_NEXAGE_USER
              + " FROM Company c LEFT JOIN c.sellerMetrics  "
              + SELLER_METRICS_TABLE_ALIAS
              + " WITH sm.startDate >= :startDate AND sm.stopDate <= :stopDate "
              + "WHERE (c.type = 'SELLER' AND c.name LIKE %:qt% AND c.pid in :companies) "
              + "GROUP BY c")
  Page<CompanyMetricsAggregation> aggregateNonNexageMetricsByNameAndCompanies(
      @NotNull @Param("startDate") Date startDate,
      @NotNull @Param("stopDate") Date stopDate,
      @NotNull @Param("qt") String qt,
      @NotNull @Param("companies") Set<Long> companies,
      @NotNull Pageable pageable);

  /**
   * Aggregate {@link Company} and {@link com.nexage.admin.core.model.SellerMetrics} into a single
   * projection, based on request. This query search companies only by name due to aggregation
   * restrictions in this specific version of Hibernate & Spring.
   *
   * @param startDate Start period for aggregation.
   * @param stopDate Stop period for aggregation.
   * @param qt qt The term to be found.
   * @param sellerSeatPid filter with companies within seller seat
   * @param pageable Pagination based on {@link Pageable}
   * @return {@link Page} of {@link CompanyMetricsAggregation} instances based on parameters.
   */
  @Query(
      value =
          "SELECT "
              + NEW_COMPANY_METRICS_AGGREGATION
              + " FROM Company c LEFT JOIN c.sellerMetrics  "
              + SELLER_METRICS_TABLE_ALIAS
              + " WITH sm.startDate >= :startDate AND sm.stopDate <= :stopDate "
              + "WHERE (c.type = 'SELLER' AND c.name LIKE %:qt% AND c.sellerSeat.pid = :sellerSeatPid) "
              + "GROUP BY c")
  Page<CompanyMetricsAggregation> aggregateMetricsByNameAndSellerSeat(
      @NotNull @Param("startDate") Date startDate,
      @NotNull @Param("stopDate") Date stopDate,
      @NotNull @Param("qt") String qt,
      @NotNull @Param("sellerSeatPid") Long sellerSeatPid,
      @NotNull Pageable pageable);

  /**
   * Count of s2s company associations for given hbpartner pid list
   *
   * @param hbPartnerPids List of hbpartner pids.
   * @return {@link Integer} Count of site pids.
   */
  @Query(
      value =
          "SELECT COUNT(c.pid) as intValue FROM Company c JOIN c.hbPartnerCompany hpc JOIN hpc.hbPartner hp WHERE hp.pid in (:hbPartnerPids)")
  Integer countCompaniesAssociatedToHbPartners(@Param("hbPartnerPids") List<Long> hbPartnerPids);

  @Query("SELECT c from Company c where c.pid in (:sellersPids) and c.type = 'SELLER'")
  Set<Company> findSellersWithSpecificPids(
      @NotNull @Param("sellersPids") Collection<Long> sellersPids);

  /**
   * Find the sellerSeatId attached to a company
   *
   * @param companyPid ID of the company
   * @return {@link Long} sellerSeatId of the company with the given sellerId or null if none exists
   */
  @Query("SELECT c.sellerSeat.pid FROM Company c WHERE c.pid = :companyPid")
  Long findSellerSeatIdByCompanyPid(@NotNull @Param("companyPid") Long companyPid);

  @Query("SELECT c.pid FROM Company c WHERE c.sellerSeat.pid = ?1")
  Set<Long> findAllIdsBySellerSeatPid(Long sellerSeatPid);

  /**
   * Finds the sellerSeatId for the company, if present.
   *
   * @param companyPid the company pid
   * @return the sellerSeatId in an Optional if found, or empty Optional if not found
   */
  @Query("SELECT c.sellerSeat.pid FROM Company c WHERE c.pid = :companyPid")
  Optional<Long> findSellerSeatIdByPid(@NotNull @Param("companyPid") Long companyPid);

  @Query(
      "SELECT c.defaultRtbProfilesEnabled from Company c where c.pid = :sellerPid  and c.type = 'SELLER'")
  Boolean isCompanyDefaultRTBProfilesEnabled(@NotNull @Param("sellerPid") Long sellerPid);

  /**
   * Finds the MDM IDs list for the company, if the company is active.<br>
   *
   * @param companyPid the company pid
   * @return the list of MDM IDs set for the company. The list can be empty if none are set or the
   *     company is not active.
   */
  @Query("SELECT c.mdmIds FROM Company c WHERE c.pid = :companyPid AND c.status = 1")
  List<MdmId> findMdmIdsByActiveCompanyPid(@NotNull @Param("companyPid") Long companyPid);

  @Query("SELECT c.pid FROM Company c WHERE c.sellerSeat.pid = :sellerSeatPid")
  List<Long> findCompanyPidsBySellerSeatPid(@Param("sellerSeatPid") long sellerSeatPid);

  boolean existsByNameAndType(String name, CompanyType type);

  List<Company> findByType(CompanyType type);

  @Query("SELECT distinct c.currency FROM Company c WHERE c.pid IN (:pids)")
  List<String> findUniqueCurrenciesByPids(@Param("pids") List<Long> pids);

  List<Company> findByPidIn(Set<Long> companyPids);

  List<CompanySearchSummaryProjection> findCompanySearchProjectionsByTypeAndNameStartingWith(
      CompanyType type, String prefix);

  @Query(
      value =
          "SELECT s.pid AS sitePid, s.name AS siteName, s.status AS siteStatus, c.pid AS companyPid, c.name AS companyName FROM site s JOIN company c ON c.pid = s.company_pid WHERE s.name LIKE %:text% AND s.status >= 0 AND c.type = 'SELLER'"
              + " UNION ALL "
              + " SELECT -1 AS sitePid, '' AS siteName, 0 AS siteStatus, pid AS companyPid, name AS companyName FROM company WHERE name LIKE %:text% AND type = 'SELLER'",
      nativeQuery = true)
  List<SearchSummaryProjection> findSearchSummaryProjectionsContaining(@Param("text") String text);

  @Query(
      value =
          "SELECT COUNT(insertionOrder.pid) AS ios FROM bdr_insertionorder insertionOrder JOIN bdr_lineitem lineitem ON lineitem.insertionorder_pid = insertionOrder.pid AND lineitem.status = 1 INNER JOIN bdr_advertiser advertiser ON advertiser.pid = insertionOrder.advertiser_pid AND advertiser.company_pid = :companyPid",
      nativeQuery = true)
  SeatHolderMetadataDTO findSeatHolderMetadataByCompanyPid(@Param("companyPid") long companyPid);

  @Query(
      value =
          "SELECT advertiser.company_pid AS company, COUNT(insertionOrder.pid) AS ios FROM bdr_insertionorder insertionOrder INNER JOIN bdr_lineitem lineitem ON lineitem.insertionorder_pid = insertionOrder.pid AND lineitem.status = 1 INNER JOIN bdr_advertiser advertiser ON advertiser.pid = insertionOrder.advertiser_pid GROUP BY advertiser.company_pid",
      nativeQuery = true)
  List<AllSeatHolderMetaDataReturnProjection> findAllSeatHolderMetaDataReturnProjections();

  @Query(
      value =
          "SELECT dp.name AS dataProvider,  a.name AS adsource, u.user_name AS user FROM company c LEFT JOIN bidder_config b ON c.pid = b.company_id LEFT JOIN bidder_subscription bs ON b.pid = bs.bidder_pid LEFT JOIN external_data_provider dp ON dp.pid = bs.data_provider_pid AND dp.enablement_status <> 2 LEFT JOIN ad_source a ON c.pid = a.company_pid AND a.status >= 0 LEFT JOIN app_user u ON c.pid = u.company_id WHERE type = 'BUYER' AND c.pid = :companyPid",
      nativeQuery = true)
  List<BuyerMetaDataForCompanyProjection> findBuyerMetaDataForCompanyProjectionsByCompanyPid(
      @Param("companyPid") long companyPid);

  @Query(
      value =
          "SELECT c.pid AS company, dp.name AS dataProvider, a.name AS adsource, u.user_name AS user FROM company c LEFT JOIN bidder_config b ON c.pid = b.company_id LEFT JOIN bidder_subscription bs ON b.pid = bs.bidder_pid LEFT JOIN external_data_provider dp ON dp.pid = bs.data_provider_pid AND dp.enablement_status <> 2 LEFT JOIN ad_source a ON c.pid = a.company_pid AND a.status >= 0 LEFT JOIN app_user u ON c.pid = u.company_id WHERE type = 'BUYER'",
      nativeQuery = true)
  List<BuyerMetaDataForCompanyProjection> findAllBuyerMetaDataForCompanyProjections();

  @Query(
      value =
          "SELECT company, SUM(num_sites) AS sites, SUM(num_tags) AS tags, SUM(num_users) AS users, SUM(num_hb_sites) as hbsites FROM "
              + " (SELECT c.pid AS company, COUNT(s.pid) AS num_sites, 0 AS  num_tags, 0 AS num_users, 0 as num_hb_sites FROM company c LEFT JOIN site s ON c.pid = s.company_pid AND c.pid = :companyPid AND c.type = 'SELLER' WHERE s.status >= 0 GROUP BY c.pid "
              + " UNION ALL "
              + " SELECT c.pid AS company, 0 AS num_sites, 0 AS  num_tags, COUNT(u.pid) AS num_users, 0 as num_hb_sites FROM company c INNER JOIN app_user u ON c.pid = u.company_id AND c.pid = :companyPid AND c.type = 'SELLER' GROUP BY c.pid "
              + " UNION ALL "
              + " SELECT c.pid AS company, 0 AS num_sites, 0 AS  num_tags, 0 AS num_users,COUNT(s.pid) as num_hb_sites FROM company c LEFT JOIN site s ON c.pid = s.company_pid AND c.pid = :companyPid AND c.type = 'SELLER' WHERE s.status >= 0 AND s.hb_enabled=1 GROUP BY c.pid "
              + " UNION ALL "
              + " SELECT c.pid AS company, 0 AS num_sites, COUNT(t.pid) AS num_tags, 0 AS num_users, 0 as num_hb_sites FROM company c INNER JOIN site s ON c.pid = s.company_pid AND c.type = 'SELLER' INNER JOIN tag t ON t.site_pid = s.pid AND c.pid = :companyPid WHERE t.status >= 0 GROUP BY c.pid ) "
              + " a GROUP BY a.company",
      nativeQuery = true)
  List<SellerMetaDataForCompanyProjection> findSellerMetaDataForCompanyProjectionsByCompanyPid(
      @Param("companyPid") long companyPid);

  @Query(
      value =
          "SELECT company, SUM(num_sites) AS sites, SUM(num_tags) AS tags, SUM(num_users) AS users, SUM(num_hb_sites) as hbsites FROM "
              + " (SELECT c.pid AS company, COUNT(s.pid) AS num_sites, 0 AS  num_tags, 0 AS num_users, 0 as num_hb_sites FROM company c LEFT JOIN site s ON c.pid = s.company_pid AND c.type = 'SELLER' WHERE s.status >= 0 GROUP BY c.pid "
              + " UNION ALL "
              + " SELECT c.pid AS company, 0 AS num_sites, 0 AS  num_tags, 0 AS num_users, COUNT(s.pid) as num_hb_sites FROM company c LEFT JOIN site s ON c.pid = s.company_pid AND c.type = 'SELLER' WHERE s.status >= 0 AND s.hb_enabled = 1 GROUP BY c.pid "
              + " UNION ALL "
              + " SELECT c.pid AS company, 0 AS num_sites, 0 AS  num_tags, COUNT(u.pid) AS num_users, 0 as num_hb_sites FROM company c INNER JOIN app_user u ON c.pid = u.company_id AND c.type = 'SELLER' GROUP BY c.pid "
              + " UNION ALL "
              + " SELECT c.pid AS company, 0 AS num_sites, COUNT(t.pid) AS num_tags, 0 AS num_users, 0 as num_hb_sites FROM company c INNER JOIN site s ON c.pid = s.company_pid AND c.type = 'SELLER' INNER JOIN tag t ON t.site_pid = s.pid WHERE t.status >= 0 GROUP BY c.pid) "
              + " a GROUP BY a.company",
      nativeQuery = true)
  List<SellerMetaDataForCompanyProjection> findAllSellerMetaDataForCompanyProjections();

  @Query(value = "SELECT COUNT(c.pid) FROM Company c WHERE c.pid IN (:pids)")
  long countByPids(@Param("pids") Set<Long> pids);

  long countByPid(Long pid);
}
