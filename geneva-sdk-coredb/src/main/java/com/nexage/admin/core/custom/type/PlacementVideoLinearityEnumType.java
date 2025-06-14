package com.nexage.admin.core.custom.type;

import com.nexage.admin.core.enums.PlacementVideoLinearity;

public class PlacementVideoLinearityEnumType extends BaseEnumType<PlacementVideoLinearity> {
  @Override
  public Class<PlacementVideoLinearity> returnedClass() {
    return PlacementVideoLinearity.class;
  }
}
