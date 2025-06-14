package com.nexage.app.web;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.nexage.admin.core.sparta.jpa.model.Region;
import com.nexage.app.services.RegionService;
import com.ssp.geneva.common.base.annotation.Legacy;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Legacy
@Tag(name = "/regions")
@RestController
@RequestMapping(value = "/regions")
public class RegionController {

  private final RegionService regionService;

  public RegionController(RegionService regionService) {
    this.regionService = regionService;
  }

  @Timed
  @ExceptionMetered
  @GetMapping
  public List<Region> readRegions() {
    return regionService.getAllRegions();
  }
}
