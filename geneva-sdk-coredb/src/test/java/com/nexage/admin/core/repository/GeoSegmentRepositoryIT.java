package com.nexage.admin.core.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.model.GeoSegment;
import java.util.List;
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
    scripts = "/data/repository/geo-segment-repository.sql",
    config = @SqlConfig(encoding = "utf-8"))
class GeoSegmentRepositoryIT extends CoreDbSdkIntegrationTestBase {

  @Autowired protected GeoSegmentRepository geoSegmentRepository;

  @Test
  void queryTest() {
    Page<GeoSegment> geoSegmentList;
    Pageable pageRequest = PageRequest.of(0, 10, ASC, "name");
    geoSegmentList =
        geoSegmentRepository.findByNameContainingIgnoreCaseAndType("usa", 0L, pageRequest);
    assertEquals(1, geoSegmentList.getNumberOfElements());

    geoSegmentList =
        geoSegmentRepository.findByNameContainingIgnoreCaseAndType("a", 0L, pageRequest);
    assertEquals(2, geoSegmentList.getNumberOfElements());

    geoSegmentList =
        geoSegmentRepository.findByNameContainingIgnoreCaseAndType("A", 0L, pageRequest);
    assertEquals(2, geoSegmentList.getNumberOfElements());
  }

  @Test
  void filterTypeTest() {
    Page<GeoSegment> geoSegmentList;
    Pageable pageRequest = PageRequest.of(0, 10, ASC, "name");

    geoSegmentList =
        geoSegmentRepository.findByNameContainingIgnoreCaseAndType("", 1L, pageRequest);
    assertEquals(1, geoSegmentList.getNumberOfElements());

    geoSegmentList = geoSegmentRepository.findByNameContainingIgnoreCase("", pageRequest);
    assertEquals(4, geoSegmentList.getNumberOfElements());
  }

  @Test
  void limitTest() {
    Page<GeoSegment> geoSegmentList;
    Pageable pageRequestSingleElement = PageRequest.of(0, 1, ASC, "name");
    Pageable pageRequestTwoElements = PageRequest.of(0, 2, ASC, "name");

    geoSegmentList =
        geoSegmentRepository.findByNameContainingIgnoreCaseAndType(
            "", 0L, pageRequestSingleElement);
    assertEquals(1, geoSegmentList.getNumberOfElements());

    geoSegmentList =
        geoSegmentRepository.findByNameContainingIgnoreCaseAndType("", 0L, pageRequestTwoElements);
    assertEquals(2, geoSegmentList.getNumberOfElements());
  }

  @Test
  void pageTest() {
    Page<GeoSegment> geoSegmentList;
    Pageable pageRequest1 = PageRequest.of(1, 1, ASC, "name");
    Pageable pageRequest2 = PageRequest.of(2, 1, ASC, "name");
    Pageable pageRequest3 = PageRequest.of(1, 10, ASC, "name");

    geoSegmentList =
        geoSegmentRepository.findByNameContainingIgnoreCaseAndType("", 0L, pageRequest1);
    assertEquals(1, geoSegmentList.getNumberOfElements());

    geoSegmentList =
        geoSegmentRepository.findByNameContainingIgnoreCaseAndType("", 0L, pageRequest2);
    assertEquals(1, geoSegmentList.getNumberOfElements());

    geoSegmentList =
        geoSegmentRepository.findByNameContainingIgnoreCaseAndType("", 0L, pageRequest3);
    assertEquals(0, geoSegmentList.getNumberOfElements());
  }

  @Test
  void sortDirectionTest() {
    List<GeoSegment> geoSegmentList;
    Pageable pageRequestAsc = PageRequest.of(0, 10, ASC, "name");
    Pageable pageRequestDesc = PageRequest.of(0, 10, DESC, "name");

    geoSegmentList =
        geoSegmentRepository
            .findByNameContainingIgnoreCaseAndType("", 0L, pageRequestAsc)
            .getContent();
    assertEquals(3, geoSegmentList.size());
    assertEquals("Canada", geoSegmentList.get(0).getName());
    assertEquals("Mexico", geoSegmentList.get(1).getName());
    assertEquals("USA", geoSegmentList.get(2).getName());

    geoSegmentList =
        geoSegmentRepository
            .findByNameContainingIgnoreCaseAndType("", 0L, pageRequestDesc)
            .getContent();
    assertEquals(3, geoSegmentList.size());
    assertEquals("Canada", geoSegmentList.get(2).getName());
    assertEquals("Mexico", geoSegmentList.get(1).getName());
    assertEquals("USA", geoSegmentList.get(0).getName());

    geoSegmentList =
        geoSegmentRepository.findByNameContainingIgnoreCase("", pageRequestDesc).getContent();
    assertEquals(4, geoSegmentList.size());
    assertEquals("Virginia", geoSegmentList.get(0).getName());
    assertEquals("USA", geoSegmentList.get(1).getName());
    assertEquals("Mexico", geoSegmentList.get(2).getName());
    assertEquals("Canada", geoSegmentList.get(3).getName());
  }

  @Test
  void existsTest() {
    assertTrue(geoSegmentRepository.existsCountryByWoeIdAndName(11L, "USA"));
    assertFalse(geoSegmentRepository.existsCountryByWoeIdAndName(22L, "USA"));
  }
}
