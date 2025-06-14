package com.nexage.app.web;

import static com.ssp.geneva.common.base.annotation.ExternalAPI.WARNING_EXTERNAL_API_NEEDS_TO_BE_CHECKED_WITH_PRODUCT_BEFORE_DEPLOYING;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.nexage.app.services.DoohScreenService;
import com.ssp.geneva.common.base.annotation.ExternalAPI;
import com.ssp.geneva.server.screenmanagement.dto.DoohScreenDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import javax.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Tag(name = "/v1/sellers/{sellerPid}/screens")
@RequestMapping(
    value = "/v1/sellers/{sellerPid}/screens",
    produces = MediaType.APPLICATION_JSON_VALUE)
public class DoohScreenDTOController {

  private final DoohScreenService doohScreenService;

  public DoohScreenDTOController(DoohScreenService doohScreenService) {
    this.doohScreenService = doohScreenService;
  }

  /**
   * POST resource for creating/replacing {@link DoohScreenDTO} for Seller
   *
   * @param sellerPid Seller Pid of seller
   * @param screens {@link MultipartFile} containing JSON {@link List <DoohScreenDTO}
   * @return ResponseEntity
   */
  @Operation(summary = "Creates or Replaces DoohScreenDTOs for given Seller Pid")
  @Timed
  @ExceptionMetered
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @ExternalAPI(WARNING_EXTERNAL_API_NEEDS_TO_BE_CHECKED_WITH_PRODUCT_BEFORE_DEPLOYING)
  public ResponseEntity<Integer> replaceDoohScreens(
      @PathVariable(value = "sellerPid") long sellerPid,
      @RequestParam("screens") @NotNull MultipartFile screens) {
    doohScreenService.replaceDoohScreens(sellerPid, screens);
    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @Operation(summary = "Get DoohScreenDTOs for a give Seller Pid")
  @ApiResponse(content = @Content(schema = @Schema(implementation = DoohScreenDTO.class)))
  @Timed
  @ExceptionMetered
  @GetMapping
  @ExternalAPI(WARNING_EXTERNAL_API_NEEDS_TO_BE_CHECKED_WITH_PRODUCT_BEFORE_DEPLOYING)
  public ResponseEntity<Page<DoohScreenDTO>> getDoohScreens(
      @PageableDefault(value = 1000, sort = "sellerScreenId") Pageable pageable,
      @PathVariable(value = "sellerPid") long sellerPid) {
    return ResponseEntity.ok(doohScreenService.getDoohScreens(pageable, sellerPid));
  }
}
