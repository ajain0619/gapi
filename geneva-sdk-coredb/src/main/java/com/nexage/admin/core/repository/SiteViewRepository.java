package com.nexage.admin.core.repository;

import com.nexage.admin.core.model.SiteView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SiteViewRepository
    extends JpaRepository<SiteView, Long>, JpaSpecificationExecutor<SiteView> {

  @Query(
      """
          SELECT NEW com.nexage.admin.core.model.SiteView(s.pid, s.name, s.status, s.url, c.name)
          FROM Site s
          JOIN Company c ON s.companyPid = c.pid
          WHERE s.companyPid = :sellerPid""")
  Page<SiteView> findAllSellerSites(@Param("sellerPid") Long sellerPid, Pageable pageable);

  @Query(
      """
          SELECT NEW com.nexage.admin.core.model.SiteView(s.pid, s.name, s.status, s.url, c.name)
          FROM Site s
          JOIN Company c ON s.companyPid = c.pid
          WHERE s.company.sellerSeat.pid = :sellerSeatPid""")
  Page<SiteView> findAllSellerSitesForSellerSeat(
      @Param("sellerSeatPid") Long sellerSeatPid, Pageable pageable);

  @Query(
      """
          SELECT NEW com.nexage.admin.core.model.SiteView(s.pid, s.name, s.status, s.url, c.name)
          FROM Site s
          JOIN Company c ON s.companyPid = c.pid
          WHERE s.companyPid = :sellerPid
          AND s.name LIKE %:qt%""")
  Page<SiteView> searchSellerSitesByName(
      @Param("sellerPid") Long sellerPid, @Param("qt") String qt, Pageable pageable);

  @Query(
      """
          SELECT NEW com.nexage.admin.core.model.SiteView(s.pid, s.name, s.status, s.url, c.name)
          FROM Site s
          JOIN Company c ON s.companyPid = c.pid
          WHERE s.company.sellerSeat.pid = :sellerSeatPid
          AND s.name LIKE %:qt%""")
  Page<SiteView> searchSellerSitesByNameForSellerSeat(
      @Param("sellerSeatPid") Long sellerSeatPid, @Param("qt") String qt, Pageable pageable);
}
