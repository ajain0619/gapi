package com.nexage.app.services.impl;

import com.google.common.base.Strings;
import com.nexage.app.security.UserContext;
import com.nexage.app.services.EntitlementService;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.error.model.CommonErrorCodes;
import com.ssp.geneva.sdk.onecentral.model.Entitlement;
import java.util.Collections;
import java.util.Set;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class EntitlementServiceImpl implements EntitlementService {

  private final UserContext userContext;

  @Autowired
  public EntitlementServiceImpl(UserContext userContext) {
    this.userContext = userContext;
  }

  /** {@inheritDoc} */
  @Override
  public Page<Entitlement> getEntitlements(String qt, Set<String> qf, Pageable pageable) {
    validateSearchParamRequest(qf, qt);
    if (qf.contains("status") && qt.contains("current")) {
      return new PageImpl<>(
          userContext.getCurrentUser().getEntitlements(),
          pageable,
          userContext.getCurrentUser().getEntitlements().size()) {};
    }
    return new PageImpl<>(Collections.emptyList(), pageable, 0);
  }

  private void validateSearchParamRequest(Set<String> qf, String qt) {
    if (CollectionUtils.isEmpty(qf) || Strings.isNullOrEmpty(qt))
      throw new GenevaValidationException(CommonErrorCodes.COMMON_BAD_REQUEST);
  }
}
