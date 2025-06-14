package com.nexage.app.util.validator.deals;

import com.nexage.admin.core.enums.DealCategory;
import com.nexage.admin.core.model.DirectDeal;
import com.nexage.app.dto.DirectDealDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DealCategoryValidator {

  /** @param dealDTO validates the fields for DirectDealCategory / Global Dashboard */
  public static void validateCreateDirectDealCategory(DirectDealDTO dealDTO) {
    defaultValidateDirectDealDTOCategory(dealDTO);
  }

  /** @param deal, dealDTO validates the fields for DirectDealCategory / Global Dashboard */
  public static void validateUpdateDirectDealCategory(DirectDeal deal, DirectDealDTO dealDTO) {
    defaultValidateDirectDealDTOCategory(dealDTO);
    defaultValidateDirectDealCategory(deal);
  }

  private static void defaultValidateDirectDealDTOCategory(DirectDealDTO dealDTO) {
    validateDealNotNull(dealDTO);
    defaultNullDirectDealDTOCategory(dealDTO);
    if (dealDTO.getDealCategory() != null) {
      validateDirectDealCategory(DealCategory.fromInt(dealDTO.getDealCategory()));
    }
  }

  private static void defaultValidateDirectDealCategory(DirectDeal deal) {
    validateDealNotNull(deal);
    defaultNullDirectDealCategory(deal);
    if (deal.getDealCategory() != null) {
      validateDirectDealCategory(DealCategory.fromInt(deal.getDealCategory()));
    }
  }

  private static void defaultNullDirectDealDTOCategory(DirectDealDTO dealDTO) {
    if (dealDTO.getDealCategory() == null) {
      dealDTO.setDealCategory(DealCategory.SSP.asInt());
    }
  }

  private static void defaultNullDirectDealCategory(DirectDeal deal) {
    if (deal.getDealCategory() == null) {
      deal.setDealCategory(DealCategory.SSP.asInt());
    }
  }

  private static void validateDealNotNull(Object deal) {
    if (deal == null) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_DEAL);
    }
  }

  private static void validateDirectDealCategory(DealCategory dealCategory) {
    if (dealCategory != DealCategory.SSP && dealCategory != DealCategory.S2S_PLACEMENT_DEAL) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_DEAL_CATEGORY);
    }
  }
}
