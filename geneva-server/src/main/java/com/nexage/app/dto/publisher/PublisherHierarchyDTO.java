package com.nexage.app.dto.publisher;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.nexage.app.dto.PubSiteHierarchyDTO;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

@JsonInclude(Include.NON_NULL)
public class PublisherHierarchyDTO {

  private Set<PubSiteHierarchyDTO> site;
  private Long publisherPid;
  private String publisherName;

  private PublisherHierarchyDTO() {}

  private PublisherHierarchyDTO(Builder builder) {
    this.site = builder.site;
    this.publisherName = builder.publisherName;
    this.publisherPid = builder.publisherPid;
  }

  public Set<PubSiteHierarchyDTO> getSite() {
    return site;
  }

  public String getPublisherName() {
    return publisherName;
  }

  public Long getPublisherPid() {
    return publisherPid;
  }

  public void addSite(PubSiteHierarchyDTO s) {
    site.add(s);
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public PubSiteHierarchyDTO containsSite(Long pid) {
    if (pid == null) {
      return null;
    }

    Iterator<PubSiteHierarchyDTO> iter = site.iterator();
    while (iter.hasNext()) {
      PubSiteHierarchyDTO s = iter.next();
      if (s.getSitePid().longValue() == pid.longValue()) {
        return s;
      }
    }
    return null;
  }

  public static final class Builder {

    private Set<PubSiteHierarchyDTO> site = new LinkedHashSet<>();
    private String publisherName;
    private Long publisherPid;

    public Builder withSite(Set<PubSiteHierarchyDTO> s) {
      this.site = s;
      return this;
    }

    public Builder withPublisherName(String name) {
      this.publisherName = name;
      return this;
    }

    public Builder withPublisherPid(Long pid) {
      this.publisherPid = pid;
      return this;
    }

    public PublisherHierarchyDTO build() {
      return new PublisherHierarchyDTO(this);
    }
  }
}
