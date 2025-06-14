package com.nexage.app.mapper.site;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nexage.admin.core.enums.site.Type;
import com.nexage.app.dto.publisher.PublisherSiteDTO.SiteType;
import org.junit.jupiter.api.Test;

class SiteTypeMapperTest {

  @Test
  void shouldMapSiteTypeDooh() {
    assertEquals(Type.DOOH, SiteTypeMapper.MAPPER.map(SiteType.DOOH));
  }

  @Test
  void shouldMapTypeDooh() {
    assertEquals(SiteType.DOOH, SiteTypeMapper.MAPPER.map(Type.DOOH));
  }

  @Test
  void shouldMapSiteTypeWebsite() {
    assertEquals(Type.WEBSITE, SiteTypeMapper.MAPPER.map(SiteType.WEBSITE));
  }

  @Test
  void shouldMapTypeWebsite() {
    assertEquals(SiteType.WEBSITE, SiteTypeMapper.MAPPER.map(Type.WEBSITE));
  }
}
