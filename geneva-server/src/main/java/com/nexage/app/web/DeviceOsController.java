package com.nexage.app.web;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.nexage.app.dto.DeviceOsDTO;
import com.nexage.app.services.DeviceOsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "/v1/device-os", description = "Get all device os")
@RequestMapping(value = "/v1/device-os", produces = MediaType.APPLICATION_JSON_VALUE)
public class DeviceOsController {

  private final DeviceOsService deviceOsService;

  public DeviceOsController(DeviceOsService deviceOsService) {
    this.deviceOsService = deviceOsService;
  }

  @Timed
  @ExceptionMetered
  @GetMapping
  @Operation(
      summary = "Get all device os",
      description = "Optional filtering arguments(name) can be passed")
  @ApiResponse(content = @Content(schema = @Schema(implementation = DeviceOsDTO.class)))
  public ResponseEntity<Page<DeviceOsDTO>> getDeviceOs(
      @Parameter(name = "Query term for search") @RequestParam(value = "qt", required = false)
          String qt,
      @Parameter(name = "Query field for search") @RequestParam(value = "qf", required = false)
          Set<String> qf,
      @PageableDefault(value = 10) Pageable pageable) {
    return ResponseEntity.ok(deviceOsService.findAllByName(qt, qf, pageable));
  }
}
