package com.nexage.admin.core.repository;

import com.nexage.admin.core.model.DealAppBundleData;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DealAppBundleDataRepository extends JpaRepository<DealAppBundleData, Long> {

  /**
   * Delete all deal app bundle entries with specified deal PID.
   *
   * @param dealPid deal PID
   */
  void deleteByDealPid(@Param("dealPid") Long dealPid);

  /**
   * Get all deal app bundle entries with specified deal PID.
   *
   * @param dealPid deal PID
   */
  @Query(
      "SELECT abd.appBundleId FROM DealAppBundleData dabd INNER JOIN AppBundleData abd ON dabd.appBundleData.pid = abd.pid WHERE dabd.dealPid = :dealPid")
  List<String> getDealAppBundleDataByDealPid(@Param("dealPid") Long dealPid);
}
