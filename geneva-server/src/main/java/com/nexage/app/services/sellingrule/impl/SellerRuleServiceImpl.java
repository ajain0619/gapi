package com.nexage.app.services.sellingrule.impl;

import com.nexage.admin.core.enums.RuleType;
import com.nexage.app.dto.sellingrule.SellerRuleDTO;
import com.nexage.app.services.sellingrule.SellerRuleService;
import java.util.Set;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** This service is for use with UI only. */
@Service
@Primary
public class SellerRuleServiceImpl extends BaseSellerRuleService implements SellerRuleService {

  private static final Set<RuleType> TYPE_LIMITATIONS = Set.of(RuleType.BRAND_PROTECTION);

  /** {@inheritDoc} */
  @Override
  @PreAuthorize(
      "(@loginUserContext.isOcUserSeller() or @loginUserContext.isOcUserNexage()) "
          + "and @loginUserContext.doSameOrNexageAffiliation(#sellerPid)")
  @Transactional(readOnly = true)
  public SellerRuleDTO findByPidAndSellerPid(Long rulePid, Long sellerPid) {
    return map(findRule(rulePid, sellerPid));
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize(
      "(@loginUserContext.isOcUserSeller() or @loginUserContext.isOcUserNexage() or @loginUserContext.isOcApiSeller()) "
          + "and @loginUserContext.doSameOrNexageAffiliation(#sellerPid)")
  @Transactional(readOnly = true)
  public Page<SellerRuleDTO> findBySellerPidAndOtherCriteria(
      Long sellerPid, SellerRuleQueryFieldParameter queryFieldParameter, Pageable pageable) {
    return findRules(sellerPid, queryFieldParameter, pageable).map(this::map);
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize(
      "(@loginUserContext.isOcAdminNexage() "
          + "or @loginUserContext.isOcManagerNexage() "
          + "or @loginUserContext.isOcManagerYieldNexage() "
          + "or @loginUserContext.isOcManagerSmartexNexage())")
  @Transactional
  public SellerRuleDTO deleteByPidAndSellerPid(Long rulePid, Long sellerPid) {
    deleteRule(rulePid, sellerPid);
    return SellerRuleDTO.builder().pid(rulePid).build();
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize(
      "(@loginUserContext.isOcManagerNexage() or @loginUserContext.isOcManagerSeller()) "
          + "and @loginUserContext.doSameOrNexageAffiliation(#sellerPid)")
  @Transactional
  public SellerRuleDTO create(Long sellerPid, SellerRuleDTO sellerRuleDTO) {
    return map(create(sellerRuleDTO, sellerPid));
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize(
      "(@loginUserContext.isOcManagerNexage() or @loginUserContext.isOcManagerSeller()) "
          + "and @loginUserContext.doSameOrNexageAffiliation(#sellerPid)")
  @Transactional
  public SellerRuleDTO update(Long sellerPid, SellerRuleDTO sellerRuleDTO) {
    return map(super.updateRule(sellerPid, sellerRuleDTO));
  }

  @Override
  protected Set<RuleType> getTypeLimitations() {
    return TYPE_LIMITATIONS;
  }
}
