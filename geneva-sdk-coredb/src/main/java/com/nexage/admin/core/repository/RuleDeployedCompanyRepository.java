package com.nexage.admin.core.repository;

import com.nexage.admin.core.model.RuleDeployedCompany;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface RuleDeployedCompanyRepository
    extends JpaRepository<RuleDeployedCompany, Long>,
        JpaSpecificationExecutor<RuleDeployedCompany> {}
