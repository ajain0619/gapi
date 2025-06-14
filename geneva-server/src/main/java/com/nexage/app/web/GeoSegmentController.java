package com.nexage.app.web;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.nexage.admin.core.model.GeoSegment;
import com.nexage.app.services.GeoSegmentService;
import com.ssp.geneva.common.base.annotation.Legacy;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Legacy
@Tag(name = "/geosegmentsinfo")
@RestController
@RequestMapping(value = "/geosegmentsinfo")
public class GeoSegmentController {

  private final GeoSegmentService geoSegmentService;

  public GeoSegmentController(GeoSegmentService geoSegmentService) {
    this.geoSegmentService = geoSegmentService;
  }

  @Timed
  @ExceptionMetered
  @GetMapping
  public List<GeoSegment> getGeoSegments(
      @RequestParam(value = "query") String query,
      @RequestParam(value = "filterType") Long filterType,
      @RequestParam(value = "limit") Integer limit,
      @RequestParam(value = "page") Integer page,
      @RequestParam(value = "sort") String sort,
      @RequestParam(value = "dir") String dir) {
    return geoSegmentService.getAllGeoSegments(query, filterType, limit, page, sort, dir);
  }
}
