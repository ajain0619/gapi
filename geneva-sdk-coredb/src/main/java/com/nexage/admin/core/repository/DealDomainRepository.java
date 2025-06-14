package com.nexage.admin.core.repository;

import com.nexage.admin.core.model.DealDomain;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DealDomainRepository extends JpaRepository<DealDomain, Long> {

  /**
   * Delete all deal domain entries with specified deal PID.
   *
   * @param dealPid deal PID
   */
  void deleteByDealPid(@Param("dealPid") Long dealPid);

  /**
   * Get all deal domain entries with specified deal PID.
   *
   * @param dealPid deal PID
   */
  @Query(
      "SELECT d.domain FROM DealDomain dd INNER JOIN Domain d ON dd.domain.pid = d.pid WHERE dd.dealPid = :dealPid")
  List<String> getDealDomainsByDealPid(@Param("dealPid") Long dealPid);
}
