package com.nexage.admin.core.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.model.AdSource;
import com.nexage.admin.core.model.AdSource.BidEnabled;
import com.nexage.admin.core.model.AdSource.DecisionMakerEnabled;
import org.junit.jupiter.api.Test;

class AdSourceSummaryTest {

  @Test
  void testAdSourceSummary() {
    AdSource adSource = new AdSource();
    adSource.setPid(1234L);
    adSource.setName("AdSourceSummary");
    adSource.setStatus(Status.INACTIVE);
    adSource.setBidEnabled(BidEnabled.YES);
    adSource.setDecisionMakerEnabled(DecisionMakerEnabled.YES);

    AdSourceSummaryDTO adSourceSummary = new AdSourceSummaryDTO(adSource);
    assertEquals(adSource.getPid(), adSourceSummary.getPid());
    assertEquals(adSource.getName(), adSourceSummary.getName());
    assertEquals(adSource.getStatus(), adSourceSummary.getStatus());
    assertEquals(adSource.getBidEnabled(), adSourceSummary.getBidEnabled());
    assertEquals(adSource.getDecisionMakerEnabled(), adSourceSummary.getDecisionMakerEnabled());
  }
}
