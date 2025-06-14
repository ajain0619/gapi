package com.nexage.app.util.assemblers.buyer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.model.BuyerGroup;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.repository.CompanyRepository;
import com.nexage.app.dto.buyer.BuyerGroupDTO;
import java.util.Random;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BuyerGroupAssemblerTest {

  @Mock private CompanyRepository companyRepository;
  @InjectMocks BuyerGroupAssembler assembler;

  @Test
  void shouldTransientEntity() {
    final long pid = new Random().nextLong();
    final long companyPid = new Random().nextLong();
    final String name = UUID.randomUUID().toString();
    final Company company = mock(Company.class);
    final boolean isBillable = true;
    when(company.getPid()).thenReturn(companyPid);
    BuyerGroupDTO source =
        BuyerGroupDTO.builder()
            .pid(pid)
            .name(name)
            .billable(isBillable)
            .companyPid(companyPid)
            .build();

    when(companyRepository.getOne(companyPid)).thenReturn(company);

    BuyerGroup result = assembler.transientEntity(companyPid, source);
    assertNotNull(result);
    assertEquals(result.getName(), source.getName());
    assertEquals(result.getCompany().getPid(), source.getCompanyPid());
    assertEquals(result.isBillable(), source.getBillable());
  }

  @Test
  void shouldMake() {
    final long pid = new Random().nextLong();
    final long companyPid = new Random().nextLong();
    final String name = UUID.randomUUID().toString();
    final Company company = mock(Company.class);
    when(company.getPid()).thenReturn(companyPid);
    BuyerGroup source = new BuyerGroup();
    source.setPid(pid);
    source.setName(name);
    source.setCompany(company);

    BuyerGroupDTO result = assembler.make(source);
    assertNotNull(result);
    assertEquals(result.getPid(), source.getPid());
    assertEquals(result.getName(), source.getName());
    assertEquals(result.getCompanyPid(), source.getCompany().getPid());
  }
}
