package com.nexage.app.util.validator.deals;

import static com.nexage.app.util.validator.deals.DealCategoryValidator.validateCreateDirectDealCategory;
import static com.nexage.app.util.validator.deals.DealCategoryValidator.validateUpdateDirectDealCategory;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.nexage.admin.core.enums.DealCategory;
import com.nexage.admin.core.model.DirectDeal;
import com.nexage.app.dto.DirectDealDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import org.junit.jupiter.api.Test;

class DealCategoryValidatorTest {

  @Test
  void shouldCreateWhenNullDealCategory() {
    DirectDealDTO deal = DirectDealDTO.builder().dealCategory(null).build();

    validateCreateDirectDealCategory(deal);

    assertEquals(DealCategory.SSP.asInt(), deal.getDealCategory());
  }

  @Test
  void shouldValidationPassWhenMultipartyDealCategoryForCreate() {
    DirectDealDTO deal = DirectDealDTO.builder().dealCategory(DealCategory.SSP.asInt()).build();

    validateCreateDirectDealCategory(deal);

    assertEquals(DealCategory.SSP.asInt(), deal.getDealCategory());
  }

  @Test
  void shouldPassValidationWhenS2SDealCategoryForCreate() {
    DirectDealDTO deal =
        DirectDealDTO.builder().dealCategory(DealCategory.S2S_PLACEMENT_DEAL.asInt()).build();

    assertDoesNotThrow(() -> validateCreateDirectDealCategory(deal));
  }

  @Test
  void shouldFailToCreateFromNullDirectDealDTO() {
    DirectDealDTO deal = null;

    GenevaValidationException exception =
        assertThrows(GenevaValidationException.class, () -> validateCreateDirectDealCategory(deal));

    assertEquals(ServerErrorCodes.SERVER_INVALID_DEAL, exception.getErrorCode());
  }

  @Test
  void shouldFailToUpdateFromNullDirectDealDTO() {
    DirectDealDTO dealDTO = null;
    DirectDeal deal = new DirectDeal();

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class, () -> validateUpdateDirectDealCategory(deal, dealDTO));

    assertEquals(ServerErrorCodes.SERVER_INVALID_DEAL, exception.getErrorCode());
  }

  @Test
  void shouldFailTUpdateFromNullDirectDeal() {
    DirectDealDTO dealDTO = DirectDealDTO.builder().build();
    DirectDeal deal = null;

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class, () -> validateUpdateDirectDealCategory(deal, dealDTO));

    assertEquals(ServerErrorCodes.SERVER_INVALID_DEAL, exception.getErrorCode());
  }

  @Test
  void shouldUpdateWhenNullDealCategory() {
    DirectDealDTO dealDTO = DirectDealDTO.builder().dealCategory(null).build();
    DirectDeal deal = new DirectDeal();
    deal.setDealCategory(null);

    validateUpdateDirectDealCategory(deal, dealDTO);

    assertEquals(DealCategory.SSP.asInt(), deal.getDealCategory());
    assertEquals(DealCategory.SSP.asInt(), dealDTO.getDealCategory());
  }

  @Test
  void shouldValidationPassWhenMultipartyDealCategoryForUpdate() {
    DirectDealDTO dealDTO = DirectDealDTO.builder().dealCategory(DealCategory.SSP.asInt()).build();
    DirectDeal deal = new DirectDeal();
    deal.setDealCategory(DealCategory.SSP.asInt());

    validateUpdateDirectDealCategory(deal, dealDTO);

    assertEquals(DealCategory.SSP.asInt(), deal.getDealCategory());
    assertEquals(DealCategory.SSP.asInt(), dealDTO.getDealCategory());
  }
}
