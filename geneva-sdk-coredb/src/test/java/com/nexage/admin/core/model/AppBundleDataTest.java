package com.nexage.admin.core.model;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class AppBundleDataTest {

  @Test
  void shouldPrePersist() {
    AppBundleData appBundleData = new AppBundleData();
    appBundleData.setAppBundleId("abc.xyz.com");
    appBundleData.setPid(1L);
    appBundleData.prePersist();

    assertNotNull(appBundleData.getCreatedOn());
    assertNotNull(appBundleData.getUpdatedOn());
  }

  @Test
  void shouldPreUpdate() {
    AppBundleData appBundleData = new AppBundleData();
    appBundleData.setAppBundleId("abc.xyz.com");
    appBundleData.setPid(1L);
    appBundleData.preUpdate();
    assertNotNull(appBundleData.getUpdatedOn());
  }
}
