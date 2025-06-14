package com.nexage.app.dto.bdr;

import com.nexage.admin.core.bidder.model.view.BDRInsertionOrderView;
import com.nexage.admin.core.bidder.model.view.BDRLineItemView;
import com.nexage.admin.core.bidder.model.view.RevInfo;
import com.nexage.admin.core.util.BDRInsertionOrderHelper;
import com.nexage.app.dto.CreativeSummaryBeanDTO;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class BDRInsertionOrderSummaryDTO extends BDRBaseSummaryDTO {

  private final BDRInsertionOrderView insertionOrder;

  private final List<BDRLineItemSummaryDTO> lineItems = new ArrayList<>();

  private Date startDate;
  private Date stopDate;
  private Long impressionGoal = 0L;
  private BigDecimal spendGoal = BigDecimal.ZERO;
  private int status = 0;
  private BigDecimal maxPrice = BigDecimal.ZERO;

  public BDRInsertionOrderSummaryDTO(
      BDRInsertionOrderView insertionOrder, Map<String, CreativeSummaryBeanDTO> map) {
    this.insertionOrder = insertionOrder;

    List<Integer> lineItemStatuses = new ArrayList<>();

    for (BDRLineItemView view : insertionOrder.getLineItems()) {
      BDRLineItemSummaryDTO summary = new BDRLineItemSummaryDTO(view, insertionOrder.getPid(), map);
      lineItems.add(summary);

      lineItemStatuses.add(summary.getStatus());

      if (summary.getSpendGoal() != null) {
        this.spendGoal = this.spendGoal.add(summary.getSpendGoal());
      }
      if (summary.getImpressionGoal() != null) {
        this.impressionGoal = this.impressionGoal + summary.getImpressionGoal();
      }
      this.maxPrice =
          this.maxPrice.compareTo(summary.getMaxPrice()) < 0
              ? summary.getMaxPrice()
              : this.maxPrice;

      if (summary.getStartDate() != null) {
        if (this.startDate == null) this.startDate = summary.getStartDate();
        else if (this.startDate.compareTo(summary.getStartDate()) > 0)
          this.startDate = summary.getStartDate();
      }
      if (summary.getStopDate() != null) {
        if (this.stopDate == null) this.stopDate = summary.getStopDate();
        else if (this.stopDate.compareTo(summary.getStopDate()) < 0)
          this.stopDate = summary.getStopDate();
      }

      this.clicks += summary.getClicks();
      this.conversions += summary.getConversions();
      this.impressions += summary.getImpressions();
      this.spend = this.spend.add(summary.getSpend());
    }

    calculate();

    this.status =
        BDRInsertionOrderHelper.getInsertionOrderStatusFromLineItemStatuses(lineItemStatuses);
  }

  public Long getPid() {
    return insertionOrder.getPid();
  }

  public String getName() {
    return insertionOrder.getName();
  }

  public Integer getType() {
    return insertionOrder.getType();
  }

  public String getAdvertiserName() {
    return insertionOrder.getAdvertiser().getName();
  }

  public Long getAdvertiserPid() {
    return insertionOrder.getAdvertiser().getPid();
  }

  public RevInfo getRevinfo() {
    return insertionOrder.getRevinfo();
  }

  public List<BDRLineItemSummaryDTO> getLineItems() {
    return lineItems;
  }

  public Date getStartDate() {
    return startDate;
  }

  public Date getStopDate() {
    return stopDate;
  }

  public Long getImpressionGoal() {
    return impressionGoal;
  }

  public BigDecimal getSpendGoal() {
    return spendGoal;
  }

  public int getStatus() {
    return status;
  }

  public BigDecimal getMaxPrice() {
    return maxPrice;
  }
}
