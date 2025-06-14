package com.nexage.app.services;

import com.nexage.admin.core.dto.AdSourceSummaryDTO;
import com.nexage.admin.core.dto.BidderSummaryDTO;
import com.nexage.admin.core.model.AdSource;
import com.nexage.admin.core.model.BidderConfig;
import com.nexage.admin.core.model.ExchangeProduction;
import com.nexage.admin.core.model.ExchangeRegional;
import com.nexage.app.dto.buyer.BuyerGroupDTO;
import com.nexage.app.dto.buyer.BuyerSeatDTO;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Pageable;

public interface BuyerService {

  List<AdSourceSummaryDTO> getAllAdSourceSummaries();

  List<BidderSummaryDTO> getAllBidderSummaries();

  BidderConfig getBidderConfig(long buyerPid, long bidderConfigPid);

  List<BidderConfig> getAllBidderConfigsByCompanyPid(Long companyPid);

  BidderConfig createBidderConfig(Long buyerPid, BidderConfig bidderConfig);

  BidderConfig updateBidderConfig(long bidderConfigPid, BidderConfig bidderConfig, Long BuyerPid);

  void deleteBidderConfig(Long bidderConfigPid);

  AdSource getAdSource(Long adSourcePid);

  List<AdSource> getAllAdSourcesByCompanyPid(Long companyPid);

  List<AdSourceSummaryDTO> getAllAdSourceSummariesByCompanyPid(Long companyPid);

  AdSource createAdSource(Long companyPid, AdSource adSource);

  /**
   * Update ad source with object provided.
   *
   * @param companyPid associated company PID
   * @param adSource updated ad source object
   * @param adSourcePid associated ad source PID
   * @return {@link AdSource} confirmed updated ad source object
   */
  AdSource updateAdSource(Long companyPid, AdSource adSource, Long adSourcePid);

  void deleteAdSource(Long adSourcePid);

  List<ExchangeRegional> getAllExchangeRegions();

  List<ExchangeProduction> getAllExchangeProductions();

  BuyerGroupDTO createBuyerGroup(Long companyPid, BuyerGroupDTO dto);

  /**
   * @param companyPid Company unique Pid
   * @return {@link List} of {@link BuyerGroupDTO}
   * @deprecated use {@link BuyerGroupDTOService#findAll(Long, Set, String, Pageable)} instead.
   */
  @Deprecated
  List<BuyerGroupDTO> getAllBuyerGroupsForCompany(Long companyPid);

  List<BuyerSeatDTO> getAllBuyerSeatsForCompanyAndName(
      Long companyPid, String name, Set<String> qf, String qt);

  BuyerGroupDTO updateBuyerGroup(Long companyPid, Long buyerGroupPid, BuyerGroupDTO dto);

  BuyerSeatDTO updateBuyerSeat(Long companyPid, Long buyerSeatPid, BuyerSeatDTO dto);

  BuyerSeatDTO createBuyerSeat(Long companyPid, BuyerSeatDTO dto);
}
