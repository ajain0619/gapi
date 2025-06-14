package com.nexage.geneva.model.crud;

public class InventoryAttribute {

  private String pid;
  private String compnayPid;
  private String name;
  private String description;
  private int status;
  private String lastUpdate;
  private int has_globalVisibility;
  private String assignedLevel;
  private boolean isRequired;
  private int version;

  public String getPid() {
    return pid;
  }

  public void setPid(String pid) {
    this.pid = pid;
  }

  public String getCompnayPid() {
    return compnayPid;
  }

  public void setCompnayPid(String compnayPid) {
    this.compnayPid = compnayPid;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public String getLastUpdate() {
    return lastUpdate;
  }

  public void setLastUpdate(String lastUpdate) {
    this.lastUpdate = lastUpdate;
  }

  public int getHas_globalVisibility() {
    return has_globalVisibility;
  }

  public void setHas_globalVisibility(int has_globalVisibility) {
    this.has_globalVisibility = has_globalVisibility;
  }

  public String getAssignedLevel() {
    return assignedLevel;
  }

  public void setAssignedLevel(String assignedLevel) {
    this.assignedLevel = assignedLevel;
  }

  public boolean isRequired() {
    return isRequired;
  }

  public void setRequired(boolean required) {
    isRequired = required;
  }

  public int getVersion() {
    return version;
  }

  public void setVersion(int version) {
    this.version = version;
  }

  @Override
  public String toString() {
    return "InventoryAttribute{"
        + "pid='"
        + pid
        + '\''
        + ", compnayPid='"
        + compnayPid
        + '\''
        + ", name='"
        + name
        + '\''
        + ", description='"
        + description
        + '\''
        + ", status="
        + status
        + ", lastUpdate='"
        + lastUpdate
        + '\''
        + ", has_globalVisibility="
        + has_globalVisibility
        + ", assignedLevel='"
        + assignedLevel
        + '\''
        + ", isRequired="
        + isRequired
        + ", version="
        + version
        + '}';
  }
}
