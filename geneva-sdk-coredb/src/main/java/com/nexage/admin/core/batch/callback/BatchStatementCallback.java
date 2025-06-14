package com.nexage.admin.core.batch.callback;

import com.nexage.admin.core.batch.executor.BatchStatementExecutor;
import java.sql.PreparedStatement;
import java.util.List;

/**
 * Defines methods for callback implementations of {@link BatchStatementExecutor}.
 *
 * @param <T> type of entity object
 */
public interface BatchStatementCallback<T, U> {

  /**
   * Native SQL to be run.
   *
   * @return SQL string
   */
  String getSql();

  /**
   * List of entities to be used in batch statement
   *
   * @return {@link List} entities
   */
  List<T> getEntities();

  /**
   * Sets the values given an entity and PreparedStatement.
   *
   * @param preparedStatement
   * @param entity
   */
  void setValues(PreparedStatement preparedStatement, T entity);

  /**
   * Get the max number of records that should be updated in each batch sql call
   *
   * @return {@link Integer} batchSize
   */
  int getBatchSize();
  /**
   * Get number of the resultant rows count affected by execution of this class
   *
   * @param u An arbitrary object that accept during execution
   * @return {@link Integer} rows affected
   */
  int execute(U u);
}
