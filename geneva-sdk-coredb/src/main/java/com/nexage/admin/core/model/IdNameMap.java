package com.nexage.admin.core.model;

import java.io.Serializable;

public class IdNameMap implements Serializable {

  private static final long serialVersionUID = 1L;

  private final long pid;
  private final String name;

  public IdNameMap(long pid, String name) {
    this.pid = pid;
    this.name = name;
  }

  public long getPid() {
    return pid;
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return "IdNameMap [pid=" + pid + ", name=" + name + "]";
  }
}
