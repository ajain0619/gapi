package com.ssp.geneva.server.bidinspector.services.impl;

import com.ssp.geneva.common.model.search.MultiValueQueryParams;
import com.ssp.geneva.common.model.search.util.MapParamDecoder;
import com.ssp.geneva.server.bidinspector.dao.BidDao;
import com.ssp.geneva.server.bidinspector.dto.AuctionDetailDTO;
import com.ssp.geneva.server.bidinspector.dto.BidDTO;
import com.ssp.geneva.server.bidinspector.services.BidService;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class BidServiceImpl implements BidService {
  private final BidDao bidDao;

  /** {@inheritDoc} */
  @Override
  @PreAuthorize("(@loginUserContext.isOcAdminNexage() or @loginUserContext.isOcManagerNexage())")
  public Page<BidDTO> getBidDetails(
      MultiValueQueryParams multiValueQueryParams, Pageable pageable) {
    Map<String, String> queryMap = MapParamDecoder.decodeMap(multiValueQueryParams.getFields());
    return bidDao.getBidDetails(
        new ArrayList<>(queryMap.keySet()), queryMap.values().stream().toList(), pageable);
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize("(@loginUserContext.isOcAdminNexage() or @loginUserContext.isOcManagerNexage())")
  public Page<AuctionDetailDTO> getAuctionDetails(
      MultiValueQueryParams multiValueQueryParams, String auctionRunId, Pageable pageable) {
    Map<String, String> queryMap = MapParamDecoder.decodeMap(multiValueQueryParams.getFields());
    return bidDao.getAuctionDetails(
        new ArrayList<>(queryMap.keySet()),
        queryMap.values().stream().collect(Collectors.toList()),
        auctionRunId,
        pageable);
  }
}
