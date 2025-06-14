package com.nexage.app.services.impl;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.nexage.admin.core.audit.AuditManager;
import com.nexage.admin.core.audit.model.AuditResponseAtRevision;
import com.nexage.admin.core.audit.model.EntityRevisionInfo;
import com.nexage.admin.core.bidder.model.BDRLineItem;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.services.AuditService;
import com.nexage.dw.geneva.util.ISO8601Util;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.error.model.CommonErrorCodes;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
@PreAuthorize("@loginUserContext.isOcUserNexage() or @loginUserContext.isOcUserSeatHolder()")
public class AuditServiceImpl implements AuditService {

  private final AuditManager auditManager;

  @Autowired
  public AuditServiceImpl(AuditManager auditManager) {
    this.auditManager = auditManager;
  }

  @Override
  @PreAuthorize(
      "(@loginUserContext.isOcUserNexage() "
          + "or @loginUserContext.isOcUserSeatHolder()) "
          + "and @loginUserContext.doSameOrNexageAffiliation(#seatholderPid) == true")
  public List<EntityRevisionInfo> getAllRevisionsForInsertionOrder(
      long seatholderPid, long insertionOrderPid, String startDate, String endDate) {

    Date start = parseDate(startDate);
    Date stop = parseDate(endDate);
    validateDates(start, stop);

    return auditManager.getAllRevisionsForInsertionOrder(insertionOrderPid, start, stop);
  }

