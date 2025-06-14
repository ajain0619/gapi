package com.nexage.app.services.sellingrule.impl;

import com.nexage.admin.core.enums.RuleType;
import com.nexage.admin.core.model.CompanyRule;
import com.nexage.app.dto.sellingrule.SellerRuleDTO;
import com.nexage.app.services.sellingrule.SellerRuleService;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** This service is for use by external API users only. */
@Service
public class SellerRuleAPIServiceImpl extends BaseSellerRuleService implements SellerRuleService {

  public static final String QUALIFIER = "sellerRuleAPIServiceImpl";
  private static final String NOT_IMPLEMENTED_MESSAGE =
      "This operation is not supported for API users.";
  private static final Set<RuleType> TYPE_LIMITATIONS = Set.of(RuleType.BRAND_PROTECTION);

  /** {@inheritDoc} */
  @Override
  @PreAuthorize(
      "@loginUserContext.isOcApi() && @loginUserContext.doSameOrNexageAffiliation(#sellerPid)")
  @Transactional(readOnly = true)
  public SellerRuleDTO findByPidAndSellerPid(Long rulePid, Long sellerPid) {
    return mapper.map(findRule(rulePid, sellerPid));
  }

  /** {@inheritDoc} */
  @Override
  public SellerRuleDTO deleteByPidAndSellerPid(Long rulePid, Long sellerPid) {
    throw new UnsupportedOperationException(NOT_IMPLEMENTED_MESSAGE);
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize(
      "@loginUserContext.isOcApi() && @loginUserContext.doSameOrNexageAffiliation(#sellerPid)")
  @Transactional
  public SellerRuleDTO create(Long sellerPid, SellerRuleDTO sellerRuleDTO) {
    sellerRuleValidator.validateBidManagementAPIRuleDTO(sellerRuleDTO);
    return map(create(sellerRuleDTO, sellerPid));
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize(
      "@loginUserContext.isOcApi() && @loginUserContext.doSameOrNexageAffiliation(#sellerPid)")
  @Transactional(readOnly = true)
  public Page<SellerRuleDTO> findBySellerPidAndOtherCriteria(
      Long sellerPid, SellerRuleQueryFieldParameter queryFieldParameter, Pageable pageable) {
    return findRules(sellerPid, queryFieldParameter, pageable).map(this::map);
  }

  /** {@inheritDoc} */
  @PreAuthorize(
      "@loginUserContext.isOcApi() && @loginUserContext.doSameOrNexageAffiliation(#sellerPid)")
  @Transactional
  @Override
  public SellerRuleDTO update(Long sellerPid, SellerRuleDTO sellerRuleDTO) {
    sellerRuleValidator.validateBidManagementAPIRuleDTO(sellerRuleDTO);
    return map(
        super.updateRule(
            sellerPid,
            sellerRuleDTO,
            targetRule -> sellerRuleValidator.validateBidManagementAPIRule(targetRule)));
  }

  @Override
  protected CompanyRule findRule(Long rulePid, Long sellerPid) {
    return companyRuleRepository.findByPidAndOwnerCompanyPidAndRuleTypeIn(
        rulePid, sellerPid, getTypeLimitations());
  }

  @Override
  protected Set<RuleType> getTypeLimitations() {
    return TYPE_LIMITATIONS;
  }
}
