package com.nexage.admin.core.batch.callback;

import com.nexage.admin.core.audit.model.RevInfo;
import java.sql.PreparedStatement;

public interface BatchAuditStatementCallback<T> {

  /**
   * SQL to update a *_aud table
   *
   * @return {@link String}
   */
  String getAuditSql();

  /**
   * Set the PreparedStatement with necessary values
   *
   * @param preparedStatement {@link PreparedStatement}
   * @param revInfo {@link RevInfo} to retrieve revision info
   */
  void setAuditValues(PreparedStatement preparedStatement, RevInfo revInfo);

  /** Get the required data type for auditing */
  Class<T> getEntity();
}
