package com.nexage.app.util.assemblers.sellingrule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

import com.nexage.admin.core.model.RuleDeployedCompany;
import com.nexage.admin.core.repository.RuleDeployedCompanyRepository;
import com.nexage.app.dto.sellingrule.PublisherAssignmentDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PublisherRuleAssignmentAssemblerTest {

  public static final String NAME = "NAME";
  public static final long PID = 1L;

  @Mock RuleDeployedCompanyRepository ruleDeployedCompanyRepository;
  @InjectMocks PublisherRuleAssignmentAssembler publisherRuleAssignmentAssembler;

  @Test
  void shouldMapDtoToEntity() {
    // given
    RuleDeployedCompany ruleDeployedCompany = new RuleDeployedCompany();
    ruleDeployedCompany.setPid(PID);
    ruleDeployedCompany.setName(NAME);

    given(ruleDeployedCompanyRepository.getOne(PID)).willReturn(ruleDeployedCompany);

    // when
    RuleDeployedCompany result =
        publisherRuleAssignmentAssembler.apply(new PublisherAssignmentDTO(PID, NAME));

    // then
    assertEquals(PID, result.getPid());
    assertEquals(NAME, result.getName());
  }
}
