package com.nexage.app.mapper.site;

import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.Site;
import com.nexage.admin.core.model.SiteView;
import com.nexage.app.dto.seller.SiteDTO;
import java.util.Optional;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SiteDTOMapper {
  SiteDTOMapper MAPPER = Mappers.getMapper(SiteDTOMapper.class);

  @Mapping(source = "company.name", target = "companyName")
  @Mapping(source = "company.globalAliasName", target = "companyGlobalAliasName")
  SiteDTO map(Site site);

  default SiteDTO map(SiteView source) {
    var target = new SiteDTO();
    target.setPid(source.getPid());
    target.setStatus(source.getStatus());
    target.setName(source.getName());
    target.setMetadataEnablement(null);
    target.setHbEnabled(null);
    target.setUrl(source.getUrl());
    target.setCompanyName(
        Optional.ofNullable(source.getCompany()).map(Company::getName).orElse(null));
    return target;
  }
}
