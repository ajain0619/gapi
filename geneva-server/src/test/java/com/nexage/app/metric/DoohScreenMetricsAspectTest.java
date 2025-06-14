package com.nexage.app.metric;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.nexage.app.error.EntityConstraintViolationException;
import com.nexage.app.error.ServerErrorCodes;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class DoohScreenMetricsAspectTest {

  @Mock private DoohScreenMetrics doohScreenMetrics;
  private DoohScreenMetricsAspect doohScreenMetricsAspect;

  @BeforeEach
  public void setUp() {
    doohScreenMetricsAspect = new DoohScreenMetricsAspect(doohScreenMetrics);
  }

  @Test
  void shouldIncrementUploadFailureMetricWhenGenevaValidationExceptionThrown() {

    var sellerPid = 123L;
    MultipartFile screens = mock(MultipartFile.class);
    var genevaValidationException =
        new GenevaValidationException(ServerErrorCodes.SERVER_DOOH_SCREENS_MAX_LIMIT);

    doohScreenMetricsAspect.recordFileUploadError(123L, screens, genevaValidationException);

    verify(doohScreenMetrics).incrementFileUploadError(sellerPid);
    verify(doohScreenMetrics, never()).incrementFileUploadSuccess(anyLong());
    verify(doohScreenMetrics, never()).recordCreatedScreenCount(anyInt(), anyLong());
  }

  @Test
  void shouldIncrementUploadFailureMetricWhenEntityConstraintViolationExceptionThrown() {

    var sellerPid = 123L;
    MultipartFile screens = mock(MultipartFile.class);
    var entityConstraintViolation = new EntityConstraintViolationException(Collections.emptySet());

    doohScreenMetricsAspect.recordFileUploadError(123L, screens, entityConstraintViolation);

    verify(doohScreenMetrics).incrementFileUploadError(sellerPid);
    verify(doohScreenMetrics, never()).incrementFileUploadSuccess(anyLong());
    verify(doohScreenMetrics, never()).recordCreatedScreenCount(anyInt(), anyLong());
  }

  @Test
  void shouldIncrementUploadSuccessMetricWhenFileUploadSuccess() {

    var sellerPid = 123L;
    MultipartFile screens = mock(MultipartFile.class);
    var numberOfScreens = 1216;

    doohScreenMetricsAspect.recordFileUploadSuccess(123L, screens, numberOfScreens);

    verify(doohScreenMetrics).incrementFileUploadSuccess(sellerPid);
    verify(doohScreenMetrics, never()).incrementFileUploadError(anyLong());
  }

  @Test
  void shouldRecordCreatedScreenCountMetricWhenFileUploadSuccess() {

    var sellerPid = 123L;
    MultipartFile screens = mock(MultipartFile.class);
    var numberOfScreens = 1216;

    doohScreenMetricsAspect.recordFileUploadSuccess(123L, screens, numberOfScreens);

    verify(doohScreenMetrics).recordCreatedScreenCount(numberOfScreens, sellerPid);
    verify(doohScreenMetrics, never()).incrementFileUploadError(anyLong());
  }
}
