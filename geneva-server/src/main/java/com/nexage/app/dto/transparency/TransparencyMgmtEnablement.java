package com.nexage.app.dto.transparency;

import java.util.HashMap;

public enum TransparencyMgmtEnablement {
  DISABLED(0),
  ENABLED(1);

  int id;

  private static final HashMap<Integer, TransparencyMgmtEnablement> IDS = new HashMap<>();

  static {
    for (TransparencyMgmtEnablement transparencyMgmtEnablement :
        TransparencyMgmtEnablement.values()) {
      IDS.put(transparencyMgmtEnablement.getId(), transparencyMgmtEnablement);
    }
  }

  TransparencyMgmtEnablement(int val) {
    this.id = val;
  }

  public int getId() {
    return id;
  }

  public static TransparencyMgmtEnablement getById(int id) {
    return IDS.get(id);
  }
}
