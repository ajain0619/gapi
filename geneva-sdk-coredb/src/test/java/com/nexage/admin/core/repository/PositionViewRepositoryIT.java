package com.nexage.admin.core.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.enums.AdSizeType;
import com.nexage.admin.core.enums.Status;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRES_NEW)
@Sql(
    scripts = {"/data/repository/position-repository.sql"},
    config = @SqlConfig(encoding = "utf-8"))
class PositionViewRepositoryIT extends CoreDbSdkIntegrationTestBase {

  private static final Long SITE_PID = 1L;
  private static final Long DELETED_SITE_PID = 3L;

  @Autowired private PositionViewRepository positionViewRepository;

  @Test
  void shouldFindAllPlacementViews() {
    Pageable pageable = PageRequest.of(0, 5);
    var out = positionViewRepository.findAllPlacements(SITE_PID, pageable);

    assertEquals(2, out.getTotalElements());
    var position = out.iterator().next();
    assertEquals(1, position.getPid());
    assertEquals("footer", position.getName());
    assertEquals("alias test1", position.getPositionAliasName());
    assertEquals(AdSizeType.DYNAMIC, position.getAdSizeType());
    assertEquals(Status.ACTIVE, position.getStatus());
  }

  @Test
  void shouldFindPlacementViewByName() {
    Pageable pageable = PageRequest.of(0, 5);
    var out = positionViewRepository.searchPlacementsByName(SITE_PID, "foot", pageable);
    assertEquals(1, out.getTotalElements());
    var position = out.iterator().next();
    assertEquals(1, position.getPid());
    assertEquals("footer", position.getName());
    assertEquals("alias test1", position.getPositionAliasName());
    assertEquals(AdSizeType.DYNAMIC, position.getAdSizeType());
    assertEquals(Status.ACTIVE, position.getStatus());
  }

  @Test
  void shouldDeletePositionAndResolveCorrectStatus() {
    Pageable pageable = PageRequest.of(0, 5);
    var out = positionViewRepository.searchPlacementsByName(DELETED_SITE_PID, "deleted", pageable);
    assertEquals(1, out.getTotalElements());
    var position = out.iterator().next();
    assertEquals(5, position.getPid());
    assertEquals("deleted", position.getName());
    assertEquals("alias test5", position.getPositionAliasName());
    assertEquals(AdSizeType.DYNAMIC, position.getAdSizeType());
    assertEquals(Status.DELETED, position.getStatus());
  }
}
