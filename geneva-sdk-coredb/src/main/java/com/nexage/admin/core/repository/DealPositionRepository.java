package com.nexage.admin.core.repository;

import com.nexage.admin.core.sparta.jpa.model.DealPosition;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DealPositionRepository
    extends JpaRepository<DealPosition, Long>, JpaSpecificationExecutor<DealPosition> {

  /**
   * Find al deal positions with specified deal PID.
   *
   * @param dealPid deal PID
   * @return list of positions
   */
  List<DealPosition> findByDealPid(Long dealPid);

  /**
   * Delete all positions with specified deal PID.
   *
   * @param dealPid deal PID
   */
  @Query("delete from DealPosition d where d.deal.pid = :dealPid")
  @Modifying
  void deleteByDealPid(@Param("dealPid") Long dealPid);
}
