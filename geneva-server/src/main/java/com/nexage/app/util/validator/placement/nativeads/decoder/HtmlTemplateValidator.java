package com.nexage.app.util.validator.placement.nativeads.decoder;

import com.nexage.app.util.validator.BaseValidator;
import javax.validation.ConstraintValidatorContext;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringEscapeUtils;

@Log4j2
public class HtmlTemplateValidator extends BaseValidator<ValidHtmlTemplate, String> {
  private static final String HTML_OPENING_BRACKET = "<";
  private static final String HTML_CLOSING_BRACKET = ">";

  @Override
  public boolean isValid(String encodedHtml, ConstraintValidatorContext context) {
    boolean isValid = true;

    String html = StringEscapeUtils.unescapeJava(encodedHtml).trim();
    if (!html.startsWith(HTML_OPENING_BRACKET) || !html.endsWith(HTML_CLOSING_BRACKET)) {
      logMissingOpenOrCloseBracket();
      isValid = false;
    } else {
      int openings = findNumberOfBrackets(html, HTML_OPENING_BRACKET);
      int closings = findNumberOfBrackets(html, HTML_CLOSING_BRACKET);
      if (openings != closings) {
        logUnevenBracketCount(openings, closings);
        isValid = false;
      }
    }

    return isValid;
  }

  private void logUnevenBracketCount(int openings, int closings) {
    log.error(
        "There is a problem in HTML content: There are {} '{}' (open brackets) while there are {} '{}' (close brackets)",
        HTML_OPENING_BRACKET,
        openings,
        HTML_CLOSING_BRACKET,
        closings);
  }

  private void logMissingOpenOrCloseBracket() {
    log.error(
        "The decoded String is not enclosed with '{}' and '{}'",
        HTML_OPENING_BRACKET,
        HTML_CLOSING_BRACKET);
  }

  private int findNumberOfBrackets(String html, String bracket) {
    int totalBrackets = 0;
    int wordLength = 0;
    int index = 0;
    while (index != -1) {
      index = html.indexOf(bracket, index + wordLength);
      if (index != -1) {
        totalBrackets++;
      }
      wordLength = bracket.length();
    }
    return totalBrackets;
  }
}
