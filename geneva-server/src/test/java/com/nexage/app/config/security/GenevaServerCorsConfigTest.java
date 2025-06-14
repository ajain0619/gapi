package com.nexage.app.config.security;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.CorsEndpointProperties;

class GenevaServerCorsConfigTest {

  private GenevaServerCorsConfig corsConfig;

  @Test
  void shouldCheckOriginsWithRegularExpressions() {
    String origin = "ssp.yahooinc.com";
    CorsEndpointProperties corsEndpointProperties = new CorsEndpointProperties();
    corsEndpointProperties.setAllowedOrigins(List.of(origin));
    corsEndpointProperties.setAllowedMethods(Collections.emptyList());
    corsEndpointProperties.setAllowedHeaders(Collections.emptyList());
    corsConfig = new GenevaServerCorsConfig(corsEndpointProperties);
    corsConfig.addAllowedOrigin(origin);

    assertThat(
        corsConfig.checkOrigin("https://ssp.yahooinc.com"),
        is(equalTo("https://ssp.yahooinc.com")));
    assertThat(corsConfig.checkOrigin("https://onemobile.com"), is(nullValue()));
    assertThat(corsConfig.checkOrigin("https://onemobileone.aol.com"), is(nullValue()));
    assertThat(corsConfig.checkOrigin("http://ssp.yahooinc.com"), is(nullValue()));
    assertThat(
        corsConfig.checkOrigin("https://uat.ssp.yahooinc.com"),
        is(equalTo("https://uat.ssp.yahooinc.com")));
    assertThat(
        corsConfig.checkOrigin("https://qa.ssp.yahooinc.com"),
        is(equalTo("https://qa.ssp.yahooinc.com")));
  }

  @Test
  void shouldCheckOriginsWithoutRegularExpressions() {
    String origin = "https://www.aol.com";
    CorsEndpointProperties corsEndpointProperties = new CorsEndpointProperties();
    corsEndpointProperties.setAllowedOrigins(List.of(origin));
    corsEndpointProperties.setAllowedMethods(Collections.emptyList());
    corsEndpointProperties.setAllowedHeaders(Collections.emptyList());
    corsConfig = new GenevaServerCorsConfig(corsEndpointProperties);
    corsConfig.addAllowedOrigin(origin);

    assertThat(corsConfig.checkOrigin("https://www.aol.com"), is(equalTo("https://www.aol.com")));
    assertThat(corsConfig.checkOrigin("https://onemobile.com"), is(nullValue()));
    assertThat(corsConfig.checkOrigin("https://onemobileone.aol.com"), is(nullValue()));
    assertThat(corsConfig.checkOrigin("http://www.aol.com"), is(nullValue()));
    assertThat(corsConfig.checkOrigin("https://uat.aol.com"), is(nullValue()));
    assertThat(corsConfig.checkOrigin("https://qa.aol.com"), is(nullValue()));
  }

  @Test
  void shouldCheckOriginsWithAndWithoutRegularExpressions() {
    String origin = "lol.com";
    String origin2 = "https://www.aol.com";
    CorsEndpointProperties corsEndpointProperties = new CorsEndpointProperties();
    corsEndpointProperties.setAllowedOrigins(List.of(origin, origin2));
    corsEndpointProperties.setAllowedMethods(Collections.emptyList());
    corsEndpointProperties.setAllowedHeaders(Collections.emptyList());
    corsConfig = new GenevaServerCorsConfig(corsEndpointProperties);
    corsConfig.addAllowedOrigin(origin);

    assertThat(corsConfig.checkOrigin("https://lol.com"), is(equalTo("https://lol.com")));
    assertThat(corsConfig.checkOrigin("https://lollol.com"), is(nullValue()));
    assertThat(corsConfig.checkOrigin("https://lol-lol.com"), is(nullValue()));
    assertThat(corsConfig.checkOrigin("http://lol.com"), is(nullValue()));
    assertThat(corsConfig.checkOrigin("https://www.lol.com"), is(equalTo("https://www.lol.com")));
    assertThat(corsConfig.checkOrigin("https://qa.lol.com"), is(equalTo("https://qa.lol.com")));

    assertThat(corsConfig.checkOrigin("https://www.aol.com"), is(equalTo("https://www.aol.com")));
    assertThat(corsConfig.checkOrigin("https://onemobile.com"), is(nullValue()));
    assertThat(corsConfig.checkOrigin("https://onemobileone.aol.com"), is(nullValue()));
    assertThat(corsConfig.checkOrigin("http://www.aol.com"), is(nullValue()));
    assertThat(corsConfig.checkOrigin("https://uat.aol.com"), is(nullValue()));
    assertThat(corsConfig.checkOrigin("https://qa.aol.com"), is(nullValue()));
  }
}
