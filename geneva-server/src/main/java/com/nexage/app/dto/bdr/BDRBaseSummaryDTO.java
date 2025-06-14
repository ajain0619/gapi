package com.nexage.app.dto.bdr;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BDRBaseSummaryDTO {

  protected Long impressions = 0L;
  protected Long clicks = 0L;
  protected Long conversions = 0L;
  protected BigDecimal spend = BigDecimal.ZERO;

  protected Double ctr = 0D;
  protected BigDecimal eCPM = BigDecimal.ZERO;
  protected BigDecimal cpi = BigDecimal.ZERO;

  protected void calculate() {
    this.ctr = (impressions > 0) ? (clicks / (double) impressions) : 0D;
    this.eCPM =
        (impressions > 0 && spend != null)
            ? spend
                .divide(BigDecimal.valueOf(impressions), RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(1000))
            : BigDecimal.ZERO;
    this.cpi =
        (conversions > 0 && spend != null)
            ? spend.divide(BigDecimal.valueOf(conversions), RoundingMode.HALF_UP)
            : BigDecimal.ZERO;
  }

  public Long getImpressions() {
    return impressions;
  }

  public Long getClicks() {
    return clicks;
  }

  public Long getConversions() {
    return conversions;
  }

  public BigDecimal getSpend() {
    return spend;
  }

  public Double getCtr() {
    return ctr;
  }

  public BigDecimal geteCPM() {
    return eCPM;
  }

  public BigDecimal getCpi() {
    return cpi;
  }
}
