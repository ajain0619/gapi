package com.nexage.admin.core.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ExchangeRatePrimaryKey implements Serializable {

  @Column(name = "currency", length = 3)
  @NotNull
  private String currency;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "check_date")
  @NotNull
  private Date checkDate;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ExchangeRatePrimaryKey)) {
      return false;
    }
    ExchangeRatePrimaryKey that = (ExchangeRatePrimaryKey) o;
    return currency.equals(that.currency) && checkDate.equals(that.checkDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(currency, checkDate);
  }
}
