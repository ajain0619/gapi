package com.nexage.app.web;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.nexage.app.dto.tag.TagDTO;
import com.nexage.app.services.TagDTOService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.text.ParseException;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@Tag(name = "/v1/sellers/{sellerId}/sites/{siteId}/placements/{placementId}/tags")
@RestController
@RequestMapping(
    value = {"/v1/sellers/{sellerId}/sites/{siteId}/placements/{placementId}/tags"},
    produces = MediaType.APPLICATION_JSON_VALUE)
public class TagDTOController {

  private final TagDTOService tagDTOService;

  public TagDTOController(TagDTOService tagDTOService) {
    this.tagDTOService = tagDTOService;
  }

  /**
   * GET resource to retrieve paginated {@link TagDTO} based on request.
   *
   * @param pageable Pagination based on {@link Pageable}
   * @param sellerId seller identifier
   * @param siteId site identifier
   * @param placementId placement identifier
   * @return {@link ResponseEntity} of type {@link Page} {@link TagDTO}
   * @throws ParseException if startDate & stopDate fails on parsing.
   */
  @Timed
  @ExceptionMetered
  @GetMapping
  @Operation(summary = "Get tags under a placement")
  @ApiResponse(content = @Content(schema = @Schema(implementation = TagDTO.class)))
  public ResponseEntity<Page<TagDTO>> getTags(
      @PageableDefault(sort = "pid", direction = Sort.Direction.ASC) Pageable pageable,
      @PathVariable Long sellerId,
      @PathVariable Long siteId,
      @PathVariable Long placementId) {
    return ResponseEntity.ok(tagDTOService.getTags(sellerId, siteId, placementId, pageable));
  }
}
