package com.nexage.admin.core.repository;

import com.nexage.admin.core.model.RuleDeployedPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface RuleDeployedPositionRepository
    extends JpaRepository<RuleDeployedPosition, Long>,
        JpaSpecificationExecutor<RuleDeployedPosition> {}
