package com.nexage.app.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class HtmlSanitizerUtilTest {

  @Test
  void testStringHasHtmlElementAndSanitizeIt() {
    // given & when
    String result =
        HtmlSanitizerUtil.sanitizeHtmlElement("<script src='http://evli.com/bad.js'></script>");

    // then
    assertEquals("&lt;script src='http://evli.com/bad.js'&gt;&lt;/script&gt;", result);
  }

  @Test
  void testStringHasNoHtmlElementAndDoNotSanitizeIt() {
    // given & when
    String result = HtmlSanitizerUtil.sanitizeHtmlElement("no html elements");

    // then
    assertEquals("no html elements", result);
  }

  @Test
  void testStringHasNoHtmlElementButCommaAndAmpersandCharactersAndDoNotSanitizeIt() {
    // given & when
    String result = HtmlSanitizerUtil.sanitizeHtmlElement("&test,test");

    // then
    assertEquals("&test,test", result);
  }

  @Test
  void testStringHasHtmlDivElementAndSanitizeIt() {
    // given & when
    String result = HtmlSanitizerUtil.sanitizeHtmlElement("<div>&test,test</div>");

    // then
    assertEquals("&lt;div&gt;&amp;test,test&lt;/div&gt;", result);
  }

  @Test
  void testStringHasHtmlElementCommaAndAmpersandCharactersAndSanitizeIt() {
    // given & when
    String result = HtmlSanitizerUtil.sanitizeHtmlElement("&<script>alert('hello')</script>");

    // then
    assertEquals("&amp;&lt;script&gt;alert('hello')&lt;/script&gt;", result);
  }

  @Test
  void testStringHasNoHtmlElementButAllowedSpecialCharactersAndDoNotSanitizeIt() {
    // given & when
    String result = HtmlSanitizerUtil.sanitizeHtmlElement("&[]test-,3");

    // then
    assertEquals("&[]test-,3", result);
  }

  @Test
  void testStringContainsAllowedSpecialCharactersAndDoNotSanitizeIt() {
    // given & when
    String result =
        HtmlSanitizerUtil.sanitizeHtmlElement(
            "USEN - Yahoo - Mail - Desktop - Hub & Article - Sky [y123133]");

    // then
    assertEquals("USEN - Yahoo - Mail - Desktop - Hub & Article - Sky [y123133]", result);
  }

  @Test
  void testStringHasHtmlScriptElementAndSanitizeIt() {
    // given & when
    String result = HtmlSanitizerUtil.sanitizeHtmlElement("<script>[alert('hello')[</script>");

    // then
    assertEquals("&lt;script&gt;[alert('hello')[&lt;/script&gt;", result);
  }

  @Test
  void testStringHasHtmlScriptElementWIthSpecialCharactersAndSanitizeIt() {
    // given & when
    String result = HtmlSanitizerUtil.sanitizeHtmlElement("<script>[alert('hello')[</script>");

    // then
    assertEquals("&lt;script&gt;[alert('hello')[&lt;/script&gt;", result);
  }

  @Test
  void testStringIsNullThenReturnNull() {
    // given & when
    String result = HtmlSanitizerUtil.sanitizeHtmlElement(null);

    // then
    assertNull(result);
  }
}
