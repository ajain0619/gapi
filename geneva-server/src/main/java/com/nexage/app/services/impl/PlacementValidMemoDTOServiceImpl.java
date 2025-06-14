package com.nexage.app.services.impl;

import com.nexage.app.dto.seller.PlacementValidMemoDTO;
import com.nexage.app.security.UserContext;
import com.nexage.app.services.PlacementValidMemoDTOService;
import com.nexage.app.util.placement.PlacementValidMemoGenerator;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
@PreAuthorize(
    "@loginUserContext.isOcAdminNexage() or @loginUserContext.isOcManagerNexage() or @loginUserContext.isOcUserNexage() or "
        + "@loginUserContext.isOcAdminSeller() or @loginUserContext.isOcManagerSeller() or @loginUserContext.isOcUserSeller()")
public class PlacementValidMemoDTOServiceImpl implements PlacementValidMemoDTOService {
  private final UserContext userContext;

  /** {@inheritDoc} */
  @Override
  @PreAuthorize("@loginUserContext.doSameOrNexageAffiliation(#sellerId) == true")
  public PlacementValidMemoDTO getValidPlacementMemo(Long siteId, Long sellerId, String memo) {
    if (Objects.isNull(siteId) || siteId < 0) {
      return PlacementValidMemoDTO.buildDefault();
    }
    return new PlacementValidMemoDTO(PlacementValidMemoGenerator.generate(siteId, memo), true);
  }
}
