package com.nexage.app.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import com.nexage.admin.core.model.GeoSegment;
import com.nexage.admin.core.repository.GeoSegmentRepository;
import com.nexage.app.web.support.TestObjectsFactory;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class GeoSegmentServiceImplTest {
  @Mock private GeoSegmentRepository geoSegmentRepository;
  @InjectMocks private GeoSegmentServiceImpl geoSegmentService;

  @Test
  void getAllGeoSegments_noFilterType() {
    String query = "query name";
    long filterType = -1;
    Page<GeoSegment> page = new PageImpl<>(TestObjectsFactory.createGeoSegments(1));
    given(geoSegmentRepository.findByNameContainingIgnoreCase(eq(query), any(Pageable.class)))
        .willReturn(page);

    List<GeoSegment> result =
        geoSegmentService.getAllGeoSegments(query, filterType, 1, 1, "name", "asc");

    assertEquals(page.getContent(), result);
  }

  @Test
  void getAllGeoSegments_filterType() {
    String query = "query name";
    long filterType = 99;
    Page<GeoSegment> page = new PageImpl<>(TestObjectsFactory.createGeoSegments(1));
    given(
            geoSegmentRepository.findByNameContainingIgnoreCaseAndType(
                eq(query), eq(filterType), any(Pageable.class)))
        .willReturn(page);

    List<GeoSegment> result =
        geoSegmentService.getAllGeoSegments(query, filterType, 1, 1, "name", "asc");

    assertEquals(page.getContent(), result);
  }
}
