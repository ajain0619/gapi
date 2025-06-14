package com.nexage.app.web;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.nexage.app.dto.ContentGenreDTO;
import com.nexage.app.services.ContentGenreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
@RequestMapping(value = "/v1/content-genres", produces = MediaType.APPLICATION_JSON_VALUE)
public class ContentGenreController {

  private final ContentGenreService contentGenreService;

  public ContentGenreController(ContentGenreService contentGenreService) {
    this.contentGenreService = contentGenreService;
  }

  /**
   * Get list of Content genres
   *
   * @param qt Query term for search
   * @param qf Query field for search
   * @param pageable Pagination based on {@link Pageable}
   * @return {@link ResponseEntity} of type {@link ContentGenreDTO}
   */
  @Timed
  @ExceptionMetered
  @GetMapping
  @Operation(summary = "Get list of Content Genres")
  @ApiResponse(content = @Content(schema = @Schema(implementation = ContentGenreDTO.class)))
  public ResponseEntity<Page<ContentGenreDTO>> getContentGenres(
      @Parameter(name = "Query term for search") @RequestParam(value = "qt", required = false)
          String qt,
      @Parameter(name = "Query field for search") @RequestParam(value = "qf", required = false)
          Set<String> qf,
      @PageableDefault(value = 10, sort = "pid") Pageable pageable) {
    return ResponseEntity.ok(contentGenreService.findAll(qt, qf, pageable));
  }
}
