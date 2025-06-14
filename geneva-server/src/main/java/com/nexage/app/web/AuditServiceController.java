package com.nexage.app.web;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.nexage.admin.core.audit.model.AuditResponseAtRevision;
import com.nexage.admin.core.audit.model.EntityRevisionInfo;
import com.nexage.admin.core.bidder.model.BDRLineItem;
import com.nexage.admin.core.bidder.model.BdrCreative;
import com.nexage.admin.core.bidder.model.BdrInsertionOrder;
import com.nexage.admin.core.bidder.model.BdrTargetGroup;
import com.nexage.admin.core.model.Position;
import com.nexage.admin.core.model.Site;
import com.nexage.app.dto.AuditDeltaResponseDTO;
import com.nexage.app.services.AuditService;
import com.nexage.app.util.AuditUtil;
import com.ssp.geneva.common.base.annotation.Legacy;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Legacy
@Tag(name = "/audit")
@RestController
@RequestMapping(value = "/audit")
public class AuditServiceController {

  private final AuditService auditService;

  public AuditServiceController(AuditService auditService) {
    this.auditService = auditService;
  }

  @Timed
  @ExceptionMetered
  @GetMapping(value = "/revisions/seatholders/{seatholderPID}/insertionorders/{insertionOrderPID}")
  @ResponseBody
  public List<EntityRevisionInfo> getAllInsertionOrderRevisions(
      @PathVariable(value = "seatholderPID") long seatholderPid,
      @PathVariable(value = "insertionOrderPID") long insertionOrderPid,
      @RequestParam(value = "start") @DateTimeFormat(iso = ISO.DATE_TIME) String startDate,
      @RequestParam(value = "stop") @DateTimeFormat(iso = ISO.DATE_TIME) String endDate) {
    return auditService.getAllRevisionsForInsertionOrder(
        seatholderPid, insertionOrderPid, startDate, endDate);
  }

  @Timed
  @ExceptionMetered
  @GetMapping(
      value =
          "/revisions/seatholders/{seatholderPID}/insertionorders/{insertionOrderPID}/{revisionNumber}")
  @ResponseBody
  public AuditDeltaResponseDTO getInsertionOrderForRevision(
      @PathVariable(value = "seatholderPID") long seatholderPid,
      @PathVariable(value = "insertionOrderPID") long insertionOrderPid,
      @PathVariable(value = "revisionNumber") long revisionNumber) {
    AuditResponseAtRevision response =
        auditService.getEntityForRevision(
            seatholderPid, insertionOrderPid, revisionNumber, BdrInsertionOrder.class);
    return getAuditDeltaResponse(response);
  }

  @Timed
  @ExceptionMetered
  @GetMapping(
      value = "/revisions/seatholders/{seatholderPID}/lineitems/{lineitemPID}/{revisionNumber}")
  @ResponseBody
  public AuditDeltaResponseDTO getLineItemForRevision(
      @PathVariable(value = "seatholderPID") long seatholderPid,
      @PathVariable(value = "lineitemPID") long lineItemPid,
      @PathVariable(value = "revisionNumber") long revisionNumber) {

    AuditResponseAtRevision response =
        auditService.getEntityForRevision(
            seatholderPid, lineItemPid, revisionNumber, BDRLineItem.class);
    return getAuditDeltaResponse(response);
  }

  @Timed
  @ExceptionMetered
  @GetMapping(
      value =
          "/revisions/seatholders/{seatholderPID}/targetgroups/{targetGroupPID}/{revisionNumber}")
  @ResponseBody
  public AuditDeltaResponseDTO getTargetGroupForRevision(
      @PathVariable(value = "seatholderPID") long seatholderPid,
      @PathVariable(value = "targetGroupPID") long targetGroupPid,
      @PathVariable(value = "revisionNumber") long revisionNumber) {
    AuditResponseAtRevision response =
        auditService.getEntityForRevision(
            seatholderPid, targetGroupPid, revisionNumber, BdrTargetGroup.class);
    return getAuditDeltaResponse(response);
  }

