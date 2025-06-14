package com.ssp.geneva.server.screenmanagement.batch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.batch.callback.BatchStatementCallback;
import com.nexage.admin.core.batch.executor.BatchStatementExecutor;
import com.nexage.admin.core.model.DoohScreen;
import com.ssp.geneva.server.screenmanagement.batch.callback.DoohScreenInsertBatchCallback;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DoohScreenInsertBatchCallbackTest {

  @Mock private BatchStatementExecutor batchStatementExecutor;
  private DoohScreenInsertBatchCallback doohScreenInsertBatchCallback;

  @BeforeEach
  void setUp() {
    doohScreenInsertBatchCallback = new DoohScreenInsertBatchCallback(batchStatementExecutor);
  }

  @Test
  void shouldReturnRecordsInsertedWhenBatchInsert() {
    when(batchStatementExecutor.executeBatchStatement(any(BatchStatementCallback.class)))
        .thenReturn(1);
    assertEquals(
        1, doohScreenInsertBatchCallback.setDoohScreens(List.of(new DoohScreen())).execute(123L));
  }
}
