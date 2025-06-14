package com.nexage.app.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class AuditDeltaResponseDTO implements Serializable {

  private static final long serialVersionUID = 3254075447445762159L;
  private Map<String, Object> delta = new HashMap<>();

  public Map<String, Object> getDelta() {
    return delta;
  }

  public void setDelta(Map<String, Object> delta) {
    this.delta = delta;
  }

  public void addToDelta(String name, Object object) {
    delta.put(name, object);
  }
}
