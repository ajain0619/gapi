package com.nexage.app.services;

import com.nexage.app.dto.ExchangePublisherDTO;
import com.nexage.app.dto.ExchangeSiteDTO;
import java.util.List;

public interface PublisherService {

  public List<ExchangePublisherDTO> getExchangePublishers();

  public List<ExchangeSiteDTO> getExchangeSites();
}
