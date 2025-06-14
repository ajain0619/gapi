package com.nexage.admin.core.repository;

import com.nexage.admin.core.model.BidderDeviceType;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BidderDeviceTypeRepository
    extends JpaRepository<BidderDeviceType, Long>, JpaSpecificationExecutor<BidderDeviceType> {
  @Query("SELECT deviceTypeId from BidderDeviceType where bidderPid = :bidderPid")
  Set<Integer> getAllowedDeviceTypesForBidderConfig(@Param("bidderPid") Long bidderPid);
}
