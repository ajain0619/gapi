package com.nexage.admin.core.specification;

import com.nexage.admin.core.enums.PlacementAction;
import com.nexage.admin.core.enums.PlacementCategory;
import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.Company_;
import com.nexage.admin.core.model.Position;
import com.nexage.admin.core.model.Position_;
import com.nexage.admin.core.model.SellerAttributes;
import com.nexage.admin.core.model.Site;
import com.nexage.admin.core.model.Site_;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PositionSpecification {

  private static final String DEFAULT_RTB_PROFILE = "defaultRtbProfile";
  private static final String LIKE_FORMAT = "%%%s%%";

  public static Specification<Position> withMemo(String memo) {
    return (root, criteriaQuery, criteriaBuilder) ->
        criteriaBuilder.like(root.get(Position_.memo), String.format(LIKE_FORMAT, memo));
  }

  public static Specification<Position> withSellerId(Long sellerId) {
    return (root, criteriaQuery, criteriaBuilder) ->
        criteriaBuilder.equal(root.join(Position_.site).get(Site_.companyPid), sellerId);
  }

  public static List<PlacementCategory> getPlacementCategories(List<String> placementTypes) {
    return placementTypes.stream().map(PlacementCategory::valueOf).collect(Collectors.toList());
  }

  public static Specification<Position> withPositionTypes(List<String> placementsTypes) {
    return (root, criteriaQuery, criteriaBuilder) ->
        root.get(Position_.placementCategory).in(getPlacementCategories(placementsTypes));
  }

  public static Specification<Position> withSiteId(Long siteId) {
    return (root, criteriaQuery, criteriaBuilder) ->
        criteriaBuilder.equal(root.get(Position_.sitePid), siteId);
  }

  public static Specification<Position> withStatus(List<String> status) {
    return (root, criteriaQuery, criteriaBuilder) ->
        root.get(Position_.status)
            .in(status.stream().map(Status::valueOf).collect(Collectors.toList()));
  }

  /**
   * Compose {@link Specification} of a {@link Position} that has a given name.
   *
   * @param name The name to be searched for
   * @return {@link Specification} of class {@link Position}
   */
  public static Specification<Position> withExactName(String name) {
    return (root, criteriaQuery, criteriaBuilder) ->
        criteriaBuilder.equal(root.get(Position_.name), name);
  }

  /**
   * Compose {@link Specification} of a {@link Position} that has a given memo.
   *
   * @param memo The memo to be searched for
   * @return {@link Specification} of class {@link Position}
   */
  public static Specification<Position> withExactMemo(String memo) {
    return (root, criteriaQuery, criteriaBuilder) ->
        criteriaBuilder.equal(root.get(Position_.memo), memo);
  }

  /**
   * Compose {@link Specification} of a {@link Position} that has a given alias.
   *
   * @param alias The alias to be searched for
   * @return {@link Specification} of class {@link Position}
   */
  public static Specification<Position> withExactAlias(String alias) {
    return (root, criteriaQuery, criteriaBuilder) ->
        criteriaBuilder.equal(root.get(Position_.positionAliasName), alias);
  }

  /**
   * Compose {@link Specification} of a {@link Position} based on name and/or memo.
   *
   * @param terms A map of terms to search by and their values
   * @return {@link Specification} of class {@link Position}
   */
  public static Specification<Position> withNameAndMemo(Map<String, String> terms) {
    return (root, criteriaQuery, criteriaBuilder) -> {
      List<Predicate> predicates = new ArrayList<>();

      if (terms.containsKey("name")) {
        predicates.add(
            criteriaBuilder.like(
                root.get(Position_.name), String.format(LIKE_FORMAT, terms.get("name"))));
      }
      if (terms.containsKey("memo")) {
        predicates.add(
            criteriaBuilder.like(
                root.get(Position_.name), String.format(LIKE_FORMAT, terms.get("memo"))));
      }

      return criteriaBuilder.or(predicates.toArray(new Predicate[predicates.size()]));
    };
  }

  /**
   * Compose {@link Specification} of a {@link Position} based on query type and list of query
   * terms.
   *
   * @param terms A map of terms to search by and their values
   * @return {@link Optional} {@link Specification} of class {@link Position}
   */
  public static Optional<Specification<Position>> withQueryTerms(Map<String, String> terms) {
    PlacementAction queryType = null;

    try {
      queryType = PlacementAction.valueOf(terms.get("action").toUpperCase());
    } catch (IllegalArgumentException | NullPointerException e) {
      return Optional.empty();
    }

    switch (queryType) {
      case SEARCH:
        return Optional.of(terms).map(PositionSpecification::withNameAndMemo);
      case DUPLICATE:
        Optional<Specification<Position>> nameSpec;
        Optional<Specification<Position>> memoSpec;
        Optional<Specification<Position>> aliasSpec;

        nameSpec =
            Optional.ofNullable(terms.get(Position_.NAME))
                .map(PositionSpecification::withExactName);
        memoSpec =
            Optional.ofNullable(terms.get(Position_.MEMO))
                .map(PositionSpecification::withExactMemo);
        aliasSpec =
            Optional.ofNullable(terms.get("alias")).map(PositionSpecification::withExactAlias);

        return SpecificationUtils.disjunction(nameSpec, memoSpec, aliasSpec);
      default:
        return Optional.empty();
    }
  }

  public static Specification<Position> withDefaultRtbProfiles(Long rtbProfile) {
    return (root, criteriaQuery, criteriaBuilder) -> {
      Join<Position, Site> siteJoin = root.join(Position_.site, JoinType.INNER);
      Join<Site, Company> companyJoin = siteJoin.join(Site_.company, JoinType.INNER);
      Join<Company, SellerAttributes> attributesJoin =
          companyJoin.join(Company_.sellerAttributes, JoinType.INNER);

      return criteriaBuilder.and(
          criteriaBuilder.greaterThan(root.get("status"), 0),
          criteriaBuilder.greaterThan(companyJoin.get("defaultRtbProfilesEnabled"), 0),
          criteriaBuilder.isNotNull(attributesJoin.get(DEFAULT_RTB_PROFILE)),
          criteriaBuilder.equal(
              criteriaBuilder
                  .coalesce()
                  .value(root.get(DEFAULT_RTB_PROFILE))
                  .value(siteJoin.get(DEFAULT_RTB_PROFILE))
                  .value(attributesJoin.get(DEFAULT_RTB_PROFILE)),
              rtbProfile));
    };
  }
}
