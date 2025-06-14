package com.nexage.admin.core.repository;

import com.nexage.admin.core.model.RuleDeployedSite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface RuleDeployedSiteRepository
    extends JpaRepository<RuleDeployedSite, Long>, JpaSpecificationExecutor<RuleDeployedSite> {}
