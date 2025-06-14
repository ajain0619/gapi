package com.nexage.app.services.validation.sellingrule;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.nexage.admin.core.enums.RuleTargetType;
import com.nexage.app.dto.sellingrule.RuleTargetDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

/**
 * TODO: This validator must be reworked as explained under
 * https://jira.vzbuilders.com/browse/MX-12971
 */
@RequiredArgsConstructor
@Log4j2
@Component
public class AdSizeTargetValidator implements RuleTargetValidatorRegistry.Validator {

  private static final String DATA_VALUES_LIST_SEPARATOR = ",";
  private static final String AD_SIZES_JSON = "/static/json/creative-sizes.json";

  private final Set<String> validAdSizes = new TreeSet<>();
  private final ObjectMapper objectMapper;

  @PostConstruct
  public void init() throws IOException {
    log.info("Registering ad size target validator...");
    List<AdSizeJson> adSizes;
    try {
      adSizes =
          objectMapper.readValue(
              getClass().getResource(AD_SIZES_JSON),
              TypeFactory.defaultInstance().constructCollectionType(List.class, AdSizeJson.class));
    } catch (IOException e) {
      log.error("Failed to initialize AdSizeTargetValidator", e);
      throw e;
    }
    validAdSizes.addAll(adSizes.stream().map(AdSizeJson::getText).collect(Collectors.toList()));
    RuleTargetValidatorRegistry.registerValidator(RuleTargetType.AD_SIZE, this);
    RuleTargetValidatorRegistry.registerValidator(RuleTargetType.MULTI_AD_SIZE, this);
  }

  @Override
  public void accept(RuleTargetDTO target) {
    RuleTargetValidatorRegistry.DEFAULT_NOT_BLANK_VALIDATOR.accept(target);

    List<String> invalidAdSizes = getInvalidAdSizes(target.getData());
    if (!invalidAdSizes.isEmpty()) {
      log.warn("Requested invalid adSize(s): {}", invalidAdSizes);
      throw new GenevaValidationException(ServerErrorCodes.SERVER_TARGET_INVALID_AD_SIZE);
    }
  }

  private List<String> getInvalidAdSizes(String targetData) {
    String[] adSizes = targetData.split(DATA_VALUES_LIST_SEPARATOR);
    return Arrays.stream(adSizes)
        .filter(adSize -> !validAdSizes.contains(adSize))
        .collect(Collectors.toList());
  }

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonIgnoreProperties(ignoreUnknown = true)
  @Getter
  @Setter
  static class AdSizeJson {
    private String text;
  }
}
