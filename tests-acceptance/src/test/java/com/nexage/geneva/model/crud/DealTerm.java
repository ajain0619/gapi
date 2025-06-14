package com.nexage.geneva.model.crud;

public class DealTerm {

  public String site_pid;
  public String effective_date;
  public String flat_brands;
  public String nexage_rev_share;
  public String revenue_model;
  public String rtb_fee;
  public String pid;
  public String version;
  public String tag_pid;

  public String getTag_pid() {
    return tag_pid;
  }

  public void setTag_pid(String tag_pid) {
    this.tag_pid = tag_pid;
  }

  public String getSite_pid() {
    return site_pid;
  }

  public void setSite_pid(String site_pid) {
    this.site_pid = site_pid;
  }

  public String getEffective_date() {
    return effective_date;
  }

  public void setEffective_date(String effective_date) {
    this.effective_date = effective_date;
  }

  public String getFlat_brands() {
    return flat_brands;
  }

  public void setFlat_brands(String flat_brands) {
    this.flat_brands = flat_brands;
  }

  public String getNexage_rev_share() {
    return nexage_rev_share;
  }

  public void setNexage_rev_share(String nexage_rev_share) {
    this.nexage_rev_share = nexage_rev_share;
  }

  public String getRevenue_model() {
    return revenue_model;
  }

  public void setRevenue_model(String revenue_model) {
    this.revenue_model = revenue_model;
  }

  public String getRtb_fee() {
    return rtb_fee;
  }

  public void setRtb_fee(String rtb_fee) {
    this.rtb_fee = rtb_fee;
  }

  public String getPid() {
    return pid;
  }

  public void setPid(String pid) {
    this.pid = pid;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(compareClones(o))) return false;
    DealTerm that = (DealTerm) o;

    if (pid != null ? !pid.equals(that.pid) : that.pid != null) return false;
    if (effective_date != null
        ? !effective_date.equals(that.effective_date)
        : that.effective_date != null) return false;
    if (site_pid != null ? !site_pid.equals(that.site_pid) : that.site_pid != null) return false;
    return !(version != null ? !version.equals(that.version) : that.version != null);
  }

  public boolean compareClones(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    DealTerm that = (DealTerm) o;

    if (flat_brands != null ? !flat_brands.equals(that.flat_brands) : that.flat_brands != null)
      return false;
    if (nexage_rev_share != null
        ? !nexage_rev_share.equals(that.nexage_rev_share)
        : that.nexage_rev_share != null) return false;
    if (revenue_model != null
        ? !revenue_model.equals(that.revenue_model)
        : that.revenue_model != null) return false;
    return !(rtb_fee != null ? !rtb_fee.equals(that.rtb_fee) : that.rtb_fee != null);
  }

  @Override
  public int hashCode() {
    int result = pid != null ? pid.hashCode() : 0;
    result = 31 * result + (site_pid != null ? site_pid.hashCode() : 0);
    result = 31 * result + (effective_date != null ? effective_date.hashCode() : 0);
    result = 31 * result + (flat_brands != null ? flat_brands.hashCode() : 0);
    result = 31 * result + (nexage_rev_share != null ? nexage_rev_share.hashCode() : 0);
    result = 31 * result + (revenue_model != null ? revenue_model.hashCode() : 0);
    result = 31 * result + (rtb_fee != null ? rtb_fee.hashCode() : 0);
    result = 31 * result + (version != null ? version.hashCode() : 0);
    return result;
  }
}
