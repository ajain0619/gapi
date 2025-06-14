package com.nexage.app.config.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

@Profile("secure")
@Configuration
@Getter
@Setter
public class CookieConfig {

  @Value("${server.servlet.session.cookie.http-only:true}")
  private Boolean useHttpOnlyCookie;

  @Value("${server.servlet.session.cookie.secure:true}")
  private Boolean useSecureCookie;

  @Value("${server.servlet.session.cookie.sameSite:Lax}")
  private String sameSite;

  @Value("${server.servlet.session.cookie.name:JSESSIONID}")
  private String cookieName;

  @Bean
  public CookieSerializer cookieSerializer() {
    DefaultCookieSerializer serializer = new DefaultCookieSerializer();
    serializer.setUseHttpOnlyCookie(useHttpOnlyCookie);
    serializer.setUseSecureCookie(useSecureCookie);
    serializer.setSameSite(sameSite);
    serializer.setCookieName(cookieName);
    return serializer;
  }
}
