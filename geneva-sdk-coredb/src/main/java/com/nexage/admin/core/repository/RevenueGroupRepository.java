package com.nexage.admin.core.repository;

import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.model.RevenueGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface RevenueGroupRepository
    extends JpaRepository<RevenueGroup, Long>, JpaSpecificationExecutor<RevenueGroup> {

  Page<RevenueGroup> findAllByStatus(Status status, Pageable pageable);

  long countByPidIn(Iterable<Long> pids);
}
