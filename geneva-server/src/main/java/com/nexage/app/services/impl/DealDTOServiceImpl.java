package com.nexage.app.services.impl;

import static com.nexage.admin.core.specification.DirectDealSpecification.QF_ALL;
import static com.nexage.admin.core.specification.DirectDealSpecification.QF_HAS_RULES;
import static com.nexage.admin.core.specification.PostAuctionDealsSpecification.withSellersAndDSPs;

import com.nexage.admin.core.enums.DealCategory;
import com.nexage.admin.core.model.DirectDeal;
import com.nexage.admin.core.repository.DirectDealRepository;
import com.nexage.admin.core.specification.DirectDealSpecification;
import com.nexage.app.dto.DirectDealDTO;
import com.nexage.app.dto.deals.DealDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.mapper.deal.DealDTOMapper;
import com.nexage.app.services.DealDTOService;
import com.nexage.app.util.DirectDealUtil;
import com.nexage.app.util.validator.SearchRequestParamValidator;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.error.model.CommonErrorCodes;
import com.ssp.geneva.common.model.search.MultiValueQueryParams;
import com.ssp.geneva.common.model.search.util.MapParamDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DealDTOServiceImpl implements DealDTOService {

  private static final Pattern MULTI_VALUE_PARAM_PATTERN =
      Pattern.compile("^\\{(\\w+=\\w+([\\|$&+,*%\\?@#\\-]{1,10}\\w+){0,10},?)+\\}$");
  public static final String DEAL_CATEGORY = "dealCategory";

  private final DirectDealRepository directDealRepository;

  @Autowired
  public DealDTOServiceImpl(DirectDealRepository directDealRepository) {
    this.directDealRepository = directDealRepository;
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize("@loginUserContext.isOcUserNexage()")
  public Page<DealDTO> findAll(String qt, Set<String> qf, Pageable pageable) {
    var paramMap = createMultiValueMap(qf);

    if (paramMap.isPresent()) {
      validateSearchParamRequest(paramMap.get().keySet(), DirectDealDTO.class);
    } else {
      validateSearchParamRequest(qf, DirectDealDTO.class);
    }
    if (qf != null && paramMap.isPresent() && paramMap.get().containsKey(DEAL_CATEGORY)) {
      validateDealCategoryIfPresent(paramMap.get());
    }

    Page<DirectDeal> dealPage =
        paramMap.isPresent()
            ? directDealRepository.findAll(DirectDealSpecification.of(paramMap.get()), pageable)
            : directDealRepository.findAll(DirectDealSpecification.of(qf, qt), pageable);
    Page<DealDTO> dealDTOPage = dealPage.map(DealDTOMapper.MAPPER::map);
    return dealDTOPage;
  }

  @Override
  public void validateDealCategoryIfPresent(Map<String, List<String>> qf) {
    for (String q : qf.get(DEAL_CATEGORY)) {
      if (!EnumUtils.isValidEnum(DealCategory.class, q.toUpperCase())) {
        throw new GenevaValidationException(CommonErrorCodes.COMMON_BAD_REQUEST);
      }
    }
  }

  /** {@inheritDoc} */
  @Transactional(readOnly = true)
  @PreAuthorize("@loginUserContext.isOcUserNexage()")
  public DealDTO findOne(Long dealPid) {
    return DealDTOMapper.MAPPER.map(
        directDealRepository
            .findByPid(dealPid)
            .orElseThrow(
                () -> new GenevaValidationException(ServerErrorCodes.SERVER_DEAL_NOT_FOUND)));
  }

  private void validateSearchParamRequest(Set<String> qf, Class classType) {
    if (!(SearchRequestParamValidator.isValid(qf, classType) || containAdditionalQueryFields(qf))) {
      throw new GenevaValidationException(CommonErrorCodes.COMMON_BAD_REQUEST);
    }
  }

  private boolean containAdditionalQueryFields(Set<String> qf) {
    return qf.contains(QF_HAS_RULES) || qf.contains(QF_ALL);
  }

  @Override
  public Optional<Map<String, List<String>>> createMultiValueMap(Set<String> qf) {
    if (qf != null) {
      var value = qf.stream().collect(Collectors.joining(","));
      var matcher = MULTI_VALUE_PARAM_PATTERN.matcher(value.replaceAll("\\s", ""));
      HashMap<String, List<String>> modifiableMap = new HashMap<>();

      if (matcher.matches()) {
        modifiableMap.putAll(MapParamDecoder.decodeQueryParam(value));
        return Optional.of(modifiableMap);
      }
    }
    return Optional.empty();
  }

  @Override
  public Page<DealDTO> getDeals(MultiValueQueryParams multiValueQueryParams, Pageable pageable) {
    var buyerAndBuyerSeatPattern =
        DirectDealUtil.getBuyerAndBuyerSeatPattern(multiValueQueryParams);
    var buyerOnlyRegexPattern = DirectDealUtil.getBuyerPattern(multiValueQueryParams);
    var deals = DirectDealUtil.getDealIds(multiValueQueryParams);
    var sellers = DirectDealUtil.getSellerPids(multiValueQueryParams);
    var specification =
        withSellersAndDSPs(sellers, deals, buyerAndBuyerSeatPattern, buyerOnlyRegexPattern);
    return directDealRepository.findAll(specification, pageable).map(DealDTOMapper.MAPPER::map);
  }
}
