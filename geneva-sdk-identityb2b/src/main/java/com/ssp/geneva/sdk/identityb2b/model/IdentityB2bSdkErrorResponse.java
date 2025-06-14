package com.ssp.geneva.sdk.identityb2b.model;

import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
public class IdentityB2bSdkErrorResponse implements Serializable {
  private final List<IdentityB2bSdkErrorResponseBody> identityB2bErrors;

  /**
   * Message format:
   *
   * <p>{ "code": 400, "message": "Bad Request", "detail": "Invalid Refresh Token" }
   */
  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class IdentityB2bSdkErrorResponseBody implements Serializable {
    private Integer code;
    private String message;
    private String detail;
  }
}
