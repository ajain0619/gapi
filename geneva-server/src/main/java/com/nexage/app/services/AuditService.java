package com.nexage.app.services;

import com.nexage.admin.core.audit.model.AuditResponseAtRevision;
import com.nexage.admin.core.audit.model.EntityRevisionInfo;
import java.util.List;

public interface AuditService {

  List<EntityRevisionInfo> getAllRevisionsForInsertionOrder(
      long seatholderPid, long insertionOrderPid, String startDate, String endDate);

  List<EntityRevisionInfo> getAllRevisionsForLineItem(
      long seatholderPid, long lineItemPid, String startDate, String endDate);

  List<EntityRevisionInfo> getAllRevisionsForTargetGroup(
      long seatholderPid, long targetGroupPid, String startDate, String endDate);

  List<EntityRevisionInfo> getAllRevisionsForTarget(
      long seatholderPid, long targetPid, String startDate, String endDate);

  List<EntityRevisionInfo> getAllRevisionsForCreative(
      long seatholderPid, long creativePid, String startDate, String endDate);

  List<EntityRevisionInfo> getAllRevisionsForSite(
      long publisher, long site, String startDate, String endDate);

  List<EntityRevisionInfo> getAllRevisionsForTag(
      long publisher, long tag, String startDate, String endDate);

  List<EntityRevisionInfo> getAllRevisionsForPosition(
      long publisher, long position, String startDate, String endDate);

  <T> AuditResponseAtRevision getEntityForRevision(
      long seatholderOrPublisherPid, long entityPid, long revisionNumber, Class<T> clazz);
}
