package com.nexage.admin.core.custom.type;

import com.nexage.admin.core.enums.RuleActionType;

public class RuleActionTypeEnumType extends BaseEnumType<RuleActionType> {
  @Override
  public Class<RuleActionType> returnedClass() {
    return RuleActionType.class;
  }
}
