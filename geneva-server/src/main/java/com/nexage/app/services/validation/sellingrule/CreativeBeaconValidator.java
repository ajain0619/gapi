package com.nexage.app.services.validation.sellingrule;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.collections.CollectionUtils.isEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.nexage.admin.core.enums.RuleTargetType;
import com.nexage.admin.core.model.BrandProtectionTag;
import com.nexage.admin.core.model.BrandProtectionTagValues;
import com.nexage.admin.core.repository.BrandProtectionTagRepository;
import com.nexage.app.dto.sellingrule.RuleTargetDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import javax.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * TODO: This validator must be reworked as explained under
 * https://jira.vzbuilders.com/browse/MX-12971
 */
@RequiredArgsConstructor
@Log4j2
@Component
public class CreativeBeaconValidator implements RuleTargetValidatorRegistry.Validator {
  protected static final Long CREATIVE_BEACON_CATEGORY_PID = 3L;
  private final BrandProtectionTagRepository brandProtectionTagRepository;
  private final ObjectMapper objectMapper;
  private final TransactionTemplate transactionTemplate;

  @PostConstruct
  public void init() {
    log.info("Registering Creative Beacons validator...");
    RuleTargetValidatorRegistry.registerValidator(RuleTargetType.CREATIVE_BEACON, this);
  }

  @Override
  public void accept(RuleTargetDTO ruleTargetDTO) {
    final Beacon creativeBeacon = readCreativeBeacon(ruleTargetDTO);
    doInTransaction(
        () -> {
          BrandProtectionTag brandProtectionTag =
              Optional.ofNullable(brandProtectionTagRepository.getOne(creativeBeacon.getTagId()))
                  .orElseThrow(
                      () ->
                          new GenevaValidationException(
                              ServerErrorCodes.SERVER_RULE_TARGET_DATA_INVALID_CREATIVE_BEACON));

          validateTagIsInCreativeBeaconCategory(brandProtectionTag);
          validateAllBeaconValuesAreDefinedForBrandProtectionTag(
              creativeBeacon, brandProtectionTag);
        });
  }

  private void doInTransaction(Runnable runnable) {
    transactionTemplate.execute(
        status -> {
          runnable.run();
          return null;
        });
  }

  private Beacon readCreativeBeacon(RuleTargetDTO ruleTargetDTO) {
    List<Beacon> creativeBeacons;
    try {
      creativeBeacons =
          objectMapper.readValue(
              ruleTargetDTO.getData(),
              TypeFactory.defaultInstance().constructCollectionType(List.class, Beacon.class));
    } catch (IOException e) {
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_RULE_TARGET_DATA_INVALID_CREATIVE_BEACON_FORMAT);
    }

    if (creativeBeacons.size() != 1 || isEmpty(creativeBeacons.get(0).tagValues)) {
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_RULE_TARGET_DATA_INVALID_CREATIVE_BEACON_FORMAT);
    }

    return creativeBeacons.get(0);
  }

  private void validateTagIsInCreativeBeaconCategory(BrandProtectionTag brandProtectionTag) {
    if (!CREATIVE_BEACON_CATEGORY_PID.equals(brandProtectionTag.getCategory().getPid())) {
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_RULE_TARGET_DATA_INVALID_CREATIVE_BEACON_CATEGORY);
    }
  }

  private void validateAllBeaconValuesAreDefinedForBrandProtectionTag(
      Beacon creativeBeacon, BrandProtectionTag brandProtectionTag) {
    List<String> bptValues =
        brandProtectionTag.getTagValues().stream()
            .map(BrandProtectionTagValues::getValue)
            .collect(toList());

    if (!bptValues.containsAll(creativeBeacon.tagValues)) {
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_RULE_TARGET_DATA_INVALID_CREATIVE_BEACON);
    }
  }

  @Getter
  protected static class Beacon {
    @JsonProperty("tag_id")
    Long tagId;

    @JsonProperty("tag_values")
    List<String> tagValues;
  }
}
