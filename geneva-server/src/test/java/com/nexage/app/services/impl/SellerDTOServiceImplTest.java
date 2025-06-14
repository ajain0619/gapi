package com.nexage.app.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.SellerEligibleBidders;
import com.nexage.admin.core.repository.CompanyRepository;
import com.nexage.app.dto.publisher.PublisherEligibleBiddersDTO;
import com.nexage.app.dto.seller.SellerDTO;
import com.nexage.app.mapper.PublisherEligibleBiddersDTOMapper;
import com.nexage.app.mapper.SellerDTOMapper;
import com.nexage.app.services.CompanyService;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
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
class SellerDTOServiceImplTest {

  @Mock private CompanyRepository companyRepository;
  @Mock private CompanyService companyService;

  @InjectMocks private SellerDTOServiceImpl sellerDTOService;

  private static final String ID = UUID.randomUUID().toString();
  private static final Long PID = new Random().nextLong();

  private static Company company;
  private static SellerDTO seller;

  @BeforeAll
  static void setUp() {
    company = new Company();
    company.setId(ID);
    company.setPid(PID);
    seller = SellerDTOMapper.MAPPER.map(company);
  }

  @Test
  void shouldThrowWhenGettingAllSellersUsingInvalidQueryField() {
    Set<String> qf = Collections.singleton("whatever");
    PageRequest pageRequest = PageRequest.of(0, 10);
    assertThrows(
        GenevaValidationException.class,
        () -> sellerDTOService.findAll(qf, null, false, pageRequest));
  }

  @Test
  void shouldReturnSellersWithPaginatedResponse() {

    Set<String> qf = Collections.emptySet();
    PageRequest pageRequest = PageRequest.of(0, 10);
    List<SellerDTO> sellers = Collections.singletonList(seller);
    Page<Company> companies = new PageImpl<>(Collections.singletonList(company));

    when(companyRepository.findAll(any(Specification.class), eq(pageRequest)))
        .thenReturn(companies);

    Page<SellerDTO> result = sellerDTOService.findAll(qf, null, false, pageRequest);
    assertNotNull(result);
    assertEquals(result.getContent(), sellers);
  }

  @Test
  void shouldReturnRtbSellersWithPaginatedResponse() {

    Set<String> qf = Collections.emptySet();
    PageRequest pageRequest = PageRequest.of(0, 10);
    List<SellerDTO> sellers = Collections.singletonList(seller);
    Page<Company> companies = new PageImpl<>(Collections.singletonList(company));

    when(companyRepository.findAll(any(Specification.class), eq(pageRequest)))
        .thenReturn(companies);

    Page<SellerDTO> result = sellerDTOService.findAll(qf, null, true, pageRequest);
    assertNotNull(result);
    assertEquals(result.getContent(), sellers);
  }

  @Test
  void shouldReturnSingleSellerAsSellerDTOObject() {

    when(companyService.getCompany(PID)).thenReturn(company);
    SellerDTO result = sellerDTOService.findOne(PID);
    Set<PublisherEligibleBiddersDTO> eligibleBidders = new HashSet<>();
    company
        .getEligibleBidders()
        .forEach(
            eligibleBidder ->
                eligibleBidders.add(PublisherEligibleBiddersDTOMapper.MAPPER.map(eligibleBidder)));
    seller.setEligibleBidderGroups(eligibleBidders);
    assertNotNull(result);
    assertEquals(result, seller);
  }

  @Test
  void shouldReturnSingleSellerAsSellerDTOObjectWithEligibleBidders() {
    Company new_company = new Company();
    new_company.setId(UUID.randomUUID().toString());
    new_company.setPid(new Random().nextLong());
    SellerDTO new_seller = SellerDTOMapper.MAPPER.map(new_company);

    SellerEligibleBidders bidder = new SellerEligibleBidders();
    bidder.setPid(5L);
    new_company.addEligibleBidders(bidder);

    when(companyService.getCompany(new_company.getPid())).thenReturn(new_company);

    SellerDTO result = sellerDTOService.findOne(new_company.getPid());
    Set<PublisherEligibleBiddersDTO> eligibleBidders = new HashSet<>();
    new_company
        .getEligibleBidders()
        .forEach(
            eligibleBidder ->
                eligibleBidders.add(PublisherEligibleBiddersDTOMapper.MAPPER.map(eligibleBidder)));
    new_seller.setEligibleBidderGroups(eligibleBidders);
    assertNotNull(result);
    assertEquals(result, new_seller);
  }
}
