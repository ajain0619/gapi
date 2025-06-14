package com.nexage.admin.core.specification;

import static java.util.Objects.nonNull;

import com.google.common.collect.Sets;
import com.nexage.admin.core.model.BaseModel_;
import com.nexage.admin.core.model.SellerSeatRule_;
import com.nexage.admin.core.model.Site_;
import com.nexage.admin.core.model.User;
import com.nexage.admin.core.model.User.Role;
import com.nexage.admin.core.model.User_;
import com.ssp.geneva.common.model.inventory.CompanyType;
import java.util.Optional;
import java.util.Set;
import javax.persistence.criteria.JoinType;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

public class UserSpecification {

  private static final String ONLY_CURRENT = "onlyCurrent";

  private UserSpecification() {}

  /**
   * Excludes <b>superadmin</b> user from query results
   *
   * @return {@link Specification} object
   */
  public static Specification<User> withoutSuperAdmin() {
    return (root, criteriaQuery, criteriaBuilder) ->
        criteriaBuilder.not(criteriaBuilder.equal(root.get(User_.USER_NAME), "superadmin"));
  }

  /**
   * Excludes user given pid from query results
   *
   * @param pid pid of the user to exclude
   * @return {@link Specification} object
   */
  public static Specification<User> withoutUser(Long pid) {
    return Specification.not(withUser(pid));
  }

  /**
   * Includes user given pid from query results
   *
   * @param pid pid of the current user to include
   * @return {@link Specification} object
   */
  public static Specification<User> withUser(Long pid) {
    return (root, criteriaQuery, criteriaBuilder) ->
        criteriaBuilder.equal(root.get(BaseModel_.PID), pid);
  }

  /**
   * Excludes users with {@link Role#ROLE_API} from query results
   *
   * @return {@link Specification} object
   */
  public static Specification<User> withoutApiRole() {
    return (root, criteriaQuery, criteriaBuilder) ->
        criteriaBuilder.not(criteriaBuilder.equal(root.get(User_.ROLE), Role.ROLE_API));
  }

  /**
   * Includes in query results only users assigned to company with given pid
   *
   * @param pid pid of the company user should be assigned to
   * @return {@link Specification} object
   */
  public static Specification<User> withCompany(Long pid) {
    return withCompanies(Set.of(pid));
  }

  /**
   * Includes in query results only users assigned to companies with given pids
   *
   * @param pids pids of companies user should be assigned to
   * @return {@link Specification} object
   */
  public static Specification<User> withCompanies(Set<Long> pids) {
    return (root, criteriaQuery, criteriaBuilder) ->
        root.join(User_.COMPANIES, JoinType.INNER).get(BaseModel_.PID).in(pids);
  }

  /**
   * Includes in query results only users assigned to seller seat with given pid
   *
   * @param pid pid of the seller seat user should be assigned to
   * @return {@link Specification} object
   */
  public static Specification<User> withSellerSeat(Long pid) {
    return (root, criteriaQuery, criteriaBuilder) ->
        criteriaBuilder.equal(root.get(User_.SELLER_SEAT), pid);
  }

  /**
   * Includes in query results only users with enabled flag set to true
   *
   * @return {@link Specification} object
   */
  public static Specification<User> isEnabled() {
    return (root, criteriaQuery, criteriaBuilder) ->
        criteriaBuilder.equal(root.get(User_.ENABLED), true);
  }

  /**
   * Find all {@link User} that are associated with parameters in a {@link Set} that match the query
   * term {@link String}.
   *
   * @param qf Query fields of type String. This is used to search for any field of users that is of
   *     type String.
   * @param qt Query term that is being searched for. The term will be partially matched.
   * @return {@link Specification} of {@link User} instances based on parameters.
   */
  public static Optional<Specification<User>> withSearchUser(Set<String> qf, String qt) {
    CustomSearchSpecification.Builder<User> builder = getBuilder(qf, qt);
    Optional<Specification<User>> build = Optional.ofNullable(builder.build());
    if (build.isPresent()) {
      return SpecificationUtils.conjunction(build);
    }
    return build;
  }

  public static Specification<User> buildUserSpecification(
      CompanyType companyType, Set<Long> companyPids, Long pid, Set<String> qf, String qt) {

    if (!CollectionUtils.isEmpty(qf) && qf.contains(ONLY_CURRENT)) {
      if (!Strings.isBlank(qt) && Boolean.parseBoolean(qt.trim())) {
        return withUser(pid);
      }
      qf.remove(ONLY_CURRENT);
    }

    Specification<User> spec = getSpecForUserWithoutApiRoleAndSuperAdmin(pid);
    if (nonNull(qt)
        && !CollectionUtils.isEmpty(qf)
        && qf.contains(SellerSeatRule_.SELLER_SEAT_PID)) {
      spec = spec.and(withSellerSeat(Long.valueOf(qt)));
    } else if (nonNull(qt) && !CollectionUtils.isEmpty(qf) && qf.contains(Site_.COMPANY_PID)) {
      spec = spec.and(withCompany(Long.valueOf(qt)));
    } else {
      if (companyType != CompanyType.NEXAGE) {
        spec = spec.and(withCompanies(companyPids));
      }
    }
    Optional<Specification<User>> searchUser = withSearchUser(qf, qt);
    if (searchUser.isPresent()) {
      spec = spec.and(searchUser.get());
    }
    return spec;
  }

  private static Specification<User> getSpecForUserWithoutApiRoleAndSuperAdmin(Long pid) {
    return Specification.where(withoutApiRole()).and(withoutSuperAdmin()).and(withoutUser(pid));
  }

  private static CustomSearchSpecification.Builder<User> getBuilder(Set<String> qf, String qt) {
    CustomSearchSpecification.Builder<User> builder = new CustomSearchSpecification.Builder<>();
    removeUnsupportedFields(qf);
    if (nonNull(qt) && !CollectionUtils.isEmpty(qf)) {
      qf.forEach(q -> builder.with(q, qt));
    }
    return builder;
  }

  private static void removeUnsupportedFields(Set<String> qf) {
    if (qf != null) {
      qf.removeIf(
          queryField ->
              Sets.newHashSet(SellerSeatRule_.SELLER_SEAT_PID, User_.COMPANY_PID)
                  .contains(queryField));
    }
  }
}
