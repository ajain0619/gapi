package com.nexage.admin.core.specification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.repository.BrandProtectionTagRepository;
import com.nexage.admin.core.specification.impl.BrandProtectionChildrenSpecificationServiceImpl;
import com.nexage.admin.core.specification.impl.BrandProtectionDefaultSpecificationServiceImpl;
import com.nexage.admin.core.specification.impl.BrandProtectionParentsWithMatchingCategoryServiceImpl;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import org.apache.commons.collections.MapUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRED)
@Sql(
    scripts = "/data/repository/brand-protection-repository.sql",
    config = @SqlConfig(encoding = "utf-8"))
class BrandProtectionParentsWithMatchingCategoryIT extends CoreDbSdkIntegrationTestBase {
  private static Map<String, Object> parentSearchParameter =
      Map.of(
          BrandProtectionChildrenSpecificationServiceImpl.CATEGORY_ID,
          6L,
          BrandProtectionDefaultSpecificationServiceImpl.QF,
          "name",
          BrandProtectionDefaultSpecificationServiceImpl.QT,
          "Advertising");

  private static Stream<Arguments> specificationEligibility() {
    return Stream.of(
        Arguments.of(parentSearchParameter, true),
        Arguments.of(
            MapUtils.putAll(
                new HashMap(),
                new Object[] {BrandProtectionDefaultSpecificationServiceImpl.QT, null}),
            false));
  }

  @Autowired private BrandProtectionTagRepository tagRepository;

  @Autowired
  private BrandProtectionParentsWithMatchingCategoryServiceImpl
      brandProtectionParentsWithMatchingCategory;

  @Test
  void shouldCorrectlySelectBrandProtectionTagsWithSearchTerm() {

    var brandProtectionDefaultTags =
        tagRepository.findAll(
            brandProtectionParentsWithMatchingCategory.selector(parentSearchParameter));
    assertEquals("Business", brandProtectionDefaultTags.get(0).getParentTag().getName());
  }

  @ParameterizedTest
  @MethodSource("specificationEligibility")
  void shouldCorrectlyCheckForParentsWithMatchingSpecificationEligibility(
      Map<String, Object> parentSearchParameters, boolean expected) {
    var valid = brandProtectionParentsWithMatchingCategory.isValid(parentSearchParameters);
    assertEquals(expected, valid);
  }

  @Test
  void shouldExpectsChildSpecificationIsFalse() {
    var valid = brandProtectionParentsWithMatchingCategory.isChildSpecification();
    assertFalse(valid);
  }
}
