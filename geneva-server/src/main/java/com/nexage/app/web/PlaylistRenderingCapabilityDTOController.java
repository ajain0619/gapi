package com.nexage.app.web;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.nexage.app.dto.PlaylistRenderingCapabilityDTO;
import com.nexage.app.services.PlaylistRenderingCapabilityDTOService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Defines endpoints for interacting with {@link PlaylistRenderingCapabilityDTO} resources. */
@RestController
@RequestMapping(
    value = "/v1/playlist-rendering-capabilities",
    produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "/v1/playlist-rendering-capabilities")
public class PlaylistRenderingCapabilityDTOController {

  private final PlaylistRenderingCapabilityDTOService playlistRenderingCapabilityDTOService;

  PlaylistRenderingCapabilityDTOController(
      PlaylistRenderingCapabilityDTOService playlistRenderingCapabilityDTOService) {
    this.playlistRenderingCapabilityDTOService = playlistRenderingCapabilityDTOService;
  }

  /**
   * Get page of {@link PlaylistRenderingCapabilityDTO}.
   *
   * @param pageable the specification of the page to get
   * @return page of {@link PlaylistRenderingCapabilityDTO}
   */
  @Operation(summary = "get page of SDK capabilities")
  @Timed
  @ExceptionMetered
  @GetMapping
  public ResponseEntity<Page<PlaylistRenderingCapabilityDTO>> getPage(
      @PageableDefault(sort = "displayValue") Pageable pageable) {
    return ResponseEntity.ok(playlistRenderingCapabilityDTOService.getPage(pageable));
  }
}
