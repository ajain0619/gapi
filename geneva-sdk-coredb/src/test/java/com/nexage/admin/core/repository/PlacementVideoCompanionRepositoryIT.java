package com.nexage.admin.core.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.sparta.jpa.model.PlacementVideoCompanion;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRES_NEW)
@Sql(
    scripts = {"/data/repository/placement-video-companion-repository.sql"},
    config = @SqlConfig(encoding = "utf-8"))
class PlacementVideoCompanionRepositoryIT extends CoreDbSdkIntegrationTestBase {
  @Autowired PlacementVideoCompanionRepository placementVideoCompanionRepository;
  private Pageable pageable = PageRequest.of(1, 10);

  @Test
  void testGetPositionVideoCompanionByPositionVideoPid() {
    Page<PlacementVideoCompanion> placementVideoCompanions =
        placementVideoCompanionRepository.findByPlacementVideoPid(null, pageable);
    assertEquals(0, placementVideoCompanions.getTotalElements());

    placementVideoCompanions.get().findFirst().orElse(null);
    // invalid position video pid
    placementVideoCompanions =
        placementVideoCompanionRepository.findByPlacementVideoPid(10L, pageable);
    assertEquals(0, placementVideoCompanions.getTotalElements());

    // valid position pid with no companion
    placementVideoCompanions =
        placementVideoCompanionRepository.findByPlacementVideoPid(1L, pageable);
    assertEquals(0, placementVideoCompanions.getTotalElements());

    // valid position pid with single companion
    placementVideoCompanions =
        placementVideoCompanionRepository.findByPlacementVideoPid(3L, pageable);
    assertEquals(1, placementVideoCompanions.getTotalElements());

    // valid position pid with multiple companion
    placementVideoCompanions =
        placementVideoCompanionRepository.findByPlacementVideoPid(4L, pageable);
    assertEquals(2, placementVideoCompanions.getTotalElements());
  }

  @Test
  void shouldTestDelete() {
    // No companion in db
    Long placementVideoPid = 1L;
    Page<PlacementVideoCompanion> placementVideoCompanions =
        placementVideoCompanionRepository.findByPlacementVideoPid(placementVideoPid, pageable);
    assertEquals(0, placementVideoCompanions.getTotalElements());

    Set<Long> placementVideoDTOCompanionPids = new HashSet<>();
    placementVideoDTOCompanionPids.add(1L);
    placementVideoDTOCompanionPids.add(2L);

    placementVideoCompanionRepository.delete(placementVideoPid, placementVideoDTOCompanionPids);
    placementVideoCompanions =
        placementVideoCompanionRepository.findByPlacementVideoPid(placementVideoPid, pageable);
    assertEquals(0, placementVideoCompanions.getTotalElements());

    // 1 companion in db and adding another one
    placementVideoPid = 3L;
    placementVideoCompanions =
        placementVideoCompanionRepository.findByPlacementVideoPid(placementVideoPid, pageable);
    assertEquals(1, placementVideoCompanions.getTotalElements());

    placementVideoDTOCompanionPids.clear();
    placementVideoDTOCompanionPids.add(10L);
    placementVideoDTOCompanionPids.add(20L);

    placementVideoCompanionRepository.delete(placementVideoPid, placementVideoDTOCompanionPids);
    placementVideoCompanions =
        placementVideoCompanionRepository.findByPlacementVideoPid(placementVideoPid, pageable);
    assertEquals(1, placementVideoCompanions.getTotalElements());

    // 2 companions in db and deleting one
    placementVideoPid = 4L;
    placementVideoCompanions =
        placementVideoCompanionRepository.findByPlacementVideoPid(placementVideoPid, pageable);
    assertEquals(2, placementVideoCompanions.getTotalElements());

    placementVideoDTOCompanionPids.clear();
    placementVideoDTOCompanionPids.add(11L);
    placementVideoDTOCompanionPids.add(20L);

    placementVideoCompanionRepository.delete(placementVideoPid, placementVideoDTOCompanionPids);
    placementVideoCompanions =
        placementVideoCompanionRepository.findByPlacementVideoPid(placementVideoPid, pageable);
    assertEquals(1, placementVideoCompanions.getTotalElements());

    // 1 companions in db and deleting all
    placementVideoPid = 4L;
    placementVideoCompanions =
        placementVideoCompanionRepository.findByPlacementVideoPid(placementVideoPid, pageable);
    assertEquals(1, placementVideoCompanions.getTotalElements());

    placementVideoDTOCompanionPids.clear();
    placementVideoDTOCompanionPids.add(20L);
    placementVideoDTOCompanionPids.add(21L);

    placementVideoCompanionRepository.delete(placementVideoPid, placementVideoDTOCompanionPids);
    placementVideoCompanions =
        placementVideoCompanionRepository.findByPlacementVideoPid(placementVideoPid, pageable);
    assertEquals(0, placementVideoCompanions.getTotalElements());
  }

  @Test
  void shouldTestDeleteByPlacementVideoPid() {
    // No companion in db
    Long placementVideoPid = 1L;
    Page<PlacementVideoCompanion> placementVideoCompanions =
        placementVideoCompanionRepository.findByPlacementVideoPid(placementVideoPid, pageable);
    assertEquals(0, placementVideoCompanions.getTotalElements());

    placementVideoCompanionRepository.delete(placementVideoPid);
    placementVideoCompanions =
        placementVideoCompanionRepository.findByPlacementVideoPid(placementVideoPid, pageable);
    assertEquals(0, placementVideoCompanions.getTotalElements());

    // One companion in db
    placementVideoPid = 3L;
    placementVideoCompanions =
        placementVideoCompanionRepository.findByPlacementVideoPid(placementVideoPid, pageable);
    assertEquals(1, placementVideoCompanions.getTotalElements());

    placementVideoCompanionRepository.delete(placementVideoPid);
    placementVideoCompanions =
        placementVideoCompanionRepository.findByPlacementVideoPid(placementVideoPid, pageable);
    assertEquals(0, placementVideoCompanions.getTotalElements());

    // Two companions in db
    placementVideoPid = 4L;
    placementVideoCompanions =
        placementVideoCompanionRepository.findByPlacementVideoPid(placementVideoPid, pageable);
    assertEquals(2, placementVideoCompanions.getTotalElements());

    placementVideoCompanionRepository.delete(placementVideoPid);
    placementVideoCompanions =
        placementVideoCompanionRepository.findByPlacementVideoPid(placementVideoPid, pageable);
    assertEquals(0, placementVideoCompanions.getTotalElements());
  }
}
