package com.nexage.app.web.security;

import static com.ssp.geneva.common.base.annotation.ExternalAPI.WARNING_EXTERNAL_API_NEEDS_TO_BE_CHECKED_WITH_PRODUCT_BEFORE_DEPLOYING;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.nexage.app.services.SessionService;
import com.ssp.geneva.common.base.annotation.ExternalAPI;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "/v1/sessions")
@RestController
@RequestMapping(value = "/v1/sessions")
public class SessionCacheController {

  private final SessionService sessionService;

  public SessionCacheController(SessionService sessionService) {
    this.sessionService = sessionService;
  }

  @Timed
  @ExceptionMetered
  @DeleteMapping
  @ExternalAPI(WARNING_EXTERNAL_API_NEEDS_TO_BE_CHECKED_WITH_PRODUCT_BEFORE_DEPLOYING)
  public ResponseEntity<Object> deleteSessionCache() {
    sessionService.deleteSessions();
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
