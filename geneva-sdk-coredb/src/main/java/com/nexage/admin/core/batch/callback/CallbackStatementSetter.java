package com.nexage.admin.core.batch.callback;

import com.nexage.admin.core.batch.CheckedBiConsumer;
import com.nexage.admin.core.error.CoreDBErrorCodes;
import com.ssp.geneva.common.error.exception.GenevaDatabaseException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.function.BiConsumer;
import lombok.extern.log4j.Log4j;

@Log4j
public class CallbackStatementSetter {

  private CallbackStatementSetter() {}

  /**
   * Helper function to provide syntactic sugar to catch {@link SQLException} when setting the
   * {@link java.sql.PreparedStatement}
   *
   * @param preparedStatementSetter {@link CheckedBiConsumer}
   * @return {@link BiConsumer}
   */
  public static <T> BiConsumer<PreparedStatement, T> checkSqlEx(
      CheckedBiConsumer<PreparedStatement, T, SQLException> preparedStatementSetter) {
    return (preparedStatement, entity) -> {
      try {
        preparedStatementSetter.accept(preparedStatement, entity);
      } catch (SQLException sqlException) {
        log.error("Could not execute prepared statement", sqlException);
        throw new GenevaDatabaseException(CoreDBErrorCodes.CORE_DB_INVALID_BATCH_STATEMENT);
      }
    };
  }
}
