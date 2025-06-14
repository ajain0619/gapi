package com.nexage.app.dto.bdr;

import com.nexage.admin.core.bidder.model.view.BDRCreativeView;
import com.nexage.admin.core.bidder.model.view.BDRTargetGroupView;
import com.nexage.admin.core.bidder.model.view.RevInfo;
import com.nexage.app.dto.CreativeSummaryBeanDTO;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BDRTargetGroupSummaryDTO extends BDRBaseSummaryDTO {

  private final BDRTargetGroupView targetGroup;

  private List<BDRCreativeSummaryDTO> creatives = new ArrayList<>();

  public BDRTargetGroupSummaryDTO(
      BDRTargetGroupView targetGroup,
      Long insertionOrderPid,
      Long lineItemPid,
      Map<String, CreativeSummaryBeanDTO> map) {
    this.targetGroup = targetGroup;

    for (BDRCreativeView view : targetGroup.getCreatives()) {
      BDRCreativeSummaryDTO summary =
          new BDRCreativeSummaryDTO(
              view, insertionOrderPid, lineItemPid, targetGroup.getPid(), map);
      creatives.add(summary);

      this.clicks += summary.getClicks();
      this.conversions += summary.getConversions();
      this.impressions += summary.getImpressions();
      this.spend = this.spend.add(summary.getSpend());
    }

    calculate();
  }

  public Long getPid() {
    return targetGroup.getPid();
  }

  public String getName() {
    return targetGroup.getName();
  }

  public int getStatus() {
    return targetGroup.getStatus();
  }

  public RevInfo getRevinfo() {
    return targetGroup.getRevinfo();
  }

  public BigDecimal getMaxPrice() {
    return targetGroup.getMaxPrice();
  }

  public List<BDRCreativeSummaryDTO> getCreatives() {
    return creatives;
  }
}
