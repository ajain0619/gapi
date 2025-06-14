package com.nexage.geneva.model.crud;

public class AdSource {
  private String pid;
  private String name;

  public String getPid() {
    return pid;
  }

  public void setPid(String pid) {
    this.pid = pid;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public AdSource() {}

  public AdSource(String pid, String name) {
    this.pid = pid;
    this.name = name;
  }
}
