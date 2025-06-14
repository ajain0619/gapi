package com.nexage.admin.core.bidder.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nexage.admin.core.model.Company;
import java.io.Serializable;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class BdrExchangeCompanyPk implements Serializable {

  private static final long serialVersionUID = 1L;

  @ManyToOne(fetch = FetchType.LAZY)
  private BDRExchange bidderExchange;

  @JsonIgnoreProperties(value = {"credits", "credit"})
  @ManyToOne(fetch = FetchType.LAZY)
  private Company company;
}
