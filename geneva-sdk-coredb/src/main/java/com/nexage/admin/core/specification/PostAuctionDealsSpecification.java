package com.nexage.admin.core.specification;

import com.nexage.admin.core.enums.MatchType;
import com.nexage.admin.core.enums.RuleTargetType;
import com.nexage.admin.core.model.DirectDeal;
import com.nexage.admin.core.model.DirectDealView_;
import com.nexage.admin.core.model.DirectDeal_;
import com.nexage.admin.core.model.RuleTarget;
import com.nexage.admin.core.model.RuleTarget_;
import com.nexage.admin.core.model.Rule_;
import com.nexage.admin.core.model.SiteView_;
import com.nexage.admin.core.sparta.jpa.model.DealPosition;
import com.nexage.admin.core.sparta.jpa.model.DealPosition_;
import com.nexage.admin.core.sparta.jpa.model.DealPublisher;
import com.nexage.admin.core.sparta.jpa.model.DealPublisher_;
import com.nexage.admin.core.sparta.jpa.model.DealRule;
import com.nexage.admin.core.sparta.jpa.model.DealRule_;
import com.nexage.admin.core.sparta.jpa.model.DealSite;
import com.nexage.admin.core.sparta.jpa.model.DealSite_;
import com.nexage.admin.core.sparta.jpa.model.PositionView_;
import java.util.Set;
import java.util.regex.Pattern;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Subquery;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PostAuctionDealsSpecification {
  private static String fnSqlName = "regexp";

  /**
   * Specification to search to deals against deals ids , seller ids
   *
   * @param sellerPids
   * @param dealIds
   * @param buyerAndBuyerSeatPattern buyerAndBuyerSeatPattern against rule_target data column values
   * @param buyerOnlyPattern buyerPattern against rule_target data column values
   * @return specification on search against sellers ids and buyer company seats
   */
  public static Specification<DirectDeal> withSellersAndDSPs(
      Set<Long> sellerPids,
      Set<String> dealIds,
      String buyerAndBuyerSeatPattern,
      String buyerOnlyPattern) {
    return (root, criteriaQuery, criteriaBuilder) -> {
      var dealPublisherSubQuery =
          buildDealPublisherSubQuery((CriteriaQuery<Long>) criteriaQuery, sellerPids);
      var dealSiteSubQuery = buildDealSiteSubQuery((CriteriaQuery<Long>) criteriaQuery, sellerPids);
      var dealPositionSubQuery =
          buildDealPositionSubQuery((CriteriaQuery<Long>) criteriaQuery, sellerPids);

      var regexPattern = Pattern.compile(buyerAndBuyerSeatPattern);
      var buyerOnlyRegexPattern = Pattern.compile(buyerOnlyPattern);
      var buyersAndSeatPatternExpression = criteriaBuilder.<String>literal(regexPattern.pattern());
      var buyersOnlySeatPatternExpression =
          criteriaBuilder.<String>literal(buyerOnlyRegexPattern.pattern());

      var ruleTargetSubQuery =
          buildRuleTargetSubQuery(
              (CriteriaQuery<Long>) criteriaQuery,
              criteriaBuilder,
              buyersAndSeatPatternExpression,
              buyersOnlySeatPatternExpression);

      var dealRuleSubQuery =
          buildDealRuleSubQuery(
              (CriteriaQuery<Long>) criteriaQuery, criteriaBuilder, ruleTargetSubQuery);

      // final
      var dealPublisherCb =
          criteriaBuilder.in(root.get(DirectDeal_.pid)).value(dealPublisherSubQuery);
      var dealSiteCb = criteriaBuilder.in(root.get(DirectDeal_.pid)).value(dealSiteSubQuery);
      var dealPosition = criteriaBuilder.in(root.get(DirectDeal_.pid)).value(dealPositionSubQuery);

      var dealRuleTarget = criteriaBuilder.in(root.get(DirectDeal_.pid)).value(dealRuleSubQuery);

      if (dealIds != null && !dealIds.isEmpty()) {

        return criteriaBuilder.and(
            criteriaBuilder.or(dealPublisherCb, dealSiteCb, dealPosition),
            dealRuleTarget,
            root.get(DirectDeal_.dealId).in(dealIds));
      }
      return criteriaBuilder.and(
          criteriaBuilder.or(dealPublisherCb, dealSiteCb, dealPosition), dealRuleTarget);
    };
  }

  /**
   * Builds subQuery to get the deals id from deal_publisher table based on seller ids
   *
   * @param criteriaQuery
   * @param sellerIds
   * @return subQuery to get deal_pid from deaL_publisher compare agoinst sellerPids
   */
  private static Subquery<Long> buildDealPublisherSubQuery(
      CriteriaQuery<Long> criteriaQuery, Set<Long> sellerIds) {

    // deal publisher
    var dealPublisherSubQuery = criteriaQuery.subquery(Long.class);
    var dealPublisherRoot = dealPublisherSubQuery.from(DealPublisher.class);
    dealPublisherSubQuery.select(dealPublisherRoot.get(DealPublisher_.deal).get(DirectDeal_.pid));
    dealPublisherSubQuery.where(dealPublisherRoot.get(DealPublisher_.pubPid).in(sellerIds));
    return dealPublisherSubQuery;
  }

  /**
   * Builds subQuery to get the deals id from deal_site table based on seller ids
   *
   * @param criteriaQuery
   * @param sellerIds
   * @return subquery to get deal_pid(s) from deal_site join site to compare against sllerPids
   */
  private static Subquery<Long> buildDealSiteSubQuery(
      CriteriaQuery<Long> criteriaQuery, Set<Long> sellerIds) {

    // deal site
    var dealSiteSubQuery = criteriaQuery.subquery(Long.class);
    var dealSiteRoot = dealSiteSubQuery.from(DealSite.class);
    var deaSiteSiteJoin = dealSiteRoot.join(DealSite_.siteView);
    dealSiteSubQuery.select(dealSiteRoot.get(DealSite_.deal).get(DirectDeal_.pid));
    dealSiteSubQuery.where(deaSiteSiteJoin.get(SiteView_.companyPid).in(sellerIds));
    return dealSiteSubQuery;
  }

  /**
   * Builds subQuery to get the deals id from deal_position table based on seller ids
   *
   * @param criteriaQuery
   * @param sellerIds
   * @return subQuery to get deal_pid(s) from deal_position table join on position and site to
   *     compare against given sellerPids
   */
  private static Subquery<Long> buildDealPositionSubQuery(
      CriteriaQuery<Long> criteriaQuery, Set<Long> sellerIds) {
    // deal position
    var dealPositionSubQuery = criteriaQuery.subquery(Long.class);
    var dealPositionRoot = dealPositionSubQuery.from(DealPosition.class);
    var dealPositionSiteJoin =
        dealPositionRoot.join(DealPosition_.positionView).join(PositionView_.siteView);
    dealPositionSubQuery
        .select(dealPositionRoot.get(DealPosition_.deal).get(DirectDeal_.pid))
        .distinct(true);
    dealPositionSubQuery.where(dealPositionSiteJoin.get(SiteView_.companyPid).in(sellerIds));
    return dealPositionSubQuery;
  }

  /**
   * Builds rule target subQuery by combining predicates of Target_type(22(buyer_seats)) and
   * Match_type(INCLUDE/EXCLUDE) and Regexp on data column to get rule_Target matched pids
   *
   * @param criteriaQuery
   * @param criteriaBuilder
   * @param buyersAndSeatPatternExpression - this regular expression matches both buyer and seat
   *     data column string value
   * @param buyersOnlySeatPatternExpression - this regular expression matches only buyers
   * @return subQuery get rule_pid(s) of rule_target table matching against data column for
   *     buyerCompany , seats pattern , match_type INCLUDE(1)/EXCLUDE(0) and targetType
   *     BUYER_SEATS(22)
   */
  private static Subquery<Long> buildRuleTargetSubQuery(
      CriteriaQuery<Long> criteriaQuery,
      CriteriaBuilder criteriaBuilder,
      Expression<String> buyersAndSeatPatternExpression,
      Expression<String> buyersOnlySeatPatternExpression) {
    var ruleSubQuery = criteriaQuery.subquery(Long.class);
    var ruleTargetRoot = ruleSubQuery.from(RuleTarget.class);
    ruleTargetRoot.join(RuleTarget_.rule);

    ruleSubQuery.select(ruleTargetRoot.get(RuleTarget_.rule).get(Rule_.PID));

    var buyerSeatPredicate =
        criteriaBuilder.equal(
            ruleTargetRoot.get(RuleTarget_.RULE_TARGET_TYPE), RuleTargetType.BUYER_SEATS);
    var includePredicate =
        criteriaBuilder.equal(
            ruleTargetRoot.get(RuleTarget_.matchType), MatchType.INCLUDE_LIST.asInt());
    var excludePredicate =
        criteriaBuilder.equal(
            ruleTargetRoot.get(RuleTarget_.matchType), MatchType.EXCLUDE_LIST.asInt());
    var dataIncludePredicate =
        criteriaBuilder.equal(
            criteriaBuilder.function(
                fnSqlName,
                Integer.class,
                ruleTargetRoot.get(RuleTarget_.DATA),
                buyersAndSeatPatternExpression),
            1);
    var dataExcludePredicate =
        criteriaBuilder.equal(
            criteriaBuilder.function(
                fnSqlName,
                Integer.class,
                ruleTargetRoot.get(RuleTarget_.DATA),
                buyersAndSeatPatternExpression),
            0);
    var buyerOnlyExcludePredicate =
        criteriaBuilder.equal(
            criteriaBuilder.function(
                fnSqlName,
                Integer.class,
                ruleTargetRoot.get(RuleTarget_.DATA),
                buyersOnlySeatPatternExpression),
            1);

    var includeCompositeWithOR =
        criteriaBuilder.or(dataIncludePredicate, buyerOnlyExcludePredicate);
    var includeCompositionPredicate =
        criteriaBuilder.and(buyerSeatPredicate, includePredicate, includeCompositeWithOR);
    var excludeCompositionPredicate =
        criteriaBuilder.and(
            buyerSeatPredicate,
            dataExcludePredicate,
            excludePredicate,
            criteriaBuilder.not(buyerOnlyExcludePredicate));
    ruleSubQuery.where(
        criteriaBuilder.or(includeCompositionPredicate, excludeCompositionPredicate));
    return ruleSubQuery;
  }

  /**
   * Get Deals ids from deal_rule table based on rule_target matched ids
   *
   * @param criteriaQuery
   * @param criteriaBuilder
   * @param ruleTargetSubQuery rule_target data match against pattern
   *     buyersAndSeatPatternExpression, buyersOnlySeatPatternExpression and seatRegExpPattern
   * @return subQuery of deal_pids from deal_rule table joined on deal table where deal_rule_pid is
   *     equal value matched against rule_target of data column for buyerCompany and buyerSeats
   */
  private static Subquery<Long> buildDealRuleSubQuery(
      CriteriaQuery<Long> criteriaQuery,
      CriteriaBuilder criteriaBuilder,
      Subquery<Long> ruleTargetSubQuery) {
    // DealRule target
    var dealRuleSubQuery = criteriaQuery.subquery(Long.class);
    var dealRuleRoot = dealRuleSubQuery.from(DealRule.class);
    dealRuleRoot.join(DealRule_.deal);
    dealRuleSubQuery.select(dealRuleRoot.get(DealRule_.deal).get(DirectDealView_.PID));
    dealRuleSubQuery.where(
        criteriaBuilder.in(dealRuleRoot.get(DealRule_.rulePid)).value(ruleTargetSubQuery));
    return dealRuleSubQuery;
  }
}
