package com.ssp.geneva.common.security.model;

import com.nexage.admin.core.model.User;
import com.ssp.geneva.sdk.onecentral.model.Entitlement;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserAuth implements Serializable {

  private static final long serialVersionUID = 1L;

  private final User user;
  private final List<Entitlement> entitlements;
}
