package com.nexage.app.mapper.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nexage.admin.core.model.AppBundleData;
import com.nexage.admin.core.model.filter.FiilterListStatus;
import com.nexage.admin.core.model.filter.FilterListAppBundle;
import com.nexage.app.dto.filter.FilterListAppBundleDTO;
import com.nexage.app.dto.filter.MediaStatusDTO;
import com.nexage.app.mapper.FilterListAppBundleMapper;
import org.junit.jupiter.api.Test;

class FilterListAppBundleMapperTest {

  @Test
  void shouldMapToDTO() {
    FilterListAppBundle filterListAppBundle = new FilterListAppBundle();
    AppBundleData appBundle = new AppBundleData();
    appBundle.setAppBundleId("com.android.app");
    filterListAppBundle.setPid(1);
    filterListAppBundle.setApp(appBundle);
    filterListAppBundle.setStatus(FiilterListStatus.INVALID);

    FilterListAppBundleDTO filterListAppBundleDTO =
        FilterListAppBundleMapper.MAPPER.map(filterListAppBundle);
    assertEquals(filterListAppBundle.getPid(), filterListAppBundleDTO.getPid());
    assertEquals(filterListAppBundle.getApp().getAppBundleId(), filterListAppBundleDTO.getApp());
    assertEquals(
        filterListAppBundle.getStatus().toString(), filterListAppBundleDTO.getStatus().toString());
  }

  @Test
  void shouldMapToEntity() {
    FilterListAppBundleDTO filterListAppBundleDTO = new FilterListAppBundleDTO();
    filterListAppBundleDTO.setApp("123456");
    filterListAppBundleDTO.setPid(2);
    filterListAppBundleDTO.setStatus(MediaStatusDTO.VALID);

    FilterListAppBundle filterListAppBundle =
        FilterListAppBundleMapper.MAPPER.map(filterListAppBundleDTO);
    assertEquals(filterListAppBundleDTO.getPid(), filterListAppBundle.getPid());
    assertEquals(filterListAppBundleDTO.getApp(), filterListAppBundle.getApp().getAppBundleId());
    assertEquals(
        filterListAppBundleDTO.getStatus().toString(), filterListAppBundle.getStatus().toString());
  }
}
