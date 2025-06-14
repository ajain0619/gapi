package com.nexage.app.services;

import com.nexage.app.dto.AccessTokenDTO;
import java.util.Set;

public interface TokenService {

  /**
   * Find all tokens filtered by request crireria
   *
   * @param qt The term to be found.
   * @param qf The field that to search on
   * @return {@link AccessTokenDTO}
   */
  AccessTokenDTO getToken(String qt, Set<String> qf);
}
