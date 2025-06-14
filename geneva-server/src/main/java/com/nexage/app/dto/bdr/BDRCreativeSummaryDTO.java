package com.nexage.app.dto.bdr;

import com.nexage.admin.core.bidder.model.view.BDRCreativeView;
import com.nexage.admin.core.bidder.model.view.RevInfo;
import com.nexage.app.dto.CreativeSummaryBeanDTO;
import java.util.Map;

public class BDRCreativeSummaryDTO extends BDRBaseSummaryDTO {

  private final BDRCreativeView creative;

  public BDRCreativeSummaryDTO(
      BDRCreativeView creative,
      Long insertionOrderPid,
      Long lineItemPid,
      Long targetGroupPid,
      Map<String, CreativeSummaryBeanDTO> map) {
    this.creative = creative;

    String key =
        new StringBuilder()
            .append(insertionOrderPid)
            .append(":")
            .append(lineItemPid)
            .append(":")
            .append(targetGroupPid)
            .append(":")
            .append(creative.getPid())
            .toString();

    CreativeSummaryBeanDTO metrics = map.get(key);
    if (metrics != null) {
      this.clicks = metrics.getClicks();
      this.conversions = metrics.getConversions();
      this.impressions = metrics.getImpressions();
      this.spend = metrics.getSpend();
    }
    calculate();
  }

  public Long getPid() {
    return creative.getPid();
  }

  public String getName() {
    return creative.getName();
  }

  public RevInfo getRevinfo() {
    return creative.getRevinfo();
  }
}
