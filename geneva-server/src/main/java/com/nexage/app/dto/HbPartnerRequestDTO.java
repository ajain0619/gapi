package com.nexage.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Pageable;

@AllArgsConstructor
@Getter
public final class HbPartnerRequestDTO {
  private final Pageable pageable;
  private final Long sellerId;
  private final Long siteId;
  private final boolean detail;

  public static HbPartnerRequestDTO of(
      Pageable pageable, Long sellerId, Long siteId, boolean detail) {
    return new HbPartnerRequestDTO(pageable, sellerId, siteId, detail);
  }
}
