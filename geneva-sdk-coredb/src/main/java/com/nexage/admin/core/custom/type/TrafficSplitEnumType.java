package com.nexage.admin.core.custom.type;

import com.nexage.admin.core.enums.TrafficSplitType;

public class TrafficSplitEnumType extends BaseEnumType<TrafficSplitType> {

  @Override
  public Class<TrafficSplitType> returnedClass() {
    return TrafficSplitType.class;
  }
}
