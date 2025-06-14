package com.nexage.app.config.security;

import static java.util.Objects.isNull;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.CorsEndpointProperties;
import org.springframework.web.cors.CorsConfiguration;

@Log4j2
@Getter
public class GenevaServerCorsConfig extends CorsConfiguration {

  private final List<Pattern> allowedOriginsRegex;
  private final String httpsRegexHeader = "^https://([A-Za-z0-9-]+\\.|www\\.|)";

  GenevaServerCorsConfig(CorsEndpointProperties corsEndpointProperties) {
    allowedOriginsRegex = new ArrayList<>();
    if (isNull(corsEndpointProperties.getAllowCredentials())) {
      setAllowCredentials(true);
    } else {
      setAllowCredentials(corsEndpointProperties.getAllowCredentials());
    }
    setAllowedHeaders(corsEndpointProperties.getAllowedHeaders());
    setAllowedMethods(corsEndpointProperties.getAllowedMethods());
    setExposedHeaders(corsEndpointProperties.getExposedHeaders());
    for (String origin : corsEndpointProperties.getAllowedOrigins()) {
      addAllowedOrigin(origin);
    }
  }

  /** {@inheritDoc} */
  @Override
  public void addAllowedOrigin(String origin) {
    super.addAllowedOrigin(origin);
    try {
      allowedOriginsRegex.add(Pattern.compile(getHttpsRegexHeader() + origin));
    } catch (PatternSyntaxException e) {
      log.warn("Wrong syntax for given allowed origin {}", origin);
    }
  }

  /** {@inheritDoc} */
  @Override
  public String checkOrigin(final String requestOrigin) {
    String result = super.checkOrigin(requestOrigin);
    log.info("CORS check, origin: {}, result: {}", requestOrigin, result);
    return result != null ? result : checkOriginWithRegularExpression(requestOrigin);
  }

  private String checkOriginWithRegularExpression(final String requestOrigin) {
    log.info("Checking origin with regular expression. Origin: {}", requestOrigin);
    var result =
        allowedOriginsRegex.stream()
            .filter(pattern -> pattern.matcher(requestOrigin).matches())
            .map(pattern -> requestOrigin)
            .findFirst()
            .orElse(null);
    log.info("check result: {}", result);
    return result;
  }
}
