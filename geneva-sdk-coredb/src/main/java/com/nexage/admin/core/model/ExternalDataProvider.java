package com.nexage.admin.core.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.envers.AuditJoinTable;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

@Entity
@Audited
@Table(name = "external_data_provider")
@NamedQuery(
    name = "getNamesForPids",
    query = "SELECT pid, name FROM ExternalDataProvider WHERE pid IN (:pids) ORDER BY name")
@NamedQuery(
    name = "getIdNamesMap",
    query = "SELECT pid, name FROM ExternalDataProvider ORDER BY name")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class ExternalDataProvider implements Serializable {

  private static final long serialVersionUID = -3178123819695469445L;

  public enum EnablementStatus {
    INACTIVE(0),
    ACTIVE(1),
    DELETED(2);

    private final int externalValue;

    EnablementStatus(int externalValue) {
      this.externalValue = externalValue;
    }

    public int getExternalValue() {
      return externalValue;
    }
  }

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(nullable = false)
  @EqualsAndHashCode.Include
  private Long pid;

  @Version
  @Column(name = "VERSION", nullable = false)
  @EqualsAndHashCode.Include
  protected Integer version;

  public String getId() {
    return getPid().toString();
  }

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "creation_date", nullable = false, updatable = false)
  @NotNull
  @EqualsAndHashCode.Include
  private java.util.Date creationDate;

  @Column(length = 255, nullable = false)
  @EqualsAndHashCode.Include
  private String name;

  @Column(name = "base_url", length = 255, nullable = false)
  @EqualsAndHashCode.Include
  private String baseUrl;

  @Enumerated(EnumType.ORDINAL)
  @Column(name = "enablement_status", nullable = false)
  @EqualsAndHashCode.Include
  private EnablementStatus enablementStatus;

  @Column(length = 255, nullable = false)
  @EqualsAndHashCode.Include
  private String description;

  @Column(name = "data_provider_impl_class", length = 255, nullable = false)
  @EqualsAndHashCode.Include
  private String dataProviderImplClass;

  @Column(name = "filter_request_rate")
  @EqualsAndHashCode.Include
  private Integer filterRequestRate;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "last_update", nullable = false)
  @EqualsAndHashCode.Include
  private java.util.Date lastUpdate;

  @Column(name = "configuration")
  private String configuration;

  /*
   * Where in the bidRequest to place the outbound private attribute
   */
  @Column(name = "bid_request_location")
  @Enumerated(EnumType.ORDINAL)
  BidRequestLocation bidRequestLocation;

  /* the outbound key in the bid request */
  @Column(name = "bid_request_attr_name")
  String bidRequestAttributeName;

  @Column(name = "bidder_alias_required", nullable = false)
  private boolean bidderAliasRequired;

  @ManyToMany
  @JoinTable(
      name = "data_provider_exchange_prod",
      joinColumns = {@JoinColumn(name = "data_provider_id", referencedColumnName = "pid")},
      inverseJoinColumns = {@JoinColumn(name = "exchange_prod_id", referencedColumnName = "pid")})
  @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
  @AuditJoinTable(name = "data_provider_exchange_prod_aud")
  @EqualsAndHashCode.Include
  private Set<ExchangeProduction> exchanges = new HashSet<>();

  public ExternalDataProvider(Long pid) {
    this.pid = pid;
  }

  /**
   * Returns a status value that is not necessarily the ordinal of the enumeration.
   *
   * @return status value
   */
  public int getExternalStatus() {
    return enablementStatus.getExternalValue();
  }

  /** Invoked before insert to set the current time. */
  @PrePersist
  public void setCreationDates() {
    creationDate = new Date();
    lastUpdate = new Date();
  }

  @PreUpdate
  protected void updateLastUpdated() {
    lastUpdate = new Date();
  }
}
