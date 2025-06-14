package com.nexage.admin.core.specification;

import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.model.Company_;
import com.nexage.admin.core.model.Site;
import com.nexage.admin.core.model.Site_;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import lombok.Builder;
import lombok.Builder.Default;
import org.springframework.data.jpa.domain.Specification;

/**
 * A {@link Specification} that is used to create SQL query used in searching for sites based on
 * different criteria from {@code qf} request parameter.
 */
@Builder
public class SiteQueryFieldSpecification implements Specification<Site> {

  private static final long serialVersionUID = -3167603026861106477L;
  private static final String STRING_FORMAT = "%%%s%%";
  @Default private boolean isAndOperator = true;
  @Default private transient Optional<Set<Long>> pids = Optional.empty();
  @Default private transient Optional<String> name = Optional.empty();
  @Default private transient Optional<String> globalAliasName = Optional.empty();
  @Default private transient Optional<Set<Long>> companyPids = Optional.empty();
  @Default private transient Optional<Set<Long>> userCompanyPids = Optional.empty();
  @Default private transient Optional<String> companyName = Optional.empty();

  @Default
  private transient Optional<Set<Integer>> statuses =
      Optional.of(Set.of(Status.INACTIVE.asInt(), Status.ACTIVE.asInt()));

  @Override
  public Predicate toPredicate(
      Root<Site> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

    var specs = new ArrayList<Specification<Site>>();

    pids.ifPresent(specsPids -> specs.add(withPid(specsPids)));
    name.ifPresent(specsName -> specs.add(withName(specsName)));
    globalAliasName.ifPresent(
        specsGlobalAliasName -> specs.add(withGlobalAliasName(specsGlobalAliasName)));
    companyPids.ifPresent(specsCompanyPid -> specs.add(withCompanyPid(specsCompanyPid)));
    companyName.ifPresent(specsCompanyName -> specs.add(withCompanyName(specsCompanyName)));

    Specification<Site> qfMainCriteria = withStatus(statuses.get());
    if (userCompanyPids.isPresent()) {
      qfMainCriteria = qfMainCriteria.and(withCompanyPid(userCompanyPids.get()));
    }

    Specification<Site> qfCriteria = Specification.where(null);
    for (Specification<Site> spec : specs) {
      if (qfCriteria != null) {
        qfCriteria = isAndOperator ? qfCriteria.and(spec) : qfCriteria.or(spec);
      }
    }
    if (qfCriteria != null) {
      qfMainCriteria = qfMainCriteria == null ? qfCriteria : qfMainCriteria.and(qfCriteria);
    }

    return qfMainCriteria == null ? null : qfMainCriteria.toPredicate(root, query, criteriaBuilder);
  }

  private Specification<Site> withName(String name) {
    return (root, criteriaQuery, criteriaBuilder) ->
        criteriaBuilder.like(root.get(Site_.name), String.format(STRING_FORMAT, name));
  }

  private Specification<Site> withPid(Set<Long> pids) {
    return (root, criteriaQuery, criteriaBuilder) -> root.get(Site_.pid).in(pids);
  }

  private Specification<Site> withCompanyPid(Set<Long> pids) {
    return (root, criteriaQuery, criteriaBuilder) -> root.get(Site_.companyPid).in(pids);
  }

  private Specification<Site> withGlobalAliasName(String globalAliasName) {
    return (root, criteriaQuery, criteriaBuilder) ->
        criteriaBuilder.like(
            root.get(Site_.globalAliasName), String.format(STRING_FORMAT, globalAliasName));
  }

  private Specification<Site> withStatus(Set<Integer> statuses) {
    return ((root, criteriaQuery, criteriaBuilder) -> root.get(Site_.status).in(statuses));
  }

  private Specification<Site> withCompanyName(String companyName) {
    return (root, criteriaQuery, criteriaBuilder) ->
        criteriaBuilder.like(
            root.get(Site_.company).get(Company_.name), String.format(STRING_FORMAT, companyName));
  }
}
