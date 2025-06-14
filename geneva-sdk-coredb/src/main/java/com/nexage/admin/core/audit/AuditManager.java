package com.nexage.admin.core.audit;

import com.nexage.admin.core.audit.model.AuditResponseAtRevision;
import com.nexage.admin.core.audit.model.EntityRevisionInfo;
import java.util.Date;
import java.util.List;

public interface AuditManager {

  List<EntityRevisionInfo> getAllRevisionsForInsertionOrder(
      long insertionOrderPid, Date start, Date stop);

  List<EntityRevisionInfo> getAllRevisionsForLineItem(long lineItemPid, Date start, Date stop);

  List<EntityRevisionInfo> getAllRevisionsForTargetGroup(
      long targetGroupPid, Date start, Date stop);

  List<EntityRevisionInfo> getAllRevisionsForTarget(long targetPid, Date start, Date stop);

  List<EntityRevisionInfo> getAllRevisionsForCreative(long creativePid, Date start, Date stop);

  List<EntityRevisionInfo> getAllRevisionsForSite(long sitePid, Date start, Date stop);

  List<EntityRevisionInfo> getAllRevisionsForTag(long tagPid, Date start, Date stop);

  List<EntityRevisionInfo> getAllRevisionsForPosition(long positionPid, Date start, Date stop);

  boolean isValidRevisionNumber(Class<?> clazz, long pid, long revision);

  <T> AuditResponseAtRevision getEntityAuditResponse(
      long entityPid, long revisionNumber, Class<T> clazz);
}
