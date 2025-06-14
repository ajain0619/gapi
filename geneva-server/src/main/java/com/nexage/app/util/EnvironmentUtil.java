package com.nexage.app.util;

import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import spark.utils.ObjectUtils;

@RequiredArgsConstructor
@Component
public class EnvironmentUtil {

  private final Environment environment;

  public boolean isAwsEnvironment() {
    if (ObjectUtils.isEmpty(environment.getActiveProfiles())) {
      return false;
    }
    String[] activeProfile = environment.getActiveProfiles();
    return Arrays.stream(activeProfile).anyMatch("aws"::equalsIgnoreCase);
  }
}
