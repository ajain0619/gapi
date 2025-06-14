package com.nexage.app.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.nexage.admin.core.model.DealInventory;
import com.nexage.admin.core.model.DealInventoryType;
import com.nexage.app.dto.deals.DealInventoriesDTO;
import com.nexage.app.mapper.deal.DealInventoriesDTOMapper;
import org.junit.jupiter.api.Test;

class DealInventoriesDTOMapperTest {

  @Test
  void shouldMapToDTO() {
    DealInventory dealInventory = new DealInventory();
    dealInventory.setPid(1234L);
    dealInventory.setFileName("domains.csv");
    dealInventory.setFileType(DealInventoryType.DOMAIN);

    DealInventoriesDTO result = DealInventoriesDTOMapper.MAPPER.map(dealInventory);
    assertNotNull(result);
    assertEquals(result.getPid(), dealInventory.getPid());
    assertEquals(result.getFileName(), dealInventory.getFileName());
    assertEquals(result.getFileType(), dealInventory.getFileType());
  }

  @Test
  void shouldMapToModel() {
    DealInventoriesDTO dealInventoriesDTO =
        new DealInventoriesDTO(1234L, "domains.csv", DealInventoryType.DOMAIN, 1L);

    DealInventory result = DealInventoriesDTOMapper.MAPPER.map(dealInventoriesDTO);
    assertNotNull(result);
    assertEquals(result.getPid(), dealInventoriesDTO.getPid());
    assertEquals(result.getFileName(), dealInventoriesDTO.getFileName());
    assertEquals(result.getFileType(), dealInventoriesDTO.getFileType());
  }
}
