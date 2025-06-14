package com.nexage.app.services;

import com.nexage.app.dto.sellingrule.SellerSeatRuleDTO;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SellerSeatRuleService {

  /**
   * Find seller seat rules belonging to a given seller seat. Optionally, filter the rules by name
   * or description.
   *
   * @param sellerSeatPid the PID of the seller seat the rules belong to
   * @param searchFields search fields; allows name and description
   * @param searchTerm search term string, applied to name and description.
   * @param pageable default paging params
   * @return paged result
   */
  Page<SellerSeatRuleDTO> findRulesInSellerSeat(
      Long sellerSeatPid,
      String ruleTypes,
      String statuses,
      Set<String> searchFields,
      String searchTerm,
      Pageable pageable);

  /**
   * Find a seller seat rule addressed by given rule pid that belongs to a given seller seat.
   *
   * @param sellerSeatPid pid of the seller seat that searched rule must belong to.
   * @param sellerSeatRulePid pid of the searched seller seat rule.
   * @return found rule.
   */
  SellerSeatRuleDTO findSellerSeatRule(Long sellerSeatPid, Long sellerSeatRulePid);

  /**
   * Save a new seller seat rule in the DB.
   *
   * @param sellerSeatPid the PID of the seller seat the rule will be created
   * @param sellerSeatRule the seller seat rule to store in the DB
   * @return stored rule
   */
  SellerSeatRuleDTO save(Long sellerSeatPid, SellerSeatRuleDTO sellerSeatRule);

  /**
   * Update an existing seller seat rule
   *
   * @param sellerSeatPid the PID od the seller seat the rule will be updated
   * @param sellerSeatRulePid the PID od the seller seat rule that will be updated
   * @param sellerSeatRule the new shape of the seller seat rule
   * @return updated rule
   */
  SellerSeatRuleDTO update(
      Long sellerSeatPid, Long sellerSeatRulePid, SellerSeatRuleDTO sellerSeatRule);

  /**
   * Mark a seller seat rule as deleted in the DB
   *
   * @param sellerSeatPid the ID of the rule to be removed
   * @param sellerSeatRulePid
   * @return deleted rule with pid only
   */
  SellerSeatRuleDTO delete(Long sellerSeatPid, Long sellerSeatRulePid);
}
