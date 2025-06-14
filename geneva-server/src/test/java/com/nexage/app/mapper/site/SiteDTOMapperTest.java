package com.nexage.app.mapper.site;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.model.SiteView;
import org.junit.jupiter.api.Test;

class SiteDTOMapperTest {

  @Test
  void shouldMapToDto() {
    var siteView = new SiteView(1L, "site", Status.ACTIVE, "example.com", "Example");

    var out = SiteDTOMapper.MAPPER.map(siteView);
    assertAll(
        () -> assertEquals(siteView.getPid(), out.getPid()),
        () -> assertEquals(siteView.getName(), out.getName()),
        () -> assertEquals(siteView.getUrl(), out.getUrl()),
        () -> assertEquals(siteView.getCompany().getName(), out.getCompanyName()),
        () -> assertNull(out.getMetadataEnablement()),
        () -> assertNull(out.getHbEnabled()));
  }
}
