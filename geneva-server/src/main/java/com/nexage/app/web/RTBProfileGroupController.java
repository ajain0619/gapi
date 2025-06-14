package com.nexage.app.web;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.nexage.app.dto.publisher.PublisherRTBProfileGroupDTO;
import com.nexage.app.services.NexageRtbProfileGroupService;
import com.ssp.geneva.common.base.annotation.Legacy;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * Controller for non pss RtbProfileGroup CRUD operations
 */
@Legacy
@Tag(name = "/rtbprofilegroup")
@RestController
@RequestMapping(value = "/rtbprofilegroup")
public class RTBProfileGroupController {

  private final NexageRtbProfileGroupService nexageRtbProfileGroupService;

  public RTBProfileGroupController(NexageRtbProfileGroupService nexageRtbProfileGroupService) {
    this.nexageRtbProfileGroupService = nexageRtbProfileGroupService;
  }

  @Timed
  @ExceptionMetered
  @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE})
  public PublisherRTBProfileGroupDTO createRTBProfileGroup(
      @RequestBody PublisherRTBProfileGroupDTO group) {
    return nexageRtbProfileGroupService.create(group.getPublisherPid(), group);
  }

  @Timed
  @ExceptionMetered
  @GetMapping(value = "/{group}")
  public PublisherRTBProfileGroupDTO getRTBProfileGroup(
      @PathVariable(value = "group") long groupPid) {
    return nexageRtbProfileGroupService.get(null, groupPid);
  }

  @Timed
  @ExceptionMetered
  @PutMapping(value = "/{group}")
  public PublisherRTBProfileGroupDTO updateRTBProfileGroup(
      @PathVariable(value = "group") long groupPid,
      @RequestBody PublisherRTBProfileGroupDTO group) {
    return nexageRtbProfileGroupService.update(groupPid, group);
  }

  @Timed
  @ExceptionMetered
  @DeleteMapping(value = "/{group}")
  public void updateRTBProfileGroup(@PathVariable(value = "group") long groupPid) {
    nexageRtbProfileGroupService.delete(groupPid);
  }
}
