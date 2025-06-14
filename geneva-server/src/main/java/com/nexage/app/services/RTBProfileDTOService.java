package com.nexage.app.services;

import com.nexage.app.dto.RTBProfileDTO;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RTBProfileDTOService {

  /**
   * Gets all RTB profiles for a given seller id ,accepts filtering by name as parameter
   *
   * @param pageable
   * @param sellerPid
   * @param qt
   * @param qf
   * @return Paginated RTBProfiles list
   */
  Page<RTBProfileDTO> getRTBProfiles(Pageable pageable, Long sellerPid, String qt, Set<String> qf);
  /**
   * Updates a RTBProfile given a Seller Id and a RTBProfile Id
   *
   * @param sellerPid
   * @param rtbProfileDTO
   * @param rtbPid
   * @return Updated RTBProfile
   */
  RTBProfileDTO update(Long sellerPid, RTBProfileDTO rtbProfileDTO, long rtbPid);
}
