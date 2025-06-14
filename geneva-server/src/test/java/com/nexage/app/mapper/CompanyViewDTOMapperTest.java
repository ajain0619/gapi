package com.nexage.app.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.SellerAttributes;
import com.nexage.app.dto.user.CompanyViewDTO;
import com.ssp.geneva.common.model.inventory.CompanyType;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CompanyViewDTOMapperTest {

  @Test
  void testMapDTO_minimumFieldsPopulated() {
    Company source = new Company(UUID.randomUUID().toString(), CompanyType.BUYER);

    CompanyViewDTO result = CompanyViewDTOMapper.MAPPER.map(source);

    assertNotNull(result);
    assertNull(result.getPid());
    assertEquals(source.getName(), result.getName());
    assertEquals(source.getType(), result.getType());
    assertFalse(result.isAdStrictApproval());
  }

  @Test
  void testMapDTO_optionalFieldsPopulated() {
    SellerAttributes attributes = new SellerAttributes();
    attributes.setAdStrictApproval(true);
    Company source = new Company(UUID.randomUUID().toString(), CompanyType.SELLER);
    source.setPid(123L);
    source.setSellerAttributes(attributes);

    CompanyViewDTO result = CompanyViewDTOMapper.MAPPER.map(source);

    assertNotNull(result);
    assertEquals(source.getName(), result.getName());
    assertEquals(source.getType(), result.getType());
    assertEquals(source.getPid(), result.getPid());
    assertTrue(result.isAdStrictApproval());
  }
}
