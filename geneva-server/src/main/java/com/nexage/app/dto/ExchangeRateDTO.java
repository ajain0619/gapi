package com.nexage.app.dto;

import java.math.BigDecimal;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ExchangeRateDTO {

  private String currency;
  private Date checkDate;
  private BigDecimal rate;
  private Long forexId;
}
