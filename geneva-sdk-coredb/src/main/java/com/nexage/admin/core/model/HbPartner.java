package com.nexage.admin.core.model;

import com.nexage.admin.core.enums.FeeType;
import com.nexage.admin.core.enums.Status;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

@Entity
@Table(name = "hb_partner")
@SQLDelete(sql = "UPDATE hb_partner SET status = -1 WHERE pid = ? and version = ?")
@Audited
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class HbPartner implements Serializable {

  private static final long serialVersionUID = -5716971729639195777L;

  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(nullable = false)
  @Id
  @EqualsAndHashCode.Include
  private Long pid;

  @Column(name = "id", nullable = false)
  @EqualsAndHashCode.Include
  private String id;

  @Column(name = "name", nullable = false)
  @EqualsAndHashCode.Include
  private String name;

  @Column(name = "partner_handler")
  private String partnerHandler;

  @Column(name = "status", nullable = false)
  @Type(type = "com.nexage.admin.core.custom.type.StatusEnumType")
  private Status status;

  @Version
  @Column(name = "version", nullable = false)
  private Integer version;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "last_update", nullable = false)
  private Date lastUpdate;

  @Column(name = "description")
  private String description;

  @Column(name = "fee")
  private BigDecimal fee;

  @Column(name = "fee_type")
  @Type(type = "com.nexage.admin.core.custom.type.FeeTypeEnumType")
  private FeeType feeType;

  @Column(name = "response_config")
  private String responseConfig;

  @OneToMany(
      mappedBy = "hbPartner",
      fetch = FetchType.LAZY,
      orphanRemoval = true,
      cascade = CascadeType.ALL)
  @NotAudited
  private Set<HbPartnerSite> hbPartnerSite = new HashSet<>();

  @OneToMany(
      mappedBy = "hbPartner",
      fetch = FetchType.LAZY,
      orphanRemoval = true,
      cascade = CascadeType.ALL)
  @NotAudited
  private Set<HbPartnerCompany> hbPartnerCompany = new HashSet<>();

  @OneToMany(
      mappedBy = "hbPartner",
      fetch = FetchType.LAZY,
      orphanRemoval = true,
      cascade = CascadeType.ALL)
  @NotAudited
  private Set<HbPartnerPosition> hbPartnerPosition = new HashSet<>();

  @ColumnDefault("false")
  @Column(name = "formatted_default_type_enabled")
  private boolean formattedDefaultTypeEnabled;

  @ColumnDefault("false")
  @Column(name = "multi_impression_bid")
  private boolean multiImpressionBid;

  @ColumnDefault("false")
  @Column(name = "fill_max_duration")
  private boolean fillMaxDuration;

  @Column(name = "max_ads_per_pod")
  private Integer maxAdsPerPod;

  @PrePersist
  @PreUpdate
  private void prePersist() {
    lastUpdate = Calendar.getInstance().getTime();
  }
}
