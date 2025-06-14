package com.nexage.app.services;

import com.ssp.geneva.sdk.onecentral.model.Entitlement;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EntitlementService {

  /**
   * @param qt query term i.e "current"
   * @param qf query field ie. "status"
   * @param pageable pagination default = 10
   * @return Returns current user entitlements
   */
  Page<Entitlement> getEntitlements(String qt, Set<String> qf, Pageable pageable);
}
