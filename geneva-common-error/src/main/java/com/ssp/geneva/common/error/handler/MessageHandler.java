package com.ssp.geneva.common.error.handler;

import java.util.Locale;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

public class MessageHandler {

  private final MessageSource messageSource;

  public MessageHandler(MessageSource messageSource) {
    this.messageSource = messageSource;
  }

  public String getMessage(String key) {
    return getMessage(key, null);
  }

  public String getMessage(String key, Object[] args) {
    Locale locale = LocaleContextHolder.getLocale();
    return messageSource.getMessage(key, args, locale);
  }
}
