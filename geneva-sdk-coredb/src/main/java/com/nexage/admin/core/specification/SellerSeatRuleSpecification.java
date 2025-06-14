package com.nexage.admin.core.specification;

import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.logging.log4j.util.Strings.isBlank;

import com.nexage.admin.core.enums.RuleType;
import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.model.Rule_;
import com.nexage.admin.core.model.SellerSeatRule;
import com.nexage.admin.core.model.SellerSeatRule_;
import java.util.Set;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SellerSeatRuleSpecification {

  /**
   * Includes only the rule with the specified pid
   *
   * @param pid rule's primary key
   * @return {@link Specification} object
   */
  public static Specification<SellerSeatRule> withPid(long pid) {
    return (root, criteriaQuery, criteriaBuilder) ->
        criteriaBuilder.equal(root.get(Rule_.PID), pid);
  }

  /**
   * Includes in the query results only rules assigned to a seller seat with given pid
   *
   * @param pid the pid of a seller seat rule should be assigned to
   * @return {@link Specification} object
   */
  public static Specification<SellerSeatRule> withSellerSeat(long pid) {
    return (root, criteriaQuery, criteriaBuilder) ->
        criteriaBuilder.equal(root.get(SellerSeatRule_.SELLER_SEAT_PID), pid);
  }

  /**
   * Includes in the query results only rules with given name
   *
   * @param name the name of rule
   * @return {@link Specification} object
   */
  public static Specification<SellerSeatRule> withName(String name) {
    return (root, criteriaQuery, criteriaBuilder) ->
        criteriaBuilder.equal(root.get(Rule_.NAME), name);
  }

  /**
   * Includes in the results only rules that match the given query field(s) and term. Possible
   * scenarios: - if query fields or term are null, return an empty predicate (equivalent of literal
   * TRUE) - if fields is only PID, match term (assumed to be numeric) by PID field - if fields is
   * only name, match term by NAME using LIKE - if both are present, combine the two above
   *
   * @param queryFields set of fields by which to filter the rules
   * @param queryTerm the term by which to filter the rules
   * @return {@link Specification} object
   */
  public static Specification<SellerSeatRule> withQuery(Set<String> queryFields, String queryTerm) {
    if (isBlank(queryTerm) || isEmpty(queryFields)) {
      return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.and();
    }

    // case 1: search for pid only using non numeric term -> throw an exception
    boolean usePid = queryFields.contains("pid");
    boolean useName = queryFields.contains("name");
    boolean isTermNumeric = queryTerm.matches("^\\d+$");
    if (usePid && !useName && !isTermNumeric) {
      throw new IllegalArgumentException(
          "When query field is only pid, the query term must be numeric.");
    }

    // case 2: search for both fields. We are certain that the term is numeric here.
    boolean searchForBoth = usePid && isTermNumeric && useName;
    if (searchForBoth) {
      return withPidOrNameLike(queryTerm);
    }

    // case 3: search for pid only. Again, term is guaranteed to be numeric,
    // otherwise we'd hit case 1.
    boolean searchForPidOnly = usePid && !useName;
    if (searchForPidOnly) {
      return withPid(Long.parseLong(queryTerm));
    }

    // case 4 (fallback): search for name only
    return withNameLike(queryTerm);
  }

  /**
   * Includes rules with rule type and status that are contained within the specified sets
   *
   * @param ruleTypes set of specified rule types
   * @return {@link Specification} object
   */
  public static Specification<SellerSeatRule> withRuleType(Set<RuleType> ruleTypes) {
    return (root, criteriaQuery, criteriaBuilder) -> root.get(Rule_.ruleType).in(ruleTypes);
  }

  /**
   * Includes rules with rule type and status that are contained within the specified sets
   *
   * @param statuses set of specified statuses
   * @return {@link Specification} object
   */
  public static Specification<SellerSeatRule> withStatus(Set<Status> statuses) {
    return (root, criteriaQuery, criteriaBuilder) -> root.get(Rule_.status).in(statuses);
  }

  /**
   * Includes in the query results only rules with given name fragment
   *
   * @param name the name of rule
   * @return {@link Specification} object
   */
  private static Specification<SellerSeatRule> withNameLike(String name) {
    return (root, criteriaQuery, criteriaBuilder) ->
        criteriaBuilder.like(root.get(Rule_.NAME), String.format("%%%s%%", name));
  }

  /**
   * Includes rules with either name or pid matching the query term. If qt is not numeric, then only
   * name is searched for.
   *
   * @param qt query term
   * @return {@link Specification} object
   */
  private static Specification<SellerSeatRule> withPidOrNameLike(String qt) {
    return (root, criteriaQuery, criteriaBuilder) ->
        criteriaBuilder.or(
            criteriaBuilder.like(root.get(Rule_.NAME), String.format("%%%s%%", qt)),
            criteriaBuilder.equal(root.get(Rule_.PID), Long.parseLong(qt)));
  }
}
