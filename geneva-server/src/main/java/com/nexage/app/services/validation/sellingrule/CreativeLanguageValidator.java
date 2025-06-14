package com.nexage.app.services.validation.sellingrule;

import static java.util.stream.Collectors.toSet;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.nexage.admin.core.enums.RuleTargetType;
import com.nexage.admin.core.repository.BrandProtectionTagRepository;
import com.nexage.app.dto.sellingrule.RuleTargetDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.io.IOException;
import java.util.List;
import java.util.Set;
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
public class CreativeLanguageValidator implements RuleTargetValidatorRegistry.Validator {

  static final Long CREATIVE_LANG_CATEGORY_ID = 4L;
  private final BrandProtectionTagRepository brandProtectionTagRepository;
  private final ObjectMapper objectMapper;

  private boolean validateJsonAgainstDataInDB(Set<Long> creativeLangIds) {
    long count =
        brandProtectionTagRepository.countByCategoryPidAndPidIn(
            CREATIVE_LANG_CATEGORY_ID, creativeLangIds);
    // count should be the same if all tag_ids are really in given category
    return count == creativeLangIds.size();
  }

  @Override
  public void accept(RuleTargetDTO ruleTargetDTO) {
    RuleTargetValidatorRegistry.DEFAULT_NOT_BLANK_VALIDATOR.accept(ruleTargetDTO);

    Set<Long> creativeLangIds =
        readDataModel(ruleTargetDTO).stream().map(CreativeLanguage::getTagId).collect(toSet());

    boolean dataValidationOk = validateJsonAgainstDataInDB(creativeLangIds);

    if (!dataValidationOk) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_TARGET_INVALID_CREATIVE_LANGUAGE);
    }
  }

  private List<CreativeLanguage> readDataModel(RuleTargetDTO ruleTargetDTO) {
    try {
      return objectMapper.readValue(
          ruleTargetDTO.getData(),
          TypeFactory.defaultInstance()
              .constructCollectionType(List.class, CreativeLanguage.class));
    } catch (IOException e) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_TARGET_INVALID_CREATIVE_LANGUAGE);
    }
  }

  @Getter
  @Setter
  protected static class CreativeLanguage {
    @JsonProperty("tag_id")
    private Long tagId;
  }

  @PostConstruct
  public void init() {
    log.info("Registering creative language target validator...");
    RuleTargetValidatorRegistry.registerValidator(RuleTargetType.CREATIVE_LANGUAGE, this);
  }
}
