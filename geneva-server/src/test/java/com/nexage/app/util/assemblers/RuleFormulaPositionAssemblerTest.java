package com.nexage.app.util.assemblers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nexage.admin.core.enums.PlacementCategory;
import com.nexage.admin.core.enums.site.Type;
import com.nexage.admin.core.model.RuleFormulaCompanyView;
import com.nexage.admin.core.model.RuleFormulaPositionView;
import com.nexage.admin.core.model.RuleFormulaSiteView;
import com.nexage.app.dto.sellingrule.RuleFormulaPositionDTO;
import com.nexage.app.util.assemblers.sellingrule.RuleFormulaPositionAssembler;
import org.junit.jupiter.api.Test;

class RuleFormulaPositionAssemblerTest {
  private RuleFormulaPositionAssembler assembler = new RuleFormulaPositionAssembler();

  @Test
  void testRuleResultAssembler() {
    Long companyPid = 1L;

    RuleFormulaCompanyView companyView = new RuleFormulaCompanyView();
    companyView.setName("Test company name");
    companyView.setPid(companyPid);

    RuleFormulaSiteView siteView = new RuleFormulaSiteView();
    siteView.setCompany(companyView);
    siteView.setPid(2L);
    siteView.setName("Test site name");
    siteView.setType(Type.APPLICATION);

    RuleFormulaPositionView positionView = new RuleFormulaPositionView();
    positionView.setSite(siteView);
    positionView.setType(PlacementCategory.BANNER);
    positionView.setPid(3L);
    positionView.setName("Test position name");
    positionView.setMemo("Test position memo");
    positionView.setHeight(300);
    positionView.setWidth(200);

    RuleFormulaPositionDTO result = assembler.make(positionView);
    assertEquals(result.getCompanyId(), companyView.getPid());
    assertEquals(result.getCompanyName(), companyView.getName());
    assertEquals(result.getPlacementId(), positionView.getPid());
    assertEquals(result.getPlacementMemo(), positionView.getMemo());
    assertEquals(result.getPlacementName(), positionView.getName());
    assertEquals(result.getWidth(), positionView.getWidth());
    assertEquals(result.getHeight(), positionView.getHeight());
    assertEquals(result.getPlacementType().toString(), positionView.getType().toString());
    assertEquals(result.getSiteName(), siteView.getName());
    assertEquals(result.getSitePid(), siteView.getPid());
    assertEquals(result.getSiteType().toString(), siteView.getType().toString());
  }
}
