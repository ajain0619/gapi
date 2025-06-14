package com.nexage.app.web;

import static com.ssp.geneva.common.base.annotation.ExternalAPI.WARNING_EXTERNAL_API_NEEDS_TO_BE_CHECKED_WITH_PRODUCT_BEFORE_DEPLOYING;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.nexage.admin.core.validator.CreateGroup;
import com.nexage.admin.core.validator.UpdateGroup;
import com.nexage.app.dto.sellingrule.SellerRuleDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.services.BeanValidationService;
import com.nexage.app.services.PublisherRuleService;
import com.nexage.app.services.sellingrule.SellerRuleService;
import com.nexage.app.services.sellingrule.impl.SellerRuleQueryField;
import com.nexage.app.services.sellingrule.impl.SellerRuleQueryFieldParameter;
import com.nexage.app.util.validator.rule.queryfield.SellerRuleQueryParams;
import com.ssp.geneva.common.base.annotation.ExternalAPI;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.model.search.annotation.MultiValueSearchParams;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Objects;
import java.util.Set;
import javax.validation.groups.Default;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
@Tag(name = "/v1/sellers/{sellerPid}/rules")
@RestController
@RequestMapping(
    value = "/v1/sellers/{sellerPid}/rules",
    produces = MediaType.APPLICATION_JSON_VALUE)
public class SellerRuleDTOController {

  private final PublisherRuleService publisherRuleService;
  private final SellerRuleService sellerRuleService;
  private final BeanValidationService beanValidationService;

  public SellerRuleDTOController(
      PublisherRuleService publisherRuleService,
      SellerRuleService sellerRuleService,
      BeanValidationService beanValidationService) {
    this.publisherRuleService = publisherRuleService;
    this.sellerRuleService = sellerRuleService;
    this.beanValidationService = beanValidationService;
  }

  /**
   * GET resource to retrieve paginated {@link SellerRuleDTO} based on request.
   *
   * @param sellerPid Seller id. Required.
   * @param type Rule types. Required.
   * @param pageable Pagination based on {@link Pageable}
   * @param qf Unique {@link Set} of fields. Query fields. Optional.
   * @param qt The term to be found. Query term. Optional.
   * @return {@link ResponseEntity} of type {@link Page} {@link SellerRuleDTO}
   */
  @Operation(summary = "Get seller rules")
  @ApiResponse(content = @Content(schema = @Schema(implementation = SellerRuleDTO.class)))
  @Timed
  @ExceptionMetered
  @GetMapping(params = {"type"})
  @ExternalAPI(WARNING_EXTERNAL_API_NEEDS_TO_BE_CHECKED_WITH_PRODUCT_BEFORE_DEPLOYING)
  public ResponseEntity<Page<SellerRuleDTO>> getRules(
      @PathVariable Long sellerPid,
      @RequestParam(value = "type", required = false) String type,
      @RequestParam(value = "status", required = false) String status,
      @PageableDefault(sort = "name", direction = Sort.Direction.ASC) Pageable pageable,
      @RequestParam(value = "qf", required = false) Set<String> qf,
      @RequestParam(value = "qt", required = false) String qt) {
    return ResponseEntity.ok(
        publisherRuleService.findRulesByPidAndTypeAndStatusWithPagination(
            sellerPid, type, status, pageable, qf, qt));
  }

  /**
   * GET operation for single rule
   *
   * @param sellerPid Seller id. Required.
   * @param rulePid Rule pid. Required.
   * @return {@link ResponseEntity} of type {@link SellerRuleDTO}
   */
  @Operation(summary = "Get a single seller rule by its pid")
  @ApiResponse(content = @Content(schema = @Schema(implementation = SellerRuleDTO.class)))
  @Timed
  @ExceptionMetered
  @GetMapping("/{rulePid}")
  @ExternalAPI(WARNING_EXTERNAL_API_NEEDS_TO_BE_CHECKED_WITH_PRODUCT_BEFORE_DEPLOYING)
  public ResponseEntity<SellerRuleDTO> findByPid(
      @PathVariable Long sellerPid, @PathVariable Long rulePid) {
    SellerRuleDTO sellerRuleDTO = sellerRuleService.findByPidAndSellerPid(rulePid, sellerPid);
    if (Objects.isNull(sellerRuleDTO)) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(sellerRuleDTO);
  }

