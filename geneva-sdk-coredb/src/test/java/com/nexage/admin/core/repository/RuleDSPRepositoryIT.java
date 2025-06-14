package com.nexage.admin.core.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.model.DealBidderConfigView;
import com.nexage.admin.core.model.RuleDSPBiddersView;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRED)
@Sql(scripts = "/data/repository/rule-dsp-repository.sql", config = @SqlConfig(encoding = "utf-8"))
class RuleDSPRepositoryIT extends CoreDbSdkIntegrationTestBase {

  @Autowired private RuleDSPRepository ruleDSPRepository;

  @Test
  void shouldReturnDspsAndBiddersWhenBidderExists() {
    List<RuleDSPBiddersView> result = ruleDSPRepository.findDSPsWithActiveBidders();
    assertEquals(1, result.size(), "Only One company BUYER-1 should be returned");
    assertEquals(
        300L, result.get(0).getPid(), "BUYER-1 should be returned and should have pid 300");
    assertNotEquals(301L, result.get(0).getPid(), "Company retrieved cannot be BUYER-2");
    assertNotNull(result.get(0).getBidders(), "Company Buyer-1 should have bidders populated");
    assertEquals(2, result.get(0).getBidders().size(), "Buyer-1 should have 2 bidders");
    for (DealBidderConfigView each : result.get(0).getBidders()) {
      assertNotNull(each.getCompanyPid(), "Each Bidder should have companyPid");
      assertNotNull(each.getName(), "Each Bidder should have name");
      assertNotNull(each.getPid(), "Each Bidder should have pic");
      assertTrue(each.isTrafficStatus());
    }
  }
}