  @Override
  @PreAuthorize(
      "(@loginUserContext.isOcUserNexage() "
          + "or @loginUserContext.isOcUserSeatHolder() "
          + "or @loginUserContext.isOcUserSeller()) "
          + "and @loginUserContext.doSameOrNexageAffiliation(#seatholderOrPublisherPid) == true")
  public <T> AuditResponseAtRevision getEntityForRevision(
      long seatholderOrPublisherPid, long entityPid, long revisionNumber, Class<T> clazz) {
    if (!auditManager.isValidRevisionNumber(clazz, entityPid, revisionNumber)) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_UNKNOWN_REVISION_NUMBER);
    }

    var mapper = getConfiguredObjectMapper();
    AuditResponseAtRevision response =
        auditManager.getEntityAuditResponse(entityPid, revisionNumber, clazz);

    try {
      var inString = mapper.writeValueAsString(response);
      return mapper.readValue(inString, new TypeReference<>() {});
    } catch (JsonProcessingException jpe) {
      log.error("Unable to serialize response", jpe);
      return null;
    }
  }

  @Override
  @PreAuthorize(
      "(@loginUserContext.isOcUserNexage() "
          + "or @loginUserContext.isOcUserSeatHolder()) "
          + "and @loginUserContext.doSameOrNexageAffiliation(#seatholderPid) == true")
  public List<EntityRevisionInfo> getAllRevisionsForLineItem(
      long seatholderPid, long lineItemPid, String startDate, String endDate) {

    Date start = parseDate(startDate);
    Date stop = parseDate(endDate);
    validateDates(start, stop);

    return auditManager.getAllRevisionsForLineItem(lineItemPid, start, stop);
  }

  @Override
  @PreAuthorize(
      "(@loginUserContext.isOcUserNexage() "
          + "or @loginUserContext.isOcUserSeatHolder()) "
          + "and @loginUserContext.doSameOrNexageAffiliation(#seatholderPid) == true")
  public List<EntityRevisionInfo> getAllRevisionsForTargetGroup(
      long seatholderPid, long targetGroupPid, String startDate, String endDate) {

    Date start = parseDate(startDate);
    Date stop = parseDate(endDate);
    validateDates(start, stop);

    return auditManager.getAllRevisionsForTargetGroup(targetGroupPid, start, stop);
  }

  @Override
  @PreAuthorize(
      "(@loginUserContext.isOcUserNexage() "
          + "or @loginUserContext.isOcUserSeatHolder()) "
          + "and @loginUserContext.doSameOrNexageAffiliation(#seatholderPid) == true")
  public List<EntityRevisionInfo> getAllRevisionsForTarget(
      long seatholderPid, long targetPid, String startDate, String endDate) {
    Date start = parseDate(startDate);
    Date stop = parseDate(endDate);
    validateDates(start, stop);

    return auditManager.getAllRevisionsForTarget(targetPid, start, stop);
  }

  @Override
  @PreAuthorize(
      "(@loginUserContext.isOcUserNexage() "
          + "or @loginUserContext.isOcUserSeatHolder()) "
          + "and @loginUserContext.doSameOrNexageAffiliation(#seatholderPid) == true")
  public List<EntityRevisionInfo> getAllRevisionsForCreative(
      long seatholderPid, long creativePid, String startDate, String endDate) {

    Date start = parseDate(startDate);
    Date stop = parseDate(endDate);
    validateDates(start, stop);

    return auditManager.getAllRevisionsForCreative(creativePid, start, stop);
  }

  @Override
  @PreAuthorize(
      "(@loginUserContext.isOcUserNexage() "
          + "or @loginUserContext.isOcUserSeller()) "
          + "and @loginUserContext.doSameOrNexageAffiliation(#publisher) == true")
  public List<EntityRevisionInfo> getAllRevisionsForSite(
      long publisher, long sitePid, String startDate, String endDate) {
    Date start = parseDate(startDate);
    Date stop = parseDate(endDate);
    validateDates(start, stop);
    return auditManager.getAllRevisionsForSite(sitePid, start, stop);
  }

  @Override
  @PreAuthorize(
      "(@loginUserContext.isOcUserNexage() "
          + "or @loginUserContext.isOcUserSeller()) "
          + "and @loginUserContext.doSameOrNexageAffiliation(#publisher) == true")
  public List<EntityRevisionInfo> getAllRevisionsForTag(
      long publisher, long tagPid, String startDate, String endDate) {
    Date start = parseDate(startDate);
    Date stop = parseDate(endDate);
    validateDates(start, stop);
    return auditManager.getAllRevisionsForTag(tagPid, start, stop);
  }

  @Override
  @PreAuthorize(
      "(@loginUserContext.isOcUserNexage() "
          + "or @loginUserContext.isOcUserSeller()) "
          + "and @loginUserContext.doSameOrNexageAffiliation(#publisher) == true")
  public List<EntityRevisionInfo> getAllRevisionsForPosition(
      long publisher, long positionPid, String startDate, String endDate) {
    Date start = parseDate(startDate);
    Date stop = parseDate(endDate);
    validateDates(start, stop);
    return auditManager.getAllRevisionsForPosition(positionPid, start, stop);
  }

  private static Date parseDate(String isoDate) {
    try {
      return ISO8601Util.parse(isoDate);
    } catch (ParseException e) {
      log.error("Error Parsing start/end dates :" + e.getMessage());
      throw new GenevaValidationException(CommonErrorCodes.COMMON_BAD_REQUEST);
    }
  }

  private static void validateDates(Date start, Date stop) {
    if (stop.before(start)) {
      log.error("End date cannot be before start date");
      throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_DATES);
    }
  }

  private ObjectMapper getConfiguredObjectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");

    mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    mapper.setSerializationInclusion(Include.NON_NULL);
    mapper.setAnnotationIntrospector(new AnnoIntrospector());
    mapper.setDateFormat(df);

    return mapper;
  }

  private static class AnnoIntrospector extends JacksonAnnotationIntrospector {

    private static final long serialVersionUID = -3229914055779275251L;

    @Override
    public boolean hasIgnoreMarker(AnnotatedMember m) {
      if (m.hasAnnotation(JsonBackReference.class)) {
        return true;
      }
      if (m.getDeclaringClass().isAssignableFrom(BDRLineItem.class)
          && (m.getMember().getName().equals("targetGroupMap")
              || m.getMember().getName().equals("getTargetGroupMap"))) {
        return true;
      }
      if (m.getMember().getName().equals("updatedOn")
          || m.getMember().getName().equals("unconvertedData")) {
        return true;
      }
      if (m.hasAnnotation(JsonIgnore.class)) {
        return false;
      }
      return super.hasIgnoreMarker(m);
    }
  }
}
