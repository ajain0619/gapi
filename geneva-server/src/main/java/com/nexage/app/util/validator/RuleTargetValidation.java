package com.nexage.app.util.validator;

import com.nexage.admin.core.enums.RuleTargetType;

public interface RuleTargetValidation {
  boolean isValid(String data);

  RuleTargetType getRuleTarget();
}
