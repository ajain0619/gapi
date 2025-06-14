package com.nexage.app.mapper.site;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.nexage.admin.core.sparta.jpa.model.PlacementVideoCompanion;
import com.nexage.app.dto.seller.PlacementVideoCompanionDTO;
import com.nexage.app.web.support.TestObjectsFactory;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.jupiter.api.Test;

class PlacementVideoCompanionDTOMapperTest {

  @Test
  void shouldMapPlacementVideoCompanionDTOToPlacementVideoCompanion() {
    PlacementVideoCompanionDTO placementVideoCompanionDTO =
        TestObjectsFactory.createDefaultPlacementVideoCompanionDTO();
    Long placementVideoCompanionPid = RandomUtils.nextLong();
    placementVideoCompanionDTO.setPid(placementVideoCompanionPid);
    placementVideoCompanionDTO.setHeight(320);
    placementVideoCompanionDTO.setWidth(480);

    PlacementVideoCompanion placementVideoCompanion =
        PlacementVideoCompanionDTOMapper.MAPPER.map(placementVideoCompanionDTO);
    assertNotNull(placementVideoCompanion);
    assertEquals(placementVideoCompanionPid, placementVideoCompanion.getPid());
    assertEquals((Integer) 320, placementVideoCompanion.getHeight());
    assertEquals((Integer) 480, placementVideoCompanion.getWidth());
  }

  @Test
  void shouldMapPlacementVideoCompanionToPlacementVideoCompanionDTO() {
    PlacementVideoCompanion placementVideoCompanion = new PlacementVideoCompanion();
    Long placementVideoCompanionPid = RandomUtils.nextLong();
    placementVideoCompanion.setPid(placementVideoCompanionPid);
    placementVideoCompanion.setHeight(320);
    placementVideoCompanion.setWidth(480);

    PlacementVideoCompanionDTO placementVideoCompanionDTO =
        PlacementVideoCompanionDTOMapper.MAPPER.map(placementVideoCompanion);
    assertNotNull(placementVideoCompanionDTO);
    assertEquals(placementVideoCompanionPid, placementVideoCompanionDTO.getPid());
    assertEquals((Integer) 320, placementVideoCompanionDTO.getHeight());
    assertEquals((Integer) 480, placementVideoCompanionDTO.getWidth());
  }
}
