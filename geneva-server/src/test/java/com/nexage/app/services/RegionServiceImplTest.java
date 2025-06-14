package com.nexage.app.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.repository.RegionRepository;
import com.nexage.admin.core.sparta.jpa.model.Region;
import com.nexage.app.services.impl.RegionServiceImpl;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RegionServiceImplTest {

  @Mock private RegionRepository regionRepository;
  @InjectMocks private RegionServiceImpl regionService;

  @Test
  void shouldGetAllRegions() {
    // given
    List<Region> regions =
        LongStream.range(0, 3).mapToObj(this::createRegion).collect(Collectors.toList());
    when(regionRepository.findAll()).thenReturn(regions);

    // when
    List<Region> returnedRegions = regionService.getAllRegions();

    // then
    assertEquals(regions, returnedRegions);
  }

  private Region createRegion(Long pid) {
    var region = new Region();
    region.setPid(pid);
    return region;
  }
}
