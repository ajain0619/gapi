package com.nexage.admin.core.model;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class AppAliasTest {

  @Test
  void shouldPrePersist() {
    AppAlias appAlias = new AppAlias();
    appAlias.setAppAlias("Samsung TV Plus");
    appAlias.setPid(1L);
    appAlias.prePersist();

    assertNotNull(appAlias.getCreatedOn());
    assertNotNull(appAlias.getUpdatedOn());
  }

  @Test
  void shouldPreUpdate() {
    AppAlias appAlias = new AppAlias();
    appAlias.setAppAlias("Samsung TV Plus");
    appAlias.setPid(1L);
    appAlias.preUpdate();
    assertNotNull(appAlias.getUpdatedOn());
  }
}
