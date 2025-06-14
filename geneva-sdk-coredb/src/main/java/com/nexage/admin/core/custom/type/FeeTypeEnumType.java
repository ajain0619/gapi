package com.nexage.admin.core.custom.type;

import com.nexage.admin.core.enums.FeeType;

public class FeeTypeEnumType extends BaseEnumType<FeeType> {
  @Override
  public Class<FeeType> returnedClass() {
    return FeeType.class;
  }
}
