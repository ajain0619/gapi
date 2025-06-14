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
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class BrandProtectionDTOParentsOnlyMapperTest {

  @Test
  void shouldReturnNonNullBPTag() {
    BrandProtectionDTO result = BrandProtectionDTOParentsOnlyMapper.MAPPER.map(null);
    assertNull(result);
  }

  @Test
  void shouldReturnValidBPTag() {
    BrandProtectionTag brandProtectionTag = new BrandProtectionTag();
    BrandProtectionTag parentTag = new BrandProtectionTag();
    parentTag.setPid(1L);
    parentTag.setName("testParent");
    parentTag.setRtbId("123456");
    parentTag.setCrsTagMappings(Set.of(new CrsTagMapping()));
    BrandProtectionCategory category = new BrandProtectionCategory();
    category.setPid(6L);
    brandProtectionTag.setCategory(category);
    brandProtectionTag.setName("test");
    brandProtectionTag.setPid(2L);
    brandProtectionTag.setRtbId("12345");
    brandProtectionTag.setParentTag(parentTag);
    List<CrsTagMapping> crsTagMappings = ImmutableList.of(new CrsTagMapping());
    brandProtectionTag.setCrsTagMappings(crsTagMappings);
    BrandProtectionDTO result = BrandProtectionDTOParentsOnlyMapper.MAPPER.map(brandProtectionTag);
    assertEquals(1L, result.getPid());
    assertEquals(6L, result.getCategoryId());
    assertEquals("testParent", result.getName());
    assertEquals("123456", result.getRtbId());
    Collection<CrsTagMappingDTO> expectedCrsList =
        crsTagMappings.stream().map(CrsTagDTOMapping.MAPPER::map).collect(Collectors.toList());
    assertEquals(expectedCrsList.size(), result.getCrsTags().size());
  }
}
