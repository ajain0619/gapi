package com.nexage.admin.core.batch;

import java.sql.PreparedStatement;
import java.util.function.BiConsumer;
import lombok.Getter;

@Getter
public class BatchColumn<T> {

  private final String name;
  private final BiConsumer<PreparedStatement, T> sqlStatementSetter;

  /**
   * Used for creating the column definitions for inserting batch records into a table
   *
   * @param name Name of the Column in the database table
   * @param sqlStatementSetter {@link BiConsumer<PreparedStatement, T>} Use PreparedStatement to set
   *     the column value of your entity T.
   */
  public BatchColumn(String name, BiConsumer<PreparedStatement, T> sqlStatementSetter) {
    this.name = name;
    this.sqlStatementSetter = sqlStatementSetter;
  }
}
