package com.nexage.admin.core.specification;

import static java.util.Objects.nonNull;

import java.util.Set;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GeneralSpecification {

  /**
   * Compose {@link Specification} of {@link T} based on given params.
   *
   * @param qf Unique {@link Set} of fields.
   * @param qt The term to be found.
   * @return {@link Specification} of class {@link T}
   */
  public static <T> Specification<T> withSearchCriteria(Set<String> qf, String qt) {
    CustomSearchSpecification.Builder<T> builder = new CustomSearchSpecification.Builder<>();
    if (nonNull(qt) && !CollectionUtils.isEmpty(qf)) {
      qf.forEach(q -> builder.with(q, qt));
    }
    return builder.build();
  }
}
