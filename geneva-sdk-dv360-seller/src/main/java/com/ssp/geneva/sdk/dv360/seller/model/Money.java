package com.ssp.geneva.sdk.dv360.seller.model;

import static java.util.Objects.isNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ssp.geneva.sdk.dv360.seller.annotation.CurrencyCode;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.validation.annotation.Validated;

@Log4j2
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Validated
@Builder
@JsonInclude(Include.NON_NULL)
public class Money implements Serializable {
  private static final int DECIMAL_PLACES = 2;
  private static final double DECIMAL_TO_INTEGER_CONVERSION = 100.00D;
  private static final int INTEGER_TO_NANOS_CONVERSION = 10000000;
  // ISO 4217 currency code
  @EqualsAndHashCode.Include @CurrencyCode private String currencyCode;
  @EqualsAndHashCode.Include private String units;
  @EqualsAndHashCode.Include private Integer nanos;

  public static Money buildMoney(
      @CurrencyCode String currencyCode, BigDecimal amount, BigDecimal defaultAmount) {
    if (isNull(amount)) {
      amount = defaultAmount;
    }
    log.debug("currencyCode: {}, amount: {}", currencyCode, amount);
    var moneyUnits = amount.longValue();
    log.debug("moneyUnits: {}", moneyUnits);
    var decimalNanos =
        BigDecimal.valueOf(amount.doubleValue() - (double) moneyUnits)
            .setScale(DECIMAL_PLACES, RoundingMode.HALF_UP)
            .doubleValue();
    log.debug("decimalNanos: {}", decimalNanos);
    var intNanos = (int) (decimalNanos * DECIMAL_TO_INTEGER_CONVERSION);
    log.debug("intNanos: {}", intNanos);
    int moneyNanos = intNanos * INTEGER_TO_NANOS_CONVERSION;
    log.debug("moneyNanos: {}", moneyNanos);

    return Money.builder()
        .currencyCode(currencyCode)
        .units(Long.toString(moneyUnits))
        .nanos(moneyNanos)
        .build();
  }
}
