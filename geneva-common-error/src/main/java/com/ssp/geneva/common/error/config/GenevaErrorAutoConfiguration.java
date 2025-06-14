package com.ssp.geneva.common.error.config;

import com.ssp.geneva.common.base.settings.SystemConfigurable;
import com.ssp.geneva.common.error.exception.logger.ExceptionLogger;
import com.ssp.geneva.common.error.handler.MessageHandler;
import java.io.IOException;
import java.util.Properties;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

@Configuration
@ConditionalOnProperty(
    prefix = "geneva.common.error",
    name = "enabled",
    havingValue = "true",
    matchIfMissing = true)
@ComponentScan("com.ssp.geneva.common.error")
@Log4j2
public class GenevaErrorAutoConfiguration {

  public GenevaErrorAutoConfiguration() {
    log.info("geneva.auto-config:GenevaErrorAutoConfiguration");
  }

  private static final String EXCEPTION_LOGGER_CONFIG_FILE = "exception-logger.properties";

  @Bean("exceptionLoggerProperties")
  public Properties exceptionLoggerProperties() {
    try {
      return PropertiesLoaderUtils.loadProperties(
          new ClassPathResource(EXCEPTION_LOGGER_CONFIG_FILE));
    } catch (IOException e) {
      log.warn(
          "Unable to load exception logger properties from: {}.", EXCEPTION_LOGGER_CONFIG_FILE);
      return new Properties();
    }
  }

  @Bean("exceptionLogger")
  @ConditionalOnClass(SystemConfigurable.class)
  public ExceptionLogger exceptionLogger(
      @Autowired SystemConfigurable sysConfigUtil,
      @Autowired Properties exceptionLoggerProperties) {
    return new ExceptionLogger(sysConfigUtil, exceptionLoggerProperties);
  }

  @Bean("messageSource")
  public ResourceBundleMessageSource messageSource() {
    var messageSource = new ResourceBundleMessageSource();
    messageSource.setFallbackToSystemLocale(true);
    messageSource.setBasenames("messages");
    return messageSource;
  }

  @Bean("messageHandler")
  @ConditionalOnClass(MessageSource.class)
  public MessageHandler exceptionLogger(@Autowired MessageSource messageSource) {
    return new MessageHandler(messageSource);
  }
}
