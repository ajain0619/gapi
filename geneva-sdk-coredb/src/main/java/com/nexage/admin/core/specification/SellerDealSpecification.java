package com.nexage.admin.core.specification;

import com.nexage.admin.core.enums.DealCategory;
import com.nexage.admin.core.model.BaseModel_;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.DirectDeal;
import com.nexage.admin.core.model.DirectDeal_;
import com.nexage.admin.core.model.SiteView;
import com.nexage.admin.core.model.SiteView_;
import com.nexage.admin.core.sparta.jpa.model.DealPosition;
import com.nexage.admin.core.sparta.jpa.model.DealPosition_;
import com.nexage.admin.core.sparta.jpa.model.DealPublisher;
import com.nexage.admin.core.sparta.jpa.model.DealPublisher_;
import com.nexage.admin.core.sparta.jpa.model.DealSite;
import com.nexage.admin.core.sparta.jpa.model.DealSite_;
import com.nexage.admin.core.sparta.jpa.model.PositionView_;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SellerDealSpecification {

  public static final String PIPELINE_DELIMITER = "|";
  private static final String QUERY_FIELD_FOR_ID = "dealId";
  private static final String QUERY_FIELD_FOR_DESCRIPTION = "description";
  private static final String QUERY_FIELD_FOR_DEALCATEGORY = "dealCategory";

  public static final Set<String> QUERYABLE_FIELDS =
      Set.of(QUERY_FIELD_FOR_ID, QUERY_FIELD_FOR_DESCRIPTION, QUERY_FIELD_FOR_DEALCATEGORY);

  /**
   * Compose {@link Specification} of {@link DirectDeal} to fetch all deals associated with a
   * seller. Deals can be associated to a seller through {@link DealPublisher}, {@link DealSite} or
   * {@link DealPosition}.
   *
   * @param publisherPid PID of the {@link Company}
   * @param qf {@link Set} of fields to query by. May include 'dealId' 'description' and
   *     'dealCategory' only
   * @param qt Term to use in query when qf parameter is specified
   * @return {@link Specification} of class {@link DirectDeal}
   */
  public static Specification<DirectDeal> buildSpecification(
      long publisherPid, Set<String> qf, String qt, boolean userDetail) {
    Specification<DirectDeal> specification =
        userDetail
            ? withSellerId(publisherPid)
            : withSellerId(publisherPid).and(Specification.not(withVisibilityAsFalse()));
    return filterArgumentsPassed(qf, qt) && specification != null
        ? specification.and(withFilterArguments(qf, qt))
        : specification;
  }

  private static Specification<DirectDeal> withVisibilityAsFalse() {
    return (root, criteriaQuery, criteriaBuilder) ->
        criteriaBuilder.and(criteriaBuilder.equal(root.get(DirectDeal_.visibility), 0));
  }

  /**
   * Compose {@link Specification} of {@link DirectDeal} to fetch all deals associated with a
   * seller. Deals can be associated to a seller through {@link DealPublisher}, {@link DealSite} or
   * {@link DealPosition}.
   *
   * @param publisherPid PID of the {@link Company}
   * @param qf {@link Map} of fields to query by. May include 'dealId' 'description''dealCategory'
   *     only
   * @return {@link Specification} of class {@link DirectDeal}
   */
  public static Specification<DirectDeal> buildSpecification(
      long publisherPid, Map<String, List<String>> qf, boolean userDetail) {
    Specification<DirectDeal> specification =
        userDetail
            ? withSellerId(publisherPid)
            : withSellerId(publisherPid).and(Specification.not(withVisibilityAsFalse()));
    return specification != null ? specification.and(withFilterArgumentsForMap(qf)) : specification;
  }
  /**
   * Compose {@link Specification} of {@link DirectDeal} to fetch a deal by pid associated with a
   * seller. Deals can be associated to a seller through {@link DealPublisher}, {@link DealSite} or
   * {@link DealPosition}.
   *
   * @param sellerId ID of the {@link Company}
   * @param pid PID of the {@link DirectDeal}
   * @return {@link Specification} of class {@link DirectDeal}
   */
  public static Specification<DirectDeal> buildSpecification(
      Long sellerId, Long pid, boolean userDetail) {
    Specification<DirectDeal> specification = withSellerId(sellerId).and(withDealPid(pid));
    if (!userDetail && specification != null) {
      return specification.and(Specification.not(withVisibilityAsFalse()));
    } else {
      return specification;
    }
  }

  private static boolean filterArgumentsPassed(Set<String> qf, String qt) {
    discardNonQueryableFields(qf, qt);
    return !CollectionUtils.isEmpty(qf) && !StringUtils.isEmpty(qt);
  }

  private static void discardNonQueryableFields(Set<String> qf, String qt) {
    if (qf != null) {
      qf.removeIf(queryField -> !QUERYABLE_FIELDS.contains(queryField));
    }

    if (qt != null && qt.contains(PIPELINE_DELIMITER) && qf != null) {
      qf.remove(QUERY_FIELD_FOR_DEALCATEGORY);
    }
  }

  private static Specification<DirectDeal> withSellerId(long publisherPid) {
    return (root, criteriaQuery, criteriaBuilder) -> {
      Subquery<DirectDeal> dealPublisherQuery =
          dealPublisherQuery(criteriaBuilder, criteriaQuery, publisherPid);
      Subquery<DirectDeal> dealSiteQuery =
          dealSiteQuery(criteriaBuilder, criteriaQuery, publisherPid);
      Subquery<DirectDeal> dealPositionQuery =
          dealPositionQuery(criteriaBuilder, criteriaQuery, publisherPid);
      return criteriaBuilder.or(
          criteriaBuilder.in(root).value(dealPositionQuery),
          criteriaBuilder.in(root).value(dealPublisherQuery),
          criteriaBuilder.in(root).value(dealSiteQuery));
    };
  }

  private static Specification<DirectDeal> withFilterArguments(Set<String> qf, String qt) {
    boolean dealId = qf.contains(QUERY_FIELD_FOR_ID);
    boolean dealDesc = qf.contains(QUERY_FIELD_FOR_DESCRIPTION);
    boolean dealCategory = qf.contains(QUERY_FIELD_FOR_DEALCATEGORY);
    Specification<DirectDeal> idSpec = withDealID(qt);
    Specification<DirectDeal> descriptionSpec = withDealDescription(qt);

    if (dealId && dealDesc && !dealCategory) {
      return idSpec.or(descriptionSpec);
    }
    if (dealCategory && !dealId && !dealDesc) {
      return withDealCategory(Collections.singletonList(qt));
    }

    return dealId ? idSpec : descriptionSpec;
  }

  private static Specification<DirectDeal> withFilterArgumentsForMap(Map<String, List<String>> qf) {
    boolean dealId = qf.containsKey(QUERY_FIELD_FOR_ID);
    boolean dealDesc = qf.containsKey(QUERY_FIELD_FOR_DESCRIPTION);
    boolean dealCategory = qf.containsKey(QUERY_FIELD_FOR_DEALCATEGORY);

    if (dealId && dealDesc && !dealCategory) {
      return (withDealID(qf.get(QUERY_FIELD_FOR_ID).get(0))
          .or(withDealDescription(qf.get(QUERY_FIELD_FOR_DESCRIPTION).get(0))));
    }
    if (dealCategory && !dealId && !dealDesc) {
      return withDealCategory(qf.get(QUERY_FIELD_FOR_DEALCATEGORY));
    }
    if (dealCategory && dealId && !dealDesc) {
      return withDealIDandDealCategory(
          qf.get(QUERY_FIELD_FOR_DEALCATEGORY), qf.get(QUERY_FIELD_FOR_ID));
    }
    if (dealCategory && !dealId && dealDesc) {
      return getWithDealDescandDealCategory(
          qf.get(QUERY_FIELD_FOR_DESCRIPTION), qf.get(QUERY_FIELD_FOR_DEALCATEGORY));
    }
    return dealId
        ? withDealID(qf.get(QUERY_FIELD_FOR_ID).get(0))
        : withDealDescription(qf.get(QUERY_FIELD_FOR_DESCRIPTION).get(0));
  }

  private static Specification<DirectDeal> getWithDealDescandDealCategory(
      List<String> desc, List<String> dealType) {
    List<Integer> type = getDealCategoryIntValues(dealType);
    return (root, criteriaQuery, criteriaBuilder) ->
        criteriaBuilder.and(
            criteriaBuilder.like(root.get(DirectDeal_.DESCRIPTION), "%" + desc.get(0) + "%"),
            root.get(DirectDeal_.dealCategory).in(type));
  }

  private static Specification<DirectDeal> withDealIDandDealCategory(
      List<String> dealType, List<String> dealId) {
    List<Integer> type = getDealCategoryIntValues(dealType);
    return (root, criteriaQuery, criteriaBuilder) ->
        criteriaBuilder.and(
            criteriaBuilder.like(root.get(DirectDeal_.dealId), "%" + dealId.get(0) + "%"),
            root.get(DirectDeal_.dealCategory).in(type));
  }

  private static Specification<DirectDeal> withDealCategory(List<String> category) {
    List<Integer> categoryType = getDealCategoryIntValues(category);
    return (root, criteriaQuery, criteriaBuilder) ->
        criteriaBuilder.and(root.get(DirectDeal_.dealCategory).in(categoryType));
  }

  private static List<Integer> getDealCategoryIntValues(List<String> category) {
    List<String> categoryUcase =
        category.stream().map(String::toUpperCase).collect(Collectors.toList());
    return categoryUcase.stream()
        .map(c -> DealCategory.valueOf(c.replace("-", "_")).asInt())
        .collect(Collectors.toList());
  }

  private static Specification<DirectDeal> withDealID(String id) {
    return (root, criteriaQuery, criteriaBuilder) ->
        criteriaBuilder.like(root.get(DirectDeal_.DEAL_ID), "%" + id + "%");
  }

  private static Specification<DirectDeal> withDealPid(Long pid) {
    return (root, criteriaQuery, criteriaBuilder) ->
        criteriaBuilder.equal(root.get(DirectDeal_.PID), pid);
  }

  private static Specification<DirectDeal> withDealDescription(String description) {
    return (root, criteriaQuery, criteriaBuilder) ->
        criteriaBuilder.like(root.get(DirectDeal_.DESCRIPTION), "%" + description + "%");
  }

  private static Subquery<DirectDeal> dealPublisherQuery(
      CriteriaBuilder criteriaBuilder, CriteriaQuery criteriaQuery, long publisherPid) {
    Subquery<DirectDeal> query = criteriaQuery.subquery(DirectDeal.class);
    Root<DealPublisher> root = query.from(DealPublisher.class);
    query
        .distinct(true)
        .select(root.get(DealPublisher_.DEAL).get(DirectDeal_.PID))
        .where(criteriaBuilder.equal(root.get(DealPublisher_.PUB_PID), publisherPid));
    return query;
  }

  private static Subquery<DirectDeal> dealSiteQuery(
      CriteriaBuilder criteriaBuilder, CriteriaQuery criteriaQuery, long publisherPid) {
    Subquery<DirectDeal> query = criteriaQuery.subquery(DirectDeal.class);
    Root<DealSite> dealSiteRoot = query.from(DealSite.class);
    Root<SiteView> siteRoot = query.from(SiteView.class);
    query
        .distinct(true)
        .select(dealSiteRoot.get(DealSite_.DEAL).get(DirectDeal_.PID))
        .where(
            criteriaBuilder.and(
                criteriaBuilder.equal(
                    siteRoot.get(SiteView_.COMPANY).get(BaseModel_.PID), publisherPid),
                criteriaBuilder.equal(
                    siteRoot.get(SiteView_.PID), dealSiteRoot.get(DealSite_.SITE_PID))));
    return query;
  }

  private static Subquery<DirectDeal> dealPositionQuery(
      CriteriaBuilder criteriaBuilder, CriteriaQuery criteriaQuery, long publisherPid) {
    Subquery<DirectDeal> dealPositionQuery = criteriaQuery.subquery(DirectDeal.class);
    Root<DealPosition> root = dealPositionQuery.from(DealPosition.class);
    dealPositionQuery
        .distinct(true)
        .select(root.get(DealPosition_.DEAL).get(DirectDeal_.PID))
        .where(
            criteriaBuilder.equal(
                root.get(DealPosition_.POSITION_VIEW)
                    .get(PositionView_.SITE_VIEW)
                    .get(SiteView_.COMPANY_PID),
                publisherPid));

    return dealPositionQuery;
  }
}
