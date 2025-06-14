package com.nexage.app.services.impl;

import com.aol.crs.cdk.cache.model.Creative;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.services.CrsService;
import com.nexage.app.services.SellerCreativeContentService;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class SellerCreativeContentServiceImpl implements SellerCreativeContentService {

  private final CrsService crsService;

  @Override
  public String getCreativeContent(Long sellerId, String creativeId) {
    Optional<Creative> creativeOptional = crsService.fetchCreative(creativeId);
    var creative =
        creativeOptional.orElseThrow(
            () -> new GenevaValidationException(ServerErrorCodes.SERVER_CREATIVE_NOT_FOUND));
    return creative.getContent();
  }
}
