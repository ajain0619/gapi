package com.nexage.admin.core.bidder.model;

import com.nexage.admin.core.bidder.type.BDRAdTemplateType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Table(name = "bdr_template")
@NamedQuery(
    name = "getAllTemplatesByType",
    query =
        "SELECT brTemplate FROM BDRTemplate brTemplate WHERE brTemplate.adType = :type ORDER BY brTemplate.version DESC")
public class BDRTemplate {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "pid")
  private Long pid;

  @Column(name = "type")
  @Enumerated(EnumType.ORDINAL)
  private BDRAdTemplateType adType = BDRAdTemplateType.BANNER;

  @Version
  @Column(name = "version", nullable = false)
  private Integer version;

  @Column(name = "markup_template", length = 65535)
  private String markup;

  @Column(name = "description", length = 255)
  private String description;

  public Long getPid() {
    return pid;
  }

  public BDRAdTemplateType getAdType() {
    return adType;
  }

  public void setAdType(BDRAdTemplateType adType) {
    this.adType = adType;
  }

  public Integer getVersion() {
    return version;
  }

  public void setVersion(Integer version) {
    this.version = version;
  }

  public String getMarkup() {
    return markup;
  }

  public void setMarkup(String markup) {
    this.markup = markup;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((adType == null) ? 0 : adType.hashCode());
    result = prime * result + ((description == null) ? 0 : description.hashCode());
    result = prime * result + ((markup == null) ? 0 : markup.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    BDRTemplate other = (BDRTemplate) obj;
    if (adType != other.adType) return false;
    if (description == null) {
      if (other.description != null) return false;
    } else if (!description.equals(other.description)) return false;
    if (markup == null) {
      if (other.markup != null) return false;
    } else if (!markup.equals(other.markup)) return false;
    return true;
  }
}
