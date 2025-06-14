package com.nexage.admin.core.repository;

import com.nexage.admin.core.model.BuyerSeat;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BuyerSeatRepository
    extends JpaRepository<BuyerSeat, Long>, JpaSpecificationExecutor<BuyerSeat> {
  long countByCompanyPidAndSeatIn(Long companyPid, Set<String> seats);

  BuyerSeat findBySeatAndCompanyPid(String seat, Long companyPid);
}
