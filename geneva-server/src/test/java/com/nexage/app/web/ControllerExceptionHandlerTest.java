package com.nexage.app.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.error.CoreDBErrorCodes;
import com.nexage.admin.core.model.User;
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
import com.ssp.geneva.common.security.error.GenevaSecurityException;
import com.ssp.geneva.common.security.error.SecurityErrorCodes;
import com.ssp.geneva.common.settings.util.SysConfigUtil;
import com.ssp.geneva.sdk.dv360.seller.error.Dv360SellerSdkErrorCodes;
import com.ssp.geneva.sdk.dv360.seller.exception.Dv360SellerSdkException;
import com.ssp.geneva.sdk.identityb2b.exception.IdentityB2bErrorCodes;
import com.ssp.geneva.sdk.identityb2b.exception.IdentityB2bSdkException;
import com.ssp.geneva.sdk.onecentral.error.OneCentralErrorCodes;
import com.ssp.geneva.sdk.onecentral.exception.OneCentralException;
import com.ssp.geneva.sdk.onecentral.model.OneCentralSdkErrorResponse;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import javax.validation.metadata.ConstraintDescriptor;
import org.hibernate.PropertyValueException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.context.request.WebRequest;

@ExtendWith(MockitoExtension.class)
class ControllerExceptionHandlerTest {

  @Mock private MessageHandler messageHandler;
  @Mock private SysConfigUtil sysConfig;
  @Mock private WebRequest webRequest;
  @Mock private ExceptionLogger exceptionLogger;
  @InjectMocks private ControllerExceptionHandler controllerExceptionHandler;

  @Test
  void shouldHandleConstraintViolationExceptionWithoutFieldErrors() {
    ConstraintViolation<User> constraintViolation =
        getConstraintViolationInstance(CoreDBErrorCodes.CORE_DB_DUPLICATE_EMAIL.name());
    ConstraintViolationException constraintViolationException =
        new ConstraintViolationException(Set.of(constraintViolation));
    when(messageHandler.getMessage(anyString())).thenReturn("duplicate email");
    ResponseEntity<Object> resp =
        controllerExceptionHandler.handleConstraintViolationException(
            constraintViolationException, webRequest);
    assertNotNull(resp);
    assertNotNull(resp.getBody());
    assertEquals("duplicate email", ((Error) resp.getBody()).getErrorMessage());
  }

