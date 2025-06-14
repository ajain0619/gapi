package com.nexage.admin.core.bidder.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nexage.admin.core.model.Company;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import javax.persistence.AssociationOverride;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

@Entity
@Table(name = "bdr_exchange_company")
@AssociationOverride(
    name = "exchangeCompanyPk.bidderExchange",
    joinColumns = @JoinColumn(name = "exchange_pid"))
@AssociationOverride(
    name = "exchangeCompanyPk.company",
    joinColumns = @JoinColumn(name = "company_pid"))
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class BdrExchangeCompany {

  @EmbeddedId @EqualsAndHashCode.Include private BdrExchangeCompanyPk exchangeCompanyPk;

  @Column(name = "bidding_fee")
  @Range(min = 0, max = 1)
  @EqualsAndHashCode.Include
  private BigDecimal biddingFee = BigDecimal.valueOf(0.0);

  @Version
  @Column(name = "version", nullable = false)
  private Integer version;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "updated_on")
  @JsonIgnore
  private Date updatedOn;

  public BdrExchangeCompany(BDRExchange exchange, Company company) {
    exchangeCompanyPk = new BdrExchangeCompanyPk(exchange, company);
  }

  @PrePersist
  @PreUpdate
  private void prePersist() {
    updatedOn = Calendar.getInstance().getTime();
  }
}
