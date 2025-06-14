package com.nexage.app.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.google.common.collect.ImmutableList;
import com.nexage.admin.core.model.BrandProtectionCategory;
import com.nexage.admin.core.model.BrandProtectionTag;
import com.nexage.admin.core.model.CrsTagMapping;
import com.nexage.app.dto.CrsTagMappingDTO;
import com.nexage.app.dto.brand.protection.BrandProtectionDTO;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class BrandProtectionDTOMapperTest {

  @Test
  void mapEmptyObjectTest() {
    BrandProtectionDTO result = BrandProtectionDTOMapper.MAPPER.map(null);
    assertNull(result);
  }

  @Test
  void mapBrandProtectionTagToDTO() {
    BrandProtectionTag brandProtectionTag = new BrandProtectionTag();
    BrandProtectionCategory category = new BrandProtectionCategory();
    category.setPid(1L);
    brandProtectionTag.setCategory(category);
    brandProtectionTag.setName("test");
    brandProtectionTag.setPid(3l);
    brandProtectionTag.setRtbId("12345");
    List<CrsTagMapping> crsTagMappings = ImmutableList.of(new CrsTagMapping());
    brandProtectionTag.setCrsTagMappings(crsTagMappings);
    BrandProtectionDTO result = BrandProtectionDTOMapper.MAPPER.map(brandProtectionTag);
    assertEquals(
        result.getCategoryId(),
        brandProtectionTag.getCategory().getPid(),
        "The category id of the result should match the pid of the brand protection category");
    Collection<CrsTagMappingDTO> expectedCrsList =
        crsTagMappings.stream().map(CrsTagDTOMapping.MAPPER::map).collect(Collectors.toList());
    assertEquals(expectedCrsList.size(), result.getCrsTags().size());
    assertEquals(result.getName(), brandProtectionTag.getName());
    assertEquals(result.getPid(), brandProtectionTag.getPid());
    assertEquals(result.getRtbId(), brandProtectionTag.getRtbId());
  }
}
