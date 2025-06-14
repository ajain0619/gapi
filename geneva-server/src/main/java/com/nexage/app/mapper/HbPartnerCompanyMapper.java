package com.nexage.app.mapper;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.HbPartner;
import com.nexage.admin.core.model.HbPartnerCompany;
import com.nexage.admin.core.repository.HbPartnerRepository;
import com.nexage.app.dto.HbPartnerAssignmentDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface HbPartnerCompanyMapper {

  HbPartnerCompanyMapper MAPPER = Mappers.getMapper(HbPartnerCompanyMapper.class);

  /**
   * This custom method used to map from hbPartenrAssignmentDTO to HbPartnerCompany
   *
   * @param source {@link HbPartnerAssignmentDTO}, company {@link Company}, hbpartnerRepository
   *     {@link HbPartnerRepository}
   * @return set of instances of type {@link HbPartnerCompany}
   */
  default Set<HbPartnerCompany> map(
      final Set<HbPartnerAssignmentDTO> source,
      final Company company,
      HbPartnerRepository hbPartnerRepository) {
    Map<Long, HbPartnerCompany> partnersMap = Maps.newHashMap();
    Set<HbPartnerCompany> hbPartnerCompanyDB = company.getHbPartnerCompany();

    if (CollectionUtils.isNotEmpty(hbPartnerCompanyDB)) {
      partnersMap =
          hbPartnerCompanyDB.stream()
              .filter(p -> p.getHbPartner() != null)
              .collect(Collectors.toMap(h -> h.getHbPartner().getPid(), Function.identity()));
    }

    Set<HbPartnerCompany> hbPartnerCompanies = Sets.newHashSet();

    for (HbPartnerAssignmentDTO hbPartnerAssignmentDTO : source) {
      Optional<HbPartner> hbPartner =
          hbPartnerRepository.findById(hbPartnerAssignmentDTO.getHbPartnerPid());
      if (hbPartner.isPresent()) {
        HbPartnerCompany hbPartnerCompany =
            partnersMap.getOrDefault(hbPartner.get().getPid(), new HbPartnerCompany());
        hbPartnerCompany.setHbPartner(hbPartner.get());
        hbPartnerCompany.setExternalPubId(hbPartnerAssignmentDTO.getExternalId());
        hbPartnerCompany.setCompany(company);
        hbPartnerCompanies.add(hbPartnerCompany);
      } else {
        throw new GenevaValidationException(ServerErrorCodes.SERVER_HB_PARTNER_NOT_FOUND);
      }
    }
    return hbPartnerCompanies;
  }
}
