package com.nexage.app.util.validator.placement.nativeads.xpath;

import com.nexage.app.util.validator.BaseValidator;
import javax.validation.ConstraintValidatorContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class XPathValidator extends BaseValidator<ValidXPath, String> {

  @Override
  public boolean isValid(String adXPath, ConstraintValidatorContext context) {
    boolean isValid = true;
    XPath xPath = XPathFactory.newInstance().newXPath();
    try {
      xPath.compile(adXPath);
    } catch (XPathExpressionException e) {
      log.error("Error compiling xPath: {}", e.getMessage(), e);
      isValid = false;
    }
    return isValid;
  }
}
