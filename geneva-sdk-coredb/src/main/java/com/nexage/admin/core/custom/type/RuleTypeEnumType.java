package com.nexage.admin.core.custom.type;

import com.nexage.admin.core.enums.RuleType;

public class RuleTypeEnumType extends BaseEnumType<RuleType> {
  @Override
  public Class<RuleType> returnedClass() {
    return RuleType.class;
  }
}
