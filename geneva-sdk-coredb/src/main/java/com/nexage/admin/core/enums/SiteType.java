package com.nexage.admin.core.enums;

public enum SiteType {
  MOBILEWEB_SERVER(0),
  MOBILEWEB_BROWSER(1),
  APP_SDK(2),
  APP_API(3);

  public int type;

  SiteType(int type) {
    this.type = type;
  }

  /** @return the type */
  public int getType() {
    return type;
  }
}
