package com.nexage.admin.core.batch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.audit.model.RevInfo;
import com.nexage.admin.core.batch.callback.BatchAuditStatementCallback;
import com.nexage.admin.core.batch.callback.BatchStatementCallback;
import com.nexage.admin.core.batch.executor.BatchStatementExecutor;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRES_NEW)
@Sql(
    scripts = "/data/repository/batch-statement-executor.sql",
    config = @SqlConfig(encoding = "utf-8"))
class BatchStatementExecutorIT extends CoreDbSdkIntegrationTestBase {

  @Autowired private BatchStatementExecutor batchStatementExecutor;
  @PersistenceContext private EntityManager entityManager;

  @Test
  @Transactional
  void shouldBatchInsert() {

    assertEquals(
        2,
        batchStatementExecutor.executeBatchStatement(
            new BatchExecutorDummyCallback(
                List.of(new BatchExecutorDummy(1L, "test"), new BatchExecutorDummy(2L, "again")))));
    assertEquals(
        1, batchStatementExecutor.executeAuditStatement(new BatchAuditExecutorDummyCallback()));
  }

  class BatchExecutorDummyCallback implements BatchStatementCallback<BatchExecutorDummy, Long> {

    List<BatchExecutorDummy> entities;

    public BatchExecutorDummyCallback(List<BatchExecutorDummy> entities) {
      this.entities = entities;
    }

    @Override
    public String getSql() {
      return "INSERT INTO batch_executor_dummy VALUES (?, ?)";
    }

    @Override
    public List<BatchExecutorDummy> getEntities() {
      return entities;
    }

    @Override
    public void setValues(PreparedStatement preparedStatement, BatchExecutorDummy entity) {
      try {
        preparedStatement.setLong(1, entity.id);
        preparedStatement.setString(2, entity.value);
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
    }

    @Override
    public int getBatchSize() {
      return 50;
    }

    @Override
    public int execute(Long o) {
      return 0;
    }
  }

  class BatchAuditExecutorDummyCallback implements BatchAuditStatementCallback<BatchExecutorDummy> {

    public BatchAuditExecutorDummyCallback() {}

    @Override
    public String getAuditSql() {
      return "INSERT INTO batch_executor_dummy_aud VALUES (?, ?,?,?,?)";
    }

    @Override
    public void setAuditValues(PreparedStatement preparedStatement, RevInfo revInfo) {
      try {
        Date date = new Date(0);
        date.setTime(System.currentTimeMillis());
        preparedStatement.setLong(1, revInfo.getId());
        preparedStatement.setString(2, "test");
        preparedStatement.setDate(3, date);
        preparedStatement.setInt(4, 1);
        preparedStatement.setShort(5, (short) 0);
        assertNotEquals(revInfo.hashCode(), new RevInfo().hashCode());
        assertNotEquals(new RevInfo(), revInfo);
        assertTrue(revInfo.toString().contains("RevInfo(id = 1"));
        assertEquals("SYSTEM_PROCESS", revInfo.getUserName());
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
    }

    @Override
    public Class<BatchExecutorDummy> getEntity() {
      return BatchExecutorDummy.class;
    }
  }

  class BatchExecutorDummy {
    public Long id;
    public String value;

    public BatchExecutorDummy(Long id, String value) {
      this.id = id;
      this.value = value;
    }
  }
}