  @Timed
  @ExceptionMetered
  @GetMapping(
      value = "/revisions/seatholders/{seatholderPID}/creatives/{creativePID}/{revisionNumber}")
  @ResponseBody
  public AuditDeltaResponseDTO getCreativeForRevision(
      @PathVariable(value = "seatholderPID") long seatholderPid,
      @PathVariable(value = "creativePID") long creativePid,
      @PathVariable(value = "revisionNumber") long revisionNumber) {
    AuditResponseAtRevision response =
        auditService.getEntityForRevision(
            seatholderPid, creativePid, revisionNumber, BdrCreative.class);
    return getAuditDeltaResponse(response);
  }

  @Timed
  @ExceptionMetered
  @GetMapping(value = "/revisions/seatholders/{seatholderPID}/lineitems/{lineItemPID}")
  @ResponseBody
  public List<EntityRevisionInfo> getAllLineItemRevisions(
      @PathVariable(value = "seatholderPID") long seatholderPid,
      @PathVariable(value = "lineItemPID") long lineItemPid,
      @RequestParam(value = "start") @DateTimeFormat(iso = ISO.DATE_TIME) String startDate,
      @RequestParam(value = "stop") @DateTimeFormat(iso = ISO.DATE_TIME) String endDate) {
    return auditService.getAllRevisionsForLineItem(seatholderPid, lineItemPid, startDate, endDate);
  }

  @Timed
  @ExceptionMetered
  @GetMapping(value = "/revisions/seatholders/{seatholderPID}/targetgroups/{targetGroupPID}")
  @ResponseBody
  public List<EntityRevisionInfo> getAllTargetGroupRevisions(
      @PathVariable(value = "seatholderPID") long seatholderPid,
      @PathVariable(value = "targetGroupPID") long targetGroupPid,
      @RequestParam(value = "start") @DateTimeFormat(iso = ISO.DATE_TIME) String startDate,
      @RequestParam(value = "stop") @DateTimeFormat(iso = ISO.DATE_TIME) String endDate) {
    return auditService.getAllRevisionsForTargetGroup(
        seatholderPid, targetGroupPid, startDate, endDate);
  }

  @Timed
  @ExceptionMetered
  @GetMapping(value = "/revisions/seatholders/{seatholderPID}/targets/{targetPID}")
  @ResponseBody
  public List<EntityRevisionInfo> getAllTargetRevisions(
      @PathVariable(value = "seatholderPID") long seatholderPid,
      @PathVariable(value = "targetPID") long targetPid,
      @RequestParam(value = "start") @DateTimeFormat(iso = ISO.DATE_TIME) String startDate,
      @RequestParam(value = "stop") @DateTimeFormat(iso = ISO.DATE_TIME) String endDate) {
    return auditService.getAllRevisionsForTarget(seatholderPid, targetPid, startDate, endDate);
  }

  @Timed
  @ExceptionMetered
  @GetMapping(value = "/revisions/seatholders/{seatholderPID}/creatives/{creativePID}")
  @ResponseBody
  public List<EntityRevisionInfo> getAllCreativeRevisions(
      @PathVariable(value = "seatholderPID") long seatholderPid,
      @PathVariable(value = "creativePID") long creativePid,
      @RequestParam(value = "start") @DateTimeFormat(iso = ISO.DATE_TIME) String startDate,
      @RequestParam(value = "stop") @DateTimeFormat(iso = ISO.DATE_TIME) String endDate) {
    return auditService.getAllRevisionsForCreative(seatholderPid, creativePid, startDate, endDate);
  }

  @Timed
  @ExceptionMetered
  @GetMapping(value = "/publisher/{publisher}/site/{site}/revision")
  @ResponseBody
  public List<EntityRevisionInfo> getAllSiteRevisions(
      @PathVariable(value = "publisher") long publisher,
      @PathVariable(value = "site") long site,
      @RequestParam(value = "start") @DateTimeFormat(iso = ISO.DATE_TIME) String startDate,
      @RequestParam(value = "stop") @DateTimeFormat(iso = ISO.DATE_TIME) String endDate) {
    return auditService.getAllRevisionsForSite(publisher, site, startDate, endDate);
  }

