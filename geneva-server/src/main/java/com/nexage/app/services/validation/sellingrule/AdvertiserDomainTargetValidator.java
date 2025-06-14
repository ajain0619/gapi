package com.nexage.app.services.validation.sellingrule;

import static java.util.Objects.isNull;
import static org.apache.commons.collections.CollectionUtils.isEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexage.admin.core.enums.RuleTargetType;
import com.nexage.admin.core.model.BrandProtectionTag;
import com.nexage.admin.core.repository.BrandProtectionTagRepository;
import com.nexage.app.dto.sellingrule.RuleTargetDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * TODO: This validator must be reworked as explained under
 * https://jira.vzbuilders.com/browse/MX-12971
 */
@RequiredArgsConstructor
@Log4j2
@Component
public class AdvertiserDomainTargetValidator implements RuleTargetValidatorRegistry.Validator {

  private static final Pattern EXACT_MATCH_ADVERTISER_DOMAIN_PATTERN =
      Pattern.compile("^([a-zA-Z0-9]([a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,63}$");
  private static final Pattern WILDCARD_ADVERTISER_DOMAIN_PATTERN =
      Pattern.compile("^[a-zA-Z0-9\\-.]{1,63}$");

  static final int MAX_TAG_VALUES_LENGTH = 10000;
  static final Long ADOMAIN_TAG_CATEGORY_PID = 5L;

  private final ObjectMapper objectMapper;
  private final BrandProtectionTagRepository brandProtectionTagRepository;

  private final TypeReference<List<AdvertiserDomain>> advertiserDomainListType =
      new TypeReference<>() {};

  @PostConstruct
  public void init() {
    log.info("Registering advertiser domain target validator...");
    RuleTargetValidatorRegistry.registerValidator(
        RuleTargetType.EXACT_MATCH_ADVERTISER_DOMAIN, this);
    RuleTargetValidatorRegistry.registerValidator(RuleTargetType.WILDCARD_ADVERTISER_DOMAIN, this);
  }

  @Override
  public void accept(RuleTargetDTO target) {
    RuleTargetValidatorRegistry.DEFAULT_NOT_BLANK_VALIDATOR.accept(target);
    AdvertiserDomain advertiserDomain = readAdvertiserDomain(target.getData());
    validateTagId(advertiserDomain.getTagId());
    validateTagValues(advertiserDomain.getTagValues(), target.getTargetType());
  }

  private AdvertiserDomain readAdvertiserDomain(String targetData) {
    List<AdvertiserDomain> advertiserDomains;
    try {
      advertiserDomains = objectMapper.readValue(targetData, advertiserDomainListType);
    } catch (IOException e) {
      log.error(
          String.format(
              "Invalid JSON for advertiser domain target data: %s, %s", targetData, e.getMessage()),
          e);
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_RULE_TARGET_DATA_INVALID_JSON_FORMAT);
    }

    if (advertiserDomains.size() != 1) {
      log.error("Advertiser domain target should contain exactly one tag with values");
      throw new GenevaValidationException(ServerErrorCodes.SERVER_TARGET_INVALID_ADVERTISER_DOMAIN);
    }

    return advertiserDomains.get(0);
  }

  private void validateTagId(Long tagId) {
    if (isNull(tagId)) {
      log.error("Advertiser domain target tag id was not provided");
      throw new GenevaValidationException(ServerErrorCodes.SERVER_TARGET_INVALID_ADVERTISER_DOMAIN);
    }
    Optional<BrandProtectionTag> brandProtectionTag = brandProtectionTagRepository.findById(tagId);
    if (brandProtectionTag.isEmpty()
        || !ADOMAIN_TAG_CATEGORY_PID.equals(brandProtectionTag.get().getCategory().getPid())) {
      log.error("Advertiser domain target tag was invalid");
      throw new GenevaValidationException(ServerErrorCodes.SERVER_TARGET_INVALID_ADVERTISER_DOMAIN);
    }
  }

  private void validateTagValues(Set<String> tagValues, RuleTargetType targetType) {
    if (isEmpty(tagValues)) {
      log.error("Advertiser domain target tag values should not be empty");
      throw new GenevaValidationException(ServerErrorCodes.SERVER_TARGET_INVALID_ADVERTISER_DOMAIN);
    }
    String tagValuesString = StringUtils.join(tagValues, ",");
    if (tagValuesString.length() > MAX_TAG_VALUES_LENGTH) {
      log.error("Advertiser domain target tag values are too long");
      throw new GenevaValidationException(ServerErrorCodes.SERVER_TARGET_INVALID_ADVERTISER_DOMAIN);
    }

    Pattern advertiserDomainPattern =
        RuleTargetType.WILDCARD_ADVERTISER_DOMAIN.equals(targetType)
            ? WILDCARD_ADVERTISER_DOMAIN_PATTERN
            : EXACT_MATCH_ADVERTISER_DOMAIN_PATTERN;

    boolean isAnyTagValueInvalid =
        tagValues.stream()
            .anyMatch(tagValue -> !advertiserDomainPattern.matcher(tagValue).matches());

    if (isAnyTagValueInvalid) {
      log.error("Advertiser domain target tag contains invalid values");
      throw new GenevaValidationException(ServerErrorCodes.SERVER_TARGET_INVALID_ADVERTISER_DOMAIN);
    }
  }

  @Getter
  @Setter
  static class AdvertiserDomain {
    @JsonProperty("tag_id")
    private Long tagId;

    @JsonProperty("tag_values")
    private Set<String> tagValues;
  }
}
