package com.nexage.app.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Date;

/**
 * @author rampatra
 * @since 2019-01-28
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CrsTagMappingDTO {

  private Long pid;
  private Long brandProtectionTagPid;
  private Long crsTagId;
  private Long crsTagAttributeId;
  private Date updateDate;

  public Long getPid() {
    return pid;
  }

  public void setPid(Long pid) {
    this.pid = pid;
  }

  public Long getBrandProtectionTagPid() {
    return brandProtectionTagPid;
  }

  public void setBrandProtectionTagPid(Long brandProtectionTagPid) {
    this.brandProtectionTagPid = brandProtectionTagPid;
  }

  public Long getCrsTagId() {
    return crsTagId;
  }

  public void setCrsTagId(Long crsTagId) {
    this.crsTagId = crsTagId;
  }

  public Long getCrsTagAttributeId() {
    return crsTagAttributeId;
  }

  public void setCrsTagAttributeId(Long crsTagAttributeId) {
    this.crsTagAttributeId = crsTagAttributeId;
  }

  public Date getUpdateDate() {
    return updateDate;
  }

  public void setUpdateDate(Date updateDate) {
    this.updateDate = updateDate;
  }
}
