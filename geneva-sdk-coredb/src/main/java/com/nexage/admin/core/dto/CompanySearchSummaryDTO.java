package com.nexage.admin.core.dto;

import java.io.Serializable;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
public final class CompanySearchSummaryDTO extends SearchSummaryDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  @ToString.Include @EqualsAndHashCode.Include private final Boolean defaultRtbProfilesEnabled;
  @ToString.Include @EqualsAndHashCode.Include private final String currency;
  @ToString.Include @EqualsAndHashCode.Include private final Integer sellerSeatPid;

  @Builder
  public CompanySearchSummaryDTO(
      long pid,
      String name,
      Type type,
      String currency,
      boolean defaultRtbProfilesEnabled,
      Integer sellerSeatPid) {
    super(pid, name, type);
    this.currency = currency;
    this.defaultRtbProfilesEnabled = defaultRtbProfilesEnabled;
    this.sellerSeatPid = sellerSeatPid;
  }
}
