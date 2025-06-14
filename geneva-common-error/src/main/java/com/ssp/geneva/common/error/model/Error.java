package com.ssp.geneva.common.error.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.common.base.Throwables;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;

@Log4j2
@JsonInclude(Include.NON_NULL)
@Getter
@Setter
@Builder
@AllArgsConstructor
public class Error implements Serializable {

  @JsonIgnore private final HttpStatus httpStatus;
  private final int httpResponse;
  private final int errorCode;
  private String errorMessage;
  private String errorTrace = null;
  private final String guid = UUID.randomUUID().toString();

  public Error(
      HttpStatus status, int code, String message, Throwable throwable, boolean stackTraceOn) {
    httpStatus = Objects.requireNonNullElse(status, HttpStatus.INTERNAL_SERVER_ERROR);
    httpResponse = httpStatus.value();
    errorCode = code;
    errorMessage = message;
    if (throwable != null) {
      errorTrace = stackTraceOn ? Throwables.getStackTraceAsString(throwable) : null;
    }
    log.error(this.toString());
  }

  public Error(HttpStatus status, int code, String message) {
    httpStatus = Objects.requireNonNullElse(status, HttpStatus.INTERNAL_SERVER_ERROR);
    httpResponse = httpStatus.value();
    errorCode = code;
    errorMessage = message;
    log.error(this.toString());
  }

  public Error(ErrorCode genevaErrorCode) {
    httpStatus = genevaErrorCode.getHttpStatus();
    httpResponse = httpStatus.value();
    errorCode = genevaErrorCode.getCode();
  }

  public static class ErrorBuilder {
    public Error build() {
      return new Error(this.httpStatus, this.errorCode, this.errorMessage);
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + errorCode;
    result = prime * result + ((errorMessage == null) ? 0 : errorMessage.hashCode());
    result = prime * result + httpResponse;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    Error other = (Error) obj;
    if (errorCode != other.errorCode) return false;
    if (errorMessage == null) {
      if (other.errorMessage != null) return false;
    } else if (!errorMessage.equals(other.errorMessage)) return false;
    if (httpResponse != other.httpResponse) return false;
    return true;
  }

  @Override
  public String toString() {
    return "Error [httpResponse="
        + httpResponse
        + ", errorCode="
        + errorCode
        + ", guid="
        + guid
        + ", errorMessage="
        + errorMessage
        + "]";
  }
}
