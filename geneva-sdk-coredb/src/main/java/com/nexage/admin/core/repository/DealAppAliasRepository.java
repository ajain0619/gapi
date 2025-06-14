package com.nexage.admin.core.repository;

import com.nexage.admin.core.model.DealAppAlias;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DealAppAliasRepository extends JpaRepository<DealAppAlias, Long> {

  /**
   * Delete all deal app alias entries with specified deal PID.
   *
   * @param dealPid deal PID
   */
  void deleteByDealPid(@Param("dealPid") Long dealPid);

  /**
   * Get all deal app alias entries with specified deal PID.
   *
   * @param dealPid deal PID
   */
  @Query(
      "SELECT aa.appAlias FROM DealAppAlias daa INNER JOIN AppAlias aa ON daa.appAlias.pid = aa.pid WHERE daa.dealPid = :dealPid")
  List<String> getDealAppAliasesByDealPid(@Param("dealPid") Long dealPid);
}
