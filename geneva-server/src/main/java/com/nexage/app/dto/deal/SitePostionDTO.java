package com.nexage.app.dto.deal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.List;

@JsonInclude(Include.NON_NULL)
public class SitePostionDTO {

  private final Long siteId;

  private final String siteName;

  private final List<PositionNameDTO> postitionNames;

  private SitePostionDTO(Builder builder) {
    this.siteId = builder.siteId;
    this.siteName = builder.siteName;
    this.postitionNames = builder.postitionNames;
  }

  public Long getSiteId() {
    return siteId;
  }

  public String getSiteName() {
    return siteName;
  }

  public List<PositionNameDTO> getPostitionNames() {
    return postitionNames;
  }

  public static class Builder {
    private Long siteId;

    private String siteName;

    private List<PositionNameDTO> postitionNames;

    public Builder setSiteId(Long siteId) {
      this.siteId = siteId;
      return this;
    }

    public Builder setSiteName(String siteName) {
      this.siteName = siteName;
      return this;
    }

    public Builder setPostitionNames(List<PositionNameDTO> postitionNames) {
      this.postitionNames = postitionNames;
      return this;
    }

    public SitePostionDTO build() {
      return new SitePostionDTO(this);
    }
  }
}
