package com.nexage.app.services.impl;

import com.nexage.admin.core.model.HbPartnerPosition;
import com.nexage.admin.core.model.HbPartnerSite;
import com.nexage.admin.core.model.Position;
import com.nexage.admin.core.model.Site;
import com.nexage.app.dto.HbPartnerAssignmentDTO;
import com.nexage.app.dto.publisher.PublisherSiteDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.services.HbPartnerSiteService;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class HbPartnerSiteServiceImpl implements HbPartnerSiteService {

  /** {@inheritDoc} */
  @Override
  public void validateHbPartnerAssociations(Site siteDTO, PublisherSiteDTO publisherSite) {
    Set<HbPartnerAssignmentDTO> hbPartnerAssignmentDTOS = publisherSite.getHbPartnerAttributes();
    List<Long> hbPartnerPidsPublisherSite = new ArrayList<>();
    if (hbPartnerAssignmentDTOS != null) {
      hbPartnerPidsPublisherSite =
          hbPartnerAssignmentDTOS.stream()
              .filter(h -> h != null)
              .map(p -> p.getHbPartnerPid())
              .collect(Collectors.toList());
    }

    Set<HbPartnerSite> hbPartnerSites = siteDTO.getHbPartnerSite();
    List<Long> hbPartnerPidsSite =
        hbPartnerSites.stream()
            .filter(h -> h != null && h.getHbPartner() != null)
            .map(p -> p.getHbPartner().getPid())
            .collect(Collectors.toList());

    if (!hbPartnerPidsSite.isEmpty()) {
      if (!hbPartnerPidsPublisherSite.containsAll(hbPartnerPidsSite)) {
        hbPartnerPidsSite.removeAll(hbPartnerPidsPublisherSite);
        for (Position position : siteDTO.getPositions()) {
          Set<HbPartnerPosition> hbPartnerPositions = position.getHbPartnerPosition();
          if (hbPartnerPositions != null && !hbPartnerPositions.isEmpty()) {
            hbPartnerPositions.forEach(
                hbPartnerPosition -> {
                  if (hbPartnerPidsSite.contains(hbPartnerPosition.getHbPartner().getPid())) {
                    throw new GenevaValidationException(
                        ServerErrorCodes.SERVER_HB_PARTNER_SITE_ASSOCIATION_DELETE_INVALID);
                  }
                });
          }
        }
      }
    }
  }
}
