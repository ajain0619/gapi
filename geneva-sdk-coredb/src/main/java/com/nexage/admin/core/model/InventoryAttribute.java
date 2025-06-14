package com.nexage.admin.core.model;

import com.nexage.admin.core.enums.Status;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Type;
import org.hibernate.envers.AuditJoinTable;
import org.hibernate.envers.Audited;

@Data
@Entity
@Audited
@Table(name = "attributes")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
public class InventoryAttribute implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @EqualsAndHashCode.Include
  @ToString.Include
  private Long pid;

  @Column(name = "company_pid", nullable = false)
  @EqualsAndHashCode.Include
  @ToString.Include
  private Long companyPid;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "company_pid",
      referencedColumnName = "pid",
      insertable = false,
      updatable = false)
  private Company ownerCompany;

  @Column(name = "name", nullable = false)
  @EqualsAndHashCode.Include
  @ToString.Include
  private String name;

  @Column(name = "description")
  @EqualsAndHashCode.Include
  @ToString.Include
  private String description;

  @Column(name = "prefix")
  @EqualsAndHashCode.Include
  @ToString.Include
  private String prefix;

  @Enumerated(EnumType.ORDINAL)
  @Column(name = "status", nullable = false)
  @Type(type = "com.nexage.admin.core.custom.type.StatusEnumType")
  @EqualsAndHashCode.Include
  @ToString.Include
  private Status status;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "last_update", nullable = false, updatable = true)
  @NotNull
  @EqualsAndHashCode.Include
  @ToString.Include
  private Date lastUpdate;

  @Column(name = "has_global_visibility", nullable = false)
  @EqualsAndHashCode.Include
  @ToString.Include
  private boolean hasGlobalVisibility;

  @Column(name = "assigned_level", nullable = false)
  @EqualsAndHashCode.Include
  @ToString.Include
  private String assignedLevel;

  @Version
  @Column(name = "version", nullable = false)
  @EqualsAndHashCode.Include
  @ToString.Include
  private Integer version;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "attributes_company_visibility",
      joinColumns = @JoinColumn(name = "attribute_pid"),
      inverseJoinColumns = @JoinColumn(name = "company_pid"))
  @AuditJoinTable(name = "attributes_company_visibility_aud")
  @OrderBy("pid asc")
  private Set<Company> visibleCompanies = new HashSet<>();

  @Column(name = "is_required", nullable = false)
  @EqualsAndHashCode.Include
  @ToString.Include
  private boolean isRequired;

  @Column(name = "is_internal", nullable = false)
  @EqualsAndHashCode.Include
  @ToString.Include
  private boolean isInternal;

  @OneToMany(
      fetch = FetchType.LAZY,
      mappedBy = "attribute",
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  @OrderBy("pid asc")
  private Set<InventoryAttributeValue> inventoryAttributeValues = new HashSet<>();

  @Transient private Long inventoryAttributeValueCount;

  @Transient private Long inventoryAttributeValueCountActive;

  @PrePersist
  @PreUpdate
  public void setLastUpdate() {
    this.lastUpdate = new Date();
  }

  public InventoryAttribute(
      Long pid,
      String name,
      Status status,
      Date lastUpdate,
      String assignedLevel,
      Long companyPid,
      Long inventoryAttributeValueCount,
      Long inventoryAttributeValueCountActive) {
    this.pid = pid;
    this.name = name;
    this.status = status;
    this.lastUpdate = lastUpdate;
    this.assignedLevel = assignedLevel;
    this.companyPid = companyPid;
    this.inventoryAttributeValueCount = inventoryAttributeValueCount;
    this.inventoryAttributeValueCountActive = inventoryAttributeValueCountActive;
  }
}
