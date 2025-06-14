package com.nexage.admin.core.sparta.jpa.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.repository.PlacementVideoCompanionRepository;
import com.nexage.admin.core.repository.PlacementVideoRepository;
import javax.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRES_NEW)
@Sql(
    scripts = {"/data/repository/placement-video-repository.sql"},
    config = @SqlConfig(encoding = "utf-8"))
class PlacementVideoCompanionIT extends CoreDbSdkIntegrationTestBase {

  @Autowired private PlacementVideoRepository placementVideoRepository;
  @Autowired private PlacementVideoCompanionRepository placementVideoCompanionRepository;

  @Test
  void testWhenPlacementVideoIsNull() {
    PlacementVideoCompanion placementVideoCompanion = createDefaultPlacementVideoCompanion();
    placementVideoCompanion.setPlacementVideo(null);
    assertThrows(
        ConstraintViolationException.class,
        () -> placementVideoCompanionRepository.save(placementVideoCompanion));
  }

  @Test
  void testWhenPlacementVideoIsInvalid() {
    PlacementVideoCompanion placementVideoCompanion = createDefaultPlacementVideoCompanion();
    PlacementVideo placementVideo = placementVideoRepository.getOne(2L);
    placementVideoCompanion.setPlacementVideo(placementVideo);
    assertThrows(
        DataIntegrityViolationException.class,
        () -> placementVideoCompanionRepository.save(placementVideoCompanion));
  }

  @Test
  void testWhenPlacementVideoIsValid() {
    Long validPlacementVideoPid = 1L;
    PlacementVideoCompanion placementVideoCompanion = createDefaultPlacementVideoCompanion();
    PlacementVideoCompanion savedPlacementVideoCompanion =
        placementVideoCompanionRepository.save(placementVideoCompanion);
    assertEquals(validPlacementVideoPid, savedPlacementVideoCompanion.getPlacementVideo().getPid());
  }

  @Test
  void testWhenHeightIsNull() {
    PlacementVideoCompanion placementVideoCompanion = createDefaultPlacementVideoCompanion();
    placementVideoCompanion.setHeight(null);
    assertThrows(
        DataIntegrityViolationException.class,
        () -> placementVideoCompanionRepository.save(placementVideoCompanion));
  }

  @Test
  void testWhenWidthIsNull() {
    PlacementVideoCompanion placementVideoCompanion = createDefaultPlacementVideoCompanion();
    placementVideoCompanion.setWidth(null);
    assertThrows(
        DataIntegrityViolationException.class,
        () -> placementVideoCompanionRepository.save(placementVideoCompanion));
  }

  @Test
  void testWhenHeightAndWidthAreNegative() {
    Integer height = -1;
    Integer width = -1;
    PlacementVideoCompanion placementVideoCompanion = createDefaultPlacementVideoCompanion();
    placementVideoCompanion.setHeight(height);
    placementVideoCompanion.setWidth(width);
    assertThrows(
        ConstraintViolationException.class,
        () -> placementVideoCompanionRepository.save(placementVideoCompanion));
  }

  @Test
  void testWhenHeightAndWidthAreZero() {
    Integer height = 0;
    Integer width = 0;
    PlacementVideoCompanion placementVideoCompanion = createDefaultPlacementVideoCompanion();
    placementVideoCompanion.setHeight(height);
    placementVideoCompanion.setWidth(width);
    assertThrows(
        ConstraintViolationException.class,
        () -> placementVideoCompanionRepository.save(placementVideoCompanion));
  }

  @Test
  void testWhenHeightAndWidthAreAbove999() {
    Integer height = 1000;
    Integer width = 1000;
    PlacementVideoCompanion placementVideoCompanion = createDefaultPlacementVideoCompanion();
    placementVideoCompanion.setHeight(height);
    placementVideoCompanion.setWidth(width);
    assertThrows(
        ConstraintViolationException.class,
        () -> placementVideoCompanionRepository.save(placementVideoCompanion));
  }

  @Test
  void testWhenHeightAndWidthAreValid() {
    Integer height = 320;
    Integer width = 480;
    PlacementVideoCompanion placementVideoCompanion = createDefaultPlacementVideoCompanion();
    placementVideoCompanion.setHeight(height);
    placementVideoCompanion.setWidth(width);

    PlacementVideoCompanion savedPlacementVideoCompanion =
        placementVideoCompanionRepository.save(placementVideoCompanion);
    assertEquals(height, savedPlacementVideoCompanion.getHeight());
    assertEquals(width, savedPlacementVideoCompanion.getWidth());
  }

  private PlacementVideoCompanion createDefaultPlacementVideoCompanion() {
    PlacementVideo placementVideo = placementVideoRepository.getOne(1L);

    PlacementVideoCompanion placementVideoCompanion = new PlacementVideoCompanion();
    placementVideoCompanion.setPlacementVideo(placementVideo);
    placementVideoCompanion.setHeight(320);
    placementVideoCompanion.setWidth(480);

    return placementVideoCompanion;
  }
}
