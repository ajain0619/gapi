package com.nexage.app.web;

import com.google.common.base.Throwables;
import com.nexage.admin.core.error.CoreDBErrorCodes;
import com.nexage.app.error.EntityConstraintViolationException;
import com.nexage.app.error.OneCentralError;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.error.ValidationError;
import com.ssp.geneva.common.error.exception.GenevaAppRuntimeException;
import com.ssp.geneva.common.error.exception.GenevaDatabaseException;
import com.ssp.geneva.common.error.exception.GenevaException;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.error.exception.logger.ExceptionLogger;
import com.ssp.geneva.common.error.handler.MessageHandler;
import com.ssp.geneva.common.error.model.CommonErrorCodes;
import com.ssp.geneva.common.error.model.Error;
import com.ssp.geneva.common.error.model.ErrorCode;
import com.ssp.geneva.common.security.error.GenevaSecurityException;
import com.ssp.geneva.common.security.error.SecurityErrorCodes;
import com.ssp.geneva.common.settings.util.SysConfigUtil;
import com.ssp.geneva.sdk.dv360.seller.exception.Dv360SellerSdkException;
import com.ssp.geneva.sdk.identityb2b.exception.IdentityB2bSdkException;
import com.ssp.geneva.sdk.onecentral.exception.OneCentralException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Collections;
import javax.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.hibernate.PropertyValueException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Log4j2
@ControllerAdvice
@RequiredArgsConstructor
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

  private final MessageHandler messageHandler;
  private final SysConfigUtil sysConfigUtil;
  private final ExceptionLogger exceptionLogger;

  /**
   * Handle {@link GenevaException} exceptions.
   *
   * @param ex {@link GenevaException}.
   * @return {@link ResponseEntity} of type {@link Error}
   */
  @ExceptionHandler({
    GenevaAppRuntimeException.class,
    GenevaDatabaseException.class,
    GenevaValidationException.class,
    GenevaSecurityException.class,
  })
  public final ResponseEntity<Error> handleGenevaExceptions(GenevaException ex) {
    exceptionLogger.logException(ex);
    return new ResponseEntity<>(getErrorResponse(ex), ex.getErrorCode().getHttpStatus());
  }

  /**
   * This method is used to get the error message from the error code passed in.
   *
   * @param code The Error code from {@link ErrorCode} that will turn into the corresponding error
   *     message
   * @return The error message that matches the passed in error code.
   */
  public String getErrorMessage(ErrorCode code) {
    return getErrorMessage(code.toString());
  }

  /**
   * This method is used to get the error message from the value of error code passed in.
   *
   * @param code The Error code from {@link String} that will turn into the corresponding error
   *     message
   * @return The error message that matches the passed in error code value.
   */
  public String getErrorMessage(String code) {
    return messageHandler.getMessage(code);
  }

  /**
   * Handle {@link ConstraintViolationException} exceptions.
   *
   * @param ex {@link ConstraintViolationException}.
   * @param request {@link WebRequest}.
   * @return {@link ResponseEntity} of type {@link Object}
   */
  @ExceptionHandler(value = {ConstraintViolationException.class})
  public ResponseEntity<Object> handleConstraintViolationException(
      ConstraintViolationException ex, WebRequest request) {
    exceptionLogger.logException(ex);
    var constraintViolation = ex.getConstraintViolations().iterator().next();
    for (CoreDBErrorCodes errorCode : CoreDBErrorCodes.getConstraintViolationErrorCodes()) {
      if (constraintViolation.getMessage().equals(errorCode.name())) {
        Error error =
            new Error(
                errorCode.getHttpStatus(),
                errorCode.getCode(),
                messageHandler.getMessage(errorCode.name()),
                ex,
                sysConfigUtil.getErrorTraceEnabled());
        return handleExceptionInternal(
            ex, error, new HttpHeaders(), error.getHttpStatus(), request);
      }
    }
    return handleEntityConstraintViolationException(
        new EntityConstraintViolationException(ex.getConstraintViolations()), request);
  }

  /**
   * Handle {@link DataIntegrityViolationException} exceptions.
   *
   * @param ex {@link DataIntegrityViolationException}.
   * @return {@link ResponseEntity} of type {@link Object}
   */
  @ExceptionHandler(value = {DataIntegrityViolationException.class})
  public ResponseEntity<Object> handleDataIntegrityViolationException(
      DataIntegrityViolationException ex, WebRequest webRequest) {
    exceptionLogger.logException(ex);

    if (ex.getCause() instanceof org.hibernate.exception.ConstraintViolationException cause) {
      return handleHibernateConstraintViolationException(cause, webRequest);
    } else if (ex.getCause() instanceof PropertyValueException cause) {
      return handlePropertyValueException(cause, webRequest);
    } else {
      return handleOtherExceptions(ex, webRequest);
    }
  }

  /**
   * Handle {@link AccessDeniedException} exceptions.
   *
   * @param ex {@link AccessDeniedException}.
   * @param request {@link WebRequest}.
   * @return {@link ResponseEntity} of type {@link Object}
   */
  @ExceptionHandler(value = {AccessDeniedException.class})
  public ResponseEntity<Object> handleAccessDeniedException(
      AccessDeniedException ex, WebRequest request) {
    var errorCode = SecurityErrorCodes.SECURITY_NOT_AUTHORIZED;
    Error error =
        new Error(
            errorCode.getHttpStatus(),
            errorCode.getCode(),
            messageHandler.getMessage(errorCode.name()),
            ex,
            sysConfigUtil.getErrorTraceEnabled());
    exceptionLogger.logException(ex);
    return handleExceptionInternal(ex, error, new HttpHeaders(), error.getHttpStatus(), request);
  }

  /**
   * Handle {@link ObjectOptimisticLockingFailureException} exceptions.
   *
   * @param ex {@link ObjectOptimisticLockingFailureException}.
   * @param request {@link WebRequest}.
   * @return {@link ResponseEntity} of type {@link Object}
   */
  @ExceptionHandler(value = {ObjectOptimisticLockingFailureException.class})
  public ResponseEntity<Object> handleStaleStateException(
      ObjectOptimisticLockingFailureException ex, WebRequest request) {
    var errorCode = CommonErrorCodes.COMMON_OPTIMISTIC_LOCK;
    Error error =
        new Error(
            errorCode.getHttpStatus(),
            errorCode.getCode(),
            messageHandler.getMessage(errorCode.name()),
            ex,
            sysConfigUtil.getErrorTraceEnabled());
    exceptionLogger.logException(ex);
    return handleExceptionInternal(ex, error, new HttpHeaders(), error.getHttpStatus(), request);
  }

  /**
   * Handle {@link SQLIntegrityConstraintViolationException} exceptions.
   *
   * @param ex {@link SQLIntegrityConstraintViolationException}.
   * @param request {@link WebRequest}.
   * @return {@link ResponseEntity} of type {@link Object}
   */
  @ExceptionHandler(value = {SQLIntegrityConstraintViolationException.class})
  public ResponseEntity<Object> handleMySQLIntegrityConstraintViolationException(
      SQLIntegrityConstraintViolationException ex, WebRequest request) {
    var errorCode = ServerErrorCodes.SERVER_CONSTRAINT_VIOLATION;
    Error error =
        new Error(
            errorCode.getHttpStatus(),
            errorCode.getCode(),
            ex.getLocalizedMessage(),
            ex,
            sysConfigUtil.getErrorTraceEnabled());
    exceptionLogger.logException(ex);
    return handleExceptionInternal(ex, error, new HttpHeaders(), error.getHttpStatus(), request);
  }

  /**
   * Handle {@link AuthenticationCredentialsNotFoundException} exceptions.
   *
   * @param ex {@link AuthenticationCredentialsNotFoundException}.
   * @param request {@link WebRequest}.
   * @return {@link ResponseEntity} of type {@link Object}
   */
  @ExceptionHandler(value = {AuthenticationCredentialsNotFoundException.class})
  public ResponseEntity<Object> handleAuthenticationCredentialsNotFoundException(
      AuthenticationCredentialsNotFoundException ex, WebRequest request) {
    var errorCode = SecurityErrorCodes.SECURITY_NOT_AUTHORIZED;
    Error error =
        new Error(
            errorCode.getHttpStatus(),
            errorCode.getCode(),
            messageHandler.getMessage(errorCode.name()),
            ex,
            sysConfigUtil.getErrorTraceEnabled());
    exceptionLogger.logException(ex);
    return handleExceptionInternal(ex, error, new HttpHeaders(), error.getHttpStatus(), request);
  }

  /**
   * Handle {@link EntityConstraintViolationException} exceptions.
   *
   * @param ex {@link EntityConstraintViolationException}.
   * @param request {@link WebRequest}.
   * @return {@link ResponseEntity} of type {@link Object}
   */
  @ExceptionHandler(EntityConstraintViolationException.class)
  public ResponseEntity<Object> handleEntityConstraintViolationException(
      EntityConstraintViolationException ex, WebRequest request) {
    var errorCode = CommonErrorCodes.COMMON_BAD_REQUEST;
    Error error =
        new ValidationError(
            errorCode.getHttpStatus(),
            errorCode.getCode(),
            messageHandler.getMessage(errorCode.name()),
            ex);
    exceptionLogger.logException(ex);
    return handleExceptionInternal(
        ex, error, new HttpHeaders(), errorCode.getHttpStatus(), request);
  }

  /**
   * Handle {@link OneCentralException} exceptions.
   *
   * @param ex {@link OneCentralException}.
   * @return {@link ResponseEntity} of type {@link Object}
   */
  @ExceptionHandler(OneCentralException.class)
  public ResponseEntity<OneCentralError> handleOneCentralSdkException(OneCentralException ex) {
    var errorCode = ex.getErrorCode();
    var oneCentralErrorMessage = ex.getOneCentralErrorResponse();
    var oneCentralError =
        new OneCentralError(
            errorCode.getHttpStatus(),
            errorCode.getCode(),
            messageHandler.getMessage(errorCode.toString()),
            ex,
            oneCentralErrorMessage == null
                ? Collections.emptyList()
                : oneCentralErrorMessage.getOneCentralErrors(),
            sysConfigUtil.getErrorTraceEnabled());
    exceptionLogger.logException(ex);
    return new ResponseEntity<>(oneCentralError, errorCode.getHttpStatus());
  }

  /**
   * Handle {@link Dv360SellerSdkException} exceptions.
   *
   * @param ex {@link Dv360SellerSdkException}.
   * @return {@link ResponseEntity} of type {@link Object}
   */
  @ExceptionHandler(Dv360SellerSdkException.class)
  public ResponseEntity<Error> handleDv360SellerSdkException(Dv360SellerSdkException ex) {
    exceptionLogger.logException(ex);
    var errorCode = ex.getErrorCode();
    var httpStatus = ex.getHttpStatus() != null ? ex.getHttpStatus() : errorCode.getHttpStatus();
    var errorResponse =
        new Error(
            httpStatus,
            errorCode.getCode(),
            messageHandler.getMessage(errorCode.toString(), ex.getMessageParams()),
            ex,
            sysConfigUtil.getErrorTraceEnabled());
    return new ResponseEntity<>(errorResponse, httpStatus);
  }

  /**
   * Handles {@link IdentityB2bSdkException} exceptions.
   *
   * @param exception the {@link IdentityB2bSdkException}.
   * @return a {@link ResponseEntity} of type {@link Error}.
   */
  @ExceptionHandler(IdentityB2bSdkException.class)
  public ResponseEntity<Error> handleIdentityb2bException(IdentityB2bSdkException exception) {
    exceptionLogger.logException(exception);
    var errorCode = exception.getErrorCode();
    var error =
        new Error(
            errorCode.getHttpStatus(),
            errorCode.getCode(),
            messageHandler.getMessage(errorCode.toString()),
            exception,
            sysConfigUtil.getErrorTraceEnabled());
    return new ResponseEntity<>(error, errorCode.getHttpStatus());
  }

  /**
   * Handle any other uncaptured {@link Exception} exceptions.
   *
   * @param ex {@link Exception}.
   * @param request {@link WebRequest}.
   * @return {@link ResponseEntity} of type {@link Object}
   */
  @ExceptionHandler(value = {Exception.class})
  public ResponseEntity<Object> handleOtherExceptions(Exception ex, WebRequest request) {
    var errorCode = CommonErrorCodes.COMMON_UNKNOWN;
    Error error =
        new Error(
            errorCode.getHttpStatus(),
            errorCode.getCode(),
            ex.getLocalizedMessage(),
            ex,
            sysConfigUtil.getErrorTraceEnabled());
    exceptionLogger.logException(ex);
    return handleExceptionInternal(ex, error, new HttpHeaders(), error.getHttpStatus(), request);
  }

  /** {@inheritDoc} */
  @Override
  protected ResponseEntity<Object> handleHttpMessageNotReadable(
      HttpMessageNotReadableException ex,
      HttpHeaders headers,
      HttpStatus status,
      WebRequest request) {
    var errorCode = CommonErrorCodes.COMMON_BAD_REQUEST;
    Error error =
        new Error(
            errorCode.getHttpStatus(),
            errorCode.getCode(),
            messageHandler.getMessage(errorCode.name()),
            ex,
            sysConfigUtil.getErrorTraceEnabled());
    exceptionLogger.logException(ex);
    return handleExceptionInternal(ex, error, new HttpHeaders(), error.getHttpStatus(), request);
  }

  /** {@inheritDoc} */
  @Override
  protected ResponseEntity<Object> handleHttpMessageNotWritable(
      HttpMessageNotWritableException ex,
      HttpHeaders headers,
      HttpStatus status,
      WebRequest request) {
    var errorCode = CommonErrorCodes.COMMON_RESPONSE_WRITE_FAILED;
    Error error =
        new Error(
            errorCode.getHttpStatus(),
            errorCode.getCode(),
            messageHandler.getMessage(errorCode.name()),
            ex,
            sysConfigUtil.getErrorTraceEnabled());
    exceptionLogger.logException(ex);
    return handleExceptionInternal(ex, error, new HttpHeaders(), error.getHttpStatus(), request);
  }

  /** {@inheritDoc} */
  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      HttpHeaders headers,
      HttpStatus status,
      WebRequest request) {
    log.error("REST API CALL exception ", ex);
    var validationException = new GenevaValidationException(ex.getBindingResult());
    Error error =
        new ValidationError(
            validationException.getErrorCode().getHttpStatus(),
            validationException.getErrorCode().getCode(),
            messageHandler.getMessage(validationException.getErrorCode().toString()),
            validationException);
    exceptionLogger.logException(ex);
    return handleExceptionInternal(
        ex, error, new HttpHeaders(), validationException.getErrorCode().getHttpStatus(), request);
  }

  /**
   * Handle {@link org.hibernate.exception.ConstraintViolationException} exceptions.
   *
   * @param ex {@link org.hibernate.exception.ConstraintViolationException}.
   * @return {@link ResponseEntity} of type {@link Object}
   */
  private ResponseEntity<Object> handleHibernateConstraintViolationException(
      org.hibernate.exception.ConstraintViolationException ex, WebRequest request) {
    exceptionLogger.logException(ex);

    ErrorCode errorCode;
    switch (ex.getConstraintName()) {
      case "unique_name_fee_adjustment":
        errorCode = ServerErrorCodes.SERVER_FEE_ADJUSTMENT_NAME_NOT_UNIQUE;
        break;
      case "discount_name":
        errorCode = ServerErrorCodes.SERVER_POST_AUCTION_DISCOUNT_NAME_NOT_UNIQUE;
        break;
      default:
        errorCode = CommonErrorCodes.COMMON_UNKNOWN;
        break;
    }

    Error error =
        new Error(
            errorCode.getHttpStatus(),
            errorCode.getCode(),
            messageHandler.getMessage(errorCode.toString()),
            ex,
            sysConfigUtil.getErrorTraceEnabled());

    return handleExceptionInternal(ex, error, new HttpHeaders(), error.getHttpStatus(), request);
  }

  /**
   * Handle {@link PropertyValueException} exceptions. Execution should never end here, it's
   * preferred to use validation annotations like {@link javax.validation.constraints.NotNull} on
   * our entities if there is no DTO associated, to avoid sending the request to the DB.
   *
   * @param ex {@link PropertyValueException}.
   * @return {@link ResponseEntity} of type {@link Object}
   */
  private ResponseEntity<Object> handlePropertyValueException(
      PropertyValueException ex, WebRequest request) {
    exceptionLogger.logException(ex);

    CommonErrorCodes errorCode = CommonErrorCodes.COMMON_BAD_REQUEST;
    Error error =
        new ValidationError(
            errorCode.getHttpStatus(),
            errorCode.getCode(),
            messageHandler.getMessage(errorCode.name()),
            ex);

    return handleExceptionInternal(
        ex, error, new HttpHeaders(), errorCode.getHttpStatus(), request);
  }

  private Error getErrorResponse(GenevaException ex) {
    var error = ex.getError();
    error.setErrorMessage(
        messageHandler.getMessage(ex.getErrorCode().toString(), ex.getMessageParams()));
    if (Boolean.TRUE.equals(sysConfigUtil.getErrorTraceEnabled())) {
      error.setErrorTrace(Throwables.getStackTraceAsString(ex));
    }
    return error;
  }
}
