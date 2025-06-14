package com.nexage.admin.core.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.error.CoreDBErrorCodes;
import com.nexage.admin.core.validator.CheckUniqueEntity;
import com.nexage.admin.core.validator.CheckUniqueGroup;
import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

/** This class is only used by ExWinsAndInboundToRevenueJob */
@Entity
@Immutable
@Table(name = "site")
@CheckUniqueEntity(
    errorCode = CoreDBErrorCodes.CORE_DB_DUPLICATE_SITE_NAME,
    groups = CheckUniqueGroup.class,
    properties = {"name", "company"})
@Data
@Audited
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SiteView implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  public SiteView(Long pid, String name, Long sellerPid, String sellerName) {
    this.pid = pid;
    this.name = name;
    this.companyPid = sellerPid;
    this.company = new Company();
    this.company.setPid(sellerPid);
    this.company.setName(sellerName);
  }

  public SiteView(Long pid, String name, Status status) {
    this.pid = pid;
    this.name = name;
    setStatus(status);
  }

  public SiteView(Long pid, String name, Status status, String url, String sellerName) {
    this.pid = pid;
    this.name = name;
    setStatus(status);
    this.url = url;
    this.company = new Company();
    this.company.setName(sellerName);
  }

  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(nullable = false, updatable = false)
  @Id
  @EqualsAndHashCode.Include
  @ToString.Include
  private Long pid;

  @Column(name = "dcn", nullable = false, updatable = false, unique = true)
  @Size(max = 32)
  @EqualsAndHashCode.Include
  @ToString.Include
  private String dcn;

  @Column(name = "id", nullable = false, updatable = false, unique = true)
  @Size(max = 32)
  @EqualsAndHashCode.Include
  @ToString.Include
  private String id;

  @Version
  @Column(name = "VERSION", nullable = false)
  @EqualsAndHashCode.Include
  @ToString.Include
  private Integer version;

  @Column(name = "company_pid")
  private Long companyPid;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "company_pid",
      referencedColumnName = "pid",
      insertable = false,
      updatable = false)
  @NotNull
  @JsonIgnore
  private Company company;

  @Column(nullable = false, unique = true)
  @Size(max = 255)
  @NotNull
  @EqualsAndHashCode.Include
  @ToString.Include
  private String name;

  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, orphanRemoval = true) // ??
  @JoinColumn(name = "site_pid", referencedColumnName = "pid")
  @Where(clause = "status >= 0")
  private Set<Tag> tags = new HashSet<>();

  @Column(name = "status", nullable = false)
  @org.hibernate.annotations.Type(type = "com.nexage.admin.core.custom.type.StatusEnumType")
  @EqualsAndHashCode.Include
  @ToString.Include
  private Status status;

  @Column(name = "url", nullable = false)
  @EqualsAndHashCode.Include
  @ToString.Include
  private String url;

  @JsonIgnore @Transient private Integer statusVal;

  public void setStatus(Status newStatus) {
    this.status = newStatus;
    this.statusVal = newStatus.asInt();
  }

  public Integer getStatusVal() {
    if (statusVal == null) {
      statusVal = status.asInt();
    }
    return statusVal;
  }

  public void setStatusVal(Integer statusVal) {
    if (statusVal != null) {
      this.statusVal = statusVal;
      this.status = Status.fromInt(statusVal);
    }
  }
}
