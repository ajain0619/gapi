package com.nexage.app.util.validator.placement.nativeads.assets;

import static com.nexage.admin.core.enums.nativeads.NativeAssetRule.REQ_ALL;
import static com.nexage.admin.core.enums.nativeads.NativeAssetRule.REQ_NONE;
import static com.nexage.admin.core.enums.nativeads.NativeAssetRule.REQ_ONE_PLUS;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.app.dto.seller.nativeads.WebNativePlacementExtensionDTO;
import com.nexage.app.dto.seller.nativeads.asset.NativeAssetSetDTO;
import com.nexage.app.dto.seller.nativeads.validators.BaseTestWebNativeExtention;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RulesOnAssetSetValidatorTest extends BaseTestWebNativeExtention {

  private RulesOnAssetSetValidator validator = new RulesOnAssetSetValidator();

  @SneakyThrows
  @Test
  void verifyInvalid_secondAssetHas_REQ_ONE_PLUS() {
    WebNativePlacementExtensionDTO webNativePlacementExtensionDTO = prepareTest();

    // REQ_ALL & REQ_ONE_PLUS:
    webNativePlacementExtensionDTO.getAssetSets().stream()
        .filter(set -> set.getRule() == REQ_NONE)
        .findFirst()
        .get()
        .setRule(REQ_ONE_PLUS);

    boolean valid = validator.isValid(webNativePlacementExtensionDTO, context);

    assertFalse(valid);
  }

  @SneakyThrows
  @Test
  void verifyInvalid_secondAssetHas_REQ_ALL() {
    WebNativePlacementExtensionDTO webNativePlacementExtensionDTO = prepareTest();

    // REQ_ALL & REQ_ALL:
    webNativePlacementExtensionDTO.getAssetSets().stream()
        .filter(set -> set.getRule() == REQ_NONE)
        .findFirst()
        .get()
        .setRule(REQ_ALL);

    boolean valid = validator.isValid(webNativePlacementExtensionDTO, context);

    assertFalse(valid);
  }

  @SneakyThrows
  @Test
  void verifyValid_singleAssetHas_REQ_NONE() {
    WebNativePlacementExtensionDTO webNativePlacementExtensionDTO = prepareTest();

    // set not-valid rule for the asset set:
    NativeAssetSetDTO req_all_set =
        webNativePlacementExtensionDTO.getAssetSets().stream()
            .filter(set -> set.getRule() == REQ_ALL)
            .findFirst()
            .get();

    webNativePlacementExtensionDTO.getAssetSets().remove(req_all_set);

    boolean valid = validator.isValid(webNativePlacementExtensionDTO, context);

    assertFalse(valid);
  }

  @SneakyThrows
  @Test
  void verifyValid_singleAssetHas_REQ_ALL() {
    WebNativePlacementExtensionDTO webNativePlacementExtensionDTO = prepareTest();

    // set not-valid rule for the asset set:
    NativeAssetSetDTO req_none_set =
        webNativePlacementExtensionDTO.getAssetSets().stream()
            .filter(set -> set.getRule() == REQ_NONE)
            .findFirst()
            .get();

    webNativePlacementExtensionDTO.getAssetSets().remove(req_none_set);

    boolean valid = validator.isValid(webNativePlacementExtensionDTO, context);

    assertTrue(valid);
  }

  @SneakyThrows
  @Test
  void verifyValid() {
    WebNativePlacementExtensionDTO webNativePlacementExtensionDTO = prepareTest();

    boolean valid = validator.isValid(webNativePlacementExtensionDTO, context);

    assertTrue(valid);
  }
}
