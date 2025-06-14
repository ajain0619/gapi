package com.nexage.geneva.model.common;

import java.util.ArrayList;
import java.util.List;

/** Class represents IabCategory entity. */
public class IabCategory {
  private String pid;
  private String name;
  private List<IabCategory> children = new ArrayList<>();

  public List<IabCategory> getChildren() {
    return children;
  }

  public void setChildren(List<IabCategory> children) {
    this.children = children;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPid() {
    return pid;
  }

  public void setPid(String pid) {
    this.pid = pid;
  }
}
