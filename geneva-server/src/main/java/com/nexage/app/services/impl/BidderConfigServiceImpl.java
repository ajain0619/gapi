package com.nexage.app.services.impl;

import static com.nexage.admin.core.util.XssSanitizerUtil.sanitize;

import com.google.common.base.Joiner;
import com.nexage.admin.core.dto.BidderSummaryDTO;
import com.nexage.admin.core.enums.BlockListInclusion;
import com.nexage.admin.core.enums.VerificationType;
import com.nexage.admin.core.enums.site.Type;
import com.nexage.admin.core.model.BidderConfig;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.repository.BidderConfigRepository;
import com.nexage.admin.core.repository.ExchangeRegionalRepository;
import com.nexage.admin.core.util.UUIDGenerator;
import com.nexage.app.services.BidderConfigService;
import java.math.BigDecimal;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BidderConfigServiceImpl implements BidderConfigService {

  private static final Long DEFAULT_EXCHANGE_REGION_PID = 1L;

  private final BidderConfigRepository bidderConfigRepository;
  private final ExchangeRegionalRepository exchangeRegionalRepository;
  private final EntityManager entityManager;

  public BidderConfigServiceImpl(
      BidderConfigRepository bidderConfigRepository,
      ExchangeRegionalRepository exchangeRegionalRepository,
      EntityManager entityManager) {
    this.bidderConfigRepository = bidderConfigRepository;
    this.exchangeRegionalRepository = exchangeRegionalRepository;
    this.entityManager = entityManager;
  }

  @Override
  public List<BidderSummaryDTO> getBidderSummaries() {
    return findActive().stream()
        .map(bc -> new BidderSummaryDTO(bc.getPid(), sanitize(bc.getName()), bc.getCompanyPid()))
        .collect(Collectors.toList());
  }

  @Override
  public BidderConfig createDefaultBidderConfigForBuyer(Company company) {
    var bidderConfig = new BidderConfig();
    bidderConfig.setCompany(company);
    bidderConfig.setTrafficStatus(false);
    bidderConfig.setAllowedTraffic(Joiner.on(",").join(EnumSet.allOf(Type.class)));

    bidderConfig.setIncludeLists(BlockListInclusion.ADAPTIVE);
    bidderConfig.setDefaultBidCurrency(company.getCurrency());
    bidderConfig.setDefaultBidUnit(0);
    bidderConfig.setRequestRateFilter(-1);
    bidderConfig.setAuctionTypeFilter(0);
    bidderConfig.setScriptAllowedFilter(false);
    bidderConfig.setVersion(1);
    bidderConfig.setVerificationType(VerificationType.STANDARD);
    bidderConfig.setBidRequestCpm(BigDecimal.ZERO);
    bidderConfig.setId((String) new UUIDGenerator().generate());
    bidderConfig.setCreationDate(new Date());
    exchangeRegionalRepository
        .findById(DEFAULT_EXCHANGE_REGION_PID)
        .ifPresent(
            defaultExchangeRegional ->
                bidderConfig.getExchangeRegionals().add(defaultExchangeRegional));
    return bidderConfig;
  }

  @Override
  public BidderConfig saveFlushAndRefreshBidderConfig(BidderConfig bidderConfig) {
    BidderConfig savedBidderConfig = bidderConfigRepository.saveAndFlush(bidderConfig);
    entityManager.refresh(savedBidderConfig);
    return savedBidderConfig;
  }

  private List<BidderConfig> findActive() {
    return bidderConfigRepository.findByTrafficStatus(true);
  }
}
