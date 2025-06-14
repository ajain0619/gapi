package com.nexage.app.dto.sellingrule;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nexage.admin.core.enums.PlacementCategory;
import com.nexage.app.dto.publisher.PublisherSiteDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FormulaInventoryDTO {
  private Long companyId;
  private String companyName;
  private Long sitePid;
  private String siteName;
  private PublisherSiteDTO.SiteType siteType;
  private Long placementId;
  private String placementName;
  private String placementMemo;
  private PlacementCategory placementType;
  private Integer height;
  private Integer width;
}
