package com.nexage.admin.core.specification.impl;

import static com.nexage.admin.core.specification.BrandProtectionTagSpecification.withCategoryId;
import static com.nexage.admin.core.specification.BrandProtectionTagSpecification.withParentTagPid;
import static com.nexage.admin.core.specification.BrandProtectionTagSpecification.withQueryFieldsAndSearchTerm;
import static java.util.Objects.nonNull;

import com.nexage.admin.core.model.BrandProtectionTag;
import com.nexage.admin.core.repository.BrandProtectionTagRepository;
import com.nexage.admin.core.specification.SpecificationUtils;
import com.nexage.admin.core.specification.service.BrandProtectionTagSpecificationSelectorService;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BrandProtectionChildrenSpecificationServiceImpl
    implements BrandProtectionTagSpecificationSelectorService {

  private final BrandProtectionTagRepository brandProtectionTagRepository;

  @Override
  public Specification<BrandProtectionTag> selector(Map<String, Object> values) {
    Optional<Specification<BrandProtectionTag>> qtSpec = Optional.empty();
    var categoryId = (Long) values.get(CATEGORY_ID);
    var parentTagPid = (Long) values.get(PARENT_ID);
    if (nonNull(values.get(QT)) && CollectionUtils.isNotEmpty((Set<String>) values.get(QF))) {
      qtSpec =
          Optional.of(
              withQueryFieldsAndSearchTerm(
                  (Set<String>) values.get(QF), String.valueOf(values.get(QT))));
    }
    var combinedSpecs =
        SpecificationUtils.conjunction(
                Optional.of(withCategoryId(categoryId)),
                qtSpec,
                Optional.of(withParentTagPid(parentTagPid)))
            .orElse(null);
    if (brandProtectionTagRepository.count(combinedSpecs) > 0) {
      return combinedSpecs;
    } else {
      return SpecificationUtils.conjunction(
              Optional.of(withCategoryId(categoryId)), Optional.of(withParentTagPid(parentTagPid)))
          .orElse(null);
    }
  }

  @Override
  public boolean isValid(Map<String, Object> values) {
    var parentTagPid = values.get(PARENT_ID);
    return parentTagPid != null && values.get(CATEGORY_ID) != null;
  }
}
