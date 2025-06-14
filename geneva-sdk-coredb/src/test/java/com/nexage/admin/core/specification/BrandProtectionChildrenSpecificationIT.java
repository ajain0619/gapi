package com.nexage.admin.core.specification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.repository.BrandProtectionTagRepository;
import com.nexage.admin.core.specification.impl.BrandProtectionChildrenSpecificationServiceImpl;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRED)
@Sql(
    scripts = "/data/repository/brand-protection-repository.sql",
    config = @SqlConfig(encoding = "utf-8"))
class BrandProtectionChildrenSpecificationIT extends CoreDbSdkIntegrationTestBase {
  private static Map<String, Object> childrenParameters =
      Map.of(
          BrandProtectionChildrenSpecificationServiceImpl.PARENT_TAG_PID,
          670L,
          BrandProtectionChildrenSpecificationServiceImpl.CATEGORY_ID,
          6L);

  @Autowired private BrandProtectionTagRepository tagRepository;

  @Autowired
  private BrandProtectionChildrenSpecificationServiceImpl brandProtectionChildrenSpecification;

  @Test
  void shouldCorrectlySelectBrandProtectionTagsWithParentTagIdAndCategoryId() {

    var brandProtectionChildrenTagsCount =
        tagRepository.count(brandProtectionChildrenSpecification.selector(childrenParameters));
    assertEquals(3, brandProtectionChildrenTagsCount);
  }

  @Test
  void shouldCorrectlyCheckForChildrenSpecificationEligibility() {
    var valid = brandProtectionChildrenSpecification.isValid(childrenParameters);
    assertTrue(valid);
  }

  @Test
  void shouldCorrectlySelectBrandProtectionTagsWithParentTagIdCategoryIdAndQT() {
    var searchParameters =
        Map.<String, Object>of(
            BrandProtectionChildrenSpecificationServiceImpl.QF,
            Set.of("name"),
            BrandProtectionChildrenSpecificationServiceImpl.QT,
            "test",
            BrandProtectionChildrenSpecificationServiceImpl.PARENT_TAG_PID,
            670L,
            BrandProtectionChildrenSpecificationServiceImpl.CATEGORY_ID,
            6L);

    var brandProtectionChildren =
        tagRepository.findAll(brandProtectionChildrenSpecification.selector(searchParameters));
    assertEquals("Advertising", brandProtectionChildren.get(0).getName());
    assertEquals(3, brandProtectionChildren.size());
  }

  @Test
  void shouldExpectsChildSpecificationIsTrue() {
    var valid = brandProtectionChildrenSpecification.isChildSpecification();
    assertTrue(valid);
  }
}
