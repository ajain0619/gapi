package com.nexage.app.mapper;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.nexage.admin.core.enums.AssociationType;
import com.nexage.admin.core.model.HbPartner;
import com.nexage.admin.core.model.HbPartnerSite;
import com.nexage.admin.core.model.Site;
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
public interface HbPartnerSiteMapper {

  HbPartnerSiteMapper MAPPER = Mappers.getMapper(HbPartnerSiteMapper.class);

  /**
   * This custom method used to map from hbPartenrAssignmentDTO to HbPartnerPosition
   *
   * @param source {@link HbPartnerAssignmentDTO}, company {@link Site}, hbpartnerRepository {@link
   *     HbPartnerRepository}
   * @return set of instances of type {@link HbPartnerSite}
   */
  default Set<HbPartnerSite> map(
      final Set<HbPartnerAssignmentDTO> source,
      final Site siteDTO,
      HbPartnerRepository hbPartnerRepository) {

    Map<Long, HbPartnerSite> partnersMap = Maps.newHashMap();
    Set<HbPartnerSite> hbPartnerSiteDb = siteDTO.getHbPartnerSite();

    if (CollectionUtils.isNotEmpty(hbPartnerSiteDb)) {
      partnersMap =
          hbPartnerSiteDb.stream()
              .filter(p -> p.getHbPartner() != null)
              .collect(Collectors.toMap(h -> h.getHbPartner().getPid(), Function.identity()));
    }

    Set<HbPartnerSite> hbPartnerSites = Sets.newHashSet();

    for (HbPartnerAssignmentDTO hbPartnerAssignmentDTO : source) {
      Optional<HbPartner> hbPartner =
          hbPartnerRepository.findById(hbPartnerAssignmentDTO.getHbPartnerPid());
      if (hbPartner.isPresent()) {
        HbPartnerSite hbPartnerSite =
            partnersMap.getOrDefault(hbPartner.get().getPid(), new HbPartnerSite());
        hbPartnerSite.setHbPartner(hbPartner.get());
        hbPartnerSite.setExternalSiteId(hbPartnerAssignmentDTO.getExternalId());
        hbPartnerSite.setSite(siteDTO);
        hbPartnerSite.setType(
            hbPartnerAssignmentDTO.getType() == null
                ? AssociationType.NON_DEFAULT.getValue()
                : hbPartnerAssignmentDTO.getType().getValue());
        hbPartnerSites.add(hbPartnerSite);
      } else {
        throw new GenevaValidationException(ServerErrorCodes.SERVER_HB_PARTNER_NOT_FOUND);
      }
    }

    return hbPartnerSites;
  }
}
