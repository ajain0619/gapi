package com.nexage.app.web;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.nexage.app.dto.DeviceTypeDTO;
import com.nexage.app.services.DeviceTypeService;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/v1/device-types", produces = MediaType.APPLICATION_JSON_VALUE)
public class DeviceTypeController {

  private final DeviceTypeService deviceTypeService;

  public DeviceTypeController(DeviceTypeService deviceTypeService) {
    this.deviceTypeService = deviceTypeService;
  }

  @Timed
  @ExceptionMetered
  @GetMapping
  public ResponseEntity<Page<DeviceTypeDTO>> getDeviceTypes(
      @Parameter(name = "Query term for search") @RequestParam(value = "qt", required = false)
          String qt,
      @Parameter(name = "Query field for search") @RequestParam(value = "qf", required = false)
          Set<String> qf,
      @PageableDefault(value = 10, sort = "id") Pageable pageable) {
    return ResponseEntity.ok(deviceTypeService.findAll(qt, qf, pageable));
  }
}
