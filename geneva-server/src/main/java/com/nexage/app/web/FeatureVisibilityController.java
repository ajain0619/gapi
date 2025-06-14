package com.nexage.app.web;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexage.app.security.FeatureVisibility;
import com.nexage.app.security.FeatureVisibility.MenuMixinForPublic;
import com.ssp.geneva.common.base.annotation.Legacy;
import java.io.File;
import java.io.IOException;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Legacy
@Log4j2
@RestController
@RequestMapping(value = "/profile")
public class FeatureVisibilityController {

  private final Resource featureVisibilityResource;

  public FeatureVisibilityController(Resource featureVisibilityResource) {
    this.featureVisibilityResource = featureVisibilityResource;
  }

  @Timed
  @ExceptionMetered
  @GetMapping
  @ResponseBody
  public FeatureVisibility getMenuVisibility() {
    FeatureVisibility menu = new FeatureVisibility();
    try {
      File f = featureVisibilityResource.getFile();
      ObjectMapper mapper = new ObjectMapper();

      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      mapper.addMixIn(FeatureVisibility.class, MenuMixinForPublic.class);
      menu = mapper.readValue(f, new TypeReference<>() {});

    } catch (IOException e) {
      log.error("Unable to read/parse FeatureVisibility.json file. Error={}", e.getMessage());
    }
    return menu;
  }
}
