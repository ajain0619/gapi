package com.nexage.app.web;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.nexage.admin.core.validator.CreateGroup;
import com.nexage.app.dto.sellingrule.SellerSeatRuleDTO;
import com.nexage.app.services.SellerSeatRuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Objects;
import java.util.Set;
import javax.validation.Valid;
import javax.validation.groups.Default;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@Tag(name = "/v1/seller-seats/{sellerSeatPid}/rules")
@RestController
@RequestMapping(
    value = "/v1/seller-seats/{sellerSeatPid}/rules",
    produces = MediaType.APPLICATION_JSON_VALUE)
public class SellerSeatRuleController {

  private final SellerSeatRuleService sellerSeatRuleService;

  public SellerSeatRuleController(SellerSeatRuleService sellerSeatRuleService) {
    this.sellerSeatRuleService = sellerSeatRuleService;
  }

  @Operation(summary = "Find seller seat rules")
  @ApiResponse(content = @Content(schema = @Schema(implementation = SellerSeatRuleDTO.class)))
  @Timed
  @ExceptionMetered
  @GetMapping
  public ResponseEntity<Page<SellerSeatRuleDTO>> findSellerSeatRules(
      @PathVariable Long sellerSeatPid,
      @RequestParam(value = "type", required = false) String ruleTypes,
      @RequestParam(value = "status", required = false) String statuses,
      @RequestParam(required = false, name = "qf") Set<String> queryFields,
      @RequestParam(required = false, name = "qt") String queryTerm,
      @PageableDefault(sort = "name") Pageable pageable) {
    return ResponseEntity.ok(
        sellerSeatRuleService.findRulesInSellerSeat(
            sellerSeatPid, ruleTypes, statuses, queryFields, queryTerm, pageable));
  }

  @Operation(summary = "Get a single seller seat rule by its pid")
  @ApiResponse(content = @Content(schema = @Schema(implementation = SellerSeatRuleDTO.class)))
  @Timed
  @ExceptionMetered
  @GetMapping("/{sellerSeatRulePid}")
  public ResponseEntity<SellerSeatRuleDTO> findByPid(
      @PathVariable Long sellerSeatPid, @PathVariable Long sellerSeatRulePid) {
    SellerSeatRuleDTO sellerSeatRuleDTO =
        sellerSeatRuleService.findSellerSeatRule(sellerSeatPid, sellerSeatRulePid);
    if (Objects.isNull(sellerSeatRuleDTO)) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(sellerSeatRuleDTO);
  }

  @Operation(summary = "Create a new seller seat rule")
  @ApiResponse(content = @Content(schema = @Schema(implementation = SellerSeatRuleDTO.class)))
  @Timed
  @ExceptionMetered
  @PostMapping
  public ResponseEntity<SellerSeatRuleDTO> createSellerSeatRule(
      @PathVariable Long sellerSeatPid,
      @RequestBody @Validated({Default.class, CreateGroup.class}) SellerSeatRuleDTO dto) {
    return new ResponseEntity<>(sellerSeatRuleService.save(sellerSeatPid, dto), HttpStatus.CREATED);
  }

  @Operation(summary = "Update a seller seat rule")
  @ApiResponse(content = @Content(schema = @Schema(implementation = SellerSeatRuleDTO.class)))
  @Timed
  @ExceptionMetered
  @PutMapping("/{sellerSeatRulePid}")
  public ResponseEntity<SellerSeatRuleDTO> updateSellerSeatRule(
      @PathVariable Long sellerSeatPid,
      @PathVariable Long sellerSeatRulePid,
      @RequestBody @Valid SellerSeatRuleDTO dto) {
    return ResponseEntity.ok(sellerSeatRuleService.update(sellerSeatPid, sellerSeatRulePid, dto));
  }

  @Operation(summary = "Delete a seller seat rule")
  @ApiResponse(content = @Content(schema = @Schema(implementation = SellerSeatRuleDTO.class)))
  @Timed
  @ExceptionMetered
  @DeleteMapping("/{sellerSeatRulePid}")
  public ResponseEntity<SellerSeatRuleDTO> deleteSellerSeatRule(
      @PathVariable Long sellerSeatPid, @PathVariable Long sellerSeatRulePid) {
    return ResponseEntity.ok(sellerSeatRuleService.delete(sellerSeatPid, sellerSeatRulePid));
  }
}
