package com.nexage.app.metric;

import lombok.extern.log4j.Log4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Aspect
@Component
@Log4j
public class DoohScreenMetricsAspect {

  private final DoohScreenMetrics doohScreenMetrics;

  public DoohScreenMetricsAspect(DoohScreenMetrics doohScreenMetrics) {
    this.doohScreenMetrics = doohScreenMetrics;
  }

  @AfterThrowing(
      pointcut = "@annotation(FileUploadErrorMetered) && args(sellerPid,screens)",
      throwing = "exception",
      argNames = "sellerPid,screens,exception")
  public void recordFileUploadError(Long sellerPid, MultipartFile screens, Exception exception) {
    doohScreenMetrics.incrementFileUploadError(sellerPid);
    log.info(
        "Metric error recorded for sellerPid [%d] uploading dooh screens file"
            .formatted(sellerPid));
  }

  @AfterReturning(
      pointcut = "@annotation(FileUploadSuccessMetered) && args(sellerPid,screens)",
      argNames = "sellerPid,screens,numberOfScreens",
      returning = "numberOfScreens")
  public void recordFileUploadSuccess(Long sellerPid, MultipartFile screens, int numberOfScreens) {
    doohScreenMetrics.incrementFileUploadSuccess(sellerPid);
    doohScreenMetrics.recordCreatedScreenCount(numberOfScreens, sellerPid);
    log.info(
        "Metric success recorded for sellerPid [%d] and screen count [%d] uploading dooh screens file"
            .formatted(sellerPid, numberOfScreens));
  }
}
