package com.nexage.app.util.assemblers.sellingrule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

import com.nexage.admin.core.model.RuleDeployedCompany;
import com.nexage.admin.core.model.RuleDeployedSite;
import com.nexage.admin.core.repository.RuleDeployedSiteRepository;
import com.nexage.app.dto.sellingrule.PublisherAssignmentDTO;
import com.nexage.app.dto.sellingrule.SiteAssignmentDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SiteRuleAssignmentAssemblerTest {

  public static final String PUBLISHER_NAME = "PUBLISHER_NAME";
  public static final String SITE_ASSIGNMENT_NAME = "SITE_ASSIGNMENT_NAME";
  public static final long SITE_PID = 2L;
  public static final long SITE_COMPANY_PID = 2L;
  public static final long COMPANY_PID = 3L;
  public static final long PUBLISHER_PID = 4L;
  public static final long SITE_ASSIGNMENT_PID = 4L;

  public static final String COMPANY_NAME = "COMPANY_NAME";
  public static final String SITE_NAME = "SITE_NAME";
  @Mock RuleDeployedSiteRepository ruleDeployedSiteRepository;
  @InjectMocks SiteRuleAssignmentAssembler siteRuleAssignmentAssembler;

  @Test
  void shouldMapDtoToEntity() {
    // given
    RuleDeployedCompany ruleDeployedCompany = new RuleDeployedCompany();
    ruleDeployedCompany.setPid(COMPANY_PID);
    ruleDeployedCompany.setName(COMPANY_NAME);

    RuleDeployedSite ruleDeployedSite = new RuleDeployedSite();
    ruleDeployedSite.setPid(SITE_PID);
    ruleDeployedSite.setName(SITE_NAME);
    ruleDeployedSite.setCompanyPid(SITE_COMPANY_PID);
    ruleDeployedSite.setCompany(ruleDeployedCompany);

    PublisherAssignmentDTO publisherAssignmentDTO =
        new PublisherAssignmentDTO(PUBLISHER_PID, PUBLISHER_NAME);
    SiteAssignmentDTO siteAssignmentDto =
        new SiteAssignmentDTO(SITE_ASSIGNMENT_PID, SITE_ASSIGNMENT_NAME, publisherAssignmentDTO);

    given(ruleDeployedSiteRepository.getOne(SITE_ASSIGNMENT_PID)).willReturn(ruleDeployedSite);

    // when
    RuleDeployedSite result = siteRuleAssignmentAssembler.apply(siteAssignmentDto);

    // then
    assertEquals(SITE_PID, result.getPid());
    assertEquals(SITE_NAME, result.getName());
    assertEquals(SITE_COMPANY_PID, result.getCompanyPid());
    assertEquals(COMPANY_PID, result.getCompany().getPid());
    assertEquals(COMPANY_NAME, result.getCompany().getName());
  }
}
