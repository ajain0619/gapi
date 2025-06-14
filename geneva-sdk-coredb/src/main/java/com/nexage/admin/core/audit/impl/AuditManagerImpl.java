package com.nexage.admin.core.audit.impl;

import com.nexage.admin.core.audit.AuditManager;
import com.nexage.admin.core.audit.model.AuditResponseAtRevision;
import com.nexage.admin.core.audit.model.EntityRevisionInfo;
import com.nexage.admin.core.bidder.model.BDRLineItem;
import com.nexage.admin.core.bidder.model.BDRTarget;
import com.nexage.admin.core.bidder.model.BdrCreative;
import com.nexage.admin.core.bidder.model.BdrInsertionOrder;
import com.nexage.admin.core.bidder.model.BdrTargetGroup;
import com.nexage.admin.core.model.Position;
import com.nexage.admin.core.model.Site;
import com.nexage.admin.core.model.Tag;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import lombok.extern.log4j.Log4j2;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.exception.RevisionDoesNotExistException;
import org.hibernate.envers.query.AuditEntity;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class AuditManagerImpl implements AuditManager {

  @PersistenceContext private EntityManager entityManager;

  @Override
  public List<EntityRevisionInfo> getAllRevisionsForInsertionOrder(
      long insertionOrderPid, Date start, Date stop) {
    return getAllRevisionsForEntity(BdrInsertionOrder.class, insertionOrderPid, start, stop);
  }

  @Override
  public List<EntityRevisionInfo> getAllRevisionsForLineItem(
      long lineItemPid, Date start, Date stop) {
    return getAllRevisionsForEntity(BDRLineItem.class, lineItemPid, start, stop);
  }

  @Override
  public List<EntityRevisionInfo> getAllRevisionsForTargetGroup(
      long targetGroupPid, Date start, Date stop) {
    return getAllRevisionsForEntity(BdrTargetGroup.class, targetGroupPid, start, stop);
  }

  @Override
  public List<EntityRevisionInfo> getAllRevisionsForTarget(long targetPid, Date start, Date stop) {
    return getAllRevisionsForEntity(BDRTarget.class, targetPid, start, stop);
  }

  @Override
  public List<EntityRevisionInfo> getAllRevisionsForCreative(
      long creativePid, Date start, Date stop) {
    return getAllRevisionsForEntity(BdrCreative.class, creativePid, start, stop);
  }

  @Override
  public List<EntityRevisionInfo> getAllRevisionsForSite(long sitePid, Date start, Date stop) {
    return getAllRevisionsForEntity(Site.class, sitePid, start, stop);
  }

  @Override
  public List<EntityRevisionInfo> getAllRevisionsForTag(long tagPid, Date start, Date stop) {
    return getAllRevisionsForEntity(Tag.class, tagPid, start, stop);
  }

  @Override
  public List<EntityRevisionInfo> getAllRevisionsForPosition(
      long positionPid, Date start, Date stop) {
    return getAllRevisionsForEntity(Position.class, positionPid, start, stop);
  }

  @Override
  public <T> AuditResponseAtRevision getEntityAuditResponse(
      long entityPid, long revisionNumber, Class<T> clazz) {

    AuditResponseAtRevision result = new AuditResponseAtRevision();
    Map<String, Object> before = new HashMap<>();
    Map<String, Object> after = new HashMap<>();

    T currentEntity = getEntityForRevision(clazz, entityPid, revisionNumber);
    if (currentEntity != null) {
      after.put(clazz.getSimpleName(), currentEntity);
      result.setAfter(after);
    }

    Long previousRevision = getPreviousRevisionNumber(clazz, entityPid, revisionNumber);
    if (previousRevision != null) {
      T previousEntity = getEntityForRevision(clazz, entityPid, previousRevision);
      if (previousEntity != null) {
        before.put(clazz.getSimpleName(), previousEntity);
        result.setBefore(before);
      }
    }
    return result;
  }

  @Override
  public boolean isValidRevisionNumber(Class<?> clazz, long pid, long revision) {
    boolean result = true;
    try {
      getAuditReader().findRevision(clazz, revision);
    } catch (RevisionDoesNotExistException e) {
      result = false;
    }
    if (result) {
      Number revisionNumber = revision;
      List<Number> availableRevisions = getAuditReader().getRevisions(clazz, pid);
      if (!availableRevisions.contains(revisionNumber)) {
        result = false;
      }
    }
    return result;
  }

  private Long getPreviousRevisionNumber(Class<?> clazz, long pid, long currentRevisionNumber) {
    Number previousRevision = null;
    try {
      previousRevision =
          (Number)
              getAuditReader()
                  .createQuery()
                  .forRevisionsOfEntity(clazz, false, false)
                  .addProjection(AuditEntity.revisionNumber().max())
                  .add(AuditEntity.id().eq(pid))
                  .add(AuditEntity.revisionNumber().lt(currentRevisionNumber))
                  .getSingleResult();
    } catch (NoResultException noresult) {
      log.warn(
          "No previous revision exists for"
              + clazz.getSimpleName()
              + " less than revision "
              + currentRevisionNumber);
    }
    return previousRevision != null ? previousRevision.longValue() : null;
  }

  @SuppressWarnings("unchecked")
  private <T> T getEntityForRevision(Class<T> clazz, long entityPid, long revisionNumber) {
    T entityAtRevision = null;
    try {
      entityAtRevision =
          (T)
              getAuditReader()
                  .createQuery()
                  .forEntitiesAtRevision(clazz, revisionNumber)
                  .add(AuditEntity.id().eq(entityPid))
                  .getSingleResult();
    } catch (NoResultException noresult) {
      log.warn(
          "No Revisions found for pid" + entityPid + "for instance of " + clazz.getSimpleName());
    }
    return entityAtRevision;
  }

  private List<EntityRevisionInfo> getAllRevisionsForEntity(
      Class<?> clazz, long pid, Date start, Date stop) {
    List<EntityRevisionInfo> resultList = new ArrayList<>();
    List<?> results = null;
    try {
      results =
          getAuditReader()
              .createQuery()
              .forRevisionsOfEntity(clazz, false, true)
              .addProjection(AuditEntity.revisionNumber())
              .addProjection(AuditEntity.revisionProperty("userName"))
              .addProjection(AuditEntity.revisionProperty("timestamp"))
              .addProjection(AuditEntity.revisionType())
              .add(AuditEntity.id().eq(pid))
              .add(AuditEntity.revisionProperty("timestamp").between(start, stop))
              .addOrder(AuditEntity.revisionNumber().asc())
              .getResultList();
    } catch (NoResultException e) {
      log.warn("No Revisions found for entity" + clazz.getSimpleName());
    }

    if (results != null && results.size() > 0) {
      for (Object result : results) {

        Object[] eachResultItems = (Object[]) result;
        Long revisionNumber = (Long) eachResultItems[0];
        String username = (String) eachResultItems[1];
        Date revisionDate = (Date) eachResultItems[2];
        RevisionType revisionType = (RevisionType) eachResultItems[3];

        EntityRevisionInfo entityRevInfo =
            new EntityRevisionInfo(revisionNumber, username, revisionDate, pid, revisionType);
        resultList.add(entityRevInfo);
      }
    }
    return resultList;
  }

  private AuditReader getAuditReader() {
    return AuditReaderFactory.get(entityManager);
  }
}
