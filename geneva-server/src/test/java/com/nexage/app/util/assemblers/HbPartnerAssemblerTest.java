package com.nexage.app.util.assemblers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.enums.FeeType;
import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.model.HbPartner;
import com.nexage.app.dto.HbPartnerDTO;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HbPartnerAssemblerTest {

  @InjectMocks HbPartnerAssembler hbPartnerAssembler;

  @Test
  void test_make() {
    HbPartner hbPartner = new HbPartner();
    hbPartner.setPid(111L);
    hbPartner.setId("test123ID");
    hbPartner.setName("test_partner");
    hbPartner.setStatus(Status.ACTIVE);
    hbPartner.setVersion(1);
    hbPartner.setPartnerHandler("google");
    hbPartner.setDescription("google partner handler 123");
    hbPartner.setResponseConfig("some_config");
    hbPartner.setFormattedDefaultTypeEnabled(true);
    hbPartner.setMultiImpressionBid(true);
    hbPartner.setMaxAdsPerPod(6);
    hbPartner.setFillMaxDuration(false);

    HbPartnerDTO hbPartnerDTO = hbPartnerAssembler.make(hbPartner);

    assertEquals(new Long(111L), hbPartnerDTO.getPid());
    assertEquals("test123ID", hbPartnerDTO.getId());
    assertEquals("test_partner", hbPartnerDTO.getName());
    assertEquals(Status.ACTIVE, hbPartnerDTO.getStatus());
    assertEquals(Integer.valueOf(1), hbPartnerDTO.getVersion());
    assertEquals("google", hbPartnerDTO.getPartnerHandler());
    assertEquals("google partner handler 123", hbPartnerDTO.getDescription());
    assertEquals("some_config", hbPartnerDTO.getResponseConfig());
    assertTrue(hbPartnerDTO.isFormattedDefaultTypeEnabled());
    assertTrue(hbPartnerDTO.isMultiImpressionBid());
    assertEquals(Integer.valueOf(6), hbPartnerDTO.getMaxAdsPerPod());
    assertFalse(hbPartnerDTO.isFillMaxDuration());
  }

  @Test
  void test_make_hbPartners_summary() {
    HbPartner partner = mock(HbPartner.class);
    when(partner.getPid()).thenReturn(123L);
    when(partner.getName()).thenReturn("test-partner-name");
    when(partner.isFormattedDefaultTypeEnabled()).thenReturn(true);
    when(partner.isMultiImpressionBid()).thenReturn(true);
    when(partner.isFillMaxDuration()).thenReturn(true);
    when(partner.getMaxAdsPerPod()).thenReturn(null);

    HbPartnerDTO hbPartnerDTO = hbPartnerAssembler.make(partner, HbPartnerAssembler.SUMMARY_FIELDS);
    assertEquals(new Long(123L), hbPartnerDTO.getPid());
    assertEquals("test-partner-name", hbPartnerDTO.getName());
    assertTrue(hbPartnerDTO.isFormattedDefaultTypeEnabled());
    assertTrue(hbPartnerDTO.isMultiImpressionBid());
    assertTrue(hbPartnerDTO.isFillMaxDuration());
    assertNull(hbPartnerDTO.getMaxAdsPerPod());
  }

  @Test
  void test_apply() throws Exception {
    HbPartnerDTO hbPartnerDTO = new HbPartnerDTO();
    hbPartnerDTO.setPid(111L);
    hbPartnerDTO.setId("test123ID");
    hbPartnerDTO.setName("test_partner");
    hbPartnerDTO.setStatus(Status.ACTIVE);
    hbPartnerDTO.setVersion(1);
    hbPartnerDTO.setPartnerHandler("google");
    hbPartnerDTO.setDescription("google partner handler 123");
    hbPartnerDTO.setFeeType(FeeType.PERCENTAGE);
    hbPartnerDTO.setFee(BigDecimal.valueOf(0.002));
    hbPartnerDTO.setResponseConfig("apply_this");
    hbPartnerDTO.setFormattedDefaultTypeEnabled(true);
    hbPartnerDTO.setMultiImpressionBid(false);
    hbPartnerDTO.setFillMaxDuration(false);
    hbPartnerDTO.setMaxAdsPerPod(null);

    HbPartner hbPartner = new HbPartner();

    try {
      hbPartner = hbPartnerAssembler.apply(hbPartner, hbPartnerDTO);
    } catch (Exception e) {
      fail("Unable to read file");
    }
    assertEquals(new Long(111L), hbPartner.getPid());
    assertEquals("test123ID", hbPartner.getId());
    assertEquals("test_partner", hbPartner.getName());
    assertEquals(Status.ACTIVE, hbPartner.getStatus());
    assertEquals(Integer.valueOf(1), hbPartner.getVersion());
    assertEquals("google", hbPartner.getPartnerHandler());
    assertEquals("google partner handler 123", hbPartner.getDescription());
    assertEquals(FeeType.PERCENTAGE, hbPartner.getFeeType());
    assertEquals(0, BigDecimal.valueOf(0.002).compareTo(hbPartner.getFee()));
    assertEquals("apply_this", hbPartner.getResponseConfig());
    assertTrue(hbPartner.isFormattedDefaultTypeEnabled());
    assertFalse(hbPartnerDTO.isMultiImpressionBid());
    assertFalse(hbPartnerDTO.isFillMaxDuration());
    assertNull(hbPartnerDTO.getMaxAdsPerPod());
  }
}
