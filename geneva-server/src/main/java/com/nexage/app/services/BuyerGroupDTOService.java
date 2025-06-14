package com.nexage.app.services;

import com.nexage.app.dto.buyer.BuyerGroupDTO;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BuyerGroupDTOService {

  /**
   * Find all {@link BuyerGroupDTO} under request criteria, returning a paginated response.
   *
   * @param companyPid Unique Pid.
   * @param qf Unique {@link Set} of fields.
   * @param qt The term to be found.
   * @param pageable Pagination based on {@link Pageable}
   * @return {@link Page} of {@link BuyerGroupDTO} instances based on parameters.
   */
  Page<BuyerGroupDTO> findAll(Long companyPid, Set<String> qf, String qt, Pageable pageable);

  /**
   * Find one {@link BuyerGroupDTO}.
   *
   * @param buyerGroupPid Unique Pid.
   * @return {@link BuyerGroupDTO} based on pid.
   */
  BuyerGroupDTO findOne(Long buyerGroupPid);

  /**
   * Saves a {@link BuyerGroupDTO} object to the database
   *
   * @param dspPid {@link Long} pid of the dsp the created buyergroup will belong to
   * @param buyerGroupDTO {@link BuyerGroupDTO} create payload
   * @return {@link BuyerGroupDTO}
   */
  BuyerGroupDTO create(Long dspPid, BuyerGroupDTO buyerGroupDTO);

  /**
   * Saves a {@link BuyerGroupDTO} object to the database
   *
   * @param dspPid pid of the dsp
   * @param buyerGroupPid pid of the bidder config
   * @param buyerGroupDTO update payload
   * @return {@link BuyerGroupDTO}
   */
  BuyerGroupDTO update(Long dspPid, Long buyerGroupPid, BuyerGroupDTO buyerGroupDTO);
}
