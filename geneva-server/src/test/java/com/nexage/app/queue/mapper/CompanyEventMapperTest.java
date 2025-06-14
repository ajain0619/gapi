package com.nexage.app.queue.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.model.Company;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CompanyEventMapperTest {

  @Test
  void testMapFromPositionToEnrichPlacementCommandMessage() {
    var company = mock(Company.class);
    when(company.getPid()).thenReturn(34L);
    when(company.getName()).thenReturn("name");
    var companyEventMessage = CompanyEventMapper.MAPPER.map(company);

    assertEquals(companyEventMessage.getId(), company.getPid().toString());
    assertEquals(companyEventMessage.getName(), company.getName());
    assertEquals("CREATE", companyEventMessage.getStatus());
  }
}
