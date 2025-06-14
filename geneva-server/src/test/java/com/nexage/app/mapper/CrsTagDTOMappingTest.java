package com.nexage.app.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.nexage.admin.core.model.BrandProtectionTag;
import com.nexage.admin.core.model.CrsTagMapping;
import com.nexage.app.dto.CrsTagMappingDTO;
import java.util.Date;
import org.junit.jupiter.api.Test;

class CrsTagDTOMappingTest {

  @Test
  void mapEmptyObjectTest() {
    CrsTagMappingDTO crstagMappingDto = CrsTagDTOMapping.MAPPER.map(null);
    assertNull(crstagMappingDto);
  }

  @Test
  void mapTest() {
    CrsTagMapping crsTagMapping = new CrsTagMapping();
    crsTagMapping.setCrsTagAttributeId(1l);
    crsTagMapping.setCrsTagId(7l);
    crsTagMapping.setPid(22l);
    BrandProtectionTag tag = new BrandProtectionTag();
    tag.setPid(27l);
    crsTagMapping.setTag(tag);
    crsTagMapping.setUpdateDate(new Date());
    CrsTagMappingDTO crsTagDTOMapping = CrsTagDTOMapping.MAPPER.map(crsTagMapping);
    assertEquals(crsTagDTOMapping.getPid(), crsTagMapping.getPid());
    assertEquals(crsTagDTOMapping.getCrsTagId(), crsTagMapping.getCrsTagId());
    assertEquals(crsTagDTOMapping.getCrsTagAttributeId(), crsTagMapping.getCrsTagAttributeId());
    assertEquals(crsTagDTOMapping.getUpdateDate(), crsTagMapping.getUpdateDate());
    assertEquals(crsTagDTOMapping.getBrandProtectionTagPid(), crsTagMapping.getTag().getPid());
  }
}
