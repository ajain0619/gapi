package com.nexage.admin.core.repository;

import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.enums.VideoSupport;
import com.nexage.admin.core.model.HbPartnersAssociationView;
import com.nexage.admin.core.model.Position;
import com.nexage.admin.core.sparta.jpa.model.PositionMetricsAggregation;
import com.nexage.admin.core.sparta.jpa.model.PositionView;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PositionRepository
    extends JpaRepository<Position, Long>, JpaSpecificationExecutor<Position> {

  String siteMetricsTableAlias = "m";
  String siteTableAlias = "n";

  /**
   * Aggregate {@link Position} and {@link com.nexage.admin.core.model.PositionMetrics} into a
   * single projection, based on request. This query does not accept search based in any query
   * param.
   *
   * @param startDate Start period for aggregation.
   * @param stopDate Stop period for aggregation.
   * @param sitePid The site pid.
   * @param pageable Pagination based on {@link Pageable}
   * @return {@link Page} of {@link PositionMetricsAggregation} instances based on parameters.
   */
  @Query(
      value =
          "SELECT new com.nexage.admin.core.sparta.jpa.model.PositionMetricsAggregation("
              + "p.pid, p.name, p.memo, p.status, p.placementCategory, p.sitePid, p.trafficType, "
              + "COALESCE(SUM(m.adRequested), 0) as adRequested, "
              + "COALESCE(SUM(m.adDelivered), 0) as adDelivered, "
              + "COALESCE(SUM(m.adServed), 0) as adServed, "
              + "COALESCE(SUM(m.sellerRevenue), 0.0) as sellerRevenue, "
              + "COALESCE(CASE WHEN (SUM(m.adDelivered) = 0) THEN 0.0 else (1000.0 * SUM(m.sellerRevenue) / SUM(m.adDelivered)) END, 0.0) as ecpm, "
              + "COALESCE(CASE WHEN (SUM(m.adRequested) = 0) THEN 0.0 else (1000.0 * SUM(m.sellerRevenue) / SUM(m.adRequested)) END, 0.0) as rpm, "
              + "COALESCE(CASE WHEN (SUM(m.adDelivered) = 0) THEN 0.0 else (100.0 * SUM(m.adClicked) / SUM(m.adDelivered)) END, 0.0) as ctr, "
              + "COALESCE(CASE WHEN (SUM(m.adRequested) = 0) THEN 0.0 else (100.0 * SUM(m.adServed) / SUM(m.adRequested)) END, 0.0) as fillRate) "
              + "FROM Position p LEFT JOIN p.metrics "
              + siteMetricsTableAlias
              + " WITH m.startDate >= :startDate AND m.startDate < :stopDate  WHERE p.sitePid = :sitePid AND p.status != -1 GROUP BY p")
  Page<PositionMetricsAggregation> findPositionsWithMetrics(
      @Param("startDate") Date startDate,
      @Param("stopDate") Date stopDate,
      @Param("sitePid") Long sitePid,
      Pageable pageable);

  /**
   * Aggregate {@link Position} and {@link com.nexage.admin.core.model.PositionMetrics} into a
   * single projection, based on request. This query search companies only by name due to
   * aggregation restrictions in this specific version of Hibernate & Spring.
   *
   * @param startDate Start period for aggregation.
   * @param stopDate Stop period for aggregation.
   * @param name The site name.
   * @param pageable Pagination based on {@link Pageable}
   * @return {@link Page} of {@link PositionMetricsAggregation} instances based on parameters.
   */
  @Query(
      value =
          "SELECT new com.nexage.admin.core.sparta.jpa.model.PositionMetricsAggregation("
              + "p.pid, p.name, p.memo, p.status, p.placementCategory, p.sitePid, p.trafficType, "
              + "COALESCE(SUM(m.adRequested), 0) as adRequested, "
              + "COALESCE(SUM(m.adDelivered), 0) as adDelivered, "
              + "COALESCE(SUM(m.adServed), 0) as adServed, "
              + "COALESCE(SUM(m.sellerRevenue), 0.0) as sellerRevenue, "
              + "COALESCE(CASE WHEN (SUM(m.adDelivered) = 0) THEN 0.0 else (1000.0 * SUM(m.sellerRevenue) / SUM(m.adDelivered)) END, 0.0) as ecpm, "
              + "COALESCE(CASE WHEN (SUM(m.adRequested) = 0) THEN 0.0 else (1000.0 * SUM(m.sellerRevenue) / SUM(m.adRequested)) END, 0.0) as rpm, "
              + "COALESCE(CASE WHEN (SUM(m.adDelivered) = 0) THEN 0.0 else (100.0 * SUM(m.adClicked) / SUM(m.adDelivered)) END, 0.0) as ctr, "
              + "COALESCE(CASE WHEN (SUM(m.adRequested) = 0) THEN 0.0 else (100.0 * SUM(m.adServed) / SUM(m.adRequested)) END, 0.0) as fillRate) "
              + "FROM Position p LEFT JOIN p.metrics "
              + siteMetricsTableAlias
              + " WITH m.startDate >= :startDate AND m.startDate < :stopDate  WHERE p.sitePid = :siteId AND UPPER(p.name) LIKE CONCAT('%', UPPER(:name), '%') AND p.status != -1  GROUP BY p")
  Page<PositionMetricsAggregation> findPositionsByNameWithMetrics(
      @Param("startDate") Date startDate,
      @Param("stopDate") Date stopDate,
      @Param("name") String name,
      @Param("siteId") Long siteId,
      Pageable pageable);

  /**
   * Aggregate {@link Position} and {@link com.nexage.admin.core.model.PositionMetrics} into a
   * single projection, based on request. This query search companies only by pid due to aggregation
   * restrictions in this specific version of Hibernate & Spring.
   *
   * @param startDate Start period for aggregation.
   * @param stopDate Stop period for aggregation.
   * @param pid The site pid.
   * @param pageable Pagination based on {@link Pageable}
   * @return {@link Page} of {@link PositionMetricsAggregation} instances based on parameters.
   */
  @Query(
      value =
          "SELECT new com.nexage.admin.core.sparta.jpa.model.PositionMetricsAggregation("
              + "p.pid, p.name, p.memo, p.status, p.placementCategory, p.sitePid, p.trafficType, "
              + "COALESCE(SUM(m.adRequested), 0) as adRequested, "
              + "COALESCE(SUM(m.adDelivered), 0) as adDelivered, "
              + "COALESCE(SUM(m.adServed), 0) as adServed, "
              + "COALESCE(SUM(m.sellerRevenue), 0.0) as sellerRevenue, "
              + "COALESCE(CASE WHEN (SUM(m.adDelivered) = 0) THEN 0.0 else (1000.0 * SUM(m.sellerRevenue) / SUM(m.adDelivered)) END, 0.0) as ecpm, "
              + "COALESCE(CASE WHEN (SUM(m.adRequested) = 0) THEN 0.0 else (1000.0 * SUM(m.sellerRevenue) / SUM(m.adRequested)) END, 0.0) as rpm, "
              + "COALESCE(CASE WHEN (SUM(m.adDelivered) = 0) THEN 0.0 else (100.0 * SUM(m.adClicked) / SUM(m.adDelivered)) END, 0.0) as ctr, "
              + "COALESCE(CASE WHEN (SUM(m.adRequested) = 0) THEN 0.0 else (100.0 * SUM(m.adServed) / SUM(m.adRequested)) END, 0.0) as fillRate) "
              + "FROM Position p LEFT JOIN p.metrics "
              + siteMetricsTableAlias
              + " WITH m.startDate >= :startDate AND m.startDate < :stopDate  "
              + "WHERE p.sitePid = :siteId AND p.pid = :pid AND p.status != -1 GROUP BY p")
  Page<PositionMetricsAggregation> findPositionsByPidWithMetrics(
      @Param("startDate") Date startDate,
      @Param("stopDate") Date stopDate,
      @Param("pid") Long pid,
      @Param("siteId") Long siteId,
      Pageable pageable);

  /**
   * Aggregate {@link Position} and {@link com.nexage.admin.core.model.PositionMetrics} into a
   * single projection, based on request.
   *
   * @param startDate Start period for aggregation.
   * @param stopDate Stop period for aggregation.
   * @param pids List of Positions pid
   * @return {@link List} of {@link PositionMetricsAggregation} instances based on parameters.
   */
  @Query(
      value =
          "SELECT new com.nexage.admin.core.sparta.jpa.model.PositionMetricsAggregation("
              + "p.pid, p.name, p.memo, p.status, p.placementCategory, p.sitePid, p.trafficType, "
              + "COALESCE(SUM(m.adRequested), 0) as adRequested, "
              + "COALESCE(SUM(m.adDelivered), 0) as adDelivered, "
              + "COALESCE(SUM(m.adServed), 0) as adServed, "
              + "COALESCE(SUM(m.sellerRevenue), 0.0) as sellerRevenue, "
              + "COALESCE(CASE WHEN (SUM(m.adDelivered) = 0) THEN 0.0 else (1000.0 * SUM(m.sellerRevenue) / SUM(m.adDelivered)) END, 0.0) as ecpm, "
              + "COALESCE(CASE WHEN (SUM(m.adRequested) = 0) THEN 0.0 else (1000.0 * SUM(m.sellerRevenue) / SUM(m.adRequested)) END, 0.0) as rpm, "
              + "COALESCE(CASE WHEN (SUM(m.adDelivered) = 0) THEN 0.0 else (100.0 * SUM(m.adClicked) / SUM(m.adDelivered)) END, 0.0) as ctr, "
              + "COALESCE(CASE WHEN (SUM(m.adRequested) = 0) THEN 0.0 else (100.0 * SUM(m.adServed) / SUM(m.adRequested)) END, 0.0) as fillRate) "
              + "FROM Position p LEFT JOIN p.metrics "
              + siteMetricsTableAlias
              + " WITH m.startDate >= :startDate AND m.startDate < :stopDate  "
              + "WHERE p.pid IN (:pids) AND p.status != -1 GROUP BY p")
  List<PositionMetricsAggregation> findPositionsByPidsWithMetrics(
      @Param("startDate") Date startDate,
      @Param("stopDate") Date stopDate,
      @Param("pids") List<Long> pids);

  /**
   * Aggregate {@link Position} and {@link com.nexage.admin.core.model.PositionMetrics} into a
   * single projection, based on request. This query search companies only by name due to
   * aggregation restrictions in this specific version of Hibernate & Spring.
   *
   * @param startDate Start period for aggregation.
   * @param stopDate Stop period for aggregation.
   * @param name The site name.
   * @param pageable Pagination based on {@link Pageable}
   * @return {@link Page} of {@link PositionMetricsAggregation} instances based on parameters.
   */
  @Query(
      value =
          "SELECT new com.nexage.admin.core.sparta.jpa.model.PositionMetricsAggregation("
              + "p.pid, p.name, p.memo, p.status, p.placementCategory, p.sitePid, p.trafficType, "
              + "COALESCE(SUM(m.adRequested), 0) as adRequested, "
              + "COALESCE(SUM(m.adDelivered), 0) as adDelivered, "
              + "COALESCE(SUM(m.adServed), 0) as adServed, "
              + "COALESCE(SUM(m.sellerRevenue), 0.0) as sellerRevenue, "
              + "COALESCE(CASE WHEN (SUM(m.adDelivered) = 0) THEN 0.0 else (1000.0 * SUM(m.sellerRevenue) / SUM(m.adDelivered)) END, 0.0) as ecpm, "
              + "COALESCE(CASE WHEN (SUM(m.adRequested) = 0) THEN 0.0 else (1000.0 * SUM(m.sellerRevenue) / SUM(m.adRequested)) END, 0.0) as rpm, "
              + "COALESCE(CASE WHEN (SUM(m.adDelivered) = 0) THEN 0.0 else (100.0 * SUM(m.adClicked) / SUM(m.adDelivered)) END, 0.0) as ctr, "
              + "COALESCE(CASE WHEN (SUM(m.adRequested) = 0) THEN 0.0 else (100.0 * SUM(m.adServed) / SUM(m.adRequested)) END, 0.0) as fillRate) "
              + "FROM Position p LEFT JOIN p.metrics "
              + siteMetricsTableAlias
              + " WITH m.startDate >= :startDate AND m.startDate < :stopDate"
              + " LEFT JOIN p.site "
              + siteTableAlias
              + " WHERE n.companyPid = :sellerId AND UPPER (p.memo) LIKE CONCAT('%', UPPER(:name), '%') AND p.status != -1  GROUP BY p")
  Page<PositionMetricsAggregation> findPositionsByNameWithMetricsNoSitePid(
      @Param("startDate") Date startDate,
      @Param("stopDate") Date stopDate,
      @Param("sellerId") Long sellerId,
      @Param("name") String name,
      Pageable pageable);

  /**
   * @param sitePid site pid
   * @return HbPartnersAssociationView holds position pid with hb partner
   */
  @Query(
      "SELECT p.pid AS pid, h.hbPartner.pid AS hbPartnerPid, p.status, h.type as type FROM Position p JOIN p.hbPartnerPosition h WHERE p.sitePid = :sitePid AND h.type IN (1,2,3) AND p.status > 0")
  List<HbPartnersAssociationView> findDefaultPositionsPerPartners(@Param("sitePid") Long sitePid);

  /**
   * @param placementPid placement pid
   * @return companyPid of the company which owns the placement
   */
  @Query(
      "SELECT c.pid AS pid FROM Position p JOIN p.site s JOIN s.company c WHERE p.pid = :placementPid AND s.status > 0")
  Long findCompanyPidByPlacementPid(@Param("placementPid") Long placementPid);

  @Query(
      value =
          "SELECT NEW com.nexage.admin.core.sparta.jpa.model.PositionView(p.pid, p.name, p.memo, p.version,p.sitePid) FROM Position p WHERE p.pid IN :positionPids")
  List<PositionView> findAllByPidIn(@Param("positionPids") Collection<Long> positionPids);

  /**
   * Check whether an active {@link Position} exists by pid.
   *
   * @param placementPid
   * @return if position is present or not
   */
  @Query(
      "SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Position p WHERE p.pid = :placementPid AND p.status > 0")
  boolean existsByPid(@Param("placementPid") Long placementPid);

  /**
   * Get {@link VideoSupport} for an active {@link Position} by pid
   *
   * @param placementPid
   * @return video support for the placement
   */
  @Query(
      "SELECT p.videoSupport AS videoSupport FROM Position p WHERE p.pid = :placementPid AND p.status > 0")
  VideoSupport findVideoSupportByPlacementPid(@Param("placementPid") Long placementPid);

  List<Position> findByDefaultRtbProfile_PidIn(List<Long> rtbProfilePids);

  List<Position> findByDefaultRtbProfile_PidAndStatusNot(Long rtbProfilePid, Status status);

  @Query("SELECT s.pid FROM Site s WHERE s.defaultRtbProfile.pid = :rtbProfilePid AND status >= 0")
  List<Long> findPidsByDefaultRtbProfile_PidAndStatusNotDeleted(
      @Param("rtbProfilePid") Long rtbProfilePid);

  List<Position> findByPositionAliasName(String aliasName);

  Position findByPidAndSite_CompanyPidAndStatus(long pid, long companyPid, Status status);

  long countBySitePidAndStatusNot(long sitePid, Status status);

  @Query(
      "SELECT p.pid FROM Position p INNER JOIN Site s ON s.pid = p.sitePid INNER JOIN Company c ON c.pid = s.companyPid WHERE c.pid = :companyPid AND c.status >=0")
  Set<Long> findPidsByCompanyPid(@Param("companyPid") Long companyPid);
}
