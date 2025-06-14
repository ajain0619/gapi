package com.nexage.app.services;

import com.ssp.geneva.server.screenmanagement.dto.DoohScreenDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface DoohScreenService {

  /**
   * Creates and/or Replaces dooh screens in {@link MultipartFile} for the given sellerPid
   *
   * @param sellerPid {@link Long} seller Pid
   * @param screens {@link MultipartFile} JSON file containing {@link List< DoohScreenDTO }
   * @return {@link Integer} number of records/screens created for the seller
   */
  int replaceDoohScreens(Long sellerPid, MultipartFile screens);

  /**
   * Get all dooh screens for given sellerPid
   *
   * @param pageable Pagination based on {@link Pageable}
   * @param sellerPid
   * @return {@link Page} of {@link DoohScreenDTO} for the seller
   */
  Page<DoohScreenDTO> getDoohScreens(Pageable pageable, Long sellerPid);
}
