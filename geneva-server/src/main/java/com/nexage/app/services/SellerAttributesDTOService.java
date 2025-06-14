package com.nexage.app.services;

import com.nexage.app.dto.seller.SellerAttributesDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SellerAttributesDTOService {

  /**
   * Gets paginated {@link SellerAttributesDTO} object from the Database
   *
   * @param sellerPid {@link Long}
   * @param pageable Pagination based on {@link Pageable}
   * @return {@link Page} {@link SellerAttributesDTO}
   */
  Page<SellerAttributesDTO> getSellerAttribute(Long sellerPid, Pageable pageable);

  /**
   * Updates an existing {@link SellerAttributesDTO} object in the Database
   *
   * @param sellerAttributesDTO {@link SellerAttributesDTO}
   * @return {@link SellerAttributesDTO}
   */
  SellerAttributesDTO updateSellerAttribute(SellerAttributesDTO sellerAttributesDTO);
}
