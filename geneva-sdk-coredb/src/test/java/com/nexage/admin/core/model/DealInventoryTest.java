package com.nexage.admin.core.model;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class DealInventoryTest {

  @Test
  void shouldSetCreationAndUpdateDatesBeforePersisting() {
    DealInventory dealInventory = new DealInventory();
    dealInventory.setPid(1234L);
    dealInventory.setFileName("domain.csv");
    dealInventory.setFileType(DealInventoryType.DOMAIN);
    dealInventory.prePersist();

    assertNotNull(dealInventory.getCreatedOn());
    assertNotNull(dealInventory.getUpdatedOn());
  }

  @Test
  void shouldSetUpdateDateBeforeUpdating() {
    DealInventory dealInventory = new DealInventory();
    dealInventory.setPid(1234L);
    dealInventory.setFileName("domain.csv");
    dealInventory.setFileType(DealInventoryType.DOMAIN);
    dealInventory.preUpdate();

    assertNotNull(dealInventory.getUpdatedOn());
  }
}
