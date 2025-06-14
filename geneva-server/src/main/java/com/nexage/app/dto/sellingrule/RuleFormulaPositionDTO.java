package com.nexage.app.dto.sellingrule;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nexage.admin.core.enums.PlacementCategory;
import com.nexage.app.dto.publisher.PublisherSiteDTO;

/** DTO representation for the RuleFormulaPosition entity */
@JsonInclude(NON_NULL)
public class RuleFormulaPositionDTO {

  private RuleFormulaPositionDTO(RuleFormulaPositionDTO.Builder builder) {
    this.companyId = builder.companyId;
    this.companyName = builder.companyName;
    this.sitePid = builder.sitePid;
    this.siteName = builder.siteName;
    this.siteType = builder.siteType;
    this.placementId = builder.placementId;
    this.placementName = builder.placementName;
    this.placementMemo = builder.placementMemo;
    this.placementType = builder.placementType;
    this.adSize = builder.adSize;
    this.height = builder.height;
    this.width = builder.width;
  }

  private Long companyId;
  private String companyName;
  private Long sitePid;
  private String siteName;
  private PublisherSiteDTO.SiteType siteType;
  private Long placementId;
  private String placementName;
  private String placementMemo;
  private PlacementCategory placementType;
  private String adSize;
  private Integer height;
  private Integer width;

  public Long getCompanyId() {
    return companyId;
  }

  public void setCompanyId(Long companyId) {
    this.companyId = companyId;
  }

  public String getCompanyName() {
    return companyName;
  }

  public void setCompanyName(String companyName) {
    this.companyName = companyName;
  }

  public Long getSitePid() {
    return sitePid;
  }

  public void setSitePid(Long sitePid) {
    this.sitePid = sitePid;
  }

  public String getSiteName() {
    return siteName;
  }

  public void setSiteName(String siteName) {
    this.siteName = siteName;
  }

  public PublisherSiteDTO.SiteType getSiteType() {
    return siteType;
  }

  public void setSiteType(PublisherSiteDTO.SiteType siteType) {
    this.siteType = siteType;
  }

  public Long getPlacementId() {
    return placementId;
  }

  public void setPlacementId(Long placementId) {
    this.placementId = placementId;
  }

  public String getPlacementName() {
    return placementName;
  }

  public void setPlacementName(String placementName) {
    this.placementName = placementName;
  }

  public String getPlacementMemo() {
    return placementMemo;
  }

  public void setPlacementMemo(String placementMemo) {
    this.placementMemo = placementMemo;
  }

  public PlacementCategory getPlacementType() {
    return placementType;
  }

  public void setPlacementType(PlacementCategory placementType) {
    this.placementType = placementType;
  }

  public String getAdSize() {
    return adSize;
  }

  public void setAdSize(String adSize) {
    this.adSize = adSize;
  }

  public Integer getHeight() {
    return height;
  }

  public void setHeight(Integer height) {
    this.height = height;
  }

  public Integer getWidth() {
    return width;
  }

  public void setWidth(Integer width) {
    this.width = width;
  }

  public static final class Builder {
    private Long companyId;
    private String companyName;
    private Long sitePid;
    private String siteName;
    private PublisherSiteDTO.SiteType siteType;
    private Long placementId;
    private String placementName;
    private String placementMemo;
    private PlacementCategory placementType;
    private String adSize;
    private Integer height;
    private Integer width;

    private Builder() {}

    public RuleFormulaPositionDTO.Builder withCompanyId(Long companyId) {
      this.companyId = companyId;
      return this;
    }

    public RuleFormulaPositionDTO.Builder withCompanyName(String companyName) {
      this.companyName = companyName;
      return this;
    }

    public RuleFormulaPositionDTO.Builder withSitePid(Long sitePid) {
      this.sitePid = sitePid;
      return this;
    }

    public RuleFormulaPositionDTO.Builder withSiteName(String siteName) {
      this.siteName = siteName;
      return this;
    }

    public RuleFormulaPositionDTO.Builder withSiteType(PublisherSiteDTO.SiteType siteType) {
      this.siteType = siteType;
      return this;
    }

    public RuleFormulaPositionDTO.Builder withPlacementId(Long placementId) {
      this.placementId = placementId;
      return this;
    }

    public RuleFormulaPositionDTO.Builder withPlacementName(String placementName) {
      this.placementName = placementName;
      return this;
    }

    public RuleFormulaPositionDTO.Builder withPlacementMemo(String placementMemo) {
      this.placementMemo = placementMemo;
      return this;
    }

    public RuleFormulaPositionDTO.Builder withPlacementType(PlacementCategory placementType) {
      this.placementType = placementType;
      return this;
    }

    public RuleFormulaPositionDTO.Builder withAdSize(String adSize) {
      this.adSize = adSize;
      return this;
    }

    public RuleFormulaPositionDTO.Builder withHeight(Integer height) {
      this.height = height;
      return this;
    }

    public RuleFormulaPositionDTO.Builder withWidth(Integer width) {
      this.width = width;
      return this;
    }

    public RuleFormulaPositionDTO build() {
      return new RuleFormulaPositionDTO(this);
    }
  }

  public static RuleFormulaPositionDTO.Builder newBuilder() {
    return new RuleFormulaPositionDTO.Builder();
  }
}
