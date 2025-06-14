package com.nexage.app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.nexage.admin.core.enums.TrafficType;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

@JsonInclude(Include.NON_NULL)
public class PubPositionHierarchy {

  private Long pid;
  private String positionName;
  private Byte placementType;
  private TrafficType trafficType;
  private Set<PubTagHierarchyDTO> tags;

  private PubPositionHierarchy() {}

  private PubPositionHierarchy(Builder builder) {
    this.pid = builder.pid;
    this.positionName = builder.positionName;
    this.placementType = builder.placementType;
    this.tags = builder.tags;
    this.trafficType = builder.trafficType;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public Long getPid() {
    return pid;
  }

  public String getPositionName() {
    return positionName;
  }

  public Byte getPlacementType() {
    return placementType;
  }

  public TrafficType getTrafficType() {
    return trafficType;
  }

  public Set<PubTagHierarchyDTO> getTags() {
    return tags;
  }

  public void addTag(PubTagHierarchyDTO t) {
    tags.add(t);
  }

  public PubTagHierarchyDTO containsTag(Long pid) {
    if (pid == null) {
      return null;
    }

    Iterator<PubTagHierarchyDTO> iter = tags.iterator();
    while (iter.hasNext()) {
      PubTagHierarchyDTO t = iter.next();
      if (t.getTagPid().longValue() == pid.longValue()) {
        return t;
      }
    }
    return null;
  }

  public static final class Builder {
    private Long pid;
    private String positionName;
    private Byte placementType;
    private TrafficType trafficType;
    private Set<PubTagHierarchyDTO> tags = new LinkedHashSet<>();

    public Builder withPid(Long pid) {
      this.pid = pid;
      return this;
    }

    public Builder withPositionName(String positionName) {
      this.positionName = positionName;
      return this;
    }

    public Builder withPlacementType(Byte placementType) {
      this.placementType = placementType;
      return this;
    }

    public Builder withTrafficType(TrafficType trafficType) {
      this.trafficType = trafficType;
      return this;
    }

    public Builder addTag(PubTagHierarchyDTO tag) {
      tags.add(tag);
      return this;
    }

    public PubPositionHierarchy build() {
      return new PubPositionHierarchy(this);
    }
  }
}
