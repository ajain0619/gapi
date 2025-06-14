package com.nexage.app.web;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nexage.admin.core.model.BrandProtectionCategory;
import com.nexage.admin.core.model.BrandProtectionTag;
import com.nexage.admin.core.model.BrandProtectionTagValues;
import com.nexage.admin.core.model.CrsTagMapping;
import com.nexage.app.services.BrandProtectionService;
import com.nexage.app.util.assemblers.BrandProtectionAssembler;
import com.nexage.app.web.support.BaseControllerItTest;
import com.nexage.app.web.support.TestObjectsFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class BrandProtectionControllerIT extends BaseControllerItTest {

  @Mock private BrandProtectionService brandProtectionService;

  @InjectMocks private BrandProtectionController brandProtectionController;

  @BeforeEach
  public void setUp() {
    this.mockMvc = MockMvcBuilders.standaloneSetup(brandProtectionController).build();
  }

  @Test
  void testGetBrandProtectionTag() throws Exception {
    BrandProtectionTag tag = TestObjectsFactory.createBrandProtectionTag();
    when(brandProtectionService.getBrandProtectionTag(anyLong()))
        .thenReturn(BrandProtectionAssembler.makeDtoFrom(tag));
    mockMvc
        .perform(get("/brandprotection/tag/123"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.pid", is(123)));
  }

  @Test
  void testGetBrandProtectionTagValues() throws Exception {
    BrandProtectionTagValues tagValues = TestObjectsFactory.createBrandProtectionTagValues();
    when(brandProtectionService.getBrandProtectionTagValues(anyLong()))
        .thenReturn(BrandProtectionAssembler.makeDtoFrom(tagValues));
    mockMvc
        .perform(get("/brandprotection/tag-values/234"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.pid", is(234)));
  }

  @Test
  void testGetBrandProtectionCategory() throws Exception {
    BrandProtectionCategory category = TestObjectsFactory.createBrandProtectionCategory();
    when(brandProtectionService.getBrandProtectionCategory(anyLong()))
        .thenReturn(BrandProtectionAssembler.makeDtoFrom(category));
    mockMvc
        .perform(get("/brandprotection/category/345"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.pid", is(345)));
  }

  @Test
  void testGetBrandProtectionCrsTagMappings() throws Exception {
    CrsTagMapping tagMapping = TestObjectsFactory.createCrsTagMapping();
    when(brandProtectionService.getCrsTagMapping(anyLong()))
        .thenReturn(BrandProtectionAssembler.makeDtoFrom(tagMapping));
    mockMvc
        .perform(get("/brandprotection/tag-mappings/456"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.pid", is(456)));
  }
}
