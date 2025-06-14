package com.nexage.admin.core.custom.type;

import com.nexage.admin.core.enums.RuleTargetType;

public class RuleTargetTypeEnumType extends BaseEnumType<RuleTargetType> {
  @Override
  public Class<RuleTargetType> returnedClass() {
    return RuleTargetType.class;
  }
}
