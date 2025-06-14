package com.nexage.app.web;

import static org.springframework.http.MediaType.ALL_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.nexage.admin.core.validator.CreateGroup;
import com.nexage.admin.core.validator.UpdateGroup;
import com.nexage.app.dto.sellingrule.SellerRuleDTO;
import com.nexage.app.services.PublisherRuleService;
import com.ssp.geneva.common.base.annotation.Legacy;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Legacy
@Tag(name = "/pss")
@RestController
@RequestMapping(value = "/pss")
public class DealRuleController {

  private final PublisherRuleService publisherRuleService;

  public DealRuleController(PublisherRuleService publisherRuleService) {
    this.publisherRuleService = publisherRuleService;
  }

  @Timed
  @ExceptionMetered
  @PostMapping(
      value = "/rule",
      consumes = APPLICATION_JSON_VALUE,
      produces = APPLICATION_JSON_VALUE)
  public SellerRuleDTO createWithoutPublisher(
      @RequestBody @Validated(CreateGroup.class) SellerRuleDTO sellerRuleDTO) {
    return publisherRuleService.create(sellerRuleDTO);
  }

  @Timed
  @ExceptionMetered
  @PutMapping(
      value = "/rule/{rulePid}",
      consumes = APPLICATION_JSON_VALUE,
      produces = APPLICATION_JSON_VALUE)
  public SellerRuleDTO update(
      @PathVariable Long rulePid,
      @RequestBody @Validated(UpdateGroup.class) SellerRuleDTO sellerRuleDTO) {
    return publisherRuleService.update(rulePid, sellerRuleDTO);
  }

  @Timed
  @ExceptionMetered
  @DeleteMapping(value = "/rule/{rulePid}", consumes = ALL_VALUE)
  public void deleteWithoutPublisherPid(@PathVariable Long rulePid) {
    publisherRuleService.delete(rulePid);
  }

  @Timed
  @ExceptionMetered
  @GetMapping(value = "/rule/{rulePid}", consumes = ALL_VALUE)
  public SellerRuleDTO getRuleByPid(@PathVariable Long rulePid) {
    return publisherRuleService.find(rulePid);
  }
}
