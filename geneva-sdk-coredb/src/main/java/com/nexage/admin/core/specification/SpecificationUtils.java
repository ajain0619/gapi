package com.nexage.admin.core.specification;

import static org.springframework.data.jpa.domain.Specification.where;

import io.vavr.collection.HashSet;
import java.util.Optional;
import org.springframework.data.jpa.domain.Specification;

public class SpecificationUtils {

  /**
   * Combines Specifications first by removing the empty ones and joining the rest with AND
   *
   * @param optionalSpecs a varargs containing Optional specs
   * @param <T> Entity being queried
   * @return An Optional Specification that contains all the predicates TODO Pass the combining
   *     operator as an argument
   */
  @SafeVarargs
  public static <T> Optional<Specification<T>> conjunction(
      Optional<Specification<T>>... optionalSpecs) {
    return HashSet.of(optionalSpecs)
        .filter(Optional::isPresent)
        .reduce((osa, osb) -> osa.flatMap(sa -> osb.map(sb -> where(sa).and(sb))));
  }

  @SafeVarargs
  public static <T> Optional<Specification<T>> disjunction(
      Optional<Specification<T>>... optionalSpecs) {
    return HashSet.of(optionalSpecs)
        .filter(Optional::isPresent)
        .reduce((osa, osb) -> osa.flatMap(sa -> osb.map(sb -> where(sa).or(sb))));
  }
}
