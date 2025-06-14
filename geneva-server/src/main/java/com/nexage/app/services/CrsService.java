package com.nexage.app.services;

import com.aol.crs.cdk.cache.model.Creative;
import com.aol.crs.cdk.cache.model.CreativeStatus;
import java.util.Optional;

public interface CrsService {

  Optional<Creative> fetchCreative(String adSourceId, String buyerId, String buyerCreativeId);

  /**
   * Find {@link Creative} under request criteria, returning a single response.
   *
   * @param crsId The CrsId to be found on MicroSoft CRS service
   * @return Optional {@link Creative} instance.
   */
  Optional<Creative> fetchCreative(String crsId);

  /**
   * Submits the creative to CRS for review and returns the creative status.
   *
   * @param creative The creative data to submit to CRS for review.
   * @return The status of the creative.
   */
  CreativeStatus checkCreative(Creative creative);
}
