package com.nexage.admin.core.specification.impl;

import static com.nexage.admin.core.specification.BrandProtectionTagSpecification.withCategoryId;
import static com.nexage.admin.core.specification.BrandProtectionTagSpecification.withNullParentTagPid;

import com.nexage.admin.core.model.BrandProtectionTag;
import com.nexage.admin.core.specification.SpecificationUtils;
import com.nexage.admin.core.specification.service.BrandProtectionTagSpecificationSelectorService;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class BrandProtectionDefaultSpecificationServiceImpl
    implements BrandProtectionTagSpecificationSelectorService {
  @Override
  public Specification<BrandProtectionTag> selector(Map<String, Object> values) {
    var categoryId = (Long) values.get(CATEGORY_ID);
    return SpecificationUtils.conjunction(
            Optional.of(withCategoryId(categoryId)), Optional.of(withNullParentTagPid()))
        .orElse(null);
  }

  @Override
  public boolean isValid(Map<String, Object> values) {
    return values.get(CATEGORY_ID) != null
        && values.entrySet().stream()
            .filter(keySet -> !keySet.getKey().equals(CATEGORY_ID))
            .map(Map.Entry::getValue)
            .allMatch(Objects::isNull);
  }
}
