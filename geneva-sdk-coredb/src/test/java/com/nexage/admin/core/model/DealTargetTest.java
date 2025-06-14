package com.nexage.admin.core.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

class DealTargetTest {
  @Test
  void equals_hashCode_PositiveTest() {
    DirectDeal directDeal1 = new DirectDeal();
    directDeal1.setDealId("directDealId");

    DealTarget dt1 = new DealTarget();
    dt1.setTargetType(BaseTarget.TargetType.COUNTRY_STATE);
    dt1.setRuleType(BaseTarget.RuleType.POSITIVE);
    dt1.setData("CHN/*,USA/MA");
    dt1.setDeal(directDeal1);

    DirectDeal directDeal2 = new DirectDeal();
    directDeal2.setDealId("directDealId"); // same dealId

    DealTarget dt2 = new DealTarget();
    dt2.setTargetType(BaseTarget.TargetType.COUNTRY_STATE);
    dt2.setRuleType(BaseTarget.RuleType.POSITIVE);
    dt2.setData("CHN/*,USA/MA");
    dt2.setDeal(directDeal2);

    // equals() and hashCode() behavior must be consistent
    assertEquals(dt1, dt2);
    assertEquals(dt1.hashCode(), dt2.hashCode());
  }

  @Test
  void equals_hashCode_DirectDealFields_PositiveTest() {
    DirectDeal directDeal1 = new DirectDeal();
    directDeal1.setDealId("directDealId");
    directDeal1.setAuctionType(0);

    DealTarget dt1 = new DealTarget();
    dt1.setTargetType(BaseTarget.TargetType.COUNTRY_STATE);
    dt1.setRuleType(BaseTarget.RuleType.POSITIVE);
    dt1.setData("CHN/*,USA/MA");
    dt1.setDeal(directDeal1);

    DirectDeal directDeal2 = new DirectDeal();
    directDeal2.setDealId("directDealId"); // same dealId
    directDeal2.setAuctionType(1); // only dealId is considered by equals() and hashCode()
    directDeal2.setStatus(
        DirectDeal.DealStatus.Active); // only dealId is considered by equals() and hashCode()
    directDeal2.setVisibility(true); // only dealId is considered by equals() and hashCode()

    DealTarget dt2 = new DealTarget();
    dt2.setTargetType(BaseTarget.TargetType.COUNTRY_STATE);
    dt2.setRuleType(BaseTarget.RuleType.POSITIVE);
    dt2.setData("CHN/*,USA/MA");
    dt2.setDeal(directDeal2);

    // equals() and hashCode() behavior must be consistent
    assertEquals(dt1, dt2);
    assertEquals(dt1.hashCode(), dt2.hashCode());
  }

  @Test
  void equals_hashCode_DealId_NegativeTest() {
    DirectDeal directDeal1 = new DirectDeal();
    directDeal1.setDealId("directDealId1");

    DealTarget dt1 = new DealTarget();
    dt1.setTargetType(BaseTarget.TargetType.COUNTRY_STATE);
    dt1.setRuleType(BaseTarget.RuleType.POSITIVE);
    dt1.setData("CHN/*,USA/MA");
    dt1.setDeal(directDeal1);

    DirectDeal directDeal2 = new DirectDeal();
    directDeal2.setDealId("directDealId2"); // different dealId

    DealTarget dt2 = new DealTarget();
    dt2.setTargetType(BaseTarget.TargetType.COUNTRY_STATE);
    dt2.setRuleType(BaseTarget.RuleType.POSITIVE);
    dt2.setData("CHN/*,USA/MA");
    dt2.setDeal(directDeal2);

    // equals() and hashCode() behavior must be consistent
    assertNotEquals(dt1, dt2);
    assertNotEquals(dt1.hashCode(), dt2.hashCode());
  }

  @Test
  void equals_hashCode_BaseTargetField_NegativeTest() {
    DirectDeal directDeal1 = new DirectDeal();
    directDeal1.setDealId("directDealId");

    DealTarget dt1 = new DealTarget();
    dt1.setTargetType(BaseTarget.TargetType.COUNTRY_STATE);
    dt1.setRuleType(BaseTarget.RuleType.POSITIVE);
    dt1.setData("CHN/*,USA/MA");
    dt1.setDeal(directDeal1);

    DirectDeal directDeal2 = new DirectDeal();
    directDeal2.setDealId("directDealId"); // same dealId

    DealTarget dt2 = new DealTarget();
    dt2.setTargetType(
        BaseTarget.TargetType.DEVICE_MAKE_MODEL); // one of BaseTarget fields different
    dt2.setRuleType(BaseTarget.RuleType.POSITIVE);
    dt2.setData("CHN/*,USA/MA");
    dt2.setDeal(directDeal2);

    // equals() and hashCode() behavior must be consistent
    assertNotEquals(dt1, dt2);
    assertNotEquals(dt1.hashCode(), dt2.hashCode());
  }
}
