package com.nexage.app.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.model.DealBidderConfigView;
import com.nexage.admin.core.repository.DealBidderRepository;
import com.nexage.app.dto.deals.DealBidderDTO;
import com.nexage.app.mapper.DealBidderDTOMapper;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.Collections;
import java.util.HashSet;
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
class DealBidderDTOServiceImplTest {

  @Mock DealBidderRepository dealBidderRepository;

  @InjectMocks private DealBidderDTOServiceImpl bidderDTOService;

  @Test
  void returnBiddersPaginatedResponse() {
    final String name = UUID.randomUUID().toString();
    final Long companyPid = new Random().nextLong();
    final Long bidderPid = new Random().nextLong();

    DealBidderConfigView dealBidderConfigView = new DealBidderConfigView();
    dealBidderConfigView.setPid(bidderPid);
    dealBidderConfigView.setCompanyPid(companyPid);
    dealBidderConfigView.setName(name);

    DealBidderDTO dealBidderDTO = DealBidderDTOMapper.MAPPER.map(dealBidderConfigView);
    Set<String> qf = Collections.emptySet();
    PageRequest pageRequest = PageRequest.of(0, 10);
    List<DealBidderDTO> bidders = Collections.singletonList(dealBidderDTO);
    Page<DealBidderConfigView> dealBidderConfigs =
        new PageImpl<>(Collections.singletonList(dealBidderConfigView));

    when(dealBidderRepository.findAll(pageRequest)).thenReturn(dealBidderConfigs);

    Page<DealBidderDTO> result = bidderDTOService.findAll(qf, null, pageRequest);
    assertNotNull(result);
    assertEquals(result.getContent(), bidders);
  }

  @Test
  void returnBiddersPaginatedResponseWithNameSearch() {
    final String name = UUID.randomUUID().toString();
    final Long companyPid = new Random().nextLong();
    final Long bidderPid = new Random().nextLong();

    DealBidderConfigView dealBidderConfigView = new DealBidderConfigView();
    dealBidderConfigView.setPid(bidderPid);
    dealBidderConfigView.setCompanyPid(companyPid);
    dealBidderConfigView.setName(name);

    DealBidderDTO dealBidderDTO = DealBidderDTOMapper.MAPPER.map(dealBidderConfigView);
    Set<String> qf = new HashSet<>();
    qf.add("name");
    PageRequest pageRequest = PageRequest.of(0, 10);
    List<DealBidderDTO> bidders = Collections.singletonList(dealBidderDTO);
    Page<DealBidderConfigView> dealBidderConfigs =
        new PageImpl<>(Collections.singletonList(dealBidderConfigView));

    when(dealBidderRepository.findAll(any(Specification.class), eq(pageRequest)))
        .thenReturn(dealBidderConfigs);

    Page<DealBidderDTO> result = bidderDTOService.findAll(qf, name, pageRequest);
    assertNotNull(result);
    assertEquals(result.getContent(), bidders);
  }

  @Test
  void complainsFindAllInvalidQueryRequest() {
    Set<String> qf = Collections.singleton("whatever");
    PageRequest pageRequest = PageRequest.of(0, 10);
    assertThrows(
        GenevaValidationException.class, () -> bidderDTOService.findAll(qf, null, pageRequest));
  }
}
