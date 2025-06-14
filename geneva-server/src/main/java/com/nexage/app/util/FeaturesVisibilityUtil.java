package com.nexage.app.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexage.app.security.FeatureVisibility;
import java.io.IOException;
import java.io.InputStream;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class FeaturesVisibilityUtil {

  private static Logger LOGGER = LoggerFactory.getLogger(FeaturesVisibilityUtil.class);

  private final Resource featureVisibilityResource;

  private static FeatureVisibility features;

  @PostConstruct
  private void init() {
    try (InputStream is = featureVisibilityResource.getInputStream()) {
      ObjectMapper mapper = new ObjectMapper();
      features = mapper.readValue(is, new TypeReference<FeatureVisibility>() {});
    } catch (IOException e) {
      LOGGER.error("Unable to read or parse the featureVisibilty.json file: {}", e.getMessage());
    }
  }

  public boolean hasDashboardSummaryCaching() {
    return features.hasDashboardSummaryCaching();
  }
}
