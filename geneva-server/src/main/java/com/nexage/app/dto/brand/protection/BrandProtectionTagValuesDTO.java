package com.nexage.app.dto.brand.protection;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Date;

/**
 * @author rampatra
 * @since 2019-01-24
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BrandProtectionTagValuesDTO {

  private Long pid;
  private Long brandProtectionTagPid;
  private String name;
  private String value;
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

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public Date getUpdateDate() {
    return updateDate;
  }

  public void setUpdateDate(Date updateDate) {
    this.updateDate = updateDate;
  }
}
