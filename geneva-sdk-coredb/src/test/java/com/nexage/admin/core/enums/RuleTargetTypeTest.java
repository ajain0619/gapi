package com.nexage.admin.core.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class RuleTargetTypeTest {

  private static final String VALID_TARGET_DATA_3_KVPS =
      "/keyValuePairs/valid-target-data-3-kvps.json";
  private static final String VALID_TARGET_DATA_1_KVP =
      "/keyValuePairs/valid-target-data-1-kvp.json";
  private static final String INVALID_TARGET_DATA_16_KVPS =
      "/keyValuePairs/invalid-target-data-16-kvps.json";

  @Test
  void whenTargetTypeIsMultiAdSize_thenCategoryIsBuyer() {
    RuleTargetType targetType = RuleTargetType.MULTI_AD_SIZE;
    assertEquals(
        RuleTargetCategory.BUYER,
        targetType.getCategory(),
        "Multi Ad Size category should be BUYER");
  }

  @Test
  void shouldReturnCorrectCategoryForContentChannelTargetType() {
    RuleTargetType targetType = RuleTargetType.CONTENT_CHANNEL;
    assertEquals(
        RuleTargetCategory.SUPPLY,
        targetType.getCategory(),
        "CONTENT CHANNEL category should be SUPPLY");
  }

  @Test
  void shouldReturnCorrectCategoryForContentSeriesTargetType() {
    RuleTargetType targetType = RuleTargetType.CONTENT_SERIES;
    assertEquals(
        RuleTargetCategory.SUPPLY,
        targetType.getCategory(),
        "CONTENT SERIES category should be SUPPLY");
  }

  @Test
  void shouldReturnCorrectCategoryForContentRatingTargetType() {
    RuleTargetType targetType = RuleTargetType.CONTENT_RATING;
    assertEquals(
        RuleTargetCategory.SUPPLY,
        targetType.getCategory(),
        "CONTENT RATING category should be SUPPLY");
  }

  @Test
  void shouldTargetTypeBeContentGenreThenCategoryIsSupply() {
    RuleTargetType targetType = RuleTargetType.CONTENT_GENRE;
    assertEquals(
        RuleTargetCategory.SUPPLY,
        targetType.getCategory(),
        "CONTENT GENRE category should be SUPPLY");
  }

  @Test
  void shouldTargetTypeBeAdFormatTypeThenCategoryIsBuyer() {
    RuleTargetType targetType = RuleTargetType.AD_FORMAT_TYPE;
    assertEquals(
        RuleTargetCategory.BUYER,
        targetType.getCategory(),
        "Ad Format Type category should be BUYER");
  }

  @Test
  void shouldValidateAdFormatTypeAsIntValue() {
    RuleTargetType targetType = RuleTargetType.AD_FORMAT_TYPE;
    assertEquals(41, targetType.asInt(), "Ad Format Type as int value should be 41");
  }

  @Test
  void shouldValidateAdFormatTypeFromIntValue() {
    RuleTargetType targetType = RuleTargetType.AD_FORMAT_TYPE;
    assertEquals(
        RuleTargetType.AD_FORMAT_TYPE,
        RuleTargetType.AD_FORMAT_TYPE.fromInt(41),
        "RuleTargetType from int 41 should be AD_FORMAT_TYPE");
  }

  @Test
  void shouldTargetTypeBePublisherThenCategoryIsSupply() {
    RuleTargetType targetType = RuleTargetType.PUBLISHER;
    assertEquals(
        RuleTargetCategory.SUPPLY, targetType.getCategory(), "Publisher category should be SUPPLY");
  }

  @Test
  void shouldValidatePublisherAsIntValue() {
    RuleTargetType targetType = RuleTargetType.PUBLISHER;
    assertEquals(42, targetType.asInt(), "Publisher as int value should be 42");
  }

  @Test
  void shouldValidatePublisherFromIntValue() {
    assertEquals(
        RuleTargetType.PUBLISHER,
        RuleTargetType.PUBLISHER.fromInt(42),
        "RuleTargetType from int 42 should be PUBLISHER");
  }

  @Test
  void shouldTargetTypeBeRevgroupThenCategoryIsSupply() {
    RuleTargetType targetType = RuleTargetType.REVGROUP;
    assertEquals(
        RuleTargetCategory.SUPPLY, targetType.getCategory(), "Revgroup category should be SUPPLY");
  }

  @Test
  void shouldValidateRevgroupAsIntValue() {
    RuleTargetType targetType = RuleTargetType.REVGROUP;
    assertEquals(43, targetType.asInt(), "Revgroup as int value should be 43");
  }

  @Test
  void shouldValidateRevgroupFromIntValue() {
    assertEquals(
        RuleTargetType.REVGROUP,
        RuleTargetType.PUBLISHER.fromInt(43),
        "RuleTargetType from int 43 should be REVGROUP");
  }

  @Test
  void shouldTargetTypeBeVideoCompletionRateThenCategoryIsSupply() {
    RuleTargetType targetType = RuleTargetType.VIDEO_COMPLETION_RATE;
    assertEquals(
        RuleTargetCategory.SUPPLY,
        targetType.getCategory(),
        "Completion rate category should be SUPPLY");
  }

  @Test
  void shouldValidateVideoCompletionRateAsIntValue() {
    RuleTargetType targetType = RuleTargetType.VIDEO_COMPLETION_RATE;
    assertEquals(44, targetType.asInt(), "Completion rate as int value should be 44");
  }

  @Test
  void shouldValidateVideoCompletionRateFromIntValue() {
    assertEquals(
        RuleTargetType.VIDEO_COMPLETION_RATE,
        RuleTargetType.VIDEO_COMPLETION_RATE.fromInt(44),
        "RuleTargetType from int 44 should be VIDEO_COMPLETION_RATE");
  }

  @Test
  void shouldValidateVideoCompletionRateTargetDataIsString() {
    RuleTargetType.VIDEO_COMPLETION_RATE.validateTargetData("0.5");
  }

  @Test
  void shouldHaveIntegerRepresentation46WhenTypeIsPlaylistRenderingCapability() {
    assertEquals(46, RuleTargetType.PLAYLIST_RENDERING_CAPABILITY.asInt());
  }

  @Test
  void shouldBeSupplyCategoryWhenTypeIsPlaylistRenderingCapability() {
    assertEquals(
        RuleTargetCategory.SUPPLY, RuleTargetType.PLAYLIST_RENDERING_CAPABILITY.getCategory());
  }

  @Test
  void shouldValidateContentLivestreamAsIntValue() {
    RuleTargetType targetType = RuleTargetType.CONTENT_LIVESTREAM;
    assertEquals(49, targetType.asInt(), "CONTENT LIVESTREAM category should be SUPPLY");
  }

  @Test
  void shouldValidateContentLivestreamFromIntValue() {
    assertEquals(
        RuleTargetType.CONTENT_LIVESTREAM,
        RuleTargetType.CONTENT_LIVESTREAM.fromInt(49),
        "RuleTargetType from int 49 should be CONTENT_LIVESTREAM");
  }

  @Test
  void shouldRuleTargetCategoryBeBuyerWhenRuleTargetTypeIsDealCategory() {
    RuleTargetType targetType = RuleTargetType.DEAL_CATEGORY;
    assertEquals(
        RuleTargetCategory.BUYER, targetType.getCategory(), "DEAL_CATEGORY should be BUYER");
  }

  @Test
  void shouldValidateDealCategoryAsIntValue() {
    RuleTargetType targetType = RuleTargetType.DEAL_CATEGORY;
    assertEquals(51, targetType.asInt(), "DEAL_CATEGORY as int value should be 51");
  }

  @Test
  void shouldValidateDealCategoryFromIntValue() {
    assertEquals(
        RuleTargetType.DEAL_CATEGORY,
        RuleTargetType.DEAL_CATEGORY.fromInt(51),
        "RuleTargetType from int 51 should be DEAL_CATEGORY");
  }

  @Test
  void shouldValidateDealCategoryTargetDataIsString() {
    RuleTargetType.DEAL_CATEGORY.validateTargetData("1,2");
  }

  @Test
  void shouldValidateContentLanguageAsIntValue() {
    RuleTargetType targetType = RuleTargetType.CONTENT_LANGUAGE;
    assertEquals(50, targetType.asInt(), "CONTENT_LANGUAGE as int value should be 50");
  }

  @Test
  void shouldValidateContentLanguageFromIntValue() {
    assertEquals(
        RuleTargetType.CONTENT_LANGUAGE,
        RuleTargetType.CONTENT_LANGUAGE.fromInt(50),
        "RuleTargetType from int 50 should be CONTENT_LANGUAGE");
  }
}
