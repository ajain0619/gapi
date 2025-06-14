package com.nexage.app.services.impl;

import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.HbPartnerCompany;
import com.nexage.admin.core.repository.SiteRepository;
import com.nexage.app.dto.HbPartnerAssignmentDTO;
import com.nexage.app.dto.publisher.PublisherDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.services.HbPartnerCompanyService;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class HbPartnerCompanyServiceImpl implements HbPartnerCompanyService {

  private final SiteRepository siteRepository;

  /** {@inheritDoc} */
  @Override
  public void validateHbPartnerAssociations(Company company, PublisherDTO publisher) {

    Set<HbPartnerAssignmentDTO> hbPartnerAssignmentDTOS = publisher.getHbPartnerAttributes();
    List<Long> hbPartnerPidsDTO = new ArrayList<>();
    if (hbPartnerAssignmentDTOS != null && !hbPartnerAssignmentDTOS.isEmpty()) {
      hbPartnerPidsDTO =
          hbPartnerAssignmentDTOS.stream()
              .filter(h -> h != null)
              .map(
                  p -> {
                    if (StringUtils.isBlank(p.getExternalId())) {
                      throw new GenevaValidationException(
                          ServerErrorCodes.SERVER_HB_PARTNER_FIELDS_MISSING);
                    }
                    return p.getHbPartnerPid();
                  })
              .collect(Collectors.toList());
    }
    Set<HbPartnerCompany> hbPartnerCompanies = company.getHbPartnerCompany();

    List<Long> hbPartnerPidsCompany =
        hbPartnerCompanies.stream()
            .filter(h -> h != null && h.getHbPartner() != null)
            .map(p -> p.getHbPartner().getPid())
            .collect(Collectors.toList());

    if (!hbPartnerPidsCompany.isEmpty())
      if (!hbPartnerPidsDTO.containsAll(hbPartnerPidsCompany)) {
        hbPartnerPidsCompany.removeAll(hbPartnerPidsDTO);
        if (siteRepository.countSiteAssociationsByCompanyPidAndHbPartnerPids(
                company.getPid(), hbPartnerPidsCompany)
            > 0)
          throw new GenevaValidationException(
              ServerErrorCodes.SERVER_HB_PARTNER_COMPANY_ASSOCIATION_DELETE_INVALID);
      }
  }
}
