package com.nexage.app.services.impl.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexage.app.dto.SupportedCurrencyDTO;
import com.nexage.app.util.assemblers.provisionable.ProvisionableUtils;
import java.io.InputStream;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class SupportedCurrencyProvider {

  public static final String SUPPORTED_CUR_FILE_NAME = "supported_currencies.json";

  private final ObjectMapper objectMapper;

  @Autowired
  public SupportedCurrencyProvider(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  public SupportedCurrencyDTO[] loadCurrencies() {
    String filePath = ProvisionableUtils.getStaticJsonFolder().concat(SUPPORTED_CUR_FILE_NAME);
    try (InputStream in = getResourceAsStream(filePath)) {
      return objectMapper.readValue(in, SupportedCurrencyDTO[].class);
    } catch (Exception e) {
      log.error("Error reading file from classpath: {}", filePath, e);
      throw new IllegalStateException(
          "Supported currency config file cannot be loaded. Path: ".concat(filePath));
    }
  }

  /**
   * This method is extracted and made public just for the sake of creating a unit test.
   *
   * @param filePath
   * @return
   */
  public InputStream getResourceAsStream(String filePath) {
    return getClass().getResourceAsStream(filePath);
  }
}
