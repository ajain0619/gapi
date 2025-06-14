package com.nexage.admin.core.sparta.jpa.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nexage.admin.core.json.BigDecimalSerializer;
import com.nexage.admin.core.model.Site;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@Audited
@Table(name = "deal_term")
public class SiteDealTerm implements Serializable {

  private static final long serialVersionUID = 5031091611155586778L;

  public enum RevenueMode {
    REV_SHARE,
    FLAT;
  }

  @Column
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @EqualsAndHashCode.Include
  @ToString.Include
  private Long pid;

  @Version
  @Column(name = "VERSION", nullable = false)
  @ToString.Include
  private Integer version;

  @Column(name = "site_pid", insertable = false, updatable = false)
  @NotAudited
  @EqualsAndHashCode.Include
  @ToString.Include
  private Long sitePid;

  @ManyToOne
  @JoinColumn(name = "site_pid", referencedColumnName = "pid")
  @NotNull
  @JsonIgnore
  @JsonBackReference
  @ToString.Include
  private Site site;

  @Column(name = "effective_date")
  @Temporal(TemporalType.TIMESTAMP)
  @ToString.Include
  private Date effectiveDate;

  @Column(name = "flat_bands")
  @EqualsAndHashCode.Include
  @ToString.Include
  private String flatBands;

  @Column(name = "revenue_mode")
  @Enumerated(EnumType.STRING)
  @NotNull
  @EqualsAndHashCode.Include
  @ToString.Include
  private RevenueMode revenueMode;

  @Column(name = "nexage_rev_share")
  @JsonSerialize(using = BigDecimalSerializer.class)
  @EqualsAndHashCode.Include
  @ToString.Include
  private BigDecimal nexageRevenueShare;

  @Column(name = "rtb_fee")
  @JsonSerialize(using = BigDecimalSerializer.class)
  @EqualsAndHashCode.Include
  @ToString.Include
  private BigDecimal rtbFee;

  @Column(name = "tag_pid")
  @EqualsAndHashCode.Include
  @ToString.Include
  private Long tagPid;

  @JsonProperty("hasDealTermChanged")
  @Transient
  @ToString.Include
  private boolean dealTermChanged = false;

  // copy constructor
  public SiteDealTerm(SiteDealTerm term) {
    this.sitePid = (term.getSite() != null ? term.getSite().getPid() : term.getSitePid());
    this.effectiveDate = term.getEffectiveDate();
    this.flatBands = term.getFlatBands();
    this.revenueMode = term.getRevenueMode();
    this.nexageRevenueShare = term.getNexageRevenueShare();
    this.rtbFee = term.getRtbFee();
    this.tagPid = term.getTagPid();
    this.pid = term.getPid();
    this.version = term.version;
  }

  public boolean equalsPss(BigDecimal otherNexageRevenueShare, BigDecimal otherRtbFee) {
    return (this.nexageRevenueShare != null || otherNexageRevenueShare == null)
        && (this.nexageRevenueShare == null || otherNexageRevenueShare != null)
        && (this.nexageRevenueShare == null
            || this.nexageRevenueShare.compareTo(otherNexageRevenueShare) == 0)
        && (this.rtbFee != null || otherRtbFee == null)
        && (this.rtbFee == null || otherRtbFee != null)
        && (this.rtbFee == null || this.rtbFee.compareTo(otherRtbFee) == 0);
  }
}
