package com.nexage.admin.core.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.admin.core.enums.FeeType;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class HbPartnerTest {

  @Test
  void hbPartnerModelTest() {

    HbPartner hbPartner = new HbPartner();
    hbPartner.setFee(BigDecimal.valueOf(1));
    hbPartner.setFeeType(FeeType.PERCENTAGE);
    hbPartner.setResponseConfig("some_string");
    hbPartner.setFormattedDefaultTypeEnabled(true);

    assertTrue(hbPartner.getFee().equals(BigDecimal.valueOf(1)));
    assertEquals(FeeType.PERCENTAGE, hbPartner.getFeeType());
    assertEquals("some_string", hbPartner.getResponseConfig());
    assertTrue(hbPartner.isFormattedDefaultTypeEnabled());
  }

  @Test
  void hbPartnerModelNullAndDefaultTest() {

    HbPartner hbPartner = new HbPartner();

    assertNull(hbPartner.getFee());
    assertNull(hbPartner.getFeeType());
    assertNull(hbPartner.getResponseConfig());
  }
}
