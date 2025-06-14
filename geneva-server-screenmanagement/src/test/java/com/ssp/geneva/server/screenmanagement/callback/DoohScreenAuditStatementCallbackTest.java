package com.ssp.geneva.server.screenmanagement.callback;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.audit.model.RevInfo;
import com.ssp.geneva.server.screenmanagement.batch.callback.DoohScreenAuditStatementCallback;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DoohScreenAuditStatementCallbackTest {

  private Long sellerPid = 1216L;
  private Integer revType = 2;
  private DoohScreenAuditStatementCallback doohScreenAuditStatementCallback =
      new DoohScreenAuditStatementCallback(revType, sellerPid);

  @Test
  void shouldGetAuditSqlForDoohScreen() {
    String auditSql = doohScreenAuditStatementCallback.getAuditSql();

    assertTrue(auditSql.contains("INSERT INTO dooh_screen_aud (REV, REVTYPE, created_on, pid, "));
    assertTrue(auditSql.contains("SELECT ? AS REV, 2 AS REVTYPE, created_on, pid, "));
    assertTrue(auditSql.contains("FROM dooh_screen WHERE seller_pid = ?"));
  }

  @Test
  void shouldSetPreparedStatementWhenSetAuditValues() throws SQLException {
    var preparedStatement = mock(PreparedStatement.class);
    var revInfo = mock(RevInfo.class);
    when(revInfo.getId()).thenReturn(72L);
    doohScreenAuditStatementCallback.setAuditValues(preparedStatement, revInfo);

    verify(preparedStatement).setLong(1, 72L);
    verify(preparedStatement).setLong(2, sellerPid);
  }

  @Test
  void shouldCheckIfFloorExistsInAuditSQL() {
    String auditSql = doohScreenAuditStatementCallback.getAuditSql();

    assertTrue(
        auditSql.contains("max_ad_duration,floor) SELECT ? AS REV, 2 AS REVTYPE, created_on, pid"));
    assertTrue(auditSql.contains("max_ad_duration,floor FROM dooh_screen WHERE seller_pid = ?"));
  }
}
