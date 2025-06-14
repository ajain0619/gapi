package com.nexage.app.util.validator;

import com.ssp.geneva.common.model.inventory.CompanyType;
import lombok.Getter;

/**
 * This class purpose is to offer a shared identifiable company to be used within the same package
 * for validation purposes, to avoid code duplications on those validations.
 */
@Getter
enum InternalCompany {
  NEXAGE(1L, CompanyType.NEXAGE);

  private final long pid;
  private final CompanyType type;

  InternalCompany(long pid, CompanyType type) {
    this.pid = pid;
    this.type = type;
  }
}
