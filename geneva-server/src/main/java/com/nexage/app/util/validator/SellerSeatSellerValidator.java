package com.nexage.app.util.validator;

import com.nexage.admin.core.repository.CompanyRepository;
import com.nexage.app.error.ServerErrorCodes;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SellerSeatSellerValidator {
  private final CompanyRepository companyRepository;

  /** Validator contains validation logic if a list of sellers belong to a seller seat */
  public void validate(Long sellerSeatId, List<Long> sellerIds) {
    if (sellerSeatId != null
        && CollectionUtils.isNotEmpty(sellerIds)
        && !companyRepository.findCompanyPidsBySellerSeatPid(sellerSeatId).containsAll(sellerIds)) {
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_SELLERS_SELLER_SEAT_INVALID_COMBINATION);
    }
  }
}
