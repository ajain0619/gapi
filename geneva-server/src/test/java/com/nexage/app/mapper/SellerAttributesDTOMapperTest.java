package com.nexage.app.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nexage.admin.core.model.SellerAttributes;
import com.nexage.app.dto.seller.SellerAttributesDTO;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class SellerAttributesDTOMapperTest {

  @Test
  void shouldReturnObjectWithSimilarValuesToEntity() {

    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerAttributes.setSellerPid(1L);
    sellerAttributes.setVersion(0);
    sellerAttributes.setHumanOptOut(false);
    sellerAttributes.setSmartQPSEnabled(false);
    sellerAttributes.setTransparencyMgmtEnablement(1);
    sellerAttributes.setIncludePubName(2);
    sellerAttributes.setPubNameAlias("1");
    sellerAttributes.setPubAliasId(1L);
    sellerAttributes.setRevenueShare(new BigDecimal("0"));
    sellerAttributes.setAdStrictApproval(false);
    sellerAttributes.setRevenueGroupPid(1L);

    SellerAttributesDTO dto = SellerAttributesDTOMapper.MAPPER.map(sellerAttributes);
    assertEquals(dto.getVersion().intValue(), sellerAttributes.getVersion());
    assertEquals(dto.getSellerPid(), sellerAttributes.getSellerPid());
    assertEquals(dto.getHumanOptOut(), sellerAttributes.getHumanOptOut());
    assertEquals(dto.getSmartQPSEnabled(), sellerAttributes.getSmartQPSEnabled());
    assertEquals(
        dto.getDefaultTransparencyMgmtEnablement().getId(),
        sellerAttributes.getTransparencyMgmtEnablement());
    assertEquals(dto.getTransparencyMode().asInt(), sellerAttributes.getIncludePubName());
    assertEquals(dto.getSellerNameAlias(), sellerAttributes.getPubNameAlias());
    assertEquals(dto.getSellerIdAlias(), sellerAttributes.getPubAliasId());
    assertEquals(dto.getRevenueShare(), sellerAttributes.getRevenueShare());
    assertEquals(dto.isAdStrictApproval(), sellerAttributes.getAdStrictApproval());
    assertEquals(dto.getRevenueGroupPid(), sellerAttributes.getRevenueGroupPid());
  }

  @Test
  void shouldReturnEmptyObjectWithEmptyEntity() {
    SellerAttributes sellerAttributes = null;
    SellerAttributesDTO dto = SellerAttributesDTOMapper.MAPPER.map(sellerAttributes);
    assertEquals(dto, new SellerAttributesDTO());
  }
}
