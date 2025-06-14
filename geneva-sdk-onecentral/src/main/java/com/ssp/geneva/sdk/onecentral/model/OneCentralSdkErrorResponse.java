package com.ssp.geneva.sdk.onecentral.model;

import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@Builder
@AllArgsConstructor
public class OneCentralSdkErrorResponse implements Serializable {
  private final List<OneCentralErrorResponseBody> oneCentralErrors;
  private final HttpStatus httpStatus;

  /**
   * Message format:
   *
   * <p>{ "code": 400, "message": "Email cannot be empty", "detail": "Email cannot be empty" }
   */
  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class OneCentralErrorResponseBody implements Serializable {
    private Integer code;
    private String message;
    private String detail;
  }
}
