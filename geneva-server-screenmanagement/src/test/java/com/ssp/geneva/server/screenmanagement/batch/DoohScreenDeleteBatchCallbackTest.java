package com.ssp.geneva.server.screenmanagement.batch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.batch.callback.BatchStatementCallback;
import com.nexage.admin.core.batch.executor.BatchStatementExecutor;
import com.ssp.geneva.server.screenmanagement.batch.callback.DoohScreenDeleteBatchCallback;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DoohScreenDeleteBatchCallbackTest {

  @Mock private BatchStatementExecutor batchStatementExecutor;

  private DoohScreenDeleteBatchCallback doohScreenDeleteBatchCallback;

  @BeforeEach
  void setUp() {
    doohScreenDeleteBatchCallback = new DoohScreenDeleteBatchCallback(batchStatementExecutor);
  }

  @Test
  void shouldReturnRecordsDeletedWhenBatchDelete() {
    when(batchStatementExecutor.executeBatchStatement(any(BatchStatementCallback.class)))
        .thenReturn(2);
    assertEquals(2, doohScreenDeleteBatchCallback.execute(123L));
  }
}
