package com.nexage.app.mapper;

import static com.nexage.app.mapper.SmartExchangeAttributesDTOMapper.SMART_EXCHANGE_ATTR_MAPPER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.admin.core.model.SellerAttributes;
import com.nexage.admin.core.sparta.jpa.model.SmartExchangeAttributes;
import com.nexage.app.dto.smartexchangeattributes.SmartExchangeAttributesDTO;
import java.util.Date;
import org.junit.jupiter.api.Test;

class SmartExchangeAttributesDTOMapperTest {

  @Test
  void shouldCreateModelFromDTO() {
    SmartExchangeAttributesDTO dto = createSmartExchangeAttributesDTO();
    SellerAttributes sellerAttributes = new SellerAttributes();

    SmartExchangeAttributes model = SMART_EXCHANGE_ATTR_MAPPER.map(dto, sellerAttributes);

    assertNotNull(model);
    assertNull(model.getPid());
    assertNull(model.getVersion());
    validateSmartExchangeAttributes(model, sellerAttributes, true);
  }

  @Test
  void shouldCreateDefaultModelWhenDTONull() {
    SellerAttributes sellerAttributes = new SellerAttributes();

    SmartExchangeAttributes model = SMART_EXCHANGE_ATTR_MAPPER.map(null, sellerAttributes);

    assertNotNull(model);
    assertNull(model.getPid());
    assertNull(model.getVersion());
    validateSmartExchangeAttributes(model, sellerAttributes, false);
  }

  @Test
  void shouldCreateDTOFromModel() {
    SmartExchangeAttributes model = createSmartExchangeAttributes();
    model.setPid(1L);
    model.setVersion(1);
    model.setSellerAttributes(new SellerAttributes());

    SmartExchangeAttributesDTO dto = SMART_EXCHANGE_ATTR_MAPPER.map(model);

    assertNotNull(dto);
    assertEquals(1, dto.getVersion());
    assertTrue(dto.getSmartMarginEnabled());
  }

  @Test
  void shouldCreateNullDTOWhenModelNull() {
    SmartExchangeAttributesDTO dto = SMART_EXCHANGE_ATTR_MAPPER.map(null);

    assertNull(dto);
  }

  @Test
  void shouldUpdateModel() {
    SellerAttributes sellerAttributes = new SellerAttributes();
    SmartExchangeAttributes original = new SmartExchangeAttributes();
    original.setPid(1L);
    original.setVersion(1);
    original.setSellerAttributes(sellerAttributes);
    Date date = new Date();
    original.setCreatedOn(date);
    original.setUpdatedOn(date);

    SmartExchangeAttributes updated = createSmartExchangeAttributes();

    SMART_EXCHANGE_ATTR_MAPPER.updateOriginal(updated, original);

    assertNotNull(original);
    assertEquals(1L, original.getPid());
    assertEquals(1, original.getVersion());
    assertEquals(date, original.getCreatedOn());
    assertEquals(date, original.getUpdatedOn());
    validateSmartExchangeAttributes(original, sellerAttributes, true);
  }

  @Test
  void shouldNotUpdateModelFromNullObject() {
    SellerAttributes sellerAttributes = new SellerAttributes();
    SmartExchangeAttributes original = new SmartExchangeAttributes();
    original.setPid(1L);
    original.setVersion(1);
    original.setSellerAttributes(sellerAttributes);

    SMART_EXCHANGE_ATTR_MAPPER.updateOriginal(null, original);

    assertNotNull(original);
    assertEquals(1L, original.getPid());
    assertEquals(1, original.getVersion());
    validateSmartExchangeAttributes(original, sellerAttributes, false);
  }

  private SmartExchangeAttributesDTO createSmartExchangeAttributesDTO() {
    return SmartExchangeAttributesDTO.newBuilder()
        .withVersion(2)
        .withSmartMarginEnabled(true)
        .build();
  }

  private SmartExchangeAttributes createSmartExchangeAttributes() {
    SmartExchangeAttributes smartExchangeAttributes = new SmartExchangeAttributes();
    smartExchangeAttributes.setSmartMarginOverride(true);

    return smartExchangeAttributes;
  }

  private void validateSmartExchangeAttributes(
      SmartExchangeAttributes smartExchangeAttributes,
      SellerAttributes sellerAttributes,
      boolean smartMarginEnabled) {

    if (smartMarginEnabled) {
      assertTrue(smartExchangeAttributes.getSmartMarginOverride());
    } else {
      assertFalse(smartExchangeAttributes.getSmartMarginOverride());
    }

    assertEquals(sellerAttributes, smartExchangeAttributes.getSellerAttributes());
  }
}