  @Test
  void shouldHandleBadRequestExceptionWithoutParams() {
    var genevaValidationException =
        new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_FILE_TYPE);
    when(messageHandler.getMessage(genevaValidationException.getErrorCode().toString(), null))
        .thenReturn("Invalid file Type");
    var responseEntity =
        controllerExceptionHandler.handleGenevaExceptions(genevaValidationException);
    assertTrue(responseEntity.getBody().toString().contains("Invalid file Type"));
  }

  @Test
  void shouldHandleBadRequestExceptionWithParams() {
    Object[] params = new Object[] {1, "test"};
    var genevaValidationException =
        new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_ENTRIES, params);
    when(messageHandler.getMessage(
            genevaValidationException.getErrorCode().toString(),
            genevaValidationException.getMessageParams()))
        .thenReturn(
            "File contains 1 invalid entries. Please review the following invalid entries: test");
    var responseEntity =
        controllerExceptionHandler.handleGenevaExceptions(genevaValidationException);
    assertTrue(responseEntity.getBody().toString().contains(String.valueOf(params[0])));
    assertTrue(responseEntity.getBody().toString().contains(String.valueOf(params[1])));
  }

  @ParameterizedTest
  @MethodSource("getExceptions")
  void shouldHandleGenevaExceptions(GenevaException exception) {
    String errorMessage = "Test Message";
    when(messageHandler.getMessage(anyString(), any())).thenReturn(errorMessage);

    ResponseEntity<Error> response = controllerExceptionHandler.handleGenevaExceptions(exception);
    assertNotNull(response);
    assertNotNull(response.getBody());
    assertNull(response.getBody().getErrorTrace());
    assertEquals(errorMessage, response.getBody().getErrorMessage());
  }

  @Test
  void shouldReturnErrorTraceIfTracingEnabled() {
    GenevaAppRuntimeException exception =
        new GenevaAppRuntimeException(CommonErrorCodes.COMMON_BAD_REQUEST);
    when(sysConfig.getErrorTraceEnabled()).thenReturn(Boolean.TRUE);

    ResponseEntity<Error> response = controllerExceptionHandler.handleGenevaExceptions(exception);
    assertNotNull(response.getBody());
    assertNotNull(response.getBody().getErrorTrace());
  }

  @Test
  void shouldNotReturnErrorTraceIfTracingDisabled() {
    GenevaAppRuntimeException exception =
        new GenevaAppRuntimeException(CommonErrorCodes.COMMON_BAD_REQUEST);
    when(sysConfig.getErrorTraceEnabled()).thenReturn(Boolean.FALSE);

    ResponseEntity<Error> response = controllerExceptionHandler.handleGenevaExceptions(exception);
    assertNotNull(response.getBody());
    assertNull(response.getBody().getErrorTrace());
  }

  @Test
  void shouldReturnOneCentralErrorOnOneCentralException() {
    String errorMessage = "Test Message";
    when(messageHandler.getMessage("ONECENTRAL_INTERNAL_ERROR")).thenReturn(errorMessage);
    OneCentralSdkErrorResponse.OneCentralErrorResponseBody oneCentralErrorResponseBody =
        new OneCentralSdkErrorResponse.OneCentralErrorResponseBody(1, "message", "detail");
    OneCentralSdkErrorResponse oneCentralSdkErrorResponse =
        new OneCentralSdkErrorResponse(List.of(oneCentralErrorResponseBody), HttpStatus.OK);

    ResponseEntity<OneCentralError> response =
        controllerExceptionHandler.handleOneCentralSdkException(
            new OneCentralException(
                OneCentralErrorCodes.ONECENTRAL_INTERNAL_ERROR, oneCentralSdkErrorResponse));
    assertNotNull(response);
    assertNotNull(response.getBody());
    assertNull(response.getBody().getErrorTrace());
    assertNotNull(response.getBody().getErrors());
    assertEquals(errorMessage, response.getBody().getErrorMessage());
  }

  @Test
  void shouldReturnEmptyOneCentralErrorOnOneCentralExceptionWhenServiceIsUnavailable() {
    String errorMessage = "Test Message";
    when(messageHandler.getMessage("ONECENTRAL_NULL_RESPONSE")).thenReturn(errorMessage);

    ResponseEntity<OneCentralError> response =
        controllerExceptionHandler.handleOneCentralSdkException(
            new OneCentralException(OneCentralErrorCodes.ONECENTRAL_NULL_RESPONSE, null));
    assertNotNull(response);
    assertNotNull(response.getBody());
    assertNull(response.getBody().getErrorTrace());
    assertEquals(0, response.getBody().getErrors().size());
    assertEquals(errorMessage, response.getBody().getErrorMessage());
  }

  @Test
  void shouldHandleDv360SellerSdkExceptionWithoutHttpStatus() {
    Dv360SellerSdkException exception =
        new Dv360SellerSdkException(Dv360SellerSdkErrorCodes.DV360_SELLER_SDK_HTTP_CLIENT_ERROR);
    ResponseEntity<Error> response =
        controllerExceptionHandler.handleDv360SellerSdkException(exception);
    assertNotNull(response.getBody());
    assertEquals(
        Dv360SellerSdkErrorCodes.DV360_SELLER_SDK_HTTP_CLIENT_ERROR.getHttpStatus(),
        response.getStatusCode());
  }

  @Test
  void shouldHandleDv360SellerSdkExceptionWithHttpStatus() {
    Dv360SellerSdkException exception =
        new Dv360SellerSdkException(
            Dv360SellerSdkErrorCodes.DV360_SELLER_SDK_HTTP_CLIENT_ERROR,
            null,
            HttpStatus.BAD_REQUEST);
    ResponseEntity<Error> response =
        controllerExceptionHandler.handleDv360SellerSdkException(exception);
    assertNotNull(response.getBody());
    assertNotEquals(
        Dv360SellerSdkErrorCodes.DV360_SELLER_SDK_HTTP_CLIENT_ERROR.getHttpStatus(),
        response.getStatusCode());
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  void shouldHandleIdentityB2bSdkException() {
    IdentityB2bSdkException exception =
        new IdentityB2bSdkException(IdentityB2bErrorCodes.IDENTITY_B2B_NOT_AUTHORIZED);
    ResponseEntity<Error> response =
        controllerExceptionHandler.handleIdentityb2bException(exception);
    assertNotNull(response.getBody());
    assertEquals(
        IdentityB2bErrorCodes.IDENTITY_B2B_NOT_AUTHORIZED.getHttpStatus(),
        response.getStatusCode());
  }

  @Test
  void shouldHandleStaleStateException() {
    ObjectOptimisticLockingFailureException exception =
        new ObjectOptimisticLockingFailureException("", null);
    ResponseEntity<Object> response =
        controllerExceptionHandler.handleStaleStateException(exception, webRequest);
    assertNotNull(response.getBody());
    assertEquals(CommonErrorCodes.COMMON_OPTIMISTIC_LOCK.getHttpStatus(), response.getStatusCode());
  }

  @Test
  void shouldHandleMySQLIntegrityConstraintViolationException() {
    SQLIntegrityConstraintViolationException exception =
        new SQLIntegrityConstraintViolationException();
    ResponseEntity<Object> response =
        controllerExceptionHandler.handleMySQLIntegrityConstraintViolationException(
            exception, webRequest);
    assertNotNull(response.getBody());
    assertEquals(
        ServerErrorCodes.SERVER_CONSTRAINT_VIOLATION.getHttpStatus(), response.getStatusCode());
  }

  @Test
  void shouldHandleAuthenticationCredentialsNotFoundException() {
    AuthenticationCredentialsNotFoundException exception =
        new AuthenticationCredentialsNotFoundException("");
    ResponseEntity<Object> response =
        controllerExceptionHandler.handleAuthenticationCredentialsNotFoundException(
            exception, webRequest);
    assertNotNull(response.getBody());
    assertEquals(
        SecurityErrorCodes.SECURITY_NOT_AUTHORIZED.getHttpStatus(), response.getStatusCode());
  }

  @Test
  void shouldHandleOtherExceptions() {
    Exception exception = new Exception();
    ResponseEntity<Object> response =
        controllerExceptionHandler.handleOtherExceptions(exception, webRequest);
    assertNotNull(response.getBody());
    assertEquals(CommonErrorCodes.COMMON_UNKNOWN.getHttpStatus(), response.getStatusCode());
  }

  @Test
  void shouldHandleHttpMessageNotWritable() {
    HttpMessageNotWritableException exception = new HttpMessageNotWritableException("");
    ResponseEntity<Object> response =
        controllerExceptionHandler.handleHttpMessageNotWritable(exception, null, null, webRequest);
    assertNotNull(response.getBody());
    assertEquals(
        CommonErrorCodes.COMMON_RESPONSE_WRITE_FAILED.getHttpStatus(), response.getStatusCode());
  }

  @Test
  void shouldHandleDataIntegrityViolationExceptionWithUnknownConstraintViolationExceptionAsCause() {
    org.hibernate.exception.ConstraintViolationException cause =
        new org.hibernate.exception.ConstraintViolationException(
            "Constraint error", null, "unknown_constraint");
    DataIntegrityViolationException exception =
        new DataIntegrityViolationException("DataIntegrityViolation exception", cause);
    ResponseEntity<Object> response =
        controllerExceptionHandler.handleDataIntegrityViolationException(exception, webRequest);

    assertNotNull(response.getBody());
    assertEquals(CommonErrorCodes.COMMON_UNKNOWN.getHttpStatus(), response.getStatusCode());
  }

  @Test
  void shouldHandleDataIntegrityViolationExceptionWithKnownConstraintViolationExceptionAsCause() {
    org.hibernate.exception.ConstraintViolationException cause =
        new org.hibernate.exception.ConstraintViolationException(
            "Constraint error", null, "unique_name_fee_adjustment");
    DataIntegrityViolationException exception =
        new DataIntegrityViolationException("DataIntegrityViolation exception", cause);
    ResponseEntity<Object> response =
        controllerExceptionHandler.handleDataIntegrityViolationException(exception, webRequest);

    assertNotNull(response.getBody());
    assertEquals(
        ServerErrorCodes.SERVER_FEE_ADJUSTMENT_NAME_NOT_UNIQUE.getHttpStatus(),
        response.getStatusCode());
  }

  @Test
  void shouldHandleDataIntegrityViolationExceptionWithPropertyValueExceptionAsCause() {
    PropertyValueException cause =
        new PropertyValueException("Property exception", "name", "may not be null");
    DataIntegrityViolationException exception =
        new DataIntegrityViolationException("DataIntegrityViolation exception", cause);
    ResponseEntity<Object> response =
        controllerExceptionHandler.handleDataIntegrityViolationException(exception, webRequest);

    assertTrue(response.getBody() instanceof ValidationError);
    assertEquals(1, ((ValidationError) response.getBody()).getFieldErrors().size());
    assertEquals(CommonErrorCodes.COMMON_BAD_REQUEST.getHttpStatus(), response.getStatusCode());
  }

  private static Stream<GenevaException> getExceptions() {
    return Stream.of(
        new GenevaAppRuntimeException(CommonErrorCodes.COMMON_BAD_REQUEST),
        new GenevaDatabaseException(CommonErrorCodes.COMMON_BAD_REQUEST),
        new GenevaValidationException(CommonErrorCodes.COMMON_BAD_REQUEST),
        new GenevaSecurityException(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED));
  }

  private ConstraintViolation<User> getConstraintViolationInstance(String message) {
    return new ConstraintViolation() {
      @Override
      public String getMessage() {
        return message;
      }

      @Override
      public String getMessageTemplate() {
        return null;
      }

      @Override
      public Object getRootBean() {
        return null;
      }

      @Override
      public Class getRootBeanClass() {
        return null;
      }

      @Override
      public Object getLeafBean() {
        return null;
      }

      @Override
      public Object[] getExecutableParameters() {
        return new Object[0];
      }

      @Override
      public Object getExecutableReturnValue() {
        return null;
      }

      @Override
      public Path getPropertyPath() {
        return null;
      }

      @Override
      public Object getInvalidValue() {
        return null;
      }

      @Override
      public ConstraintDescriptor<?> getConstraintDescriptor() {
        return null;
      }

      @Override
      public Object unwrap(Class aClass) {
        return null;
      }
    };
  }
}
