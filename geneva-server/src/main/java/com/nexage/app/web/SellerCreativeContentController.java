package com.nexage.app.web;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.nexage.app.services.SellerCreativeContentService;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.security.error.GenevaSecurityException;
import com.ssp.geneva.common.security.error.SecurityErrorCodes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.servlet.http.HttpServletResponse;
import lombok.Getter;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Retrieve creative for a given seller via CRS service")
@RequestMapping(value = "/v1/sellers/{sellerId}/creatives")
public class SellerCreativeContentController {

  private static final String CREATIVE_TESTING_ENABLED = "geneva.server.creative.testing.enabled";
  private final SellerCreativeContentService sellerCreativeContentService;
  private final Environment environment;

  public SellerCreativeContentController(
      SellerCreativeContentService sellerCreativeContentService, Environment environment) {
    this.sellerCreativeContentService = sellerCreativeContentService;
    this.environment = environment;
  }

  /**
   * GET creative content for MSFT with given creative id.
   *
   * @param creativeId creative id
   * @return {@link ResponseEntity} of type String.
   */
  @Operation(summary = "Get creative markup for MicroSoft")
  @Timed
  @ExceptionMetered
  @GetMapping(value = "/{creativeId}", produces = MediaType.TEXT_HTML_VALUE)
  public ResponseEntity<String> getCreativeContent(
      @PathVariable Long sellerId,
      @PathVariable String creativeId,
      HttpServletResponse httpServletResponse) {
    if (!BooleanUtils.toBoolean(environment.getProperty(CREATIVE_TESTING_ENABLED))) {
      throw new GenevaSecurityException(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED);
    }
    String body = sellerCreativeContentService.getCreativeContent(sellerId, creativeId);
    httpServletResponse.setHeader(SecureHttpHeader.CSP.name, SecureHttpHeader.CSP.value);
    return ResponseEntity.ok(body);
  }

  @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Not Found")
  @ExceptionHandler(value = {GenevaValidationException.class})
  public void handleNotFoundException() {}

  @ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "Unauthorized")
  @ExceptionHandler(value = {GenevaSecurityException.class})
  public void handleUserNotAuthorizedException() {}

  @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Internal Server Error")
  @ExceptionHandler(value = {Exception.class})
  public void handleGeneralException() {}

  @Getter
  enum SecureHttpHeader {
    CSP("Content-Security-Policy", "default-src 'unsafe-inline' 'unsafe-eval' *");

    private String name;
    private String value;

    SecureHttpHeader(String name, String value) {
      this.name = name;
      this.value = value;
    }
  }
}
