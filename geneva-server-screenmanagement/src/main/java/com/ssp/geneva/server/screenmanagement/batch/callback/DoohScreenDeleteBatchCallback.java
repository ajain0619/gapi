package com.ssp.geneva.server.screenmanagement.batch.callback;

import com.nexage.admin.core.batch.callback.BatchStatementCallback;
import com.nexage.admin.core.batch.callback.CallbackStatementSetter;
import com.nexage.admin.core.batch.executor.BatchStatementExecutor;
import com.nexage.admin.core.model.DoohScreen;
import java.sql.PreparedStatement;
import java.util.List;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;

@Log4j
@Component
public class DoohScreenDeleteBatchCallback implements BatchStatementCallback<DoohScreen, Long> {

  private static final int BATCH_SIZE = 1;
  private final DoohScreen doohScreen = new DoohScreen();

  private final BatchStatementExecutor batchStatementExecutor;

  public DoohScreenDeleteBatchCallback(BatchStatementExecutor batchStatementExecutor) {
    this.batchStatementExecutor = batchStatementExecutor;
  }

  @Override
  public String getSql() {
    return "DELETE FROM dooh_screen where seller_pid = ?";
  }

  @Override
  public List<DoohScreen> getEntities() {
    return List.of(doohScreen);
  }

  @Override
  public void setValues(PreparedStatement preparedStatement, DoohScreen doohScreen) {
    CallbackStatementSetter.<DoohScreen>checkSqlEx(
            (ps, screen) -> ps.setLong(1, screen.getSellerPid()))
        .accept(preparedStatement, doohScreen);
  }

  @Override
  public int getBatchSize() {
    return BATCH_SIZE;
  }

  @Override
  public int execute(Long sellerPid) {
    doohScreen.setSellerPid(sellerPid);
    var rows = this.batchStatementExecutor.executeBatchStatement(this);
    this.batchStatementExecutor.executeAuditStatement(
        new DoohScreenAuditStatementCallback(2, sellerPid));
    return rows;
  }
}
