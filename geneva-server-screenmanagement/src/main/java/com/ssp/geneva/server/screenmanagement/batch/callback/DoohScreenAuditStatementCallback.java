package com.ssp.geneva.server.screenmanagement.batch.callback;

import com.nexage.admin.core.audit.model.RevInfo;
import com.nexage.admin.core.batch.BatchColumn;
import com.nexage.admin.core.batch.callback.BatchAuditStatementCallback;
import com.nexage.admin.core.batch.callback.CallbackStatementSetter;
import com.nexage.admin.core.model.DoohScreen;
import com.nexage.admin.core.model.DoohScreenBatch;
import java.sql.PreparedStatement;
import java.util.stream.Collectors;

public class DoohScreenAuditStatementCallback implements BatchAuditStatementCallback<DoohScreen> {

  private final int revType;
  private final Long sellerPid;

  public DoohScreenAuditStatementCallback(int revType, Long sellerPid) {
    this.revType = revType;
    this.sellerPid = sellerPid;
  }

  @Override
  public String getAuditSql() {
    var columnNames =
        DoohScreenBatch.COLUMNS.stream().map(BatchColumn::getName).collect(Collectors.joining(","));

    return String.format(
        "INSERT INTO dooh_screen_aud (REV, REVTYPE, created_on, pid, %1$s) SELECT ? AS REV, %2$d AS REVTYPE, created_on, pid, %1$s FROM dooh_screen WHERE seller_pid = ?",
        columnNames, revType);
  }

  @Override
  public void setAuditValues(PreparedStatement preparedStatement, RevInfo nexageRevEntity) {
    CallbackStatementSetter.<RevInfo>checkSqlEx(
            (ps, revInfo) -> {
              ps.setLong(1, revInfo.getId());
              ps.setLong(2, this.sellerPid);
            })
        .accept(preparedStatement, nexageRevEntity);
  }

  @Override
  public Class<DoohScreen> getEntity() {
    return DoohScreen.class;
  }
}
