package com.nexage.app.util.validator.placement.nativeads.assets;

import static com.nexage.admin.core.enums.nativeads.NativeAssetRule.REQ_ALL;
import static com.nexage.admin.core.enums.nativeads.NativeAssetRule.REQ_NONE;
import static java.util.Collections.EMPTY_SET;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import com.nexage.admin.core.enums.nativeads.NativeAssetRule;
import com.nexage.admin.core.enums.nativeads.NativeAssetType;
import com.nexage.app.dto.seller.nativeads.WebNativePlacementExtensionDTO;
import com.nexage.app.dto.seller.nativeads.asset.NativeAssetSetDTO;
import com.nexage.app.dto.seller.nativeads.asset.type.NativeAssetDTO;
import com.nexage.app.dto.seller.nativeads.asset.type.NativeDataAssetDTO;
import com.nexage.app.dto.seller.nativeads.asset.type.NativeImageAssetDTO;
import com.nexage.app.util.validator.BaseValidator;
import com.nexage.app.util.validator.ValidationUtils;
import com.nexage.app.util.validator.placement.nativeads.template.TemplateEngineFacade;
import com.nexage.app.util.validator.placement.nativeads.template.TemplateInfo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import javax.validation.ConstraintValidatorContext;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

@Log4j2
public class ValidAssetsWithTemplateValidator
    extends BaseValidator<ValidAssetsWithTemplate, WebNativePlacementExtensionDTO> {

  private static final String LINK_URL = "-linkUrl";
  private static final String VALUE = "-value";
  private static final String SOURCE_URL = "-sourceUrl";
  private static final String ASSET_SET_FIELD = "asset-set";

  private static final String PH_PRIVACY_LINK = "privacyLink";
  private static final Map<String, Set<String>> unconventionalPlaceholders =
      Map.of(PH_PRIVACY_LINK, Set.of(PH_PRIVACY_LINK));

  @Autowired private TemplateEngineFacade templateEngineFacade;

  @Override
  public boolean isValid(
      WebNativePlacementExtensionDTO nativePlacementExtension, ConstraintValidatorContext context) {
    boolean isValid;

    String html =
        StringEscapeUtils.unescapeJava(nativePlacementExtension.getRenderingTemplate()).trim();
    Set<NativeAssetSetDTO> assetSets = nativePlacementExtension.getAssetSets();
    TemplateInfo templateInfo = templateEngineFacade.processTemplate(html);

    isValid =
        isNotEmpty(assetSets)
            && isHtmlContainsOnlyAllowedTemplateTokens(context, templateInfo)
            && isMandatoryAssetsValid(context, templateInfo, assetSets)
            && isOptionalAssetsValid(context, templateInfo, assetSets);

    return isValid;
  }

  private boolean isHtmlContainsOnlyAllowedTemplateTokens(
      ConstraintValidatorContext context, TemplateInfo templateInfo) {
    boolean isTemplateOk = true;
    if (!isEmpty(templateInfo.getNotAllowedMarks())) {
      createCustomValidationErrorForInvalidTemplateEngineMarks(
          context, templateInfo.getNotAllowedMarks());
      isTemplateOk = false;
    }
    return isTemplateOk;
  }

  private boolean isOptionalAssetsValid(
      ConstraintValidatorContext context,
      TemplateInfo templateInfo,
      Set<NativeAssetSetDTO> assetSets) {
    Set<NativeAssetDTO> optionalAssets = getOptionalAssetSets(assetSets);
    Set<String> existingMandatoryPH = getExistingMandatoryPlaceholders(assetSets);

    Set<String> conditionalPlaceholders = templateInfo.getPlaceholdersInsideConditionMap().keySet();
    return !areThereAssetsWithoutHtmlPlaceholder(context, optionalAssets, conditionalPlaceholders)
        && !areTherePlaceholdersWithoutAssets(context, optionalAssets, conditionalPlaceholders)
        && !areThereConditionalPlaceholdersWithInvalidContent(
            context, templateInfo, existingMandatoryPH);
  }

  private Set<String> getExistingMandatoryPlaceholders(Set<NativeAssetSetDTO> assetSets) {
    Set<NativeAssetDTO> mandatoryAssets = getMandatoryAssetSets(assetSets);
    return mandatoryAssets.stream()
        .flatMap(nativeAsset -> nativeAsset.getPlaceholders().stream())
        .collect(toSet());
  }

  private boolean areThereConditionalPlaceholdersWithInvalidContent(
      ConstraintValidatorContext context,
      TemplateInfo templateInfo,
      Set<String> allExistingMandatoryPH) {
    List<String> conditionalPlaceholdersWithInvalidContent =
        getConditionalPlaceholdersWithInvalidContent(templateInfo, allExistingMandatoryPH);

    if (isNotEmpty(conditionalPlaceholdersWithInvalidContent)) {
      createCustomValidationErrorForInvalidConditionalPlaceholder(
          context, conditionalPlaceholdersWithInvalidContent);
      return true;
    }
    return false;
  }

  private Set<NativeAssetDTO> getOptionalAssetSets(Set<NativeAssetSetDTO> assetSets) {
    Optional<NativeAssetSetDTO> optOptionalAssetSet = getAssetSetByRule(assetSets, REQ_NONE);
    return optOptionalAssetSet.map(NativeAssetSetDTO::getAssets).orElse(EMPTY_SET);
  }

  private Set<NativeAssetDTO> getMandatoryAssetSets(Set<NativeAssetSetDTO> assetSets) {
    Optional<NativeAssetSetDTO> optOptionalAssetSet = getAssetSetByRule(assetSets, REQ_ALL);
    return optOptionalAssetSet.map(NativeAssetSetDTO::getAssets).orElse(EMPTY_SET);
  }

  private boolean areThereAssetsWithoutHtmlPlaceholder(
      ConstraintValidatorContext context,
      Set<NativeAssetDTO> optionalAssets,
      Set<String> placeholders) {
    List<NativeAssetDTO> assetsMissingInHtml = getAssetsMissingInHtml(placeholders, optionalAssets);
    if (isNotEmpty(assetsMissingInHtml)) {
      createCustomValidationErrorForMissingPlaceholders(context, assetsMissingInHtml);
      return true;
    }
    return false;
  }

  private boolean isMandatoryAssetsValid(
      ConstraintValidatorContext context,
      TemplateInfo templateInfo,
      Set<NativeAssetSetDTO> assetSets) {

    Optional<NativeAssetSetDTO> optMandatoryAssetSet = getAssetSetByRule(assetSets, REQ_ALL);
    return optMandatoryAssetSet.isPresent()
        && isValidAssetsAndHtml(context, templateInfo, optMandatoryAssetSet.get().getAssets());
  }

  private boolean isValidAssetsAndHtml(
      ConstraintValidatorContext context,
      TemplateInfo templateInfo,
      Set<NativeAssetDTO> mandatoryAssets) {
    Set<String> placeholdersForMandatoryAssets = templateInfo.getNonConditionalPlaceholders();

    if (areThereAssetsWithoutHtmlPlaceholder(
        context, mandatoryAssets, placeholdersForMandatoryAssets)) {
      return false;
    }

    return !areTherePlaceholdersWithoutAssets(
        context, mandatoryAssets, placeholdersForMandatoryAssets);
  }

  private Optional<NativeAssetSetDTO> getAssetSetByRule(
      Set<NativeAssetSetDTO> assetSets, NativeAssetRule rule) {
    return assetSets.stream()
        .filter(nativeAssetSetDTO -> nativeAssetSetDTO.getRule() == rule)
        .findFirst();
  }

  private boolean areTherePlaceholdersWithoutAssets(
      ConstraintValidatorContext context,
      Set<NativeAssetDTO> mandatoryAssets,
      Set<String> placeholdersForMandatoryAssets) {
    List<String> placeholdersWithoutAsset =
        getPlaceholdersWithoutAsset(placeholdersForMandatoryAssets, mandatoryAssets);
    if (isNotEmpty(placeholdersWithoutAsset)) {
      createCustomValidationErrorForMissingAsset(context, placeholdersWithoutAsset);
      return true;
    }
    return false;
  }

  private List<String> getConditionalPlaceholdersWithInvalidContent(
      TemplateInfo templateInfo, Set<String> allExistingMandatoryPH) {
    return templateInfo.getPlaceholdersInsideConditionMap().entrySet().stream()
        .filter(
            placeholderInfo -> {
              List<String> internalPlaceholders = placeholderInfo.getValue();
              return isWrongConditionalContent(
                  placeholderInfo, internalPlaceholders, allExistingMandatoryPH);
            })
        .map(Map.Entry::getKey)
        .collect(toList());
  }

  private boolean isWrongConditionalContent(
      Map.Entry<String, List<String>> placeholderInfo,
      List<String> internalPlaceholders,
      Set<String> existingMandatoryPlaceholders) {
    if (internalPlaceholders.isEmpty()) {
      return false;
    }
    return internalPlaceholders.stream()
        .filter(Objects::nonNull)
        .anyMatch(
            internalPlaceholder ->
                unconventionalPlaceholders.containsKey(placeholderInfo.getKey())
                    ? isUnconventionalPlaceholderHaveWrongName(
                        placeholderInfo.getKey(), internalPlaceholder)
                    : isPlaceholderHaveWrongName(
                        placeholderInfo.getKey(),
                        internalPlaceholder,
                        existingMandatoryPlaceholders));
  }

  private boolean isUnconventionalPlaceholderHaveWrongName(
      String placeholderName, String internalPlaceholder) {
    return !unconventionalPlaceholders.get(placeholderName).contains(internalPlaceholder);
  }

  private boolean isPlaceholderHaveWrongName(
      String placeholderName, String internalPlaceholder, Set<String> mandatoryPlaceholders) {
    String key = placeholderName.split("-")[0];
    List<String> allowedInternalPlaceholders =
        new ArrayList<>(Arrays.asList(key + VALUE, key + SOURCE_URL, key + LINK_URL));
    allowedInternalPlaceholders.addAll(mandatoryPlaceholders);

    return allowedInternalPlaceholders.stream()
        .noneMatch(internal -> internal.equals(internalPlaceholder));
  }

  private void createCustomValidationErrorForMissingPlaceholders(
      ConstraintValidatorContext context, List<NativeAssetDTO> assetsMissingInHtml) {
    String message =
        assetsMissingInHtml.stream().map(this::getMissingAssetName).collect(joining(", "));
    String fullMessage = context.getDefaultConstraintMessageTemplate() + message;
    context.disableDefaultConstraintViolation();
    ValidationUtils.addConstraintMessage(context, ASSET_SET_FIELD, fullMessage);
  }

  private void createCustomValidationErrorForMissingAsset(
      ConstraintValidatorContext context, List<String> placeholders) {
    String message =
        "There is a mismatch for the following placeholder(s) with relation to their asset(s): "
            + placeholders.stream().map(ph -> "'" + ph + "'").collect(joining(", "));
    String fullMessage = context.getDefaultConstraintMessageTemplate() + message;
    context.disableDefaultConstraintViolation();
    ValidationUtils.addConstraintMessage(context, ASSET_SET_FIELD, fullMessage);
  }

  private void createCustomValidationErrorForInvalidConditionalPlaceholder(
      ConstraintValidatorContext context, List<String> placeholders) {
    String message =
        "Following conditional placeholder(s) have invalid or missing content: "
            + placeholders.stream().map(ph -> "'" + ph + "'").collect(joining(", "));
    String fullMessage = context.getDefaultConstraintMessageTemplate() + message;
    context.disableDefaultConstraintViolation();
    ValidationUtils.addConstraintMessage(context, ASSET_SET_FIELD, fullMessage);
  }

  private void createCustomValidationErrorForInvalidTemplateEngineMarks(
      ConstraintValidatorContext context, Set<String> notAllowedMarks) {
    String message =
        "Following Template engine mark(s) are not allowed in HTML template: "
            + notAllowedMarks.stream().map(ph -> "'" + ph + "'").collect(joining(", "));
    String fullMessage = context.getDefaultConstraintMessageTemplate() + message;
    context.disableDefaultConstraintViolation();
    ValidationUtils.addConstraintMessage(context, ASSET_SET_FIELD, fullMessage);
  }

  private String getMissingAssetName(NativeAssetDTO asset) {
    return "Mandatory Asset which does not appear in HTML template: "
        + "'"
        + asset.getType().toString()
        + "'"
        + getSubType(asset);
  }

  private String getSubType(NativeAssetDTO asset) {
    if (asset.getType() == NativeAssetType.IMAGE) {
      return " and sub type of '" + ((NativeImageAssetDTO) asset).getImage().getType() + "'";
    }
    if (asset.getType() == NativeAssetType.DATA) {
      return " and sub type of '" + ((NativeDataAssetDTO) asset).getData().getType() + "'";
    }
    return StringUtils.EMPTY;
  }

  private List<NativeAssetDTO> getAssetsMissingInHtml(
      Set<String> placeholders, Set<NativeAssetDTO> assets) {
    return assets.stream()
        .filter(asset -> isAssetsWithoutPlaceholder(placeholders, asset))
        .collect(toList());
  }

  private List<String> getPlaceholdersWithoutAsset(
      Set<String> placeholders, Set<NativeAssetDTO> assets) {
    List<String> allowedPlaceholders =
        assets.stream()
            .flatMap(nativeAsset -> nativeAsset.getPlaceholders().stream())
            .collect(toList());
    return placeholders.stream()
        .filter(
            placeholder ->
                !allowedPlaceholders.contains(placeholder)
                    && !unconventionalPlaceholders.containsKey(placeholder))
        .collect(toList());
  }

  private boolean isAssetsWithoutPlaceholder(Set<String> placeholders, NativeAssetDTO asset) {
    String mandatoryPlaceholder = asset.getMandatoryPlaceholder();
    return !placeholders.contains(mandatoryPlaceholder);
  }
}