  @Timed
  @ExceptionMetered
  @GetMapping(value = "/publisher/{publisher}/tag/{tag}/revision")
  @ResponseBody
  public List<EntityRevisionInfo> getAllTagRevisions(
      @PathVariable(value = "publisher") long publisher,
      @PathVariable(value = "tag") long tag,
      @RequestParam(value = "start") @DateTimeFormat(iso = ISO.DATE_TIME) String startDate,
      @RequestParam(value = "stop") @DateTimeFormat(iso = ISO.DATE_TIME) String endDate) {
    return auditService.getAllRevisionsForTag(publisher, tag, startDate, endDate);
  }

  @Timed
  @ExceptionMetered
  @GetMapping(value = "/publisher/{publisher}/position/{position}/revision")
  @ResponseBody
  public List<EntityRevisionInfo> getAllPositionRevisions(
      @PathVariable(value = "publisher") long publisher,
      @PathVariable(value = "position") long position,
      @RequestParam(value = "start") @DateTimeFormat(iso = ISO.DATE_TIME) String startDate,
      @RequestParam(value = "stop") @DateTimeFormat(iso = ISO.DATE_TIME) String endDate) {
    return auditService.getAllRevisionsForPosition(publisher, position, startDate, endDate);
  }

  @Timed
  @ExceptionMetered
  @GetMapping(value = "/publisher/{publisher}/site/{site}/revision/{revisionNumber}")
  @ResponseBody
  public AuditDeltaResponseDTO getSiteForRevision(
      @PathVariable(value = "publisher") long publisher,
      @PathVariable(value = "site") long site,
      @PathVariable(value = "revisionNumber") long revisionNumber) {
    AuditResponseAtRevision response =
        auditService.getEntityForRevision(publisher, site, revisionNumber, Site.class);
    return getAuditDeltaResponse(response);
  }

  @Timed
  @ExceptionMetered
  @GetMapping(value = "/publisher/{publisher}/tag/{tag}/revision/{revisionNumber}")
  @ResponseBody
  public AuditDeltaResponseDTO getTagForRevision(
      @PathVariable(value = "publisher") long publisher,
      @PathVariable(value = "tag") long tag,
      @PathVariable(value = "revisionNumber") long revisionNumber) {
    AuditResponseAtRevision response =
        auditService.getEntityForRevision(publisher, tag, revisionNumber, Tag.class);
    return getAuditDeltaResponse(response);
  }

  @Timed
  @ExceptionMetered
  @GetMapping(value = "/publisher/{publisher}/position/{position}/revision/{revisionNumber}")
  @ResponseBody
  public AuditDeltaResponseDTO getPositionForRevision(
      @PathVariable(value = "publisher") long publisher,
      @PathVariable(value = "position") long position,
      @PathVariable(value = "revisionNumber") long revisionNumber) {
    AuditResponseAtRevision response =
        auditService.getEntityForRevision(publisher, position, revisionNumber, Position.class);
    return getAuditDeltaResponse(response);
  }

  private AuditDeltaResponseDTO getAuditDeltaResponse(AuditResponseAtRevision response) {
    AuditDeltaResponseDTO deltaResponse = new AuditDeltaResponseDTO();
    Set<Entry<String, Object>> entrySet = new HashSet<>();

    if (response != null) {
      if (response.getBefore() != null) {
        entrySet = response.getBefore().entrySet();
      }
      if (response.getAfter() != null) {
        entrySet = response.getAfter().entrySet();
      }

      for (Entry<String, Object> entry : entrySet) {
        Object before =
            (response.getBefore() == null) ? null : response.getBefore().get(entry.getKey());
        Object after =
            (response.getAfter() == null) ? null : response.getAfter().get(entry.getKey());
        deltaResponse.addToDelta(entry.getKey(), AuditUtil.getJsonDelta(before, after));
      }
    }
    return deltaResponse;
  }
}
