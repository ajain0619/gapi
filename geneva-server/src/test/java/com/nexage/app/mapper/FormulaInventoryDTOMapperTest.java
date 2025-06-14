package com.nexage.app.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.nexage.admin.core.enums.PlacementCategory;
import com.nexage.admin.core.enums.site.Type;
import com.nexage.admin.core.model.RuleFormulaCompanyView;
import com.nexage.admin.core.model.RuleFormulaPositionView;
import com.nexage.admin.core.model.RuleFormulaSiteView;
import com.nexage.app.dto.sellingrule.FormulaInventoryDTO;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.jupiter.api.Test;

class FormulaInventoryDTOMapperTest {
  @Test
  void shouldMapRuleFormulaPositionViewToFormulaInventoryDTO() {
    Long companyPid = RandomUtils.nextLong();
    String companyName = "testCompany";

    Long sitePid = RandomUtils.nextLong();
    String siteName = "testSite";
    Type type = Type.APPLICATION;

    Long placementPid = RandomUtils.nextLong();
    String placementName = "testPlacement";
    String placementMemo = "testMemo";
    PlacementCategory category = PlacementCategory.BANNER;
    Integer height = 480;
    Integer width = 640;

    RuleFormulaPositionView ruleFormulaPositionView = new RuleFormulaPositionView();
    RuleFormulaCompanyView ruleFormulaCompanyView = new RuleFormulaCompanyView();
    ruleFormulaCompanyView.setPid(companyPid);
    ruleFormulaCompanyView.setName(companyName);
    RuleFormulaSiteView ruleFormulaSiteView = new RuleFormulaSiteView();
    ruleFormulaSiteView.setPid(sitePid);
    ruleFormulaSiteView.setName(siteName);
    ruleFormulaSiteView.setType(type);
    ruleFormulaSiteView.setCompany(ruleFormulaCompanyView);

    ruleFormulaPositionView.setSite(ruleFormulaSiteView);
    ruleFormulaPositionView.setPid(placementPid);
    ruleFormulaPositionView.setName(placementName);
    ruleFormulaPositionView.setMemo(placementMemo);
    ruleFormulaPositionView.setType(category);
    ruleFormulaPositionView.setHeight(height);
    ruleFormulaPositionView.setWidth(width);

    FormulaInventoryDTO formulaInventoryDTO =
        FormulaInventoryDTOMapper.MAPPER.map(ruleFormulaPositionView);
    assertNotNull(formulaInventoryDTO);
    assertEquals(companyPid, formulaInventoryDTO.getCompanyId());
    assertEquals(companyName, formulaInventoryDTO.getCompanyName());
    assertEquals(sitePid, formulaInventoryDTO.getSitePid());
    assertEquals(siteName, formulaInventoryDTO.getSiteName());
    assertEquals(type.toString(), formulaInventoryDTO.getSiteType().toString());
    assertEquals(placementPid, formulaInventoryDTO.getPlacementId());
    assertEquals(placementName, formulaInventoryDTO.getPlacementName());
    assertEquals(placementMemo, formulaInventoryDTO.getPlacementMemo());
    assertEquals(category, formulaInventoryDTO.getPlacementType());
    assertEquals(height, formulaInventoryDTO.getHeight());
    assertEquals(width, formulaInventoryDTO.getWidth());
  }
}
