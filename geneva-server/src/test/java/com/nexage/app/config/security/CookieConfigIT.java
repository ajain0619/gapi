package com.nexage.app.config.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {CookieConfig.class})
@ActiveProfiles("secure")
@TestPropertySource(
    properties = {
      "server.servlet.session.cookie.http-only=true",
      "server.servlet.session.cookie.secure=false",
      "server.servlet.session.cookie.sameSite=Whatever",
      "server.servlet.session.cookie.name=MYSESSION"
    })
class CookieConfigIT {

  @Autowired ApplicationContext context;

  @Test
  void shouldSetPropertyAccordingly() {
    CookieSerializer cookieSerializer = (CookieSerializer) context.getBean("cookieSerializer");
    assertNotNull(cookieSerializer);

    var sameSiteValue = ReflectionTestUtils.getField(cookieSerializer, "sameSite");
    assertNotNull(sameSiteValue);
    assertEquals("Whatever", sameSiteValue);

    var cookieNameValue = ReflectionTestUtils.getField(cookieSerializer, "cookieName");
    assertNotNull(cookieNameValue);
    assertEquals("MYSESSION", cookieNameValue);

    var httpOnlyValue = ReflectionTestUtils.getField(cookieSerializer, "useHttpOnlyCookie");
    assertNotNull(httpOnlyValue);
    assertEquals(true, httpOnlyValue);

    var secureValue = ReflectionTestUtils.getField(cookieSerializer, "useSecureCookie");
    assertNotNull(secureValue);
    assertEquals(false, secureValue);
  }
}
