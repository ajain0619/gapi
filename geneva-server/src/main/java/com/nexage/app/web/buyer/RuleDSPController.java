package com.nexage.app.web.buyer;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.nexage.app.dto.RuleDSPBiddersDTO;
import com.nexage.app.services.RuleDSPService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "/v1/rules/dsps")
@RestController
@RequestMapping(value = "/v1/rules/dsps", produces = MediaType.APPLICATION_JSON_VALUE)
public class RuleDSPController {

  final RuleDSPService ruleDSPService;

  public RuleDSPController(RuleDSPService ruleDSPService) {
    this.ruleDSPService = ruleDSPService;
  }

  /**
   * GET resource to retrieve all {@link RuleDSPBiddersDTO} to be used to populate a dropdown in UI
   *
   * @return {@link ResponseEntity} of type {@link List} {@link RuleDSPBiddersDTO}.
   */
  @Timed
  @ExceptionMetered
  @GetMapping
  public ResponseEntity<List<RuleDSPBiddersDTO>> getDSPs() {
    return ResponseEntity.ok(ruleDSPService.findAll());
  }
}
