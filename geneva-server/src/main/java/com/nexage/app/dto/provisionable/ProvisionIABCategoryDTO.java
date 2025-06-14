package com.nexage.app.dto.provisionable;

/** Created by e.kripinevich on 9/21/17. */
public class ProvisionIABCategoryDTO {
  private String id;
  private String name;
  private ProvisionIABCategoryDTO[] children;
  private ProvisionIABCategoryDTO parent;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ProvisionIABCategoryDTO[] getChildren() {
    return children;
  }

  public void setChildren(ProvisionIABCategoryDTO[] children) {
    this.children = children;
  }

  public ProvisionIABCategoryDTO getParent() {
    return parent;
  }

  public void setParent(ProvisionIABCategoryDTO parent) {
    this.parent = parent;
  }
}
