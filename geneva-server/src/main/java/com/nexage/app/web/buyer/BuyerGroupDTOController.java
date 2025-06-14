package com.nexage.app.web.buyer;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.nexage.app.dto.buyer.BuyerGroupDTO;
import com.nexage.app.services.BuyerGroupDTOService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.Set;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping(
    value = {"/v1/dsps"},
    produces = MediaType.APPLICATION_JSON_VALUE)
public class BuyerGroupDTOController {

  private final BuyerGroupDTOService buyerGroupDTOService;

  public BuyerGroupDTOController(BuyerGroupDTOService buyerGroupDTOService) {
    this.buyerGroupDTOService = buyerGroupDTOService;
  }

  /**
   * GET resource to retrieve paginated {@link BuyerGroupDTO} based on request.
   *
   * @param dspPid Unique Pid.
   * @param qf Unique {@link Set} of fields. Query fields. Optional.
   * @param qt The term to be found. Query term. Optional.
   * @param pageable Pagination based on {@link Pageable}.
   * @return {@link ResponseEntity} of type {@link Page} {@link BuyerGroupDTO}.
   */
  @Operation(description = "Find all Buyer Groups")
  @Timed
  @ExceptionMetered
  @ResponseBody
  @GetMapping(
      value = {"/{dspPid}/buyer-groups", "/{dspPid}/buyerGroups"},
      produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<Page<BuyerGroupDTO>> findAll(
      @PathVariable(value = "dspPid") @NotNull Long dspPid,
      @RequestParam(value = "qf", required = false) Set<String> qf,
      @RequestParam(value = "qt", required = false) String qt,
      @PageableDefault(sort = "pid") Pageable pageable) {
    log.debug("qf={}, qt={}, pageable={}", qf, qt, pageable);
    return ResponseEntity.ok(buyerGroupDTOService.findAll(dspPid, qf, qt, pageable));
  }

  /**
   * Create {@link BuyerGroupDTO}.
   *
   * @param dspPid pid of the dsp the created buyer group will belong to
   * @param buyerGroupDTO create payload
   * @return {@link ResponseEntity} of type {@link BuyerGroupDTO}.
   */
  @Operation(description = "Create Buyer Group")
  @Timed
  @ExceptionMetered
  @PostMapping(
      value = "/{dspPid}/buyer-groups",
      consumes = APPLICATION_JSON_VALUE,
      produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<BuyerGroupDTO> create(
      @PathVariable(value = "dspPid") @NotNull Long dspPid,
      @RequestBody(required = false) @Valid BuyerGroupDTO buyerGroupDTO) {
    BuyerGroupDTO created = buyerGroupDTOService.create(dspPid, buyerGroupDTO);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  /**
   * Get {@link BuyerGroupDTO} by pid.
   *
   * @param buyerGroupPid Unique Pid.
   * @return {@link ResponseEntity} of type {@link BuyerGroupDTO}.
   */
  @Operation(description = "Get one Buyer Group")
  @Timed
  @ExceptionMetered
  @ResponseBody
  @GetMapping(
      value = {"/buyer-groups/{buyerGroupPid}"},
      produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<BuyerGroupDTO> findOne(
      @PathVariable(value = "buyerGroupPid") @NotNull Long buyerGroupPid) {
    return ResponseEntity.ok(buyerGroupDTOService.findOne(buyerGroupPid));
  }

  /**
   * Update {@link BuyerGroupDTO}.
   *
   * @param dspPid pid of the dsp the bidder config to update belongs to
   * @param buyerGroupPid pid of the bidder config to update
   * @param buyerGroupDTO update payload
   * @return {@link ResponseEntity} of type {@link BuyerGroupDTO}.
   */
  @Operation(summary = "Update buyer group")
  @ApiResponse(content = @Content(schema = @Schema(implementation = BuyerGroupDTO.class)))
  @Timed
  @ExceptionMetered
  @PutMapping(value = "/{dspPid}/buyer-groups/{buyerGroupPid}", consumes = APPLICATION_JSON_VALUE)
  public ResponseEntity<BuyerGroupDTO> updateBuyerGroup(
      @PathVariable(value = "dspPid") Long dspPid,
      @PathVariable(value = "buyerGroupPid") Long buyerGroupPid,
      @RequestBody @Valid BuyerGroupDTO buyerGroupDTO) {
    return ResponseEntity.ok(buyerGroupDTOService.update(dspPid, buyerGroupPid, buyerGroupDTO));
  }
}
