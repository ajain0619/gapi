package com.nexage.app.config.security;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.CookieSerializer.CookieValue;

class CookieConfigTest {

  @Test
  void verifyCookieSettings() {
    CookieConfig config = new CookieConfig();
    config.setUseHttpOnlyCookie(true);
    config.setUseSecureCookie(true);
    config.setCookieName("SESSION");
    config.setSameSite("None");
    CookieSerializer serializer = config.cookieSerializer();
    assertNotNull(serializer);

    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    ArgumentCaptor<Cookie> captor = ArgumentCaptor.forClass(Cookie.class);
    CookieValue cookieValue = new CookieValue(request, response, "value");

    serializer.writeCookieValue(cookieValue);
    verify(response)
        .addHeader("Set-Cookie", "SESSION=dmFsdWU=; Path=null/; Secure; HttpOnly; SameSite=None");
  }
}
