package com.nexage.app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.nexage.app.dto.tag.TagPerformanceMetricsDTO;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = PositionArchiveTransactionDTO.Builder.class)
public class PositionArchiveTransactionDTO {
  private final List<TagPerformanceMetricsDTO> performanceMetrics;
  private final String txId;

  private PositionArchiveTransactionDTO(Builder builder) {
    this.performanceMetrics = builder.performanceMetrics;
    this.txId = builder.txId;
  }

  public List<TagPerformanceMetricsDTO> getPerformanceMetrics() {
    return performanceMetrics;
  }

  public String getTxId() {
    return txId;
  }

  public static final class Builder {
    private List<TagPerformanceMetricsDTO> performanceMetrics;
    private String txId;

    public Builder withTransaction(String txId) {
      this.txId = txId;
      return this;
    }

    public Builder withPerformanceMetrics(List<TagPerformanceMetricsDTO> performanceMetrics) {
      this.performanceMetrics = performanceMetrics;
      return this;
    }

    public PositionArchiveTransactionDTO build() {
      return new PositionArchiveTransactionDTO(this);
    }
  }
}
