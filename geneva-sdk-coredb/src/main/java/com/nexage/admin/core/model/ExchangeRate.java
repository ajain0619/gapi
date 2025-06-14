package com.nexage.admin.core.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "exchange_rate")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRate implements Serializable {

  @EmbeddedId private ExchangeRatePrimaryKey id;

  @Column(name = "rate", scale = 6, precision = 8)
  @NotNull
  private BigDecimal rate;

  @Column(name = "forex_id")
  @NotNull
  private Long forexId;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ExchangeRate)) {
      return false;
    }
    ExchangeRate that = (ExchangeRate) o;
    return id.equals(that.id) && rate.equals(that.rate) && forexId.equals(that.forexId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, rate, forexId);
  }
}
