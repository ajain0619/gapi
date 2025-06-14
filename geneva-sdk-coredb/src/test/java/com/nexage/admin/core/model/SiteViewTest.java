package com.nexage.admin.core.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nexage.admin.core.enums.Status;
import org.junit.jupiter.api.Test;

class SiteViewTest {

  @Test
  void shouldAllowSettingStatus() {
    SiteView siteView = new SiteView(1L, "foo", Status.INACTIVE);

    siteView.setStatus(Status.ACTIVE);

    assertEquals(Status.ACTIVE, siteView.getStatus());
    assertEquals(1, siteView.getStatusVal());
  }

  @Test
  void shouldAllowSettingStatusVal() {
    SiteView siteView = new SiteView(1L, "foo", Status.INACTIVE);

    siteView.setStatusVal(1);

    assertEquals(Status.ACTIVE, siteView.getStatus());
    assertEquals(1, siteView.getStatusVal());
  }
}
