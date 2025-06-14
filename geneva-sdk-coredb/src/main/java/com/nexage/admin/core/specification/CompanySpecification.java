package com.nexage.admin.core.specification;

import static java.util.Objects.nonNull;

import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.Company_;
import com.ssp.geneva.common.model.inventory.CompanyType;
import java.util.Optional;
import java.util.Set;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CompanySpecification {

  /**
   * Compose {@link Specification} of {@link Company} based on given type.
   *
   * @param type {@link CompanyType}
   * @return {@link Specification} of class {@link Company}
   */
  public static Specification<Company> withType(final CompanyType type) {
    return (root, criteriaQuery, criteriaBuilder) ->
        criteriaBuilder.equal(root.get(Company_.TYPE), type);
  }

  /**
   * Compose {@link Specification} of {@link Company} based on given name.
   *
   * @param name {@link String}
   * @return {@link Specification} of class {@link Company}
   */
  public static Specification<Company> withNameLike(final String name) {
    return (root, criteriaQuery, criteriaBuilder) ->
        criteriaBuilder.like(root.get(Company_.NAME), String.format("%%%s%%", name));
  }

  /**
   * Compose {@link Specification} of {@link Company} based on given status.
   *
   * @param status {@link Status}
   * @return {@link Specification} of class {@link Company}
   */
  public static Specification<Company> withStatus(final Status status) {
    return (root, criteriaQuery, criteriaBuilder) ->
        criteriaBuilder.equal(root.get(Company_.STATUS), status);
  }

  /**
   * Compose {@link Specification} of {@link Company} based on given status.
   *
   * @param rtbEnabled {@link Company.defaultRtbProfilesEnabled}
   * @return {@link Specification} of class {@link Company}
   */
  public static Specification<Company> withDefaultRtbProfilesEnabled(final boolean rtbEnabled) {
    return (root, criteriaQuery, criteriaBuilder) ->
        criteriaBuilder.equal(root.get(Company_.defaultRtbProfilesEnabled), rtbEnabled);
  }

  /**
   * Compose {@link Specification} of {@link Company} based on given status.
   *
   * @param rtbEnabled {@link Company.rtbEnabled}
   * @return {@link Specification} of class {@link Company}
   */
  public static Specification<Company> withRtbEnabled(final boolean rtbEnabled) {
    return (root, criteriaQuery, criteriaBuilder) ->
        criteriaBuilder.equal(root.get(Company_.rtbEnabled), rtbEnabled);
  }
  /**
   * Compose {@link Specification} of {@link Company} type Seller based on given params.
   *
   * @param qf Unique {@link Set} of fields.
   * @param qt The term to be found.
   * @param rtbEnabled Flag to return RTB enabled companies.
   * @return {@link Specification} of class {@link Company}
   */
  public static Specification<Company> ofSellerTypeWith(
      Set<String> qf, String qt, boolean rtbEnabled) {
    if (rtbEnabled) {
      return ofTypeWithRtbEnabled(CompanyType.SELLER, qf, qt, withRtbEnabled(rtbEnabled));
    }
    return ofTypeWith(CompanyType.SELLER, qf, qt);
  }

  /**
   * Compose {@link Specification} of {@link Company} type Buyer based on given params.
   *
   * @param qf Unique {@link Set} of fields.
   * @param qt The term to be found.
   * @param rtbEnabled Flag to return RTB enabled companies.
   * @return {@link Specification} of class {@link Company}
   */
  public static Specification<Company> ofBuyerTypeWith(
      Set<String> qf, String qt, boolean rtbEnabled) {
    if (rtbEnabled) {
      return ofTypeWithRtbEnabled(CompanyType.BUYER, qf, qt, withRtbEnabled(rtbEnabled));
    }
    return ofTypeWith(CompanyType.BUYER, qf, qt);
  }

  /**
   * Compose {@link Specification} of {@code Company} type SELLER and {@code rtbEnabled=true}.
   *
   * @return {@link Specification} of RTB-enabled sellers
   */
  public static Specification<Company> ofRtbEnabledSellers() {
    Specification<Company> sellerSpec =
        ((root, criteriaQuery, criteriaBuilder) ->
            criteriaBuilder.equal(root.get(Company_.TYPE), CompanyType.SELLER));
    Specification<Company> rtbEnabledSpec =
        ((root, criteriaQuery, criteriaBuilder) ->
            criteriaBuilder.equal(root.get(Company_.RTB_ENABLED), true));
    return SpecificationUtils.conjunction(Optional.of(sellerSpec), Optional.of(rtbEnabledSpec))
        .orElse(null);
  }

  private static Specification<Company> ofTypeWith(CompanyType type, Set<String> qf, String qt) {
    CustomSearchSpecification.Builder<Company> builder =
        new CustomSearchSpecification.Builder<>(Company.class);
    if (nonNull(qt) && !CollectionUtils.isEmpty(qf)) {
      qf.forEach(q -> builder.with(q, qt));
    }
    return SpecificationUtils.conjunction(
            Optional.ofNullable(builder.build()), Optional.of(withType(type)))
        .orElse(withType(type));
  }

  private static Specification<Company> ofTypeWithRtbEnabled(
      CompanyType type, Set<String> qf, String qt, Specification<Company> withRtbEnabled) {
    CustomSearchSpecification.Builder<Company> builder =
        new CustomSearchSpecification.Builder<>(Company.class);
    if (nonNull(qt) && !CollectionUtils.isEmpty(qf)) {
      qf.forEach(q -> builder.with(q, qt));
    }
    return SpecificationUtils.conjunction(
            Optional.ofNullable(builder.build()),
            Optional.of(withType(type)),
            Optional.ofNullable(withRtbEnabled))
        .orElse(withType(type));
  }
}
