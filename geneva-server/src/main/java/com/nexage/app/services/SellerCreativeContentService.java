package com.nexage.app.services;

public interface SellerCreativeContentService {

  /**
   * Return one {@link String} under request criteria, returning a single string response.
   *
   * @param creativeId creativeId
   * @return {@link String} instance based on parameters.
   */
  String getCreativeContent(Long sellerId, String creativeId);
}
