package com.nexage.app.dto.bdr;

import com.nexage.admin.core.bidder.model.view.BDRLineItemView;
import com.nexage.admin.core.bidder.model.view.BDRTargetGroupView;
import com.nexage.admin.core.bidder.model.view.RevInfo;
import com.nexage.app.dto.CreativeSummaryBeanDTO;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class BDRLineItemSummaryDTO extends BDRBaseSummaryDTO {

  private final BDRLineItemView lineItem;

  private final List<BDRTargetGroupSummaryDTO> targetGroups = new ArrayList<>();

  private BigDecimal maxPrice = BigDecimal.ZERO;

  public BDRLineItemSummaryDTO(
      BDRLineItemView lineItem, Long insertionOrderPid, Map<String, CreativeSummaryBeanDTO> map) {
    this.lineItem = lineItem;

    for (BDRTargetGroupView view : lineItem.getTargetGroups()) {
      BDRTargetGroupSummaryDTO summary =
          new BDRTargetGroupSummaryDTO(view, insertionOrderPid, lineItem.getPid(), map);
      targetGroups.add(summary);

      if (summary.getMaxPrice() == null) this.maxPrice = BigDecimal.ZERO;
      else
        this.maxPrice =
            this.maxPrice.compareTo(summary.getMaxPrice()) < 0
                ? summary.getMaxPrice()
                : this.maxPrice;

      this.clicks += summary.getClicks();
      this.conversions += summary.getConversions();
      this.impressions += summary.getImpressions();
      this.spend = this.spend.add(summary.getSpend());
    }

    calculate();
  }

  public Long getPid() {
    return lineItem.getPid();
  }

  public String getName() {
    return lineItem.getName();
  }

  public Date getStartDate() {
    return lineItem.getStartDate();
  }

  public Date getStopDate() {
    return lineItem.getStopDate();
  }

  public Long getImpressionGoal() {
    return lineItem.getImpressionGoal();
  }

  public BigDecimal getSpendGoal() {
    return lineItem.getSpendGoal();
  }

  public int getStatus() {
    return lineItem.getStatus();
  }

  public RevInfo getRevinfo() {
    return lineItem.getRevinfo();
  }

  public BigDecimal getMaxPrice() {
    return maxPrice;
  }

  public List<BDRTargetGroupSummaryDTO> getTargetGroups() {
    return targetGroups;
  }
}
