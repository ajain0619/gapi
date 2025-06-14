package com.nexage.app.mapper;

import com.nexage.admin.core.enums.AssociationType;
import com.nexage.admin.core.model.HbPartnerCompany;
import com.nexage.admin.core.model.HbPartnerPosition;
import com.nexage.admin.core.model.HbPartnerSite;
import com.nexage.app.dto.HbPartnerAssignmentDTO;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = HbPartnerAssignmentDTOMapper.EnumMappings.class)
public interface HbPartnerAssignmentDTOMapper {

  HbPartnerAssignmentDTOMapper MAPPER = Mappers.getMapper(HbPartnerAssignmentDTOMapper.class);

  @Mapping(source = "hbPartner.pid", target = "hbPartnerPid")
  @Mapping(source = "externalPubId", target = "externalId")
  HbPartnerAssignmentDTO mapHbPartnerCompany(HbPartnerCompany source);

  Set<HbPartnerAssignmentDTO> mapHbPartnerCompany(Set<HbPartnerCompany> source);

  @Mapping(source = "hbPartner.pid", target = "hbPartnerPid")
  @Mapping(source = "externalSiteId", target = "externalId")
  @Mapping(source = "type", target = "type")
  HbPartnerAssignmentDTO mapHbPartnerSite(HbPartnerSite source);

  Set<HbPartnerAssignmentDTO> mapHbPartnerSite(Set<HbPartnerSite> source);

  @Mapping(source = "hbPartner.pid", target = "hbPartnerPid")
  @Mapping(source = "externalPositionId", target = "externalId")
  HbPartnerAssignmentDTO mapHbPartnerPosition(HbPartnerPosition source);

  Set<HbPartnerAssignmentDTO> mapHbPartnerPosition(Set<HbPartnerPosition> source);

  class EnumMappings {
    protected AssociationType mapHbPartnerTypeEnum(Integer intType) {
      return AssociationType.getFromValue(intType);
    }
  }
}
