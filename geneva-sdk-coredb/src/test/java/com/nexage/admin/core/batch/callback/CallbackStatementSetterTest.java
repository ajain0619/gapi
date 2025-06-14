package com.nexage.admin.core.batch.callback;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import com.nexage.admin.core.batch.CheckedBiConsumer;
import com.nexage.admin.core.error.CoreDBErrorCodes;
import com.nexage.admin.core.model.DoohScreen;
import com.ssp.geneva.common.error.exception.GenevaDatabaseException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.function.BiConsumer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CallbackStatementSetterTest {

  @Test
  void shouldThrowExceptionWhenCallingPreparedStatement() throws Exception {
    var preparedStatement = mock(PreparedStatement.class);

    CheckedBiConsumer<PreparedStatement, DoohScreen, SQLException> preparedStatementSetter =
        (ps, screen) -> ps.setLong(0, 0L);
    BiConsumer<PreparedStatement, DoohScreen> consumer =
        CallbackStatementSetter.checkSqlEx(preparedStatementSetter);

    doThrow(new SQLException()).when(preparedStatement).setLong(0, 0L);
    var doohScreen = new DoohScreen();
    GenevaDatabaseException exception =
        assertThrows(
            GenevaDatabaseException.class, () -> consumer.accept(preparedStatement, doohScreen));
    assertEquals(CoreDBErrorCodes.CORE_DB_INVALID_BATCH_STATEMENT, exception.getErrorCode());
  }
}
