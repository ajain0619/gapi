package com.nexage.admin.core.custom.type;

import com.nexage.admin.core.enums.PlacementVideoVastVersion;

public class PlacementVideoVastVersionEnumType extends BaseEnumType<PlacementVideoVastVersion> {
  @Override
  public Class<PlacementVideoVastVersion> returnedClass() {
    return PlacementVideoVastVersion.class;
  }
}
