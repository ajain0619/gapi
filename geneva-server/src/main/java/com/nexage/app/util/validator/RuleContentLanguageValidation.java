package com.nexage.app.util.validator;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexage.admin.core.enums.RuleTargetType;
import com.nexage.admin.core.model.IsoLanguage;
import com.nexage.admin.core.repository.IsoLanguageRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class RuleContentLanguageValidation implements RuleTargetValidation {
  private final IsoLanguageRepository isoLanguageRepository;
  private final ObjectMapper objectMapper;

  @Override
  public boolean isValid(String data) {
    /**
     * languageData : "{\"languages\": [\"en\", \"fr\" ], \"excludeTrafficWithoutLanguage\" : 1 }"
     */
    final RuleContentLanguageValidation.LanguageData languageData;
    try {
      languageData = objectMapper.readValue(data, RuleContentLanguageValidation.LanguageData.class);
    } catch (JsonProcessingException e) {
      log.debug("Invalid Json : " + e);
      return false;
    }

    return languageData.hasValidData() && hasValidLanguages(languageData);
  }

  @Override
  public RuleTargetType getRuleTarget() {
    return RuleTargetType.CONTENT_LANGUAGE;
  }

  private boolean hasValidLanguages(RuleContentLanguageValidation.LanguageData languageData) {
    List<String> languageList = languageData.languages;

    List<IsoLanguage> resultList = isoLanguageRepository.findByLanguageCodeIn(languageList);

    return (languageList.size() == resultList.size());
  }

  private static class LanguageData {
    private List<String> languages;
    private int excludeTrafficWithoutLanguage;

    public LanguageData(
        @JsonProperty(value = "languages", required = true) List<String> languages,
        @JsonProperty(value = "excludeTrafficWithoutLanguage", required = true)
            int excludeTrafficWithoutLanguage) {
      this.languages = languages;
      this.excludeTrafficWithoutLanguage = excludeTrafficWithoutLanguage;
    }

    public boolean hasValidData() {
      return ((excludeTrafficWithoutLanguage == 0 || excludeTrafficWithoutLanguage == 1)
          && !(languages.isEmpty() && 0 == excludeTrafficWithoutLanguage));
    }
  }
}
