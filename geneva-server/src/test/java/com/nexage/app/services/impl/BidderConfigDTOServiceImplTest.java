package com.nexage.app.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.model.BidderConfig;
import com.nexage.admin.core.model.BidderConfig_;
import com.nexage.admin.core.repository.BidderConfigRepository;
import com.nexage.admin.core.repository.CompanyRepository;
import com.nexage.app.dto.BidderConfigDTO;
import com.nexage.app.dto.BidderConfigDTOView;
import com.nexage.app.mapper.BidderConfigDTOMapper;
import com.nexage.app.mapper.BidderConfigDTOViewMapper;
import com.nexage.app.services.BuyerService;
import com.nexage.app.web.support.TestObjectsFactory;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.apache.commons.lang3.RandomStringUtils;
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
class BidderConfigDTOServiceImplTest {

  @Mock private BidderConfigDTOMapper bidderConfigDTOMapper;
  @Mock private BuyerService buyerService;
  @InjectMocks private BidderConfigDTOServiceImpl bidderConfigDTOService;
  @Mock private BidderConfigRepository bidderConfigRepository;
  @Mock private CompanyRepository companyRepository;

  Set<String> VALID_QF = Set.of(BidderConfig_.NAME);

  @Test
  void createMapsInputDelegatesToBuyerServiceAndMapsReturn() {
    BidderConfigDTO inputDTO = new BidderConfigDTO();
    BidderConfig toPersist = new BidderConfig();
    BidderConfig persisted = new BidderConfig();
    BidderConfigDTO outputDTO = new BidderConfigDTO();
    when(bidderConfigDTOMapper.map(same(inputDTO))).thenReturn(toPersist);
    when(buyerService.createBidderConfig(eq(7L), same(toPersist))).thenReturn(persisted);
    when(bidderConfigDTOMapper.map(same(persisted))).thenReturn(outputDTO);

    assertSame(outputDTO, bidderConfigDTOService.create(7L, inputDTO));
  }

  @Test
  void createMapsInputDelegatesToBuyerServiceAndMapsReturn_NullPayload() {
    BidderConfigDTO inputDTO = null;
    BidderConfig toPersist = new BidderConfig();
    BidderConfig persisted = new BidderConfig();
    BidderConfigDTO outputDTO = new BidderConfigDTO();
    when(bidderConfigDTOMapper.map(inputDTO)).thenReturn(toPersist);
    when(buyerService.createBidderConfig(eq(7L), same(toPersist))).thenReturn(persisted);
    when(bidderConfigDTOMapper.map(same(persisted))).thenReturn(outputDTO);

    assertSame(outputDTO, bidderConfigDTOService.create(7L, inputDTO));
  }

  @Test
  void getDelegatesToBuyerServiceAndMapsReturn() {
    BidderConfig bidderConfig = new BidderConfig();
    BidderConfigDTO bidderConfigDTO = new BidderConfigDTO();
    when(buyerService.getBidderConfig(5L, 8L)).thenReturn(bidderConfig);
    when(bidderConfigDTOMapper.map(same(bidderConfig))).thenReturn(bidderConfigDTO);

    assertSame(bidderConfigDTO, bidderConfigDTOService.get(5L, 8L));
  }

  @Test
  void updateMapsInputDelegatesToBuyerServiceAndMapsReturn() {
    BidderConfigDTO inputDTO = new BidderConfigDTO();
    BidderConfig toPersist = new BidderConfig();
    BidderConfig persisted = new BidderConfig();
    BidderConfigDTO outputDTO = new BidderConfigDTO();
    when(bidderConfigDTOMapper.map(same(inputDTO))).thenReturn(toPersist);
    when(buyerService.updateBidderConfig(eq(4L), same(toPersist), eq(7L))).thenReturn(persisted);
    when(bidderConfigDTOMapper.map(same(persisted))).thenReturn(outputDTO);

    assertSame(outputDTO, bidderConfigDTOService.update(7L, 4L, inputDTO));
  }

  @Test
  void shouldReturnBidderConfigDTOSummaryWithNameSearchWithPaginatedResponse() {

    final String qt = "test";
    final Long companyPid = new Random().nextLong();

    BidderConfig bidderConfig = TestObjectsFactory.createBidderConfig();
    bidderConfig.setId(RandomStringUtils.randomAlphanumeric(8));
    bidderConfig.setCompanyPid(companyPid);
    bidderConfig.setVersion(1);
    Page<BidderConfig> bidderConfigPage = new PageImpl<>(List.of(bidderConfig));
    when(bidderConfigRepository.findAll(any(Specification.class), any(PageRequest.class)))
        .thenReturn(bidderConfigPage);

    when(companyRepository.existsById(any())).thenReturn(true);

    BidderConfigDTOView bidderConfigDTOView = BidderConfigDTOViewMapper.MAPPER.map(bidderConfig);

    Page<BidderConfigDTOView> result =
        bidderConfigDTOService.findAllBidderConfigs(
            companyPid, VALID_QF, qt, PageRequest.of(0, 10));
    BidderConfigDTOView resultDTO = result.getContent().get(0);
    assertNotNull(result);
    assertEquals(1, result.getTotalElements());
    assertEquals(1, result.getTotalPages());
    assertEquals(bidderConfigDTOView, resultDTO);
  }

  @Test
  void shouldThrowExceptionWhenInValidCompanyPid() {
    PageRequest page = PageRequest.of(0, 10);
    when(companyRepository.existsById(any())).thenReturn(false);

    assertThrows(
        GenevaValidationException.class,
        () -> bidderConfigDTOService.findAllBidderConfigs(1L, VALID_QF, "test", page));
  }
}
