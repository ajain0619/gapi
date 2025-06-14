package com.nexage.geneva.model.crud;

public class InventoryAttributeValue {
  private String pid;
  private String name;
  private boolean isEnabled;
  private String lastupdate;
  private String attributePid;
  private int version;

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

  public boolean isEnabled() {
    return isEnabled;
  }

  public void setEnabled(boolean enabled) {
    isEnabled = enabled;
  }

  public String getLastupdate() {
    return lastupdate;
  }

  public void setLastupdate(String lastupdate) {
    this.lastupdate = lastupdate;
  }

  public String getAttributePid() {
    return attributePid;
  }

  public void setAttributePid(String attributePid) {
    this.attributePid = attributePid;
  }

  public int getVersion() {
    return version;
  }

  public void setVersion(int version) {
    this.version = version;
  }

  @Override
  public String toString() {
    return "InventoryAttributeValue{"
        + "pid='"
        + pid
        + '\''
        + ", name='"
        + name
        + '\''
        + ", isEnabled="
        + isEnabled
        + ", lastupdate='"
        + lastupdate
        + '\''
        + ", attributePid='"
        + attributePid
        + '\''
        + ", version="
        + version
        + '}';
  }
}
