package com.nexage.app.services;

import com.nexage.admin.core.model.ExchangeRate;
import com.nexage.app.dto.ExchangeRateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ExchangeRateService {

  /**
   * Retrieve a page of {@link ExchangeRateDTO} available objects based on the query/page
   * parameters. Note that this service is intended to provide a mechanism to discover any available
   * {@link ExchangeRateDTO} entities which are available.
   *
   * @param qf Query field of type {@link String}. This is used to search by a field of {@link
   *     ExchangeRate}.
   * @param qt Query term of type {@link String} that is being searched for.
   * @param pageable The {@link Pageable} pagination parameters.
   * @param latest A {@link Boolean} value that is used to decide whether to filter only latest
   *     exchange rates.
   * @return {@link Page} of {@link ExchangeRateDTO} objects based on the query/page/latest
   *     parameters.
   */
  Page<ExchangeRateDTO> getAllExchangeRates(
      String qf, String qt, Pageable pageable, Boolean latest);
}
