package com.nexage.app.util.validator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableList;
import com.nexage.admin.core.repository.CompanyRepository;
import com.nexage.app.error.ServerErrorCodes;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SellerSeatSellerValidatorTest {

  @Mock CompanyRepository companyRepository;
  @InjectMocks private SellerSeatSellerValidator sellerSeatSellerValidator;

  private List<Long> sellers = ImmutableList.of(1L, 2L, 3L);

  @Test
  void sellerDoesNotBelongToSellerSeat() {

    when(companyRepository.findCompanyPidsBySellerSeatPid(anyLong())).thenReturn(sellers);
    List<Long> sellerIds = ImmutableList.of(4L);
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> sellerSeatSellerValidator.validate(1L, sellerIds));

    assertEquals(
        ServerErrorCodes.SERVER_SELLERS_SELLER_SEAT_INVALID_COMBINATION, exception.getErrorCode());
  }

  @Test
  void sellerDoesNotBelongToSellerSeat2() {
    when(companyRepository.findCompanyPidsBySellerSeatPid(anyLong())).thenReturn(sellers);
    List<Long> sellerIds = ImmutableList.of(1L, 2L, 4L);
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> sellerSeatSellerValidator.validate(1L, sellerIds));

    assertEquals(
        ServerErrorCodes.SERVER_SELLERS_SELLER_SEAT_INVALID_COMBINATION, exception.getErrorCode());
  }

  @Test
  void sellerSeatDoesNotHaveAnySellers() {
    when(companyRepository.findCompanyPidsBySellerSeatPid(anyLong())).thenReturn(sellers);
    List<Long> sellerIds = ImmutableList.of(4L);
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> sellerSeatSellerValidator.validate(1L, sellerIds));
    assertEquals(
        ServerErrorCodes.SERVER_SELLERS_SELLER_SEAT_INVALID_COMBINATION, exception.getErrorCode());
  }

  @Test
  void sellerBelongToSellerSeat() {
    when(companyRepository.findCompanyPidsBySellerSeatPid(anyLong())).thenReturn(sellers);
    sellerSeatSellerValidator.validate(1L, sellers);
  }
}
