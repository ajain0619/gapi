package com.nexage.app.config.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.CorsEndpointProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@Getter
@Setter
public class CorsWebSecurityConfig {

  private final CorsEndpointProperties corsEndpointProperties;

  public CorsWebSecurityConfig(CorsEndpointProperties corsEndpointProperties) {
    this.corsEndpointProperties = corsEndpointProperties;
  }

  /** This method creates an instance of {@link GenevaServerCorsConfig} bases on conditions. */
  @Bean(value = "genevaServerCorsConfig")
  @ConditionalOnClass(CorsConfiguration.class)
  public GenevaServerCorsConfig genevaServerCorsConfig() {
    return new GenevaServerCorsConfig(corsEndpointProperties);
  }

  /**
   * This method creates an instance of {@link GenevaServerCorsConfig} bases on conditions.
   *
   * @param genevaServerCorsConfig {@link GenevaServerCorsConfig}
   */
  @Bean(value = "urlBasedCorsConfigurationSource")
  @Primary
  @ConditionalOnBean(name = "genevaServerCorsConfig")
  @ConditionalOnClass(GenevaServerCorsConfig.class)
  public UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource(
      final GenevaServerCorsConfig genevaServerCorsConfig) {
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", genevaServerCorsConfig);
    return source;
  }

  /**
   * This method creates an instance of {@link CorsFilter} bases on conditions.
   *
   * @param urlBasedCorsConfigurationSource {@link UrlBasedCorsConfigurationSource}
   */
  @Bean
  @ConditionalOnBean(name = "urlBasedCorsConfigurationSource")
  @ConditionalOnClass(UrlBasedCorsConfigurationSource.class)
  public CorsFilter genevaServerCorsFilter(
      final UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource) {
    return new CorsFilter(urlBasedCorsConfigurationSource);
  }
}
