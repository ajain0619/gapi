package com.nexage.app.mapper;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.nexage.admin.core.enums.AssociationType;
import com.nexage.admin.core.model.HbPartner;
import com.nexage.admin.core.model.HbPartnerPosition;
import com.nexage.admin.core.model.Position;
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
public interface HbPartnerPositionMapper {

  HbPartnerPositionMapper MAPPER = Mappers.getMapper(HbPartnerPositionMapper.class);

  /**
   * This custom method used to map from hbPartenrAssignmentDTO to HbPartnerPosition
   *
   * @param source {@link HbPartnerAssignmentDTO}, company {@link Position}, hbpartnerRepository
   *     {@link HbPartnerRepository}
   * @return set of instances of type {@link HbPartnerPosition}
   */
  default Set<HbPartnerPosition> map(
      final Set<HbPartnerAssignmentDTO> source,
      final Position position,
      HbPartnerRepository hbPartnerRepository) {

    Map<Long, HbPartnerPosition> partnersMap = Maps.newHashMap();
    Set<HbPartnerPosition> hbPartnerPositionDb = position.getHbPartnerPosition();

    if (CollectionUtils.isNotEmpty(hbPartnerPositionDb)) {
      partnersMap =
          hbPartnerPositionDb.stream()
              .filter(p -> p.getHbPartner() != null)
              .collect(Collectors.toMap(h -> h.getHbPartner().getPid(), Function.identity()));
    }

    Set<HbPartnerPosition> hbPartnerPositions = Sets.newHashSet();

    for (HbPartnerAssignmentDTO hbPartnerAssignmentDTO : source) {
      Optional<HbPartner> hbPartner =
          hbPartnerRepository.findById(hbPartnerAssignmentDTO.getHbPartnerPid());
      if (hbPartner.isPresent()) {
        HbPartnerPosition hbPartnerPos =
            partnersMap.getOrDefault(hbPartner.get().getPid(), new HbPartnerPosition());
        hbPartnerPos.setHbPartner(hbPartner.get());
        hbPartnerPos.setExternalPositionId(hbPartnerAssignmentDTO.getExternalId());
        hbPartnerPos.setPosition(position);
        hbPartnerPos.setType(
            hbPartnerAssignmentDTO.getType() == null
                ? AssociationType.NON_DEFAULT.getValue()
                : hbPartnerAssignmentDTO.getType().getValue());
        hbPartnerPositions.add(hbPartnerPos);
      } else {
        throw new GenevaValidationException(ServerErrorCodes.SERVER_HB_PARTNER_NOT_FOUND);
      }
    }

    return hbPartnerPositions;
  }
}
