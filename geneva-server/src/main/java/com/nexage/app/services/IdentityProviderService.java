package com.nexage.app.services;

import com.nexage.app.dto.IdentityProviderDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/** Defines business logic operations for interacting with {@link IdentityProviderDTO}. */
public interface IdentityProviderService {

  /**
   * Get all {@link IdentityProviderDTO}, returning a paginated response.
   *
   * @param pageable Pagination based on {@link Pageable}
   * @return {@link Page} of {@link IdentityProviderDTO} instances based on parameters.
   */
  Page<IdentityProviderDTO> getAllIdentityProviders(Pageable pageable);
}
