package com.nexage.app.dto;

import java.math.BigDecimal;

public final class CreativeSummaryBeanDTO {
  BigDecimal spend = BigDecimal.ZERO;
  Long clicks = 0L;
  Long impressions = 0L; // delivered
  Long conversions = 0L;
  Long io_pid = 0L;
  Long lineitem_pid = 0L;
  Long targetgroup_pid = 0L;
  Long creative_pid = 0L;

  public BigDecimal getSpend() {
    return spend;
  }

  public void setSpend(BigDecimal spend) {
    this.spend = spend;
  }

  public Long getClicks() {
    return clicks;
  }

  public void setClicks(Long clicks) {
    this.clicks = clicks;
  }

  public Long getImpressions() {
    return impressions;
  }

  public void setImpressions(Long impressions) {
    this.impressions = impressions;
  }

  public Long getConversions() {
    return conversions;
  }

  public void setConversions(Long conversions) {
    this.conversions = conversions;
  }

  public Long getIoPid() {
    return io_pid;
  }

  public void setIoPid(Long io_pid) {
    this.io_pid = io_pid;
  }

  public Long getLineitemPid() {
    return lineitem_pid;
  }

  public void setLineitemPid(Long lineitem_pid) {
    this.lineitem_pid = lineitem_pid;
  }

  public Long getTargetgroupPid() {
    return targetgroup_pid;
  }

  public void setTargetgroupPid(Long targetgroup_pid) {
    this.targetgroup_pid = targetgroup_pid;
  }

  public Long getCreativePid() {
    return creative_pid;
  }

  public void setCreativePid(Long creative_pid) {
    this.creative_pid = creative_pid;
  }
}
