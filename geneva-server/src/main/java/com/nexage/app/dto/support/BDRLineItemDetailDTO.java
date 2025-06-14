package com.nexage.app.dto.support;

import com.nexage.admin.core.bidder.type.BDRFreqCapMode;
import com.nexage.admin.core.bidder.type.BDRLineItemStatus;
import com.nexage.admin.core.bidder.type.BDRLineItemType;
import java.math.BigDecimal;

/**
 * Dto for BDRLineItem {@link com.nexage.admin.core.bidder.model.BDRLineItem}
 *
 * @author Eugeny Yurko
 * @since 19.09.2014
 */
public class BDRLineItemDetailDTO extends BDRLineItemDTO {

  private String externalId;
  private BDRLineItemStatus status;
  private BigDecimal spend;
  private Long impressions;
  private BDRLineItemType type;
  private Long dailyImpressionCap;
  private BigDecimal dailySpendCap;
  private BDRFreqCapMode frequencyCapMode;
  private Long frequencyPerDay;
  private Long frequencyPerWeek;
  private Long frequencyPerMonth;
  private Long frequencyPerLife;
  private String app;
  private boolean deployable;
  private String start;
  private String stop;
  private Long insertionOrderPid;

  protected BDRLineItemDetailDTO(Builder builder) {
    super(builder);
    externalId = builder.externalId;
    status = builder.status;
    spend = builder.spend;
    impressions = builder.impressions;
    type = builder.type;
    dailyImpressionCap = builder.dailyImpressionCap;
    dailySpendCap = builder.dailySpendCap;
    frequencyCapMode = builder.frequencyCapMode;
    frequencyPerDay = builder.frequencyPerDay;
    frequencyPerWeek = builder.frequencyPerWeek;
    frequencyPerMonth = builder.frequencyPerMonth;
    frequencyPerLife = builder.frequencyPerLife;
    app = builder.app;
    deployable = builder.deployable;
    start = builder.start;
    stop = builder.stop;
    insertionOrderPid = builder.insertionOrderPid;
  }

  public String getExternalId() {
    return externalId;
  }

  public BDRLineItemStatus getStatus() {
    return status;
  }

  public BigDecimal getSpend() {
    return spend;
  }

  public Long getImpressions() {
    return impressions;
  }

  public BDRLineItemType getType() {
    return type;
  }

  public Long getDailyImpressionCap() {
    return dailyImpressionCap;
  }

  public BigDecimal getDailySpendCap() {
    return dailySpendCap;
  }

  public BDRFreqCapMode getFrequencyCapMode() {
    return frequencyCapMode;
  }

  public Long getFrequencyPerDay() {
    return frequencyPerDay;
  }

  public Long getFrequencyPerWeek() {
    return frequencyPerWeek;
  }

  public Long getFrequencyPerMonth() {
    return frequencyPerMonth;
  }

  public Long getFrequencyPerLife() {
    return frequencyPerLife;
  }

  public String getApp() {
    return app;
  }

  public boolean isDeployable() {
    return deployable;
  }

  public String getStart() {
    return start;
  }

  public String getStop() {
    return stop;
  }

  public Long getInsertionOrderPid() {
    return insertionOrderPid;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder extends BDRLineItemDTO.Builder {
    private String externalId;
    private BDRLineItemStatus status;
    private BigDecimal spend;
    private Long impressions;
    private BDRLineItemType type;
    private Long dailyImpressionCap;
    private BigDecimal dailySpendCap;
    private BDRFreqCapMode frequencyCapMode;
    private Long frequencyPerDay;
    private Long frequencyPerWeek;
    private Long frequencyPerMonth;
    private Long frequencyPerLife;
    private String app;
    private boolean deployable;
    private String start;
    private String stop;
    private Long insertionOrderPid;

    private Builder() {
      super();
    }

    public Builder withExternalId(String externalId) {
      this.externalId = externalId;
      return this;
    }

    public Builder withStatus(BDRLineItemStatus status) {
      this.status = status;
      return this;
    }

    public Builder withSpend(BigDecimal spend) {
      this.spend = spend;
      return this;
    }

    public Builder withImpressions(Long impressions) {
      this.impressions = impressions;
      return this;
    }

    public Builder withType(BDRLineItemType type) {
      this.type = type;
      return this;
    }

    public Builder withDailyImpressionCap(Long dailyImpressionCap) {
      this.dailyImpressionCap = dailyImpressionCap;
      return this;
    }

    public Builder withDailySpendCap(BigDecimal dailySpendCap) {
      this.dailySpendCap = dailySpendCap;
      return this;
    }

    public Builder withFrequencyCapMode(BDRFreqCapMode frequencyCapMode) {
      this.frequencyCapMode = frequencyCapMode;
      return this;
    }

    public Builder withFrequencyPerDay(Long frequencyPerDay) {
      this.frequencyPerDay = frequencyPerDay;
      return this;
    }

    public Builder withFrequencyPerWeek(Long frequencyPerWeek) {
      this.frequencyPerWeek = frequencyPerWeek;
      return this;
    }

    public Builder withFrequencyPerMonth(Long frequencyPerMonth) {
      this.frequencyPerMonth = frequencyPerMonth;
      return this;
    }

    public Builder withFrequencyPerLife(Long frequencyPerLife) {
      this.frequencyPerLife = frequencyPerLife;
      return this;
    }

    public Builder withApp(String app) {
      this.app = app;
      return this;
    }

    public Builder withDeployable(boolean deployable) {
      this.deployable = deployable;
      return this;
    }

    public Builder withStart(String start) {
      this.start = start;
      return this;
    }

    public Builder withStop(String stop) {
      this.stop = stop;
      return this;
    }

    public Builder withInsertionOrderPid(Long insertionOrderPid) {
      this.insertionOrderPid = insertionOrderPid;
      return this;
    }

    @Override
    public BDRLineItemDetailDTO build() {
      return new BDRLineItemDetailDTO(this);
    }
  }
}
