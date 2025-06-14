package com.nexage.app.dto.bdr;

import java.io.Serializable;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class BdrExchangeCompanyDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  @EqualsAndHashCode.Include private BdrExchangeCompanyPkDTO exchangeCompanyPk;
  @EqualsAndHashCode.Include private BigDecimal biddingFee;
  private Integer version;
}
