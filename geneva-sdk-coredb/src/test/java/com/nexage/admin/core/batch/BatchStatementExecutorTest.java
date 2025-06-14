package com.nexage.admin.core.batch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.audit.model.RevInfo;
import com.nexage.admin.core.batch.callback.BatchAuditStatementCallback;
import com.nexage.admin.core.batch.callback.BatchStatementCallback;
import com.nexage.admin.core.batch.executor.BatchStatementExecutor;
import com.nexage.admin.core.model.DoohScreen;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import org.hibernate.Session;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.jdbc.ReturningWork;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

@ExtendWith(MockitoExtension.class)
class BatchStatementExecutorTest {

  @Mock private EntityManager entityManager;
  @Mock private Session session;
  @Mock private PreparedStatement preparedStatement;
  @Mock private Connection connection;

  private BatchStatementExecutor batchStatementExecutor;

  @BeforeEach
  void setup() {
    batchStatementExecutor = new BatchStatementExecutor(entityManager);
  }

  @Test
  void shouldReturnRecordCountWhenExecutingBatchStatement() {
    BatchStatementCallback callback = mock(BatchStatementCallback.class);
    when(entityManager.unwrap((Session.class))).thenReturn(session);
    when(session.doReturningWork(
            argThat(
                (ArgumentMatcher<ReturningWork<Integer>>)
                    returningWork -> {
                      var sql = "select * from table";
                      when(callback.getSql()).thenReturn(sql);
                      when(callback.getBatchSize()).thenReturn(10);
                      when(callback.getEntities()).thenReturn(List.of(new Object(), new Object()));
                      try {
                        when(connection.prepareStatement(sql)).thenReturn(preparedStatement);
                        when(preparedStatement.executeBatch()).thenReturn(new int[] {1, 1});
                        return 2 == returningWork.execute(connection);
                      } catch (SQLException sqlException) {
                        sqlException.printStackTrace();
                      }
                      return false;
                    })))
        .thenReturn(2);
    assertEquals(2, batchStatementExecutor.executeBatchStatement(callback));
  }

  @Test
  void shouldReturnRecordCountWhenExecutingAuditStatement() {
    RevInfo revInfo = new RevInfo();
    revInfo.setUserName("test");
    revInfo.setId(1L);
    revInfo.setTimestamp(Date.from(Instant.now()));

    BatchAuditStatementCallback callback = mock(BatchAuditStatementCallback.class);
    when(callback.getEntity()).thenReturn(DoohScreen.class);
    try (MockedStatic<AuditReaderFactory> auditReader =
        Mockito.mockStatic(AuditReaderFactory.class)) {
      AuditReader auditReader1 = mock(AuditReader.class);
      auditReader.when(() -> AuditReaderFactory.get(entityManager)).thenReturn(auditReader1);
      Answer<RevInfo> answer = invocationOnMock -> revInfo;
      when(auditReader1.getCurrentRevision(DoohScreen.class, true)).thenAnswer(answer);
      when(entityManager.unwrap(Session.class)).thenReturn(session);
      when(session.doReturningWork(
              argThat(
                  (ArgumentMatcher<ReturningWork<Integer>>)
                      returningWork -> {
                        var sql = "select * from table_aud";
                        when(callback.getAuditSql()).thenReturn(sql);
                        try {
                          when(connection.prepareStatement(sql)).thenReturn(preparedStatement);
                          when(preparedStatement.executeUpdate()).thenReturn(2);
                          return 2 == returningWork.execute(connection);
                        } catch (SQLException sqlException) {
                          sqlException.printStackTrace();
                        }
                        return false;
                      })))
          .thenReturn(2);

      assertEquals(2, batchStatementExecutor.executeAuditStatement(callback));
    }
  }

  @Test
  void shouldExecuteInBatchesWhenExecuteBatchStatement() {
    BatchStatementCallback callback = mock(BatchStatementCallback.class);
    when(entityManager.unwrap((Session.class))).thenReturn(session);
    when(session.doReturningWork(
            argThat(
                (ArgumentMatcher<ReturningWork<Integer>>)
                    returningWork -> {
                      var sql = "select * from table";
                      when(callback.getBatchSize()).thenReturn(2);
                      when(callback.getSql()).thenReturn(sql);
                      when(callback.getEntities())
                          .thenReturn(
                              List.of(
                                  new Object(),
                                  new Object(),
                                  new Object(),
                                  new Object(),
                                  new Object()));
                      try {
                        when(connection.prepareStatement(sql)).thenReturn(preparedStatement);
                        when(preparedStatement.executeBatch())
                            .thenReturn(new int[] {1, 1})
                            .thenReturn(new int[] {1, 1})
                            .thenReturn(new int[] {1});
                        int retVal = returningWork.execute(connection);
                        //              verify(preparedStatement, times(3)).executeBatch();
                        return 5 == retVal;
                      } catch (SQLException sqlException) {
                        sqlException.printStackTrace();
                      }
                      return false;
                    })))
        .thenReturn(5);
    assertEquals(5, batchStatementExecutor.executeBatchStatement(callback));
  }
}
