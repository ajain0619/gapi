package com.nexage.app.dto.deal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.List;

@JsonInclude(Include.NON_NULL)
public class PublisherSitePositionDTO {

  private final long publisherId;

  private final String publisherName;

  private final List<SitePostionDTO> sites;

  private PublisherSitePositionDTO(Builder builder) {
    this.publisherId = builder.publisherId;
    this.publisherName = builder.publisherName;
    this.sites = builder.sites;
  }

  public long getPublisherId() {
    return this.publisherId;
  }

  public String getPublisherName() {
    return this.publisherName;
  }

  public List<SitePostionDTO> getSites() {
    return this.sites;
  }

  public static class Builder {

    private long publisherId;

    private String publisherName;

    private List<SitePostionDTO> sites;

    public Builder setPublisherId(long publisherId) {
      this.publisherId = publisherId;
      return this;
    }

    public Builder setPublisherName(String publisherName) {
      this.publisherName = publisherName;
      return this;
    }

    public Builder setSites(List<SitePostionDTO> sites) {
      this.sites = sites;
      return this;
    }

    public PublisherSitePositionDTO build() {
      return new PublisherSitePositionDTO(this);
    }
  }
}
