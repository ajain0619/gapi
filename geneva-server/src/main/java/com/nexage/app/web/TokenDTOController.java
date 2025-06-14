package com.nexage.app.web;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.nexage.app.dto.AccessTokenDTO;
import com.nexage.app.services.TokenService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Collections;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "v1/tokens")
@RestController
@RequestMapping(value = "v1/tokens")
public class TokenDTOController {

  private final TokenService tokenService;

  public TokenDTOController(TokenService tokenService) {
    this.tokenService = tokenService;
  }

  /**
   * Get a paginated list of tokens with the token for current logged in user when passed qt=status
   * and qt=current
   *
   * @return a paginated list of tokens with a valid user token for a period if time specified in
   *     expiresIn.
   */
  @Timed
  @ExceptionMetered
  @GetMapping
  public ResponseEntity<Page<AccessTokenDTO>> getCurrentUserToken(
      @RequestParam(value = "qf", required = false) Set<String> qf,
      @RequestParam(value = "qt", required = false) String qt) {
    return ResponseEntity.ok(
        new PageImpl<>(
            Collections.singletonList(tokenService.getToken(qt, qf)), Pageable.unpaged(), 1));
  }
}
