package com.nexage.admin.core.repository;

import com.nexage.admin.core.sparta.jpa.model.DealPublisher;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DealPublisherRepository
    extends JpaRepository<DealPublisher, Long>, JpaSpecificationExecutor<DealPublisher> {

  /**
   * Find all publishers with specified deal PID.
   *
   * @param dealPid deal PID
   * @return list of publishers
   */
  List<DealPublisher> findByDealPid(Long dealPid);

  /**
   * Delete all publishers with specified deal PID.
   *
   * @param dealPid deal PID
   */
  @Query("delete from DealPublisher dp where dp.deal.pid = :dealPid")
  @Modifying
  void deleteByDealPid(@Param("dealPid") Long dealPid);
}
