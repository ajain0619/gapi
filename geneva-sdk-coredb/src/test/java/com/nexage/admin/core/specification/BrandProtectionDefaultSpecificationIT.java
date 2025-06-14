package com.nexage.admin.core.specification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.repository.BrandProtectionTagRepository;
import com.nexage.admin.core.specification.impl.BrandProtectionChildrenSpecificationServiceImpl;
import com.nexage.admin.core.specification.impl.BrandProtectionDefaultSpecificationServiceImpl;
import java.util.Map;
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
class BrandProtectionDefaultSpecificationIT extends CoreDbSdkIntegrationTestBase {

  private static Map<String, Object> defaultParameters =
      Map.of(BrandProtectionChildrenSpecificationServiceImpl.CATEGORY_ID, 6L);
  @Autowired private BrandProtectionTagRepository tagRepository;

  @Autowired
  private BrandProtectionDefaultSpecificationServiceImpl brandProtectionDefaultSpecification;

  @Test
  void shouldCorrectlySelectDefaultBrandProtectionTags() {

    var brandProtectionDefaultTagsCount =
        tagRepository.count(brandProtectionDefaultSpecification.selector(defaultParameters));
    assertEquals(2, brandProtectionDefaultTagsCount);
  }

  @Test
  void shouldCorrectlyCheckForDefaultSpecificationEligibility() {
    var valid = brandProtectionDefaultSpecification.isValid(defaultParameters);
    assertTrue(valid);
  }
}
