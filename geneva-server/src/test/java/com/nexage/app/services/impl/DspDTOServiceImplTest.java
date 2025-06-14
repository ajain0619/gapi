package com.nexage.app.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.repository.CompanyRepository;
import com.nexage.app.dto.dsp.DspDTO;
import com.nexage.app.dto.dsp.DspSummaryDTO;
import com.nexage.app.mapper.DspDTOMapper;
import com.nexage.app.mapper.DspSummaryDTOMapper;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class DspDTOServiceImplTest {

  @Mock private CompanyRepository companyRepository;

  @InjectMocks private DspDTOServiceImpl dspDTOService;

  @Test
  void complainsFindAllInvalidQueryRequest() {
    Set<String> qf = Collections.singleton("whatever");
    PageRequest pageRequest = PageRequest.of(0, 10);
    assertThrows(
        GenevaValidationException.class, () -> dspDTOService.findAll(qf, null, pageRequest, true));
  }

  @Test
  void returnBuyersWithPaginatedResponse() {

    final String id = UUID.randomUUID().toString();
    final Long pid = new Random().nextLong();

    Company company = new Company();
    company.setId(id);
    company.setPid(pid);

    DspDTO dsp = DspDTOMapper.MAPPER.map(company);

    Set<String> qf = Collections.emptySet();
    PageRequest pageRequest = PageRequest.of(0, 10);
    List<DspDTO> buyers = Collections.singletonList(dsp);
    Page<Company> companies = new PageImpl<>(Collections.singletonList(company));

    when(companyRepository.findAll(any(Specification.class), eq(pageRequest)))
        .thenReturn(companies);

    Page<DspDTO> result = dspDTOService.findAll(qf, null, pageRequest, true);
    assertNotNull(result);
    assertEquals(result.getContent(), buyers);
  }

  @Test
  void complainsFindAllSummaryInvalidQueryRequest() {
    Set<String> qf = Collections.singleton("whatever");
    PageRequest pageRequest = PageRequest.of(0, 10);
    assertThrows(
        GenevaValidationException.class,
        () -> dspDTOService.findAllSummary(qf, null, pageRequest, true));
  }

  @Test
  void returnBuyerSummaryWithPaginatedResponse() {

    final String id = UUID.randomUUID().toString();
    final Long pid = new Random().nextLong();

    Company company = new Company();
    company.setId(id);
    company.setPid(pid);

    Page<Company> companies = new PageImpl<>(Collections.singletonList(company));
    DspSummaryDTO dspSummary = DspSummaryDTOMapper.MAPPER.map(company);

    Set<String> qf = Collections.emptySet();
    PageRequest pageRequest = PageRequest.of(0, 10);
    List<DspSummaryDTO> dspList = Collections.singletonList(dspSummary);

    when(companyRepository.findAll(any(Specification.class), eq(pageRequest)))
        .thenReturn(companies);

    Page<DspSummaryDTO> result = dspDTOService.findAllSummary(qf, null, pageRequest, true);
    assertNotNull(result);
    assertEquals(result.getContent(), dspList);
  }
}
