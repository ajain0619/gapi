package com.nexage.app.util.validator.placement.nativeads.assets;

import static com.nexage.admin.core.enums.nativeads.NativeAssetRule.REQ_ALL;
import static com.nexage.admin.core.enums.nativeads.NativeAssetRule.REQ_NONE;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.enums.nativeads.NativeAssetRule;
import com.nexage.app.dto.seller.nativeads.WebNativePlacementExtensionDTO;
import com.nexage.app.dto.seller.nativeads.asset.NativeAssetSetDTO;
import com.nexage.app.dto.seller.nativeads.asset.type.NativeAssetDTO;
import com.nexage.app.dto.seller.nativeads.asset.type.NativeTitleAssetDTO;
import com.nexage.app.dto.seller.nativeads.validators.BaseTestWebNativeExtention;
import com.nexage.app.util.validator.placement.nativeads.template.TemplateEngineFacade;
import com.nexage.app.util.validator.placement.nativeads.template.TemplateInfo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ValidAssetsWithTemplateValidatorTest extends BaseTestWebNativeExtention {

  private List<String> nonConditionalPlaceholders;
  private Map<String, List<String>> conditionalInfo;
  private Set<String> notAllowedMarks;

  @Mock TemplateEngineFacade engineFacade;

  @InjectMocks private ValidAssetsWithTemplateValidator validator;

  @BeforeEach
  public void init() {
    nonConditionalPlaceholders = getBasicNonConditionalPlaceholders();
    conditionalInfo = getBasicConditionalPlaceholders();
    notAllowedMarks = new HashSet<>();
    initContextMocks();
    lenient()
        .when(context.getDefaultConstraintMessageTemplate())
        .thenReturn("There is a problem with Assets and the provided HTML Template: ");
  }

  @SneakyThrows
  @Test
  void verify_Valid() {
    WebNativePlacementExtensionDTO nativePlacementExtension = prepareTest();
    TemplateInfo templateInfo =
        getTemplateInfo(nonConditionalPlaceholders, conditionalInfo, notAllowedMarks);
    when(engineFacade.processTemplate(anyString())).thenReturn(templateInfo);

    boolean valid = validator.isValid(nativePlacementExtension, context);

    assertTrue(valid);
  }

  @SneakyThrows
  @Test
  void verify_Invalid_WhenOptionalPhonePlaceholderInTemplateWithoutOptionalAsset() {
    WebNativePlacementExtensionDTO nativePlacementExtension = prepareTest();
    conditionalInfo.put("phoneData-value", List.of("phoneData-value"));
    TemplateInfo templateInfo =
        getTemplateInfo(nonConditionalPlaceholders, conditionalInfo, notAllowedMarks);
    when(engineFacade.processTemplate(anyString())).thenReturn(templateInfo);

    boolean valid = validator.isValid(nativePlacementExtension, context);

    assertFalse(valid);

    validateErrorMessage(List.of("'phoneData-value'"));
  }

  @SneakyThrows
  @Test
  void verify_Invalid_WhenMandatoryVideoAssetWithoutPlaceholder() {
    WebNativePlacementExtensionDTO nativePlacementExtension = prepareTest();
    addVideoToMandatoryAssets(nativePlacementExtension);

    TemplateInfo templateInfo =
        getTemplateInfo(nonConditionalPlaceholders, conditionalInfo, notAllowedMarks);
    when(engineFacade.processTemplate(anyString())).thenReturn(templateInfo);

    boolean valid = validator.isValid(nativePlacementExtension, context);

    assertFalse(valid);

    validateErrorMessage(List.of("'VIDEO'"));
  }

  @SneakyThrows
  @Test
  void verify_Valid_WhenConditionalTemplatePlaceholderContainEmptyContent() {
    WebNativePlacementExtensionDTO nativePlacementExtension = prepareTest();
    conditionalInfo.put("priceData-value", List.of());
    conditionalInfo.put("ctaData-value", List.of());

    TemplateInfo templateInfo =
        getTemplateInfo(nonConditionalPlaceholders, conditionalInfo, notAllowedMarks);
    when(engineFacade.processTemplate(anyString())).thenReturn(templateInfo);

    boolean valid = validator.isValid(nativePlacementExtension, context);

    assertTrue(valid);
  }

  @SneakyThrows
  @Test
  void verify_Invalid_WhenConditionalTemplatePlaceholderContainIncorrectContent() {
    WebNativePlacementExtensionDTO nativePlacementExtension = prepareTest();
    conditionalInfo.put("priceData-value", List.of("priceData-value", "video-value"));
    conditionalInfo.put("ctaData-value", List.of("video"));

    TemplateInfo templateInfo =
        getTemplateInfo(nonConditionalPlaceholders, conditionalInfo, notAllowedMarks);
    when(engineFacade.processTemplate(anyString())).thenReturn(templateInfo);

    boolean valid = validator.isValid(nativePlacementExtension, context);

    assertFalse(valid);

    validateErrorMessage(List.of("'priceData-value'", "'ctaData-value'"));
  }

  @SneakyThrows
  @Test
  void
      verify_valid_WhenConditionalTemplatePlaceholderContainCorrectContentWithMandatoryPlaceholder() {
    WebNativePlacementExtensionDTO nativePlacementExtension = prepareTest();
    conditionalInfo.put("priceData-value", List.of("priceData-value", "title-linkUrl"));
    nonConditionalPlaceholders.remove("title-linkUrl");

    TemplateInfo templateInfo =
        getTemplateInfo(nonConditionalPlaceholders, conditionalInfo, notAllowedMarks);
    when(engineFacade.processTemplate(anyString())).thenReturn(templateInfo);

    boolean valid = validator.isValid(nativePlacementExtension, context);

    assertTrue(valid);
  }

  @SneakyThrows
  @Test
  void verify_valid_WhenRecognizedUnconventionalPlaceholder() {
    WebNativePlacementExtensionDTO nativePlacementExtension = prepareTest();
    conditionalInfo.put("privacyLink", List.of("privacyLink"));
    nonConditionalPlaceholders.add("privacyLink");

    TemplateInfo templateInfo =
        getTemplateInfo(nonConditionalPlaceholders, conditionalInfo, notAllowedMarks);
    when(engineFacade.processTemplate(anyString())).thenReturn(templateInfo);

    boolean valid = validator.isValid(nativePlacementExtension, context);

    assertTrue(valid);
  }

  @SneakyThrows
  @Test
  void verify_invalid_WhenUnrecognizedUnconventionalPlaceholder() {
    WebNativePlacementExtensionDTO nativePlacementExtension = prepareTest();
    conditionalInfo.put("privacyLink", List.of("privacyLink", "invalidPlaceholder"));
    nonConditionalPlaceholders.add("privacyLink");

    TemplateInfo templateInfo =
        getTemplateInfo(nonConditionalPlaceholders, conditionalInfo, notAllowedMarks);
    when(engineFacade.processTemplate(anyString())).thenReturn(templateInfo);

    boolean valid = validator.isValid(nativePlacementExtension, context);

    assertFalse(valid);
  }

  @SneakyThrows
  @Test
  void
      verify_Valid_WhenConditionalTemplatePlaceholderContainIncorrectContentWithMandatoryPlaceholderAndOptionalPlaceholder() {
    WebNativePlacementExtensionDTO nativePlacementExtension = prepareTest();
    conditionalInfo.put(
        "priceData-value", List.of("priceData-value", "title-value", "video-value"));

    TemplateInfo templateInfo =
        getTemplateInfo(nonConditionalPlaceholders, conditionalInfo, notAllowedMarks);
    when(engineFacade.processTemplate(anyString())).thenReturn(templateInfo);

    boolean valid = validator.isValid(nativePlacementExtension, context);

    assertFalse(valid);

    validateErrorMessage(List.of("'priceData-value'"));
  }

  @SneakyThrows
  @Test
  void verify_Invalid_WhenPriceConditionalAssetHasItsPlaceholderNotInsideIf() {
    WebNativePlacementExtensionDTO nativePlacementExtension = prepareTest();

    conditionalInfo.remove("priceData-value");
    nonConditionalPlaceholders.addAll(Arrays.asList("priceData-value", "priceData-linkUrl"));

    TemplateInfo templateInfo =
        getTemplateInfo(nonConditionalPlaceholders, conditionalInfo, notAllowedMarks);
    when(engineFacade.processTemplate(anyString())).thenReturn(templateInfo);

    boolean valid = validator.isValid(nativePlacementExtension, context);

    assertFalse(valid);

    validateErrorMessage(List.of("'priceData-value'", "'priceData-linkUrl'"));
  }

  @SneakyThrows
  @Test
  void verify_Invalid_WhenPriceConditionalAssetHasNoConditionalPlacement() {
    WebNativePlacementExtensionDTO nativePlacementExtension = prepareTest();

    conditionalInfo.remove("priceData-value");

    TemplateInfo templateInfo =
        getTemplateInfo(nonConditionalPlaceholders, conditionalInfo, notAllowedMarks);
    when(engineFacade.processTemplate(anyString())).thenReturn(templateInfo);

    boolean valid = validator.isValid(nativePlacementExtension, context);

    assertFalse(valid);

    validateErrorMessage(List.of("'DATA'", "'PRICE'"));
  }

  @SneakyThrows
  @Test
  void verify_Invalid_WhenTitleMandatoryAssetHasItsPlaceholderInsideIf() {
    WebNativePlacementExtensionDTO nativePlacementExtension = prepareTest();

    moveTitleAssetFromMandatoryToOptional(nativePlacementExtension);

    TemplateInfo templateInfo =
        getTemplateInfo(nonConditionalPlaceholders, conditionalInfo, notAllowedMarks);
    when(engineFacade.processTemplate(anyString())).thenReturn(templateInfo);

    boolean valid = validator.isValid(nativePlacementExtension, context);

    assertFalse(valid);

    validateErrorMessage(List.of("'title-value'", "'title-linkUrl'"));
  }

  @SneakyThrows
  @Test
  void verify_Invalid_WhenTemplateContainNotAllowedTemplateMarks() {
    WebNativePlacementExtensionDTO nativePlacementExtension = prepareTest();
    notAllowedMarks.add("#elseIf");

    TemplateInfo templateInfo =
        getTemplateInfo(nonConditionalPlaceholders, conditionalInfo, notAllowedMarks);

    when(engineFacade.processTemplate(anyString())).thenReturn(templateInfo);
    boolean valid = validator.isValid(nativePlacementExtension, context);

    assertFalse(valid);

    validateErrorMessage(List.of("'#elseIf'"));
  }

  @SneakyThrows
  @Test
  void verify_Invalid_WhenNewVideoPlaceholderInTemplateButDoesNotBelongToAnySelectedAsset() {
    WebNativePlacementExtensionDTO nativePlacementExtension = prepareTest();
    nonConditionalPlaceholders.add("video-data");

    TemplateInfo templateInfo =
        getTemplateInfo(nonConditionalPlaceholders, conditionalInfo, notAllowedMarks);
    when(engineFacade.processTemplate(anyString())).thenReturn(templateInfo);

    boolean valid = validator.isValid(nativePlacementExtension, context);

    assertFalse(valid);

    validateErrorMessage(List.of("'video-data'"));
  }

  private void validateErrorMessage(List<String> expectedInvalidInMessage) {
    verify(context).buildConstraintViolationWithTemplate(contextCaptor.capture());
    String validationMessage = contextCaptor.getValue();
    expectedInvalidInMessage.forEach(expected -> assertTrue(validationMessage.contains(expected)));
  }

  private void moveTitleAssetFromMandatoryToOptional(
      WebNativePlacementExtensionDTO nativePlacementExtension) {
    NativeAssetSetDTO req_all = getAssetSetByRule(nativePlacementExtension, REQ_ALL).get();
    NativeAssetSetDTO req_none = getAssetSetByRule(nativePlacementExtension, REQ_NONE).get();
    NativeTitleAssetDTO title = getNativeTitleAssetDTO();
    req_all.getAssets().remove(title);
    req_none.getAssets().add(title);
  }

  private void addVideoToMandatoryAssets(WebNativePlacementExtensionDTO nativePlacementExtension) {
    NativeAssetDTO videoAsset = getNativeVideoAssetDTO();
    getAssetSetByRule(nativePlacementExtension, REQ_ALL).get().getAssets().add(videoAsset);
  }

  private Optional<NativeAssetSetDTO> getAssetSetByRule(
      WebNativePlacementExtensionDTO nativePlacementExtension, NativeAssetRule rule) {
    return nativePlacementExtension.getAssetSets().stream()
        .filter(set -> set.getRule() == rule)
        .findFirst();
  }

  private List<String> getBasicNonConditionalPlaceholders() {
    return new ArrayList<>(
        Arrays.asList(
            "mainImage-linkUrl",
            "title-linkUrl",
            "title-value",
            "iconImage-linkUrl",
            "iconImage-sourceUrl",
            "sponsoredData-linkUrl",
            "sponsoredData-value",
            "mainImage-sourceUrl"));
  }

  private TemplateInfo getTemplateInfo(
      List<String> basicNonConditionalPlaceholders,
      Map<String, List<String>> basicConditionalInfo,
      Set<String> notAllowedMarks) {
    return TemplateInfo.builder()
        .nonConditionalPlaceholders(new HashSet<>(basicNonConditionalPlaceholders))
        .placeholdersInsideConditionMap(basicConditionalInfo)
        .notAllowedMarks(notAllowedMarks)
        .build();
  }

  private Map<String, List<String>> getBasicConditionalPlaceholders() {
    Map<String, List<String>> expectedConditions = new HashMap<>();
    expectedConditions.put("ctaData-value", List.of("ctaData-linkUrl", "ctaData-value"));
    expectedConditions.put("priceData-value", List.of("priceData-value", "priceData-linkUrl"));
    return expectedConditions;
  }
}