  @Operation(summary = "Get seller rules by given query")
  @ApiResponse(content = @Content(schema = @Schema(implementation = SellerRuleDTO.class)))
  @Timed
  @ExceptionMetered
  @GetMapping
  @ExternalAPI(WARNING_EXTERNAL_API_NEEDS_TO_BE_CHECKED_WITH_PRODUCT_BEFORE_DEPLOYING)
  public ResponseEntity<Page<SellerRuleDTO>> getRulesForSeller(
      @PathVariable Long sellerPid,
      @PageableDefault(sort = "name", direction = Sort.Direction.ASC) Pageable pageable,
      @MultiValueSearchParams SellerRuleQueryParams queryParams) {
    beanValidationService.validate(queryParams);
    SellerRuleQueryFieldParameter queryFieldParam =
        SellerRuleQueryFieldParameter.createFrom(queryParams, SellerRuleQueryField.values());

    Page<SellerRuleDTO> sellerRules =
        sellerRuleService.findBySellerPidAndOtherCriteria(sellerPid, queryFieldParam, pageable);

    return ResponseEntity.ok(sellerRules);
  }

  /**
   * DELETE operation for rule soft-delete by its pid
   *
   * @param sellerPid Seller id. Required.
   * @param rulePid Rule pid. Required.
   * @return {@link ResponseEntity} of type {@link SellerRuleDTO} composed only of <code>pid</code>
   *     of deleted rule
   */
  @Operation(summary = "Soft-delete a seller rule by its pid")
  @ApiResponse(content = @Content(schema = @Schema(implementation = SellerRuleDTO.class)))
  @Timed
  @ExceptionMetered
  @DeleteMapping("/{rulePid}")
  @ExternalAPI(WARNING_EXTERNAL_API_NEEDS_TO_BE_CHECKED_WITH_PRODUCT_BEFORE_DEPLOYING)
  public ResponseEntity<SellerRuleDTO> deleteByPid(
      @PathVariable Long sellerPid, @PathVariable Long rulePid) {
    return ResponseEntity.ok(sellerRuleService.deleteByPidAndSellerPid(rulePid, sellerPid));
  }

  /**
   * POST operation for seller rule creation
   *
   * @param sellerPid Seller id. Required.
   * @param sellerRuleDTO object containing rule data to create. Required.
   * @return {@link ResponseEntity} of type {@link SellerRuleDTO} representing created rule
   */
  @Operation(summary = "Create a seller rule")
  @ApiResponse(content = @Content(schema = @Schema(implementation = SellerRuleDTO.class)))
  @Timed
  @ExceptionMetered
  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  @ExternalAPI(WARNING_EXTERNAL_API_NEEDS_TO_BE_CHECKED_WITH_PRODUCT_BEFORE_DEPLOYING)
  public ResponseEntity<SellerRuleDTO> createRule(
      @PathVariable Long sellerPid,
      @RequestBody @Validated({Default.class, CreateGroup.class}) SellerRuleDTO sellerRuleDTO) {
    return new ResponseEntity<>(
        sellerRuleService.create(sellerPid, sellerRuleDTO), HttpStatus.CREATED);
  }

  /**
   * PUT operation for seller rule update
   *
   * @param sellerPid Seller id. Required.
   * @param rulePid Rule pid. Required.
   * @param sellerRuleDTO object containing rule data to create. Required.
   * @return {@link ResponseEntity} of type {@link SellerRuleDTO} representing rule
   */
  @Operation(summary = "Update a seller rule")
  @ApiResponse(content = @Content(schema = @Schema(implementation = SellerRuleDTO.class)))
  @Timed
  @ExceptionMetered
  @PutMapping(path = "/{rulePid}", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ExternalAPI(WARNING_EXTERNAL_API_NEEDS_TO_BE_CHECKED_WITH_PRODUCT_BEFORE_DEPLOYING)
  public ResponseEntity<SellerRuleDTO> updateRule(
      @PathVariable Long sellerPid,
      @PathVariable Long rulePid,
      @RequestBody @Validated({Default.class, UpdateGroup.class}) SellerRuleDTO sellerRuleDTO) {
    if (!rulePid.equals(sellerRuleDTO.getPid())) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_REQUEST_PARAM_BODY_NOT_MATCH);
    }
    SellerRuleDTO updated = sellerRuleService.update(sellerPid, sellerRuleDTO);
    return ResponseEntity.ok(updated);
  }
}
