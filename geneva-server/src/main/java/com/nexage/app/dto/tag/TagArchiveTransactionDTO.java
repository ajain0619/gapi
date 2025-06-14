package com.nexage.app.dto.tag;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class TagArchiveTransactionDTO {

  private final TagPerformanceMetricsDTO performanceMetrics;
  private final String txid;

  private TagArchiveTransactionDTO(Builder builder) {
    this.performanceMetrics = builder.tagPerformanceMetrics;
    this.txid = builder.transactionId;
  }

  public TagPerformanceMetricsDTO getPerformanceMetrics() {
    return performanceMetrics;
  }

  public String getTxId() {
    return txid;
  }

  public static final class Builder {

    private String transactionId;
    private TagPerformanceMetricsDTO tagPerformanceMetrics;

    public Builder withTransaction(String transactionId) {
      this.transactionId = transactionId;
      return this;
    }

    public Builder withPerformanceMetrics(TagPerformanceMetricsDTO tagPerformanceMetrics) {
      this.tagPerformanceMetrics = tagPerformanceMetrics;
      return this;
    }

    public TagArchiveTransactionDTO build() {
      return new TagArchiveTransactionDTO(this);
    }
  }
}
