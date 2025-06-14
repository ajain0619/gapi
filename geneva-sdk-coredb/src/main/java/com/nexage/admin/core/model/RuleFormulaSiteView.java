package com.nexage.admin.core.model;

import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.enums.site.Platform;
import java.io.Serializable;
import java.util.Set;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Type;

/** <code>RuleFormulaSiteView</code> */
@Entity
@Table(name = "site")
@Immutable
public class RuleFormulaSiteView implements Serializable {

  private static final long serialVersionUID = 5428166185117801701L;

  @Id private Long pid;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "company_pid", referencedColumnName = "pid")
  private RuleFormulaCompanyView company;

  @Column
  @Enumerated(EnumType.STRING)
  private com.nexage.admin.core.enums.site.Type type;

  @Column
  @Enumerated(EnumType.STRING)
  private Platform platform;

  @Column private String name;

  @Type(type = "com.nexage.admin.core.custom.type.StatusEnumType")
  private Status status;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "site_attributes",
      joinColumns = @JoinColumn(name = "site_pid"),
      inverseJoinColumns = @JoinColumn(name = "attribute_values_pid"))
  private Set<RuleFormulaAttributeValueView> attributeValues;

  @ElementCollection(fetch = FetchType.LAZY)
  @CollectionTable(
      name = "iab_cat",
      joinColumns = @JoinColumn(name = "site_pid", referencedColumnName = "pid"))
  @Column(name = "category")
  private Set<String> iabCategories;

  public Long getPid() {
    return pid;
  }

  public void setPid(Long pid) {
    this.pid = pid;
  }

  public RuleFormulaCompanyView getCompany() {
    return company;
  }

  public void setCompany(RuleFormulaCompanyView company) {
    this.company = company;
  }

  public com.nexage.admin.core.enums.site.Type getType() {
    return type;
  }

  public void setType(com.nexage.admin.core.enums.site.Type type) {
    this.type = type;
  }

  public Platform getPlatform() {
    return platform;
  }

  public void setPlatform(Platform platform) {
    this.platform = platform;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public Set<RuleFormulaAttributeValueView> getAttributeValues() {
    return attributeValues;
  }

  public Set<String> getIabCategories() {
    return iabCategories;
  }

  @Override
  public String toString() {
    return "RuleFormulaSiteView{"
        + "pid="
        + pid
        + ", company="
        + company
        + ", type="
        + type
        + ", platform="
        + platform
        + ", name='"
        + name
        + '\''
        + ", status="
        + status
        + '}';
  }
}
