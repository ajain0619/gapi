package com.nexage.app.util.validator;

import static com.nexage.app.util.validator.RuleTargetDataValidationHelper.convertToList;
import static com.nexage.app.util.validator.RuleTargetDataValidationHelper.hasUniqueElements;

import com.nexage.admin.core.enums.RuleTargetType;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class RuleRevGroupValidation implements RuleTargetValidation {

  @Override
  public boolean isValid(String data) {
    List<Long> revGroupPids = convertToList(data, ",");
    return !revGroupPids.isEmpty() && hasUniqueElements(revGroupPids);
  }

  @Override
  public RuleTargetType getRuleTarget() {
    return RuleTargetType.REVGROUP;
  }
}
