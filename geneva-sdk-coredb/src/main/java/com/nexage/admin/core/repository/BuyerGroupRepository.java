package com.nexage.admin.core.repository;

import com.nexage.admin.core.model.BuyerGroup;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BuyerGroupRepository
    extends JpaRepository<BuyerGroup, Long>, JpaSpecificationExecutor<BuyerGroup> {
  long countByCompanyPidAndPidIn(Long companyPid, Set<Long> pids);

  List<BuyerGroup> findAllByCompanyPid(long companyPid);
}
