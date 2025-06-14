package com.nexage.admin.core.repository;

import com.nexage.admin.core.model.RuleIntendedAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface RuleIntendedActionRepository
    extends JpaRepository<RuleIntendedAction, Long>, JpaSpecificationExecutor<RuleIntendedAction> {}
