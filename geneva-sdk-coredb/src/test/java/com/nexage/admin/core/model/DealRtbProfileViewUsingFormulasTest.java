package com.nexage.admin.core.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.admin.core.enums.PlacementCategory;
import com.nexage.admin.core.enums.VideoSupport;
import com.nexage.admin.core.enums.site.Platform;
import com.nexage.admin.core.sparta.jpa.model.DealTagRuleViewNoTagReference;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;
import org.apache.commons.lang.reflect.FieldUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class DealRtbProfileViewUsingFormulasTest {

  private static Stream<Arguments> provideGetCategoriesValues() {
    return Stream.of(
        Arguments.of(null, new HashSet<>()),
        Arguments.of("category1,category2", Set.of("category1", "category2")));
  }

  @ParameterizedTest
  @MethodSource("provideGetCategoriesValues")
  void shouldCorrectlyResolveCategories(String categories, Set<String> expectedResult)
      throws IllegalAccessException {
    // given
    DealRtbProfileViewUsingFormulas dealRtbProfileViewUsingFormulas =
        new DealRtbProfileViewUsingFormulas();
    FieldUtils.writeField(dealRtbProfileViewUsingFormulas, "categories", categories, true);

    // when
    Set<String> result = dealRtbProfileViewUsingFormulas.getCategories();

    // then
    assertEquals(expectedResult, result);
  }

  @Test
  void shouldResolveNullTargetToAnEmptyCountriesSet() {
    // given
    DealRtbProfileViewUsingFormulas dealRtbProfileViewUsingFormulas =
        new DealRtbProfileViewUsingFormulas();

    // when & then
    assertTrue(dealRtbProfileViewUsingFormulas.getCountries().isEmpty());
  }

  @Test
  void shouldResolveNotNullTargetToSetOfOneDealTagRuleView() throws IllegalAccessException {
    // given
    DealRtbProfileViewUsingFormulas dealRtbProfileViewUsingFormulas =
        new DealRtbProfileViewUsingFormulas();
    FieldUtils.writeField(dealRtbProfileViewUsingFormulas, "target", "target", true);
    FieldUtils.writeField(dealRtbProfileViewUsingFormulas, "countryPid", 1L, true);
    FieldUtils.writeField(dealRtbProfileViewUsingFormulas, "ruleType", "ruleType", true);
    FieldUtils.writeField(dealRtbProfileViewUsingFormulas, "targetType", "targetType", true);

    // when
    Set<DealTagRuleViewNoTagReference> countries = dealRtbProfileViewUsingFormulas.getCountries();

    // then
    assertEquals(1, countries.size());
    DealTagRuleViewNoTagReference dealTagRuleViewNoTagReference = countries.iterator().next();
    assertEquals("target", dealTagRuleViewNoTagReference.getTarget());
    assertEquals(1L, dealTagRuleViewNoTagReference.getPid().longValue());
    assertEquals("ruleType", dealTagRuleViewNoTagReference.getRuleType());
    assertEquals("targetType", dealTagRuleViewNoTagReference.getTargetType());
  }

  @Test
  void shouldCorrectlyResolvePlatformToPlatformEnumValue() throws IllegalAccessException {
    // given
    DealRtbProfileViewUsingFormulas dealRtbProfileViewUsingFormulas =
        new DealRtbProfileViewUsingFormulas();
    FieldUtils.writeField(dealRtbProfileViewUsingFormulas, "platform", "ANDROID", true);

    // when & then
    assertEquals(Platform.ANDROID, dealRtbProfileViewUsingFormulas.getPlatform());
  }

  @Test
  void shouldCorrectlyResolveVideoSupportToVideoSupportEnumValue() throws IllegalAccessException {
    // given
    DealRtbProfileViewUsingFormulas dealRtbProfileViewUsingFormulas =
        new DealRtbProfileViewUsingFormulas();
    FieldUtils.writeField(dealRtbProfileViewUsingFormulas, "videoSupport", 1, true);

    // when & then
    assertEquals(VideoSupport.VIDEO, dealRtbProfileViewUsingFormulas.getVideoSupport());
  }

  @Test
  void shouldResolveNullVideoSupportToNull() {
    // given
    DealRtbProfileViewUsingFormulas dealRtbProfileViewUsingFormulas =
        new DealRtbProfileViewUsingFormulas();

    // when & then
    assertNull(dealRtbProfileViewUsingFormulas.getVideoSupport());
  }

  @Test
  void shouldCorrectlyResolvePlacementCategoryToPlacementCategoryEnumValue()
      throws IllegalAccessException {
    // given
    DealRtbProfileViewUsingFormulas dealRtbProfileViewUsingFormulas =
        new DealRtbProfileViewUsingFormulas();
    FieldUtils.writeField(dealRtbProfileViewUsingFormulas, "placementCategory", 3, true);

    // when & then
    assertEquals(PlacementCategory.NATIVE, dealRtbProfileViewUsingFormulas.getPlacementType());
  }

  @Test
  void shouldResolveNullPlacementCategoryToNull() {
    // given
    DealRtbProfileViewUsingFormulas dealRtbProfileViewUsingFormulas =
        new DealRtbProfileViewUsingFormulas();

    // when & then
    assertNull(dealRtbProfileViewUsingFormulas.getPlacementCategory());
  }

  private static Stream<Arguments> provideGetIncludeSiteNameValues() {
    return Stream.of(Arguments.of(null, "true"), Arguments.of(1, "true"), Arguments.of(2, "false"));
  }

  @ParameterizedTest
  @MethodSource("provideGetIncludeSiteNameValues")
  void shouldCorrectlyResolveIncludeSiteName(Integer includeSiteName, String expectedResult)
      throws IllegalAccessException {
    // given
    DealRtbProfileViewUsingFormulas dealRtbProfileViewUsingFormulas =
        new DealRtbProfileViewUsingFormulas();
    FieldUtils.writeField(
        dealRtbProfileViewUsingFormulas, "includeSiteName", includeSiteName, true);

    // when
    String result = dealRtbProfileViewUsingFormulas.getIsRealName();

    // then
    assertEquals(expectedResult, result);
  }
}
