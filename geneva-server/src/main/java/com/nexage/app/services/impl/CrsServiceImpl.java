package com.nexage.app.services.impl;

import com.aol.crs.cdk.cache.model.Creative;
import com.aol.crs.cdk.cache.model.CreativeStatus;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.services.CrsService;
import com.nexage.app.services.crs.CdkClientResource;
import com.ssp.geneva.common.error.exception.GenevaAppRuntimeException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class CrsServiceImpl implements CrsService {

  private final CdkClientResource cdkClientResource;

  @Autowired
  public CrsServiceImpl(CdkClientResource cdkClientResource) {
    this.cdkClientResource = cdkClientResource;
  }

  @Override
  public Optional<Creative> fetchCreative(
      String adSourceId, String buyerId, String buyerCreativeId) {
    try {
      CompletableFuture<Creative> creative =
          cdkClientResource
              .getCdkClient()
              .fetchCreative(adSourceId, buyerId, buyerCreativeId, null, true, true, true);
      return Optional.ofNullable(creative.get());
    } catch (Exception e) {
      log.error("Error occurred in CRS while getting creative details: {}", e.getMessage(), e);
    }
    return Optional.empty();
  }

  @Override
  public Optional<Creative> fetchCreative(String crsId) {
    try {
      CompletableFuture<Creative> creative =
          cdkClientResource.getCdkClient().fetchCreative(crsId, null, null, null, true);
      return Optional.ofNullable(creative.get());
    } catch (Exception e) {
      log.error("Error occurred in CRS while getting creative details: {}", e.getMessage(), e);
      throw new GenevaAppRuntimeException(ServerErrorCodes.SERVER_CRS_INTERNAL_ERROR);
    }
  }

  /** {@inheritDoc} */
  @Override
  public CreativeStatus checkCreative(Creative creative) {
    return cdkClientResource.getCdkClient().checkCreative(creative);
  }
}
