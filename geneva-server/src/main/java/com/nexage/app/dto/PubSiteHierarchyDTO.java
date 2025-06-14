package com.nexage.app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.nexage.admin.core.enums.Mode;
import com.nexage.admin.core.enums.Status;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

@JsonInclude(Include.NON_NULL)
public class PubSiteHierarchyDTO {

  private Long sitePid;
  private Long sitealias;
  private String siteNameAlias;
  private Character siteType;
  private String siteName;
  private Status status;
  private Mode mode;
  private Set<PubPositionHierarchy> positions;

  private PubSiteHierarchyDTO() {}

  private PubSiteHierarchyDTO(Builder builder) {
    this.sitealias = builder.sitealias;
    this.siteNameAlias = builder.siteNameAlias;
    this.siteType = builder.siteType;
    this.sitePid = builder.sitePid;
    this.siteName = builder.siteName;
    this.status = builder.status;
    this.mode = builder.mode;
    this.positions = builder.positions;
  }

  public Long getSitePid() {
    return sitePid;
  }

  public Long getSitealias() {
    return sitealias;
  }

  public String getSiteNameAlias() {
    return siteNameAlias;
  }

  public String getSiteName() {
    return siteName;
  }

  public Character getSiteType() {
    return siteType;
  }

  public Status getStatus() {
    return status;
  }

  public Mode getMode() {
    return mode;
  }

  public PubPositionHierarchy containsPosition(Long pid) {
    if (pid == null) {
      return null;
    }

    Iterator<PubPositionHierarchy> iter = positions.iterator();
    while (iter.hasNext()) {
      PubPositionHierarchy p = iter.next();
      if (p.getPid().longValue() == pid.longValue()) {
        return p;
      }
    }
    return null;
  }

  public void addPosition(PubPositionHierarchy p) {
    positions.add(p);
  }

  public Set<PubPositionHierarchy> getPositions() {
    return positions;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder {
    private Long sitePid;
    private Long sitealias;
    private String siteNameAlias;
    private Character siteType;
    private String siteName;
    private Status status;
    private Mode mode;

    private Set<PubPositionHierarchy> positions = new LinkedHashSet<>();

    public Builder withSitePid(Long pid) {
      this.sitePid = pid;
      return this;
    }

    public Builder withSitealias(Long alias) {
      this.sitealias = alias;
      return this;
    }

    public Builder withSiteNameAlias(String name) {
      this.siteNameAlias = name;
      return this;
    }

    public Builder withSiteType(Character type) {
      this.siteType = type;
      return this;
    }

    public Builder withSiteName(String name) {
      this.siteName = name;
      return this;
    }

    public Builder withSiteStatus(Status status) {
      this.status = status;
      return this;
    }

    public Builder withSiteMode(Mode mode) {
      this.mode = mode;
      return this;
    }

    public Builder addPosition(PubPositionHierarchy pos) {
      positions.add(pos);
      return this;
    }

    public PubSiteHierarchyDTO build() {
      return new PubSiteHierarchyDTO(this);
    }
  }
}
