package com.nexage.app.web;

import static org.springframework.http.MediaType.ALL_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.nexage.app.dto.HbPartnerDTO;
import com.nexage.app.dto.HbPartnerRequestDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.services.HbPartnerService;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "/v1/hbpartners")
@RestController
@RequestMapping(value = "/v1/hbpartners")
public class HbPartnerController {

  private final HbPartnerService hbPartnerService;

  public HbPartnerController(HbPartnerService hbPartnerService) {
    this.hbPartnerService = hbPartnerService;
  }

  /**
   * GET resource to retrieve paginated {@link HbPartnerDTO} based on request.
   *
   * @param pageable Pagination based on {@link Pageable}.
   * @param sellerPid seller pid
   * @param sitePid site pid
   * @param detail
   * @return {@link ResponseEntity} of type {@link Page} {@link HbPartnerDTO}.
   */
  @Timed
  @ExceptionMetered
  @GetMapping(produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<Page<HbPartnerDTO>> getHbPartners(
      @PageableDefault(size = 1000) Pageable pageable,
      @RequestParam(value = "sellerId", required = false) Long sellerPid,
      @RequestParam(value = "siteId", required = false) Long sitePid,
      @RequestParam(value = "detail", required = false, defaultValue = "true") boolean detail) {
    return ResponseEntity.ok(
        hbPartnerService.getHbPartners(
            HbPartnerRequestDTO.of(pageable, sellerPid, sitePid, detail)));
  }

  /**
   * GET resource to retrieve {@link HbPartnerDTO} based on request.
   *
   * @param pid
   * @return {@link ResponseEntity} of type {@link HbPartnerDTO}.
   */
  @Timed
  @ExceptionMetered
  @GetMapping(value = "/{pid}", consumes = ALL_VALUE, produces = APPLICATION_JSON_VALUE)
  public ResponseEntity getHbPartner(@PathVariable(value = "pid") Long pid) {
    return ResponseEntity.ok(hbPartnerService.getHbPartner(pid));
  }

  /**
   * POST request to create {@link HbPartnerDTO}
   *
   * @param hbPartnerDTO
   * @return {@link ResponseEntity} of {@link HbPartnerDTO}.
   */
  @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
  public ResponseEntity createHbPartner(@RequestBody @Valid HbPartnerDTO hbPartnerDTO) {
    return ResponseEntity.ok(hbPartnerService.createHbPartner(hbPartnerDTO));
  }

  /**
   * PUT request to update {@link HbPartnerDTO}
   *
   * @param pid hbPartner pid
   * @param hbPartnerDTO
   * @return {@link ResponseEntity} of {@link HbPartnerDTO}.
   */
  @PutMapping(
      value = "/{pid}",
      consumes = APPLICATION_JSON_VALUE,
      produces = APPLICATION_JSON_VALUE)
  public ResponseEntity updateHbPartner(
      @PathVariable(value = "pid") Long pid, @RequestBody @Valid HbPartnerDTO hbPartnerDTO) {
    if (!pid.equals(hbPartnerDTO.getPid())) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_PIDS_MISMATCH);
    }

    return ResponseEntity.ok(hbPartnerService.updateHbPartner(hbPartnerDTO));
  }

  /**
   * DELETE request to update {@link HbPartnerDTO}
   *
   * @param pid pid of hbPartner
   */
  @DeleteMapping(value = "/{pid}", consumes = ALL_VALUE)
  public void deactivateHbPartner(@PathVariable(value = "pid") Long pid) {
    hbPartnerService.deactivateHbPartner(pid);
  }
}
