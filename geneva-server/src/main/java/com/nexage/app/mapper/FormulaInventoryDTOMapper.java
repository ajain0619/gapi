package com.nexage.app.mapper;

import com.nexage.admin.core.enums.site.Type;
import com.nexage.admin.core.model.RuleFormulaPositionView;
import com.nexage.app.dto.publisher.PublisherSiteDTO.SiteType;
import com.nexage.app.dto.sellingrule.FormulaInventoryDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FormulaInventoryDTOMapper {
  FormulaInventoryDTOMapper MAPPER = Mappers.getMapper(FormulaInventoryDTOMapper.class);

  @Mappings({
    @Mapping(source = "site.company.pid", target = "companyId"),
    @Mapping(source = "site.company.name", target = "companyName"),
    @Mapping(source = "site.pid", target = "sitePid"),
    @Mapping(source = "site.name", target = "siteName"),
    @Mapping(source = "site.type", target = "siteType", qualifiedByName = "convertSiteType"),
    @Mapping(source = "pid", target = "placementId"),
    @Mapping(source = "name", target = "placementName"),
    @Mapping(source = "memo", target = "placementMemo"),
    @Mapping(source = "type", target = "placementType"),
    @Mapping(source = "height", target = "height"),
    @Mapping(source = "width", target = "width"),
  })
  FormulaInventoryDTO map(RuleFormulaPositionView source);

  @Named("convertSiteType")
  default SiteType convertSiteType(Type type) {
    if (type == null) {
      return null;
    }
    return SiteType.valueOf(type.toString());
  }
}
