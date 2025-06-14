package com.nexage.app.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.admin.core.model.CompanyMdmId;
import com.nexage.admin.core.model.CompanyMdmView;
import com.nexage.admin.core.model.SellerSeatMdmId;
import com.nexage.admin.core.model.SellerSeatMdmView;
import com.nexage.app.dto.InventoryMdmIdDTO;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CompanyMdmViewMapperTest {

  @Test
  void shouldConvertSuccessfullyWhenCompanyMdmIdsUnavailable() {
    CompanyMdmView companyMdmView = new CompanyMdmView(10L, Collections.emptyList(), null);

    InventoryMdmIdDTO inventoryMdmIdDTO = CompanyMdmViewMapper.MAPPER.map(companyMdmView);

    assertEquals(companyMdmView.getPid(), inventoryMdmIdDTO.getSellerPid());
    assertTrue(inventoryMdmIdDTO.getCompanyMdmIds().isEmpty());
    assertTrue(inventoryMdmIdDTO.getSellerSeatMdmIds().isEmpty());
  }

  @Test
  void shouldConvertSuccessfullyWhenSellerSeatUnavailable() {
    CompanyMdmId mdmId1 = new CompanyMdmId();
    mdmId1.setId("mdm1");
    CompanyMdmId mdmId2 = new CompanyMdmId();
    mdmId2.setId("mdm2");
    CompanyMdmView companyMdmView = new CompanyMdmView(10L, List.of(mdmId1, mdmId2), null);

    InventoryMdmIdDTO inventoryMdmIdDTO = CompanyMdmViewMapper.MAPPER.map(companyMdmView);

    assertEquals(companyMdmView.getPid(), inventoryMdmIdDTO.getSellerPid());
    assertEquals(Set.of("mdm1", "mdm2"), inventoryMdmIdDTO.getCompanyMdmIds());
    assertTrue(inventoryMdmIdDTO.getSellerSeatMdmIds().isEmpty());
  }

  @Test
  void shouldConvertSuccessfullyWhenSellerSeatAvailable() {
    SellerSeatMdmId mdmId1 = new SellerSeatMdmId();
    mdmId1.setId("mdm1");
    CompanyMdmId mdmId2 = new CompanyMdmId();
    mdmId2.setId("mdm2");
    SellerSeatMdmView selerSeatMdmView = new SellerSeatMdmView(2L, List.of(mdmId1));
    CompanyMdmView companyMdmView = new CompanyMdmView(10L, List.of(mdmId2), selerSeatMdmView);

    InventoryMdmIdDTO inventoryMdmIdDTO = CompanyMdmViewMapper.MAPPER.map(companyMdmView);

    assertEquals(companyMdmView.getPid(), inventoryMdmIdDTO.getSellerPid());
    assertEquals(Set.of("mdm2"), inventoryMdmIdDTO.getCompanyMdmIds());
    assertEquals(Set.of("mdm1"), inventoryMdmIdDTO.getSellerSeatMdmIds());
  }
}
