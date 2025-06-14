package com.nexage.app.dto.publisher;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class PublisherRTBProfileLibraryDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  private Long pid;
  private String name;
  private Integer version;
  private Long publisherPid;
  private String privilegeLevel;
  private Boolean isDefaultEligible = false;
  Set<PublisherRTBProfileGroupDTO> groups = new HashSet<>();

  public PublisherRTBProfileLibraryDTO() {}

  public PublisherRTBProfileLibraryDTO(
      Long pid,
      String name,
      Integer version,
      String privilegeLevel,
      Long publisherPid,
      Boolean isDefaultEligible) {
    this.pid = pid;
    this.name = name;
    this.version = version;
    this.privilegeLevel = privilegeLevel;
    this.publisherPid = publisherPid;
    this.isDefaultEligible = isDefaultEligible;
  }

  public void setGroups(Set<PublisherRTBProfileGroupDTO> groups) {
    this.groups.clear();
    this.groups.addAll(groups);
  }

  public Long getPid() {
    return pid;
  }

  public String getName() {
    return name;
  }

  public Integer getVersion() {
    return version;
  }

  public Long getPublisherPid() {
    return publisherPid;
  }

  public String getPrivilegeLevel() {
    return privilegeLevel;
  }

  public Boolean getIsDefaultEligible() {
    return isDefaultEligible;
  }

  public Set<PublisherRTBProfileGroupDTO> getGroups() {
    return groups;
  }

  public void setPid(Long pid) {
    this.pid = pid;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setVersion(Integer version) {
    this.version = version;
  }

  public void setPublisherPid(Long publisherPid) {
    this.publisherPid = publisherPid;
  }

  public void setPrivilegeLevel(String privilegeLevel) {
    this.privilegeLevel = privilegeLevel;
  }

  public void setIsDefaultEligible(Boolean defaultEligible) {
    isDefaultEligible = defaultEligible;
  }
}
