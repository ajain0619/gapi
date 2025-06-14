package com.nexage.app.util.validator;

import static com.nexage.app.util.validator.RuleTargetDataValidationHelper.convertToList;
import static com.nexage.app.util.validator.RuleTargetDataValidationHelper.hasUniqueElements;

import com.nexage.admin.core.enums.RuleTargetType;
import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.repository.CompanyRepository;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class RulePublisherValidation implements RuleTargetValidation {

  private final CompanyRepository companyRepository;

  public RulePublisherValidation(CompanyRepository companyRepository) {
    this.companyRepository = companyRepository;
  }

  @Override
  public boolean isValid(String data) {
    List<Long> publisherPids = convertToList(data, ",");
    if (!publisherPids.isEmpty() && hasUniqueElements(publisherPids)) {
      Set<Company> companies = companyRepository.findSellersWithSpecificPids(publisherPids);
      return companies.size() == publisherPids.size()
          && companies.stream().allMatch(company -> company.getStatus() == Status.ACTIVE);
    }
    return false;
  }

  @Override
  public RuleTargetType getRuleTarget() {
    return RuleTargetType.PUBLISHER;
  }
}
