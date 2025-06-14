package com.nexage.admin.core.repository;

import com.nexage.admin.core.model.DirectDealView;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface DirectDealViewRepository
    extends JpaRepository<DirectDealView, Long>, JpaSpecificationExecutor<DirectDealView> {
  List<DirectDealView> findByPidIn(List<Long> pids);
}
