package com.ssp.geneva.server.screenmanagement.batch.callback;

import com.nexage.admin.core.batch.BatchColumn;
import com.nexage.admin.core.batch.callback.BatchStatementCallback;
import com.nexage.admin.core.batch.executor.BatchStatementExecutor;
import com.nexage.admin.core.model.DoohScreen;
import com.nexage.admin.core.model.DoohScreenBatch;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Log4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DoohScreenInsertBatchCallback implements BatchStatementCallback<DoohScreen, Long> {

  private static final int BATCH_SIZE = 5000;

  private List<DoohScreen> doohScreens;
  private final BatchStatementExecutor batchStatementExecutor;

  @Autowired
  public DoohScreenInsertBatchCallback(BatchStatementExecutor batchStatementExecutor) {
    this.batchStatementExecutor = batchStatementExecutor;
  }

  public DoohScreenInsertBatchCallback setDoohScreens(@NonNull List<DoohScreen> doohScreens) {
    this.doohScreens = doohScreens;
    return this;
  }

  @Override
  public String getSql() {
    return DoohScreenBatch.COLUMNS.stream()
        .map(BatchColumn::getName)
        .collect(Collectors.joining(",", "INSERT INTO dooh_screen (created_on, ", ")"))
        .concat(
            DoohScreenBatch.COLUMNS.stream()
                .map(s -> "?")
                .collect(Collectors.joining(",", " VALUES (current_timestamp(), ", ")")));
  }

  @Override
  public List<DoohScreen> getEntities() {
    return this.doohScreens;
  }

  @Override
  public void setValues(PreparedStatement ps, DoohScreen doohScreen) {
    DoohScreenBatch.COLUMNS.stream()
        .map(BatchColumn::getSqlStatementSetter)
        .forEach(batchColumn -> batchColumn.accept(ps, doohScreen));
  }

  @Override
  public int getBatchSize() {
    return BATCH_SIZE;
  }

  @Override
  public int execute(Long sellerPid) {
    var rows = this.batchStatementExecutor.executeBatchStatement(this);
    this.batchStatementExecutor.executeAuditStatement(
        new DoohScreenAuditStatementCallback(0, sellerPid));

    return rows;
  }
}
