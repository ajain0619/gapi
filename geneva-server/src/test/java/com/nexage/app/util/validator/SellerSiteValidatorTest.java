package com.nexage.app.util.validator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.google.common.collect.ImmutableList;
import com.nexage.admin.core.repository.SiteRepository;
import com.nexage.app.error.ServerErrorCodes;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SellerSiteValidatorTest {

  @Mock SiteRepository siteRepository;
  @InjectMocks private SellerSiteValidator sellerSiteValidator;

  private final Set<Long> sites = Set.of(1L, 2L, 3L);
  private final List<Long> sellers = List.of(100L);

  @Test
  void sellerDoesNotBelongToSellerSeat() {
    // given
    given(siteRepository.findPidsByCompanyPidsWithStatusNotDeleted(any())).willReturn(sites);
    List<Long> siteIds = ImmutableList.of(4L);

    // when
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class, () -> sellerSiteValidator.validate(sellers, siteIds));

    // then
    assertEquals(
        ServerErrorCodes.SERVER_SITES_SELLERS_INVALID_COMBINATION, exception.getErrorCode());
  }

  @Test
  void sellerDoesNotBelongToSellerSeat2() {
    // given
    given(siteRepository.findPidsByCompanyPidsWithStatusNotDeleted(any())).willReturn(sites);
    List<Long> siteIds = ImmutableList.of(1L, 2L, 4L);

    // when
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class, () -> sellerSiteValidator.validate(sellers, siteIds));

    // then
    assertEquals(
        ServerErrorCodes.SERVER_SITES_SELLERS_INVALID_COMBINATION, exception.getErrorCode());
  }

  @Test
  void sellerSeatDoesNotHaveAnySellers() {
    // given
    given(siteRepository.findPidsByCompanyPidsWithStatusNotDeleted(any())).willReturn(sites);
    List<Long> siteIds = ImmutableList.of(4L);

    // when
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class, () -> sellerSiteValidator.validate(sellers, siteIds));

    // then
    assertEquals(
        ServerErrorCodes.SERVER_SITES_SELLERS_INVALID_COMBINATION, exception.getErrorCode());
  }

  @Test
  void sellerBelongToSellerSeat() {
    // given
    given(siteRepository.findPidsByCompanyPidsWithStatusNotDeleted(any())).willReturn(sites);

    // when
    sellerSiteValidator.validate(sellers, List.of(1L, 2L, 3L));

    // then should not throw any exception
  }
}
