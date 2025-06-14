package com.nexage.app.web;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.nexage.app.dto.RTBProfileLibraryCloneDataDTO;
import com.nexage.app.dto.publisher.PublisherRTBProfileLibraryDTO;
import com.nexage.app.services.NexageRtbProfileLibraryService;
import com.ssp.geneva.common.base.annotation.Legacy;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Legacy
@Tag(name = "/rtbprofilelibrary")
@RestController
@RequestMapping(value = "/rtbprofilelibrary")
public class RTBProfileLibraryController {

  private final NexageRtbProfileLibraryService nexageRtbProfileLibraryService;

  public RTBProfileLibraryController(
      NexageRtbProfileLibraryService nexageRtbProfileLibraryService) {
    this.nexageRtbProfileLibraryService = nexageRtbProfileLibraryService;
  }

  @Timed
  @ExceptionMetered
  @GetMapping(value = "/{library}")
  public PublisherRTBProfileLibraryDTO getRTBProfileLibrary(
      @PathVariable(value = "library") long libraryPid) {
    return nexageRtbProfileLibraryService.get(libraryPid);
  }

  @Timed
  @ExceptionMetered
  @GetMapping
  public List<PublisherRTBProfileLibraryDTO> getAllRTBProfileLibraries() {
    return nexageRtbProfileLibraryService.getAll();
  }

  @Timed
  @ExceptionMetered
  @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE})
  public PublisherRTBProfileLibraryDTO createRTBProfileLibrary(
      @RequestBody PublisherRTBProfileLibraryDTO library) {
    return nexageRtbProfileLibraryService.create(library);
  }

  @Timed
  @ExceptionMetered
  @PutMapping(
      value = "/{library}",
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  public PublisherRTBProfileLibraryDTO updateRTBProfileLibrary(
      @PathVariable(value = "library") long libraryPid,
      @RequestBody PublisherRTBProfileLibraryDTO library) {
    return nexageRtbProfileLibraryService.update(libraryPid, library);
  }

  @Timed
  @ExceptionMetered
  @DeleteMapping(value = "/{library}")
  public void deleteRTBProfileLibrary(@PathVariable(value = "library") long libraryPid) {
    nexageRtbProfileLibraryService.delete(libraryPid);
  }

  @Timed
  @ExceptionMetered
  @PostMapping(value = "/clone")
  public PublisherRTBProfileLibraryDTO cloneRTBProfileLibraries(
      @RequestBody RTBProfileLibraryCloneDataDTO data) {
    return nexageRtbProfileLibraryService.cloneRTBProfileLibraries(data);
  }
}
