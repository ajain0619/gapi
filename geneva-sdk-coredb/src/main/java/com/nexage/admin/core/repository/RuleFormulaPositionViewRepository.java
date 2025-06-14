package com.nexage.admin.core.repository;

import com.nexage.admin.core.model.RuleFormulaPositionView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface RuleFormulaPositionViewRepository
    extends JpaRepository<RuleFormulaPositionView, Long>,
        JpaSpecificationExecutor<RuleFormulaPositionView> {}
