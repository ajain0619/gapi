package com.nexage.app.util.validator;

import com.nexage.admin.core.enums.DealCategory;
import com.nexage.admin.core.enums.RuleTargetType;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class RuleDealCategoryValidation implements RuleTargetValidation {
  private static final Set<String> SUPPORTED_DEAL_CATEGORIES =
      Set.of(String.valueOf(DealCategory.SSP.asInt()));

  @Override
  public boolean isValid(String data) {
    List<String> commaSeparatedData = Arrays.stream(data.split(",")).map(String::trim).toList();

    return !commaSeparatedData.isEmpty()
        && SUPPORTED_DEAL_CATEGORIES.stream().filter(commaSeparatedData::contains).count()
            == commaSeparatedData.size();
  }

  @Override
  public RuleTargetType getRuleTarget() {
    return RuleTargetType.DEAL_CATEGORY;
  }
}
