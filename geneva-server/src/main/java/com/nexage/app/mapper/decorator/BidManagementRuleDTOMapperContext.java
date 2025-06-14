package com.nexage.app.mapper.decorator;

import com.nexage.admin.core.repository.CompanyRepository;
import com.nexage.admin.core.repository.RuleDeployedCompanyRepository;
import com.nexage.admin.core.repository.RuleDeployedPositionRepository;
import com.nexage.admin.core.repository.RuleDeployedSiteRepository;
import com.nexage.admin.core.repository.RuleIntendedActionRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Getter
@Component
public class BidManagementRuleDTOMapperContext {
  private final CompanyRepository companyRepository;
  private final RuleIntendedActionRepository ruleIntendedActionRepository;
  private final RuleDeployedCompanyRepository ruleDeployedCompanyRepository;
  private final RuleDeployedSiteRepository ruleDeployedSiteRepository;
  private final RuleDeployedPositionRepository ruleDeployedPositionRepository;
}
