package com.nexage.admin.core.specification;

import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.enums.site.Platform;
import com.nexage.admin.core.enums.site.PublisherSiteType;
import com.nexage.admin.core.enums.site.Type;
import com.nexage.admin.core.model.Company_;
import com.nexage.admin.core.model.Position_;
import com.nexage.admin.core.model.SellerSeat_;
import com.nexage.admin.core.model.Site;
import com.nexage.admin.core.model.Site_;
import io.vavr.Tuple2;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SiteSpecification {

  public static Specification<Site> withSellerId(Long sellerId) {
    return (root, criteriaQuery, criteriaBuilder) ->
        criteriaBuilder.equal(root.get(Site_.companyPid), sellerId);
  }

  public static Specification<Site> withNameLike(String name) {
    return (root, criteriaQuery, criteriaBuilder) ->
        criteriaBuilder.like(root.get(Site_.name), String.format("%%%s%%", name));
  }

  public static Specification<Site> withStatus(List<String> status) {
    return ((root, criteriaQuery, criteriaBuilder) ->
        root.get(Site_.status)
            .in(status.stream().map(Status::valueOf).collect(Collectors.toList())));
  }

  public static Specification<Site> withStatus(Status status) {
    return (root, criteriaQuery, criteriaBuilder) ->
        criteriaBuilder.equal(root.get(Site_.status), status);
  }

  public static List<Tuple2<Type, Set<Platform>>> typeAndPlatformFromSiteTypes(
      List<String> siteTypes) {
    return siteTypes.stream()
        .map(PublisherSiteType::valueOf)
        .map(PublisherSiteType::platformsFromSiteType)
        .collect(Collectors.toList());
  }

  /**
   * @param typeAndPlatforms A collection of pairs (Type, Set&lt;Platform&gt;) each pair contains a
   *     type along with a set of platforms associated with that type
   * @return Predicate that is a disjunction of a collection of the predicates, `where type = type
   *     and platform in platforms` (one for each (Type,Set&lt;Platform&gt;) )
   */
  public static Specification<Site> withSiteTypes(
      List<Tuple2<Type, Set<Platform>>> typeAndPlatforms) {
    return (root, criteriaQuery, criteriaBuilder) -> {
      List<Predicate> allSiteTypes =
          typeAndPlatforms.stream()
              .map(
                  tuple ->
                      criteriaBuilder.and(
                          root.get(Site_.platform).in(tuple._2),
                          criteriaBuilder.equal(root.get(Site_.TYPE), tuple._1)))
              .collect(Collectors.toList());
      return criteriaBuilder.or(allSiteTypes.toArray(new Predicate[allSiteTypes.size()]));
    };
  }

  public static Specification<Site> searchSitesAndPositions(String searchTerm) {
    return (root, criteriaQuery, criteriaBuilder) -> {
      criteriaQuery.distinct(true);
      return criteriaBuilder.or(
          criteriaBuilder.like(
              root.join(Site_.positions).get(Position_.memo), String.format("%%%s%%", searchTerm)),
          withNameLike(searchTerm).toPredicate(root, criteriaQuery, criteriaBuilder));
    };
  }

  /**
   * Specification to find {@link Site} based on search query field and ids.
   *
   * @param qf query field
   * @param ids {@link Long} of Ids
   * @return {@link Specification} of {@link Site}
   */
  public static Specification<Site> withIds(String qf, Set<Long> ids) {
    return ((root, criteriaQuery, criteriaBuilder) -> root.get(qf).in(ids));
  }

  public static Specification<Site> withSellerSeatPid(Long sellerSeatPid) {
    return (root, criteriaQuery, criteriaBuilder) ->
        criteriaBuilder.equal(
            root.join(Site_.company).get(Company_.sellerSeat).get(SellerSeat_.pid), sellerSeatPid);
  }
}
