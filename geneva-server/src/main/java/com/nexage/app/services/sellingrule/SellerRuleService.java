package com.nexage.app.services.sellingrule;

import com.nexage.app.dto.sellingrule.SellerRuleDTO;
import com.nexage.app.services.sellingrule.impl.SellerRuleQueryFieldParameter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SellerRuleService {

  /**
   * Find a seller rule addressed by given rule pid that belongs to a given seller
   *
   * @param rulePid pid of the searched seller rule
   * @param sellerPid pid of the seller that searched rule must belong to
   * @return found rule
   */
  SellerRuleDTO findByPidAndSellerPid(Long rulePid, Long sellerPid);

  /**
   * Searches for rules that belongs to a given seller using various criteria specified in a query
   * field
   *
   * @param sellerPid pid of a seller that found rules belong to
   * @param queryFieldParameter query field object representation
   * @param pageable object used for result set paging
   * @return a collection of rules that match the given criteria and belongs to a given seller
   */
  Page<SellerRuleDTO> findBySellerPidAndOtherCriteria(
      Long sellerPid, SellerRuleQueryFieldParameter queryFieldParameter, Pageable pageable);

  /**
   * Soft-delete a seller rule addressed by given rule pid that belongs to a given seller
   *
   * @param rulePid pid of the seller rule
   * @param sellerPid pid of the seller that rule must belong to
   * @return deleted rule with pid only
   */
  SellerRuleDTO deleteByPidAndSellerPid(Long rulePid, Long sellerPid);

  /**
   * Creates a new rule for a given seller.
   *
   * @param sellerPid pid of the seller
   * @param sellerRuleDTO payload
   * @return created rule
   */
  SellerRuleDTO create(Long sellerPid, SellerRuleDTO sellerRuleDTO);

  /**
   * Updates a rule for a given seller.
   *
   * @param sellerPid pid of the seller
   * @param sellerRuleDTO payload
   * @return updated rule
   */
  SellerRuleDTO update(Long sellerPid, SellerRuleDTO sellerRuleDTO);
}
