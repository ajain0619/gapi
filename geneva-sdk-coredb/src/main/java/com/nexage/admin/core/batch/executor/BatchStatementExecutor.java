package com.nexage.admin.core.batch.executor;

import com.nexage.admin.core.audit.model.RevInfo;
import com.nexage.admin.core.batch.callback.BatchAuditStatementCallback;
import com.nexage.admin.core.batch.callback.BatchStatementCallback;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import javax.persistence.EntityManager;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;
import org.hibernate.envers.AuditReaderFactory;
import org.springframework.stereotype.Repository;

/**
 * Executes SQL statements in batch mode. SQL and parameters are provided through {@link
 * BatchStatementCallback} instance.
 */
@Repository
@Log4j2
public class BatchStatementExecutor {

  private final EntityManager entityManager;

  public BatchStatementExecutor(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  /**
   * Perform batch execution of SQL statement. Use {@link BatchStatementCallback} parameter to
   * specify details.
   *
   * @param callback {@link BatchStatementCallback}
   * @return number of updated rows
   */
  public <T, U> int executeBatchStatement(BatchStatementCallback<T, U> callback) {
    return entityManager
        .unwrap(Session.class)
        .doReturningWork(
            connection -> {
              var recordsUpdated = 0;
              var preparedStatement = connection.prepareStatement(callback.getSql());
              var recordsBatched = 0;
              for (T e : callback.getEntities()) {
                callback.setValues(preparedStatement, e);
                preparedStatement.addBatch();
                recordsBatched++;
                if (recordsBatched >= callback.getBatchSize()) {
                  recordsUpdated += commitBatch(preparedStatement);
                  recordsBatched = 0;
                }
              }
              if (recordsBatched > 0) {
                recordsUpdated += commitBatch(preparedStatement);
              }
              return recordsUpdated;
            });
  }

  /**
   * Perform audit execution. Retrieves the {@link RevInfo} to lookup revision of entity to be used
   * in audit sql
   *
   * @param callback {@link BatchAuditStatementCallback}
   * @return number of updated rows
   */
  public <T> int executeAuditStatement(BatchAuditStatementCallback<T> callback) {
    var revInfo =
        (RevInfo)
            AuditReaderFactory.get(entityManager).getCurrentRevision(callback.getEntity(), true);
    return entityManager
        .unwrap(Session.class)
        .doReturningWork(
            connection -> {
              PreparedStatement pstmt = connection.prepareStatement(callback.getAuditSql());
              callback.setAuditValues(pstmt, revInfo);
              return pstmt.executeUpdate();
            });
  }

  private int commitBatch(PreparedStatement preparedStatement) throws SQLException {
    return Arrays.stream(preparedStatement.executeBatch()).sum();
  }
}
