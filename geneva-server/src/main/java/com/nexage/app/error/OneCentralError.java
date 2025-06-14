package com.nexage.app.error;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ssp.geneva.common.error.model.Error;
import com.ssp.geneva.sdk.onecentral.model.OneCentralSdkErrorResponse.OneCentralErrorResponseBody;
import java.util.List;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class OneCentralError extends Error {

  @JsonProperty private final List<OneCentralErrorResponseBody> errors;

  public OneCentralError(
      HttpStatus status,
      int code,
      String message,
      Throwable throwable,
      List<OneCentralErrorResponseBody> oneCentralSdkErrors,
      boolean stackTraceOn) {
    super(status, code, message, throwable, false);
    this.errors = oneCentralSdkErrors;
  }
}
