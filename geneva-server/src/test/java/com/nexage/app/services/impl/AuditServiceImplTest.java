package com.nexage.app.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import com.nexage.admin.core.audit.AuditManager;
import com.nexage.admin.core.audit.model.AuditResponseAtRevision;
import com.nexage.admin.core.audit.model.EntityRevisionInfo;
import com.nexage.admin.core.bidder.model.BdrInsertionOrder;
import com.nexage.app.error.ServerErrorCodes;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.hibernate.envers.RevisionType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuditServiceImplTest {

  private static final long SEATHOLDER_PID = 1L;
  private static final long REVISION_NUMBER = 2L;
  private static final long ENTITY_PID = 3L;
  private static final Date DATE = new Date();
  private static final String START_DATE_AS_STRING =
      new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(DATE.getTime() - 1000);
  private static final String END_DATE_AS_STRING =
      new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(new Date(DATE.getTime() + 1000));
  private static final List<EntityRevisionInfo> REVISIONS =
      List.of(new EntityRevisionInfo(1, "user", DATE, 10L, RevisionType.MOD));

  @Mock private AuditManager auditManager;
  @InjectMocks private AuditServiceImpl auditService;

  @Test
  void shouldGetAllRevisionsForInsertionOrder() {
    // given
    given(auditManager.getAllRevisionsForInsertionOrder(eq(ENTITY_PID), any(), any()))
        .willReturn(REVISIONS);

    // when
    List<EntityRevisionInfo> result =
        auditService.getAllRevisionsForInsertionOrder(
            SEATHOLDER_PID, ENTITY_PID, START_DATE_AS_STRING, END_DATE_AS_STRING);

    // then
    assertEquals(REVISIONS, result);
  }

  @Test
  void shouldThrowWhenGettingAllRevisionsForInsertionOrderForSwappedDates() {
    GenevaValidationException ex =
        assertThrows(
            GenevaValidationException.class,
            () ->
                auditService.getAllRevisionsForInsertionOrder(
                    SEATHOLDER_PID, ENTITY_PID, END_DATE_AS_STRING, START_DATE_AS_STRING));
    assertEquals(ServerErrorCodes.SERVER_INVALID_DATES, ex.getErrorCode());
  }

  @Test
  void shouldGetSingleEntityForRevision() {
    // given
    var expected = new AuditResponseAtRevision();
    expected.setBefore(Map.of("hero", "batman"));
    expected.setAfter(Map.of("villain", "joker"));

    given(auditManager.isValidRevisionNumber(BdrInsertionOrder.class, ENTITY_PID, REVISION_NUMBER))
        .willReturn(true);
    given(auditManager.getEntityAuditResponse(ENTITY_PID, REVISION_NUMBER, BdrInsertionOrder.class))
        .willReturn(expected);

    // when
    AuditResponseAtRevision result =
        auditService.getEntityForRevision(
            SEATHOLDER_PID, ENTITY_PID, REVISION_NUMBER, BdrInsertionOrder.class);

    // then
    assertEquals(expected.getBefore(), result.getBefore());
    assertEquals(expected.getAfter(), result.getAfter());
  }

  @Test
  void shouldThrowWhenGettingSingleEntityForRevisionButRevisionNumberIsInvalid() {
    var ex =
        assertThrows(
            GenevaValidationException.class,
            () ->
                auditService.getEntityForRevision(
                    SEATHOLDER_PID, ENTITY_PID, REVISION_NUMBER, BdrInsertionOrder.class));
    assertEquals(ServerErrorCodes.SERVER_UNKNOWN_REVISION_NUMBER, ex.getErrorCode());
  }

  @Test
  void shouldGetAllRevisionsForLineItem() {
    // given
    given(auditManager.getAllRevisionsForLineItem(eq(ENTITY_PID), any(), any()))
        .willReturn(REVISIONS);

    // when
    List<EntityRevisionInfo> result =
        auditService.getAllRevisionsForLineItem(
            SEATHOLDER_PID, ENTITY_PID, START_DATE_AS_STRING, END_DATE_AS_STRING);

    // then
    assertEquals(REVISIONS, result);
  }

  @Test
  void shouldGetAllRevisionsForTargetGroup() {
    // given
    given(auditManager.getAllRevisionsForTargetGroup(eq(ENTITY_PID), any(), any()))
        .willReturn(REVISIONS);

    // when
    List<EntityRevisionInfo> result =
        auditService.getAllRevisionsForTargetGroup(
            SEATHOLDER_PID, ENTITY_PID, START_DATE_AS_STRING, END_DATE_AS_STRING);

    // then
    assertEquals(REVISIONS, result);
  }

  @Test
  void shouldGetAllRevisionsForTarget() {
    // given
    given(auditManager.getAllRevisionsForTarget(eq(ENTITY_PID), any(), any()))
        .willReturn(REVISIONS);

    // when
    List<EntityRevisionInfo> result =
        auditService.getAllRevisionsForTarget(
            SEATHOLDER_PID, ENTITY_PID, START_DATE_AS_STRING, END_DATE_AS_STRING);

    // then
    assertEquals(REVISIONS, result);
  }

  @Test
  void shouldGetAllRevisionsForCreative() {
    // given
    given(auditManager.getAllRevisionsForCreative(eq(ENTITY_PID), any(), any()))
        .willReturn(REVISIONS);

    // when
    List<EntityRevisionInfo> result =
        auditService.getAllRevisionsForCreative(
            SEATHOLDER_PID, ENTITY_PID, START_DATE_AS_STRING, END_DATE_AS_STRING);

    // then
    assertEquals(REVISIONS, result);
  }

  @Test
  void shouldGetAllRevisionsForSite() {
    // given
    given(auditManager.getAllRevisionsForSite(eq(ENTITY_PID), any(), any())).willReturn(REVISIONS);

    // when
    List<EntityRevisionInfo> result =
        auditService.getAllRevisionsForSite(
            SEATHOLDER_PID, ENTITY_PID, START_DATE_AS_STRING, END_DATE_AS_STRING);

    // then
    assertEquals(REVISIONS, result);
  }

  @Test
  void shouldGetAllRevisionsForTag() {
    // given
    given(auditManager.getAllRevisionsForTag(eq(ENTITY_PID), any(), any())).willReturn(REVISIONS);

    // when
    List<EntityRevisionInfo> result =
        auditService.getAllRevisionsForTag(
            SEATHOLDER_PID, ENTITY_PID, START_DATE_AS_STRING, END_DATE_AS_STRING);

    // then
    assertEquals(REVISIONS, result);
  }

  @Test
  void shouldGetAllRevisionsForPosition() {
    // given
    given(auditManager.getAllRevisionsForPosition(eq(ENTITY_PID), any(), any()))
        .willReturn(REVISIONS);

    // when
    List<EntityRevisionInfo> result =
        auditService.getAllRevisionsForPosition(
            SEATHOLDER_PID, ENTITY_PID, START_DATE_AS_STRING, END_DATE_AS_STRING);

    // then
    assertEquals(REVISIONS, result);
  }
}
