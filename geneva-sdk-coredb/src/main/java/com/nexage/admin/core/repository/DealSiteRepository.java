package com.nexage.admin.core.repository;

import com.nexage.admin.core.sparta.jpa.model.DealSite;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DealSiteRepository
    extends JpaRepository<DealSite, Long>, JpaSpecificationExecutor<DealSite> {

  /**
   * Find al deal sites with specified deal PID.
   *
   * @param dealPid deal PID
   * @return list of sites
   */
  List<DealSite> findByDealPid(Long dealPid);

  /**
   * Delete all sites with specified deal PID.
   *
   * @param dealPid deal PID
   */
  @Query("delete from DealSite ds where ds.deal.pid = :dealPid")
  @Modifying
  void deleteByDealPid(@Param("dealPid") Long dealPid);
}
