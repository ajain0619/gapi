package com.nexage.admin.core.repository;

import com.nexage.admin.core.model.feeadjustment.FeeAdjustmentCompanyView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface FeeAdjustmentCompanyViewRepository
    extends JpaRepository<FeeAdjustmentCompanyView, Long>,
        JpaSpecificationExecutor<FeeAdjustmentCompanyView> {}
