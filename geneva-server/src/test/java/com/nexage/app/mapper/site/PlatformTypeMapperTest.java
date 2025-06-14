package com.nexage.app.mapper.site;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nexage.admin.core.enums.site.Platform;
import com.nexage.app.dto.publisher.PublisherSiteDTO;
import org.junit.jupiter.api.Test;

class PlatformTypeMapperTest {

  @Test
  void shouldMapPlatformTypeOther() {
    assertEquals(Platform.OTHER, PlatformTypeMapper.MAPPER.map(PublisherSiteDTO.Platform.OTHER));
  }

  @Test
  void shouldMapPublisherSiteDTOPlatformOther() {
    assertEquals(PublisherSiteDTO.Platform.OTHER, PlatformTypeMapper.MAPPER.map(Platform.OTHER));
  }
}
