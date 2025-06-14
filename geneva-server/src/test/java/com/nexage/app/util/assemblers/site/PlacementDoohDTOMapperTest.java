package com.nexage.app.util.assemblers.site;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.nexage.admin.core.model.PlacementDooh;
import com.nexage.admin.core.model.Position;
import com.nexage.app.dto.seller.PlacementDoohDTO;
import com.nexage.app.mapper.PlacementDoohDTOMapper;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class PlacementDoohDTOMapperTest {

  @Test
  void shouldMapDefaultsToPlacementDoohWhenNull() {
    PlacementDooh placementDooh = PlacementDoohDTOMapper.MAPPER.map(new PlacementDoohDTO());
    assertNotNull(placementDooh);
    assertEquals(BigDecimal.ONE, placementDooh.getDefaultImpressionMultiplier());
  }

  @Test
  void shouldMapPlacementDoohDTOToPlacementDooh() {
    PlacementDoohDTO placementDoohDTO = new PlacementDoohDTO();
    placementDoohDTO.setVersion(0);
    placementDoohDTO.setDefaultImpressionMultiplier(BigDecimal.TEN);
    PlacementDooh placementDooh = PlacementDoohDTOMapper.MAPPER.map(placementDoohDTO);
    assertNotNull(placementDooh);
    assertEquals(BigDecimal.TEN, placementDooh.getDefaultImpressionMultiplier());
    assertEquals(0, placementDooh.getVersion().intValue());
  }

  @Test
  void shouldMapPlacementDoohToPlacementDoohDTO() {
    PlacementDooh placementDooh = new PlacementDooh();
    placementDooh.setDefaultImpressionMultiplier(BigDecimal.TEN);
    placementDooh.setPosition(new Position());
    placementDooh.setVersion(0);
    placementDooh.setPid(1L);

    PlacementDoohDTO placementDoohDTO = PlacementDoohDTOMapper.MAPPER.map(placementDooh);
    assertNotNull(placementDoohDTO);
    assertEquals(BigDecimal.TEN, placementDooh.getDefaultImpressionMultiplier());
    assertNotNull(placementDooh.getPosition());
    assertEquals(0, placementDooh.getVersion().intValue());
    assertEquals(1L, placementDooh.getPid().longValue());
  }

  @Test
  void shouldMapDefaultsToPlacementDoohDTOWhenNull() {
    PlacementDoohDTO placementDoohDTO = PlacementDoohDTOMapper.MAPPER.map(new PlacementDooh());
    assertNotNull(placementDoohDTO);
    assertEquals(BigDecimal.ONE, placementDoohDTO.getDefaultImpressionMultiplier());
  }
}
