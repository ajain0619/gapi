package com.nexage.app.util;

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import javax.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Log4j2
@Component
public class HbPartnerUtils {

  private static List<String> PARTNER_HANDLERS;
  private static List<ResponseConfigOptions> RESPONSECONFIG_OPTIONS;
  private static String PARTNERS_FILE = "hb_partner_handlers.json";
  private static String RESPONSECONFIG_OPTIONS_FILE = "hb_partner_responseConfig_options.json";
  private static String PARTNERS_FILE_PATH = "/static/json/";

  private final CustomObjectMapper objectMapper;

  @PostConstruct
  public void loadData() {
    try {
      fetchHBPartnerConfiguration();
    } catch (Exception e) {
      log.error("Error loading Hb Partner handlers json or response Configuration json", e);
      throw e;
    }
  }

  /**
   * validates the partnerName against a known list of partner names. The known list of HB Partner
   * handlers are maintained in the file /static/json/hb_partner_handlers.json
   *
   * @param partnerName name of the partner to be validated
   * @return {@code Boolean} the partnerName is valid or not
   */
  public boolean isValidPartnerName(String partnerName) {
    return StringUtils.isNotEmpty(partnerName)
        ? getPartnersHandlers().contains(partnerName)
        : false;
  }

  /**
   * Fetches the list of known HB Partner Handlers The known list of HB Partner handlers are
   * maintained in the file /static/json/hb_partner_handlers.json
   *
   * @return {@code List<String>} returns the list of partner names
   */
  public List<String> getPartnersHandlers() {
    if (PARTNER_HANDLERS == null) {
      loadData();
    }
    return PARTNER_HANDLERS;
  }

  /**
   * Fetches the JSON formatted list of configKey/Config Options allowed for {@code responseConfig}
   * of {@link com.nexage.app.dto.HbPartnerDTO} entity
   *
   * @return {@code List<ResponseConfigOptions>} that are valid configKey and configOptions
   */
  public List<ResponseConfigOptions> getResponseConfigKeysAndOptions() {
    if (RESPONSECONFIG_OPTIONS == null) {
      loadData();
    }
    return RESPONSECONFIG_OPTIONS;
  }

  private void fetchHBPartnerConfiguration() {
    try (InputStream partnerListStream =
            HbPartnerUtils.class.getResourceAsStream(PARTNERS_FILE_PATH + PARTNERS_FILE);
        InputStream responseConfigStream =
            HbPartnerUtils.class.getResourceAsStream(
                PARTNERS_FILE_PATH + RESPONSECONFIG_OPTIONS_FILE); ) {
      PARTNER_HANDLERS =
          objectMapper.readValue(partnerListStream, new TypeReference<List<String>>() {});
      RESPONSECONFIG_OPTIONS =
          objectMapper.readValue(
              responseConfigStream, new TypeReference<List<ResponseConfigOptions>>() {});
    } catch (IOException e) {
      log.error(
          "Error loading Hb Partner handlers and/or Hb Partner Response Config json file",
          e.getMessage());
    }
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class ResponseConfigOptions {
    private String configKey;
    private List<String> configOptions;
  }
}
