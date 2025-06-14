package com.nexage.app.web;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.ssp.geneva.common.base.annotation.Legacy;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Legacy
@RestController
@RequestMapping(value = "/unauthorized", produces = MediaType.APPLICATION_JSON_VALUE)
public class UnauthorizedController {

  @GetMapping()
  @ResponseBody
  public ResponseEntity<String> getUnauthorized() {
    return ResponseEntity.status(UNAUTHORIZED).body(UNAUTHORIZED.getReasonPhrase());
  }
}
