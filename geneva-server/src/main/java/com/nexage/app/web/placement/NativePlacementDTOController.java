package com.nexage.app.web.placement;

import static com.ssp.geneva.common.base.annotation.ExternalAPI.WARNING_EXTERNAL_API_NEEDS_TO_BE_CHECKED_WITH_PRODUCT_BEFORE_DEPLOYING;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.nexage.admin.core.validator.NativePlacementCreateGroup;
import com.nexage.admin.core.validator.NativePlacementUpdateGroup;
import com.nexage.app.dto.NativePlacementRequestParamsDTO;
import com.nexage.app.dto.seller.nativeads.NativePlacementDTO;
import com.nexage.app.services.BeanValidationService;
import com.nexage.app.services.NativePlacementDTOService;
import com.ssp.geneva.common.base.annotation.ExternalAPI;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.groups.Default;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "/v1/sellers/{sellerId}/sites/{siteId}/placements")
@ApiResponse(headers = @Header(name = NativePlacementDTOController.NATIVE_HEADER))
@RequestMapping(
    value = "/v1/sellers/{sellerId}",
    produces = NativePlacementDTOController.NATIVE_HEADER)
@Log4j2
public class NativePlacementDTOController {

  public static final String NATIVE_HEADER = "application/vnd.geneva-api.native+json";

  private final NativePlacementDTOService nativePlacementDTOService;
  private final BeanValidationService beanValidationService;

  public NativePlacementDTOController(
      NativePlacementDTOService nativePlacementDTOService,
      BeanValidationService beanValidationService) {
    this.nativePlacementDTOService = nativePlacementDTOService;
    this.beanValidationService = beanValidationService;
  }

  @Timed
  @ExceptionMetered
  @PostMapping(value = "/sites/{siteId}/placements", consumes = NATIVE_HEADER)
  @ExternalAPI(WARNING_EXTERNAL_API_NEEDS_TO_BE_CHECKED_WITH_PRODUCT_BEFORE_DEPLOYING)
  public ResponseEntity<NativePlacementDTO> createNativePlacement(
      @Parameter(name = "seller id") @PathVariable Long sellerId,
      @Parameter(name = "site id") @PathVariable Long siteId,
      @RequestBody NativePlacementDTO nativePlacementDTO) {
    NativePlacementRequestParamsDTO nativePlacementRequestParams =
        NativePlacementRequestParamsDTO.builder()
            .sellerId(sellerId)
            .siteId(siteId)
            .nativePlacement(nativePlacementDTO)
            .build();

    log.info("createPlacement for [{}] ", nativePlacementRequestParams);
    beanValidationService.validate(
        nativePlacementDTO, Default.class, NativePlacementCreateGroup.class);
    return ResponseEntity.ok(
        nativePlacementDTOService.createPlacement(nativePlacementRequestParams));
  }

  @Timed
  @ExceptionMetered
  @GetMapping(value = "/sites/{siteId}/placements/{placementId}")
  @ExternalAPI(WARNING_EXTERNAL_API_NEEDS_TO_BE_CHECKED_WITH_PRODUCT_BEFORE_DEPLOYING)
  public ResponseEntity<NativePlacementDTO> getNativePlacementById(
      @Parameter(name = "seller id") @PathVariable Long sellerId,
      @Parameter(name = "site id") @PathVariable Long siteId,
      @Parameter(name = "placement id") @PathVariable Long placementId) {
    return ResponseEntity.ok(
        nativePlacementDTOService.getNativePlacementById(sellerId, siteId, placementId));
  }

  @Timed
  @ExceptionMetered
  @PutMapping(value = "/sites/{siteId}/placements/{placementId}", consumes = NATIVE_HEADER)
  @ExternalAPI(WARNING_EXTERNAL_API_NEEDS_TO_BE_CHECKED_WITH_PRODUCT_BEFORE_DEPLOYING)
  public ResponseEntity<NativePlacementDTO> updateNativePlacement(
      @Parameter(name = "seller id") @PathVariable Long sellerId,
      @Parameter(name = "site id") @PathVariable Long siteId,
      @Parameter(name = "placement id") @PathVariable Long placementId,
      @RequestBody NativePlacementDTO nativePlacementDTO) {
    NativePlacementRequestParamsDTO nativePlacementRequestParams =
        NativePlacementRequestParamsDTO.builder()
            .sellerId(sellerId)
            .siteId(siteId)
            .placementId(placementId)
            .nativePlacement(nativePlacementDTO)
            .build();
    log.info("updateNativePlacement [{}] ", nativePlacementRequestParams);
    beanValidationService.validate(
        nativePlacementDTO, Default.class, NativePlacementUpdateGroup.class);
    return ResponseEntity.ok(
        nativePlacementDTOService.updatePlacement(nativePlacementRequestParams));
  }
}
