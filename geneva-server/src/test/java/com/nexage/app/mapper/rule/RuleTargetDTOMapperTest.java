package com.nexage.app.mapper.rule;

import static com.nexage.app.web.support.TestObjectsFactory.createRuleTarget;
import static com.nexage.app.web.support.TestObjectsFactory.createRuleTargetDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.model.RuleTarget;
import com.nexage.admin.core.repository.BidderConfigRepository;
import com.nexage.app.dto.sellingrule.RuleTargetDTO;
import com.nexage.app.util.CustomObjectMapper;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RuleTargetDTOMapperTest {

  private RuleTargetDTOMapper mapper = RuleTargetDTOMapper.MAPPER;

  private CustomObjectMapper objectMapper = new CustomObjectMapper();

  private BidderConfigRepository bidderConfigRepository =
      Mockito.mock(BidderConfigRepository.class);

  private RuleTargetDataConverter ruleTargetDataConverter =
      new RuleTargetDataConverter(objectMapper, bidderConfigRepository);

  @BeforeEach
  public void setUp() throws Exception {
    when(bidderConfigRepository.findCompanyPidByPid(1000L)).thenReturn(20L);
    when(bidderConfigRepository.findCompanyPidByPid(900L)).thenReturn(21L);
  }

  @Test
  void entityDtoMappingTest() throws IOException {
    RuleTarget target = createRuleTarget();
    target.setData("[{\"bidder\":1000}, {\"buyerCompany\":20}, {\"bidder\":900}]");
    RuleTargetDTO mapped = mapper.map(target, ruleTargetDataConverter);

    assertEquals(target.getPid(), mapped.getPid());
    assertEquals(target.getVersion(), mapped.getVersion());
    assertEquals(target.getStatus(), mapped.getStatus());
    assertEquals(target.getMatchType(), mapped.getMatchType());
    assertEquals(target.getRuleTargetType(), mapped.getTargetType());
    assertEquals(
        objectMapper.readTree(
            "[{\"buyerCompany\":20,\"bidders\":[1000],\"bidder\":1000},{\"buyerCompany\":20}, {\"buyerCompany\":21,\"bidders\":[900],\"bidder\":900}]"),
        objectMapper.readTree(mapped.getData()));
  }

  @Test
  void dtoEntityMappingTest() {
    RuleTargetDTO dto = createRuleTargetDto();
    RuleTarget mapped = mapper.map(dto);

    assertNull(mapped.getRule());
    assertEquals(dto.getPid(), mapped.getPid());
    assertEquals(dto.getVersion(), mapped.getVersion());
    assertEquals(dto.getStatus(), mapped.getStatus());
    assertEquals(dto.getMatchType(), mapped.getMatchType());
    assertEquals(dto.getTargetType(), mapped.getRuleTargetType());
    assertEquals(dto.getData(), mapped.getData());
  }

  @Test
  void dtoEntityApplyTest() {
    RuleTargetDTO dto = createRuleTargetDto();
    RuleTarget target = createRuleTarget();
    mapper.apply(dto, target);

    assertNotEquals(dto.getPid(), target.getPid());
    assertNotEquals(dto.getVersion(), target.getVersion());
    assertEquals(dto.getStatus(), target.getStatus());
    assertEquals(dto.getMatchType(), target.getMatchType());
    assertEquals(dto.getTargetType(), target.getRuleTargetType());
    assertEquals(dto.getData(), target.getData());
  }

  private RuleTarget getRuleTarget() {
    return createRuleTarget();
  }
}
