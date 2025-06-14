package com.nexage.app.dto.brand.protection;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Date;

/**
 * @author rampatra
 * @since 2019-01-23
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BrandProtectionTagDTO {

  private Long pid;
  private Long parentTagPid;
  private Long categoryPid;
  private String name;
  private String rtbId;
  private Boolean freeTextTag;
  private Date updateDate;

  public Long getPid() {
    return pid;
  }

  public void setPid(Long pid) {
    this.pid = pid;
  }

  public Long getParentTagPid() {
    return parentTagPid;
  }

  public void setParentTagPid(Long parentTagPid) {
    this.parentTagPid = parentTagPid;
  }

  public Long getCategoryPid() {
    return categoryPid;
  }

  public void setCategoryPid(Long categoryPid) {
    this.categoryPid = categoryPid;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getRtbId() {
    return rtbId;
  }

  public void setRtbId(String rtbId) {
    this.rtbId = rtbId;
  }

  public Boolean isFreeTextTag() {
    return freeTextTag;
  }

  public void setFreeTextTag(Boolean freeTextTag) {
    this.freeTextTag = freeTextTag;
  }

  public Date getUpdateDate() {
    return updateDate;
  }

  public void setUpdateDate(Date updateDate) {
    this.updateDate = updateDate;
  }
}
