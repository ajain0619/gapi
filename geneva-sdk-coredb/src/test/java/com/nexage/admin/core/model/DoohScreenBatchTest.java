package com.nexage.admin.core.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.nexage.admin.core.batch.BatchColumn;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DoohScreenBatchTest {

  @Test
  void shouldCheckIfFloorExistsInColumnList() throws SQLException {
    var preparedStatement = mock(PreparedStatement.class);
    var doohScreen = new DoohScreen();
    doohScreen.setFloorPrice(BigDecimal.valueOf(3.5));
    doohScreen.setMaxAdDuration(5);

    List<BatchColumn<DoohScreen>> batchColumnList = DoohScreenBatch.COLUMNS;

    assertEquals(31, batchColumnList.size());

    batchColumnList.get(29).getSqlStatementSetter().accept(preparedStatement, doohScreen);
    verify(preparedStatement).setInt(30, 5);

    batchColumnList.get(30).getSqlStatementSetter().accept(preparedStatement, doohScreen);
    verify(preparedStatement).setBigDecimal(31, BigDecimal.valueOf(3.5));
  }
}
