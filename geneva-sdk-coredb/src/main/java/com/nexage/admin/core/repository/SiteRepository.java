package com.nexage.admin.core.repository;

import com.nexage.admin.core.dto.SiteSummaryDTO;
import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.model.HbPartnersAssociationView;
import com.nexage.admin.core.model.Site;
import com.nexage.admin.core.model.SiteView;
import com.nexage.admin.core.projections.SiteWithInactiveTagProjection;
import com.nexage.admin.core.sparta.jpa.model.SiteMetricsAggregation;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SiteRepository extends JpaRepository<Site, Long>, JpaSpecificationExecutor<Site> {

  String SITE_METRICS_TABLE_ALIAS = "m";

  /**
   * Aggregate {@link Site} and {@link com.nexage.admin.core.model.SiteMetrics} into a single
   * projection, based on request. This query does not accept search based in any query param.
   *
   * @param startDate Start period for aggregation.
   * @param stopDate Stop period for aggregation.
   * @param companyPid The companyId.
   * @param pageable Pagination based on {@link Pageable}
   * @return {@link Page} of {@link SiteMetricsAggregation} instances based on parameters.
   */
  @Query(
      value =
          "SELECT new com.nexage.admin.core.sparta.jpa.model.SiteMetricsAggregation("
              + "s.dcn, s.id, s.pid, s.type, s.live, s.name, s.platform, s.status, "
              + "COALESCE(SUM(m.sellerRevenue), 0.0) as sellerRevenue, "
              + "COALESCE(SUM(m.adRequested), 0) as adRequested, "
              + "COALESCE(SUM(m.adDelivered), 0) as adDelivered, "
              + "COALESCE(SUM(m.adServed), 0) as adServed, "
              + "COALESCE(SUM(m.adClicked), 0) as adClicked, "
              + "COALESCE(CASE WHEN (SUM(m.adRequested) = 0) THEN 0.0 else (100.0 * SUM(m.adServed) / SUM(m.adRequested)) END, 0.0) as fillRate, "
              + "COALESCE(CASE WHEN (SUM(m.adDelivered) = 0) THEN 0.0 else (1000.0 * SUM(m.sellerRevenue) / SUM(m.adDelivered)) END, 0.0) as ecpm, "
              + "COALESCE(CASE WHEN (SUM(m.adDelivered) = 0) THEN 0.0 else (100.0 * SUM(m.adClicked) / SUM(m.adDelivered)) END, 0.0) as ctr, "
              + "COALESCE(CASE WHEN (SUM(m.adRequested) = 0) THEN 0.0 else (1000.0 * SUM(m.sellerRevenue) / SUM(m.adRequested)) END, 0.0) as rpm) "
              + "FROM Site s LEFT JOIN s.metrics "
              + SITE_METRICS_TABLE_ALIAS
              + " WITH m.startDate >= :startDate AND m.startDate < :stopDate  WHERE s.status >= 0 AND s.companyPid = :companyPid GROUP BY s")
  Page<SiteMetricsAggregation> aggregateMetrics(
      @Param("startDate") Date startDate,
      @Param("stopDate") Date stopDate,
      @Param("companyPid") Long companyPid,
      Pageable pageable);

  /**
   * Aggregate {@link Site} and {@link com.nexage.admin.core.model.SiteMetrics} into a single
   * projection, based on request. This query does not accept search based in any query param.
   *
   * @param startDate Start period for aggregation.
   * @param stopDate Stop period for aggregation.
   * @param companyPid The companyId.
   * @param sitePids A list of site primary ids.
   * @param pageable Pagination based on {@link Pageable}
   * @return {@link Page} of {@link SiteMetricsAggregation} instances based on parameters.
   */
  @Query(
      value =
          "SELECT new com.nexage.admin.core.sparta.jpa.model.SiteMetricsAggregation("
              + "s.dcn, s.id, s.pid, s.type, s.live, s.name, s.platform, s.status, "
              + "COALESCE(SUM(m.sellerRevenue), 0.0) as sellerRevenue, "
              + "COALESCE(SUM(m.adRequested), 0) as adRequested, "
              + "COALESCE(SUM(m.adDelivered), 0) as adDelivered, "
              + "COALESCE(SUM(m.adServed), 0) as adServed, "
              + "COALESCE(SUM(m.adClicked), 0) as adClicked, "
              + "COALESCE(CASE WHEN (SUM(m.adRequested) = 0) THEN 0.0 else (100.0 * SUM(m.adServed) / SUM(m.adRequested)) END, 0.0) as fillRate, "
              + "COALESCE(CASE WHEN (SUM(m.adDelivered) = 0) THEN 0.0 else (1000.0 * SUM(m.sellerRevenue) / SUM(m.adDelivered)) END, 0.0) as ecpm, "
              + "COALESCE(CASE WHEN (SUM(m.adDelivered) = 0) THEN 0.0 else (100.0 * SUM(m.adClicked) / SUM(m.adDelivered)) END, 0.0) as ctr, "
              + "COALESCE(CASE WHEN (SUM(m.adRequested) = 0) THEN 0.0 else (1000.0 * SUM(m.sellerRevenue) / SUM(m.adRequested)) END, 0.0) as rpm) "
              + "FROM Site s LEFT JOIN s.metrics "
              + SITE_METRICS_TABLE_ALIAS
              + " WITH m.startDate >= :startDate AND m.startDate < :stopDate  WHERE s.status >= 0 AND s.companyPid = :companyPid AND s.pid IN (:sitePids) GROUP BY s")
  Page<SiteMetricsAggregation> aggregateMetricsWithPids(
      @Param("startDate") Date startDate,
      @Param("stopDate") Date stopDate,
      @Param("companyPid") Long companyPid,
      @Param("sitePids") Optional<List<Long>> sitePids,
      Pageable pageable);

  /**
   * Aggregate {@link Site} and {@link com.nexage.admin.core.model.SiteMetrics} into a single
   * projection, based on request. This query search sites only by name due to aggregation
   * restrictions in this specific version of Hibernate & Spring.
   *
   * @param startDate Start period for aggregation.
   * @param stopDate Stop period for aggregation.
   * @param companyPid The companyId.
   * @param name The site name
   * @param pageable Pagination based on {@link Pageable}
   * @return {@link Page} of {@link SiteMetricsAggregation} instances based on parameters.
   */
  @Query(
      value =
          "SELECT new com.nexage.admin.core.sparta.jpa.model.SiteMetricsAggregation("
              + "s.dcn, s.id, s.pid, s.type, s.live, s.name, s.platform, s.status, "
              + "COALESCE(SUM(m.sellerRevenue), 0.0) as sellerRevenue, "
              + "COALESCE(SUM(m.adRequested), 0) as adRequested, "
              + "COALESCE(SUM(m.adDelivered), 0) as adDelivered, "
              + "COALESCE(SUM(m.adServed), 0) as adServed, "
              + "COALESCE(SUM(m.adClicked), 0) as adClicked, "
              + "COALESCE(CASE WHEN (SUM(m.adRequested) = 0) THEN 0.0 else (100.0 * SUM(m.adServed) / SUM(m.adRequested)) END, 0.0) as fillRate, "
              + "COALESCE(CASE WHEN (SUM(m.adDelivered) = 0) THEN 0.0 else (1000.0 * SUM(m.sellerRevenue) / SUM(m.adDelivered)) END, 0.0) as ecpm, "
              + "COALESCE(CASE WHEN (SUM(m.adDelivered) = 0) THEN 0.0 else (100.0 * SUM(m.adClicked) / SUM(m.adDelivered)) END, 0.0) as ctr, "
              + "COALESCE(CASE WHEN (SUM(m.adRequested) = 0) THEN 0.0 else (1000.0 * SUM(m.sellerRevenue) / SUM(m.adRequested)) END, 0.0) as rpm) "
              + "FROM Site s LEFT JOIN s.metrics "
              + SITE_METRICS_TABLE_ALIAS
              + " WITH m.startDate >= :startDate AND m.startDate < :stopDate  WHERE s.status >= 0 AND s.companyPid = :companyPid  AND UPPER(s.name) LIKE CONCAT('%', UPPER(:name), '%') GROUP BY s")
  Page<SiteMetricsAggregation> aggregateMetricsWithName(
      @Param("startDate") Date startDate,
      @Param("stopDate") Date stopDate,
      @Param("name") String name,
      @Param("companyPid") Long companyPid,
      Pageable pageable);

  /**
   * Aggregate {@link Site} and {@link com.nexage.admin.core.model.SiteMetrics} into a single
   * projection, based on request. This query search sites only by id due to aggregation
   * restrictions in this specific version of Hibernate & Spring.
   *
   * @param startDate Start period for aggregation.
   * @param stopDate Stop period for aggregation.
   * @param companyPid The companyId.
   * @param pid The site pid
   * @param pageable Pagination based on {@link Pageable}
   * @return {@link Page} of {@link SiteMetricsAggregation} instances based on parameters.
   */
  @Query(
      value =
          "SELECT new com.nexage.admin.core.sparta.jpa.model.SiteMetricsAggregation("
              + "s.dcn, s.id, s.pid, s.type, s.live, s.name, s.platform, s.status, "
              + "COALESCE(SUM(m.sellerRevenue), 0.0) as sellerRevenue, "
              + "COALESCE(SUM(m.adRequested), 0) as adRequested, "
              + "COALESCE(SUM(m.adDelivered), 0) as adDelivered, "
              + "COALESCE(SUM(m.adServed), 0) as adServed, "
              + "COALESCE(SUM(m.adClicked), 0) as adClicked, "
              + "COALESCE(CASE WHEN (SUM(m.adRequested) = 0) THEN 0.0 else (100.0 * SUM(m.adServed) / SUM(m.adRequested)) END, 0.0) as fillRate, "
              + "COALESCE(CASE WHEN (SUM(m.adDelivered) = 0) THEN 0.0 else (1000.0 * SUM(m.sellerRevenue) / SUM(m.adDelivered)) END, 0.0) as ecpm, "
              + "COALESCE(CASE WHEN (SUM(m.adDelivered) = 0) THEN 0.0 else (100.0 * SUM(m.adClicked) / SUM(m.adDelivered)) END, 0.0) as ctr, "
              + "COALESCE(CASE WHEN (SUM(m.adRequested) = 0) THEN 0.0 else (1000.0 * SUM(m.sellerRevenue) / SUM(m.adRequested)) END, 0.0) as rpm) "
              + "FROM Site s LEFT JOIN s.metrics "
              + SITE_METRICS_TABLE_ALIAS
              + " WITH m.startDate >= :startDate AND m.startDate < :stopDate  WHERE s.status >= 0 AND s.companyPid = :companyPid  AND s.pid = :pid GROUP BY s")
  Page<SiteMetricsAggregation> aggregateMetricsWithPid(
      @Param("startDate") Date startDate,
      @Param("stopDate") Date stopDate,
      @Param("pid") Long pid,
      @Param("companyPid") Long companyPid,
      Pageable pageable);

  /**
   * Find Seller Revenue sum for a list of sites
   *
   * @param startDate Start period for aggregation.
   * @param stopDate Stop period for aggregation.
   * @param pids List of site pids
   * @return {@link List} of {@link SiteMetricsAggregation} instances based on parameters.
   */
  @Query(
      value =
          "SELECT new com.nexage.admin.core.sparta.jpa.model.SiteMetricsAggregation("
              + "m.site.pid as pid, "
              + "COALESCE(SUM(m.sellerRevenue), 0.0) as sellerRevenue) "
              + "FROM SiteMetrics m WHERE m.startDate >= :startDate AND m.startDate < :stopDate AND m.site.pid IN (:pids) GROUP BY m.site")
  List<SiteMetricsAggregation> findSellerRevenue(
      @Param("startDate") Date startDate,
      @Param("stopDate") Date stopDate,
      @Param("pids") List<Long> pids);

  /**
   * Site Summary given a date range and a company pid
   *
   * @param startDate Start period for aggregation.
   * @param stopDate Stop period for aggregation.
   * @param companyPid The companyId.
   * @return {@link Page} of {@link SiteMetricsAggregation} instances based on parameters.
   */
  @Query(
      value =
          "SELECT new com.nexage.admin.core.sparta.jpa.model.SiteMetricsAggregation("
              + "COALESCE(SUM(m.sellerRevenue), 0.0) as sellerRevenue, "
              + "COALESCE(SUM(m.adRequested), 0) as adRequested, "
              + "COALESCE(SUM(m.adDelivered), 0) as adDelivered, "
              + "COALESCE(SUM(m.adServed), 0) as adServed, "
              + "COALESCE(SUM(m.adClicked), 0) as adClicked, "
              + "COALESCE(CASE WHEN (SUM(m.adRequested) = 0) THEN 0.0 else (100.0 * SUM(m.adServed) / SUM(m.adRequested)) END, 0.0) as fillRate, "
              + "COALESCE(CASE WHEN (SUM(m.adDelivered) = 0) THEN 0.0 else (1000.0 * SUM(m.sellerRevenue) / SUM(m.adDelivered)) END, 0.0) as ecpm, "
              + "COALESCE(CASE WHEN (SUM(m.adDelivered) = 0) THEN 0.0 else (100.0 * SUM(m.adClicked) / SUM(m.adDelivered)) END, 0.0) as ctr, "
              + "COALESCE(CASE WHEN (SUM(m.adRequested) = 0) THEN 0.0 else (1000.0 * SUM(m.sellerRevenue) / SUM(m.adRequested)) END, 0.0) as rpm) "
              + "FROM Site s LEFT JOIN s.metrics m WITH m.startDate >= :startDate AND m.startDate < :stopDate WHERE s.status >= 0 AND s.companyPid = :companyPid GROUP BY s.company")
  SiteMetricsAggregation findSummaryByCompanyPid(
      @Param("startDate") Date startDate,
      @Param("stopDate") Date stopDate,
      @Param("companyPid") Long companyPid);

  /**
   * @param companyPid seller company pid
   * @return HbPartnersAssociationView holds site pid with hb partner
   */
  @Query(
      "SELECT s.pid AS pid, h.hbPartner.pid AS hbPartnerPid FROM Site s JOIN s.hbPartnerSite h WHERE s.companyPid = :companyPid AND h.type = 1 ")
  List<HbPartnersAssociationView> findDefaultSitesPerPartners(@Param("companyPid") Long companyPid);

  /**
   * Count of s2s site associations for given company and given hbpartner pid list
   *
   * @param companyPid company pid.
   * @param hbPartnerPids List of hbpartner pids.
   * @return {@link Integer} Count of site pids.
   */
  @Query(
      value =
          "SELECT COUNT(s.pid) AS pid FROM Site s JOIN s.hbPartnerSite hps JOIN hps.hbPartner hp WHERE s.companyPid = :companyPid and hp.pid in (:hbPartnerPids)")
  Integer countSiteAssociationsByCompanyPidAndHbPartnerPids(
      @Param("companyPid") Long companyPid, @Param("hbPartnerPids") List<Long> hbPartnerPids);

  /**
   * Find companyPid by pid
   *
   * @param pid site pid.
   * @return {@link Long} company pid.
   */
  @Query(value = "SELECT s.companyPid FROM Site s WHERE s.pid = :pid AND s.status >= 0")
  Long findCompanyPidByPidWithStatusNotDeleted(@Param("pid") Long pid);

  @Query(value = "SELECT s.companyPid FROM Site s WHERE s.pid = :pid")
  Long findCompanyPidByPid(@Param("pid") Long pid);

  @Query(
      "SELECT NEW com.nexage.admin.core.model.SiteView(s.pid, s.name, c.pid, c.name) FROM Site s JOIN Company c ON s.companyPid = c.pid WHERE s.pid IN :sitePids ")
  List<SiteView> findBySitePidIn(@Param("sitePids") Collection<Long> sitePids);

  @Query(
      "SELECT NEW com.nexage.admin.core.model.Site(s.pid, s.name, s.status, s.dcn, s.id, s.version, s.companyPid, s.metadataEnablement, s.hbEnabled) FROM Site s WHERE s.companyPid = :companyPid AND s.status >= 0")
  Page<Site> findLimitedSiteByCompanyPid(@Param("companyPid") Long companyPid, Pageable pageable);

  @Query(
      """
          SELECT NEW com.nexage.admin.core.model.Site(s.pid, s.name, s.status, s.dcn, s.id,
            s.version, s.companyPid, s.metadataEnablement, s.hbEnabled)
          FROM Site s
          WHERE s.company.sellerSeat.pid = :sellerSeatPid
          AND s.status >= 0""")
  Page<Site> findLimitedSiteBySellerSeatPid(
      @Param("sellerSeatPid") Long sellerSeatPid, Pageable pageable);

  boolean existsByCompanyPidAndGroupsEnabledTrueAndStatusGreaterThanEqual(
      Long companyPid, Status status);

  boolean existsByDcn(String dcn);

  boolean existsByPidNotAndCompanyPidAndName(Long pid, Long companyPid, String name);

  boolean existsByPidAndCompanyPidAndStatus(Long pid, Long companyPid, Status status);

  boolean existsByCompanyPidAndStatusNot(Long companyPid, Status status);

  long countByCompanyPidAndStatusNot(Long companyPid, Status status);

  List<Site> findByDefaultRtbProfile_PidIn(List<Long> rtbProfilePids);

  List<Site> findByDefaultRtbProfile_PidAndStatusNot(Long rtbProfilePid, Status status);

  @Query("SELECT s.pid FROM Site s WHERE s.defaultRtbProfile.pid = :rtbProfilePid AND status >= 0")
  List<Long> findPidsByDefaultRtbProfile_PidWithStatusNotDeleted(
      @Param("rtbProfilePid") Long rtbProfilePid);

  @Query("SELECT s.pid FROM Site s WHERE s.companyPid = :companyPid")
  List<Long> findPidsByCompanyPid(@Param("companyPid") Long companyPid);

  @Query("SELECT s.pid FROM Site s WHERE s.companyPid IN :companyPids AND s.status >= 0")
  Set<Long> findPidsByCompanyPidsWithStatusNotDeleted(
      @Param("companyPids") Collection<Long> companyPids);

  @Query(
      "SELECT s.pid FROM Site s WHERE s.companyPid IN :companyPids AND s.status >= 0 AND s.pid NOT IN (SELECT pk.siteId FROM UserRestrictedSite WHERE pk.userId = :userPid)")
  Set<Long> findPidsByCompanyPidsWithStatusNotDeletedAndSiteNotRestricted(
      @Param("userPid") Long userPid, @Param("companyPids") Collection<Long> companyPids);

  @Query(
      "SELECT s FROM Site s WHERE s.companyPid = :companyPid AND s.status >= 0 AND s.pid NOT IN (SELECT urs.pk.siteId FROM UserRestrictedSite urs WHERE urs.pk.userId = :userPid)")
  List<Site> findByCompanyPidWithStatusNotDeletedAndSiteNotRestricted(
      @Param("userPid") Long userPid, @Param("companyPid") Long companyPid);

  @Query(
      "SELECT new com.nexage.admin.core.dto.SiteSummaryDTO(s.id, s.pid, s.url, s.name, s.globalAliasName, s.type, s.platform, s.status, s.live, c.pid AS sellerPid, c.name AS sellerName, s.domain) FROM Site s JOIN s.company c WHERE s.status >= 0")
  List<SiteSummaryDTO> findSummaryDtosWithStatusNotDeleted();

  @Query(
      "SELECT new com.nexage.admin.core.dto.SiteSummaryDTO(s.id, s.pid, s.url, s.name, s.globalAliasName, s.type, s.platform, s.status, s.live, c.pid AS sellerPid, c.name AS sellerName, s.domain) FROM Site s JOIN s.company c WHERE s.status >= 0 AND c.pid = :companyPid")
  List<SiteSummaryDTO> findSummaryDtosByCompanyPidWithStatusNotDeleted(
      @Param("companyPid") Long companyPid);

  @Query(
      "SELECT new com.nexage.admin.core.dto.SiteSummaryDTO(s.id, s.pid, s.url, s.name, s.globalAliasName, s.type, s.platform, s.status, s.live, c.pid AS sellerPid, c.name AS sellerName, s.domain) FROM Site s JOIN s.company c WHERE s.status >= 0 AND c.pid IN :companyPids AND s.pid NOT IN (SELECT pk.siteId FROM UserRestrictedSite WHERE pk.userId = :userPid)")
  List<SiteSummaryDTO> findSummaryDtosByCompanyPidsWithStatusNotDeletedAndSiteNotRestricted(
      @Param("userPid") Long userPid, @Param("companyPids") Collection<Long> companyPids);

  @Query(
      value =
          "SELECT p.site_pid AS sitePid, p.pid AS positionPid, p.name AS positionName, tr.pid AS tierPid, t.pid AS tagPid FROM tier_tag tt JOIN tier tr ON tr.pid = tt.tier_pid JOIN position p ON tr.position_pid = p.pid JOIN tag t ON t.pid = tt.tag_pid WHERE t.status = 0 AND p.site_pid = :pid ORDER BY p.site_pid, tr.position_pid, tt.tier_pid",
      nativeQuery = true)
  List<SiteWithInactiveTagProjection> findSiteWithInactiveTagProjectionsByPid(
      @Param("pid") Long pid);

  @Query(
      value =
          "SELECT p.site_pid AS sitePid, p.pid AS positionPid, p.name AS positionName, tr.pid AS tierPid, t.pid AS tagPid FROM tier_tag tt JOIN tier tr ON tr.pid = tt.tier_pid JOIN position p ON tr.position_pid = p.pid JOIN tag t ON t.pid = tt.tag_pid WHERE t.status = 0 ORDER BY p.site_pid, tr.position_pid, tt.tier_pid",
      nativeQuery = true)
  List<SiteWithInactiveTagProjection> findAllSiteWithInactiveTagProjections();

  // findById causes slow SQL queries, use findByPid instead
  Optional<Site> findByPid(Long pid);
}
