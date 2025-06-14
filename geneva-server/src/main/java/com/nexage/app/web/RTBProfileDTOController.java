package com.nexage.app.web;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.nexage.app.dto.RTBProfileDTO;
import com.nexage.app.services.RTBProfileDTOService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Set;
import javax.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "/v1/sellers/{sellerPid}/rtb-profiles")
@RestController
@RequestMapping(value = "/v1/sellers/{sellerPid}/rtb-profiles")
public class RTBProfileDTOController {

  private final RTBProfileDTOService rtbProfileDTOService;

  public RTBProfileDTOController(RTBProfileDTOService rtbProfileDTOService) {
    this.rtbProfileDTOService = rtbProfileDTOService;
  }

  /**
   * GET resource to retrieve paginated {@link RTBProfileDTO} based on request.
   *
   * @param pageable Pagination based on {@link Pageable}
   * @param sellerPid SellerId to grab RTBProfiles
   * @param qf Unique {@link Set} of fields. Query fields. Optional.
   * @param qt The term to be found. Query term. Optional.cd
   * @return {@link ResponseEntity} of type {@link Page} {@link RTBProfileDTO}
   */
  @Timed
  @ExceptionMetered
  @GetMapping
  public ResponseEntity<Page<RTBProfileDTO>> getAllRTBProfiles(
      Pageable pageable,
      @PathVariable("sellerPid") Long sellerPid,
      @RequestParam(value = "qf", required = false) Set<String> qf,
      @RequestParam(value = "qt", required = false) String qt) {
    return ResponseEntity.ok(rtbProfileDTOService.getRTBProfiles(pageable, sellerPid, qt, qf));
  }

  /**
   * PUT resource to update a {@link RTBProfileDTO} based on request
   *
   * @param sellerPid SellerId to update RTBProfile
   * @param rtbProfileDTO Values to be updated
   * @param rtbPid RTBProfile Id to update
   * @return {@link RTBProfileDTO}
   */
  @Timed
  @ExceptionMetered
  @PutMapping(path = "/{rtbPid}", consumes = MediaType.APPLICATION_JSON_VALUE)
  public RTBProfileDTO update(
      @PathVariable("sellerPid") Long sellerPid,
      @Valid @RequestBody RTBProfileDTO rtbProfileDTO,
      @PathVariable(value = "rtbPid") long rtbPid) {
    return rtbProfileDTOService.update(sellerPid, rtbProfileDTO, rtbPid);
  }
}
