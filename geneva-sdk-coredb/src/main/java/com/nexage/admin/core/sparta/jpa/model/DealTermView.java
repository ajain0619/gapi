package com.nexage.admin.core.sparta.jpa.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nexage.admin.core.json.BigDecimalSerializer;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Immutable;

@Entity
@Immutable
@Table(name = "deal_term")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class DealTermView implements Serializable {

  private static final long serialVersionUID = -4326589955439773514L;

  @EqualsAndHashCode.Include @ToString.Include @Id private Long pid;

  @Version
  @Column(name = "VERSION", nullable = false)
  private Integer version;

  @EqualsAndHashCode.Include
  @ToString.Include
  @Column(name = "site_pid", insertable = false, updatable = false)
  private Long sitePid;

  @EqualsAndHashCode.Include
  @ToString.Include
  @Column(name = "effective_date")
  @Temporal(TemporalType.TIMESTAMP)
  private Date effectiveDate;

  @Column(name = "flat_bands")
  private String flatBands;

  @EqualsAndHashCode.Include
  @ToString.Include
  @Column(name = "revenue_mode")
  @Enumerated(EnumType.STRING)
  private SiteDealTerm.RevenueMode revenueMode;

  @EqualsAndHashCode.Include
  @ToString.Include
  @Column(name = "nexage_rev_share")
  @JsonSerialize(using = BigDecimalSerializer.class)
  private BigDecimal nexageRevenueShare;

  @EqualsAndHashCode.Include
  @ToString.Include
  @Column(name = "rtb_fee")
  @JsonSerialize(using = BigDecimalSerializer.class)
  private BigDecimal rtbFee;

  @EqualsAndHashCode.Include
  @ToString.Include
  @Column(name = "tag_pid")
  private Long tagPid;
}
