package com.nexage.admin.core.specification;

import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.logging.log4j.util.Strings.isBlank;

import com.nexage.admin.core.enums.RuleType;
import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.model.CompanyRule;
import com.nexage.admin.core.model.CompanyRule_;
import com.nexage.admin.core.model.Rule_;
import java.util.EnumSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CompanyRuleSpecification {
  /**
   * Compose {@link Specification} of {@link CompanyRule} based on owner company pid and rule name
   *
   * @param publisherPid pid of publisher that the rule should belong to
   * @param types list of {@link RuleType} that should be included in the result
   * @param statuses list of {@link Status} that should be included in the result. Deleted rules
   *     will never be included
   * @param queryFields set of fields to search <code>queryTerm</code> in
   * @param queryTerm term to search in given fields
   * @return {@link Specification} of class {@link CompanyRule}
   */
  public static Specification<CompanyRule> withQuery(
      Long publisherPid,
      Set<RuleType> types,
      Set<Status> statuses,
      Set<String> queryFields,
      String queryTerm) {

    if (types == null || types.isEmpty()) {
      types = EnumSet.allOf(RuleType.class);
    }

    if (statuses == null || statuses.isEmpty()) {
      statuses = EnumSet.of(Status.ACTIVE, Status.INACTIVE);
    } else if (statuses.contains(Status.DELETED)) {
      throw new IllegalArgumentException("Cannot retrieve deleted rules.");
    }

    if (isBlank(queryTerm) || isEmpty(queryFields)) {
      return withoutSearch(publisherPid, types, statuses);
    }

    boolean usePid = queryFields.contains("pid");
    boolean useName = queryFields.contains("name");
    boolean isNumericTerm = queryTerm.matches("^\\d+$");

    // case 1: search for pid only using non-numeric term -> throw an exception
    if (usePid && !useName && !isNumericTerm) {
      throw new IllegalArgumentException(
          "When query field is only pid, the query term must be numeric.");
    }

    // case 2: search for both fields. We are certain that the term is numeric here.
    if (usePid && useName && isNumericTerm) {
      return withPidNameSearch(publisherPid, types, statuses, queryTerm);
    }

    // case 3: search for pid only. Again, term is guaranteed to be numeric,
    // otherwise we'd hit case 1.
    if (usePid && !useName) {
      return withPidSearch(publisherPid, types, statuses, queryTerm);
    }

    // case 4 (fallback): search for name only
    return withPublisherPidAndName(publisherPid, types, statuses, queryTerm);
  }

  /**
   * Compose {@link Specification} of {@link CompanyRule} based on owner company pid and rule name
   *
   * @param publisherPid pid of publisher rule should belong to
   * @param name name of rule
   * @return {@link Specification} of class {@link CompanyRule}
   */
  public static Specification<CompanyRule> withPublisherPidAndName(Long publisherPid, String name) {
    return (root, criteriaQuery, criteriaBuilder) ->
        criteriaBuilder.and(
            criteriaBuilder.equal(root.get(CompanyRule_.ownerCompanyPid), publisherPid),
            criteriaBuilder.equal(root.get(Rule_.name), name));
  }

  private static Specification<CompanyRule> withPublisherPidAndName(
      Long publisherPid, Set<RuleType> types, Set<Status> statuses, String name) {
    return (root, criteriaQuery, criteriaBuilder) ->
        criteriaBuilder.and(
            criteriaBuilder.equal(root.get(CompanyRule_.ownerCompanyPid), publisherPid),
            criteriaBuilder.like(root.get(Rule_.name), String.format("%%%s%%", name)),
            root.get(Rule_.ruleType).in(types),
            root.get(Rule_.status).in(statuses));
  }

  private static Specification<CompanyRule> withPidSearch(
      Long publisherPid, Set<RuleType> types, Set<Status> statuses, String pid) {
    return (root, criteriaQuery, criteriaBuilder) ->
        criteriaBuilder.and(
            criteriaBuilder.equal(root.get(CompanyRule_.ownerCompanyPid), publisherPid),
            criteriaBuilder.equal(root.get(Rule_.PID), Long.valueOf(pid)),
            root.get(Rule_.ruleType).in(types),
            root.get(Rule_.status).in(statuses));
  }

  private static Specification<CompanyRule> withPidNameSearch(
      Long publisherPid, Set<RuleType> types, Set<Status> statuses, String term) {
    return (root, criteriaQuery, criteriaBuilder) ->
        criteriaBuilder.and(
            criteriaBuilder.equal(root.get(CompanyRule_.ownerCompanyPid), publisherPid),
            criteriaBuilder.or(
                criteriaBuilder.equal(root.get(Rule_.PID), Long.valueOf(term)),
                criteriaBuilder.like(root.get(Rule_.name), String.format("%%%s%%", term))),
            root.get(Rule_.ruleType).in(types),
            root.get(Rule_.status).in(statuses));
  }

  private static Specification<CompanyRule> withoutSearch(
      Long publisherPid, Set<RuleType> types, Set<Status> statuses) {
    return (root, criteriaQuery, criteriaBuilder) ->
        criteriaBuilder.and(
            criteriaBuilder.equal(root.get(CompanyRule_.ownerCompanyPid), publisherPid),
            root.get(Rule_.ruleType).in(types),
            root.get(Rule_.status).in(statuses));
  }
}
