package com.nexage.app.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.model.BuyerGroup;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.repository.BuyerGroupRepository;
import com.nexage.admin.core.repository.CompanyRepository;
import com.nexage.app.dto.buyer.BuyerGroupDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.mapper.BuyerGroupDTOMapper;
import com.nexage.app.web.support.TestObjectsFactory;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.model.inventory.CompanyType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
class BuyerGroupDTOServiceImplTest {

  @Mock private BuyerGroupRepository buyerGroupRepository;

  @Mock private CompanyRepository companyRepository;

  @InjectMocks private BuyerGroupDTOServiceImpl buyerGroupDTOService;

  @Test
  void complainsFindAllInvalidQueryRequest() {
    final long companyPid = new Random().nextLong();
    Set<String> qf = Collections.singleton("whatever");
    PageRequest pageRequest = PageRequest.of(0, 10);
    assertThrows(
        GenevaValidationException.class,
        () -> buyerGroupDTOService.findAll(companyPid, qf, null, pageRequest));
  }

  @Test
  void returnBuyerGroupsSingleElementWithPaginatedResponse() {

    final String name = UUID.randomUUID().toString();
    final Long pid = new Random().nextLong();
    final Long companyPid = new Random().nextLong();
    final Company company = mock(Company.class);
    when(company.getPid()).thenReturn(companyPid);

    BuyerGroup buyerGroup = new BuyerGroup();
    buyerGroup.setName(name);
    buyerGroup.setPid(pid);
    buyerGroup.setCompany(company);

    BuyerGroupDTO buyerGroupDTO = BuyerGroupDTOMapper.MAPPER.map(buyerGroup);

    Set<String> qf = Collections.emptySet();
    PageRequest pageRequest = PageRequest.of(0, 10);
    List<BuyerGroupDTO> buyerGroupDTOs = Collections.singletonList(buyerGroupDTO);
    Page<BuyerGroup> buyerGroups = new PageImpl<>(Collections.singletonList(buyerGroup));

    when(buyerGroupRepository.findAll(any(Specification.class), eq(pageRequest)))
        .thenReturn(buyerGroups);

    Page<BuyerGroupDTO> result = buyerGroupDTOService.findAll(companyPid, qf, null, pageRequest);
    assertNotNull(result);
    assertEquals(result.getContent(), buyerGroupDTOs);
  }

  @Test
  void returnBuyerGroupsWithPaginatedResponse() {

    final String name1 = UUID.randomUUID().toString();
    final Long pid1 = new Random().nextLong();
    final String name2 = UUID.randomUUID().toString();
    final Long pid2 = new Random().nextLong();
    final Long companyPid = new Random().nextLong();
    final Company company = mock(Company.class);
    when(company.getPid()).thenReturn(companyPid);

    BuyerGroup buyerGroup1 = new BuyerGroup();
    buyerGroup1.setName(name1);
    buyerGroup1.setPid(pid1);
    buyerGroup1.setCompany(company);
    BuyerGroup buyerGroup2 = new BuyerGroup();
    buyerGroup2.setName(name2);
    buyerGroup2.setPid(pid2);
    buyerGroup2.setCompany(company);

    BuyerGroupDTO buyerGroupDTO1 = BuyerGroupDTOMapper.MAPPER.map(buyerGroup1);
    BuyerGroupDTO buyerGroupDTO2 = BuyerGroupDTOMapper.MAPPER.map(buyerGroup2);

    Set<String> qf = Collections.emptySet();
    PageRequest pageRequest = PageRequest.of(0, 10);
    List<BuyerGroupDTO> buyerGroupDTOs = new ArrayList<>();
    buyerGroupDTOs.add(buyerGroupDTO1);
    buyerGroupDTOs.add(buyerGroupDTO2);
    List<BuyerGroup> resultBuyerGroups = new ArrayList<>();
    resultBuyerGroups.add(buyerGroup1);
    resultBuyerGroups.add(buyerGroup2);

    Page<BuyerGroup> buyerGroups = new PageImpl<>(Collections.unmodifiableList(resultBuyerGroups));

    when(buyerGroupRepository.findAll(any(Specification.class), eq(pageRequest)))
        .thenReturn(buyerGroups);

    Page<BuyerGroupDTO> result = buyerGroupDTOService.findAll(companyPid, qf, null, pageRequest);
    assertNotNull(result);
    assertEquals(result.getContent(), buyerGroupDTOs);
  }

  @Test
  void returnBuyerGroupsWithNameSearchWithPaginatedResponse() {

    final String name = "whatever";
    final Set<String> qf = Collections.singleton("name");
    final String qt = "whoever";
    final Long pid = new Random().nextLong();
    final Long companyPid = new Random().nextLong();
    final Company company = mock(Company.class);

    BuyerGroup buyerGroup = new BuyerGroup();
    buyerGroup.setName(name);
    buyerGroup.setPid(pid);
    buyerGroup.setCompany(company);

    PageRequest pageRequest = PageRequest.of(0, 10);
    Page<BuyerGroup> buyerGroups = new PageImpl<>(Collections.emptyList());

    when(buyerGroupRepository.findAll(any(Specification.class), eq(pageRequest)))
        .thenReturn(buyerGroups);

    Page<BuyerGroupDTO> result = buyerGroupDTOService.findAll(companyPid, qf, qt, pageRequest);
    assertNotNull(result);
    assertEquals(Collections.emptyList(), result.getContent());
  }

  @Test
  void shouldReturnValidDtoOnCreate() {
    Company company = TestObjectsFactory.createCompany(CompanyType.BUYER);
    BuyerGroup buyerGroup = TestObjectsFactory.createBuyerGroup(company, "VND", "ARG");
    BuyerGroupDTO buyerGroupDTO = BuyerGroupDTOMapper.MAPPER.map(buyerGroup);

    when(buyerGroupRepository.save(any(BuyerGroup.class))).thenReturn(buyerGroup);
    when(companyRepository.findById(company.getPid())).thenReturn(Optional.of(company));
    // when
    BuyerGroupDTO result = buyerGroupDTOService.create(company.getPid(), buyerGroupDTO);
    // then
    assertEquals(result.getCompanyPid(), company.getPid());
    assertEquals(result.getCurrency(), buyerGroupDTO.getCurrency());
    assertEquals(result.getBillingCountry(), buyerGroupDTO.getBillingCountry());
  }

  @Test
  void shouldThrowExceptionOnInvalidCompanyPid() {
    Company company = TestObjectsFactory.createCompany(CompanyType.BUYER);
    BuyerGroup buyerGroup = TestObjectsFactory.createBuyerGroup(company, "VND", "ARG");
    BuyerGroupDTO buyerGroupDTO = BuyerGroupDTOMapper.MAPPER.map(buyerGroup);
    Long companyPid = company.getPid();

    when(companyRepository.findById(company.getPid())).thenReturn(Optional.empty());
    // throws exception when
    GenevaValidationException ex =
        assertThrows(
            GenevaValidationException.class,
            () -> buyerGroupDTOService.create(companyPid, buyerGroupDTO));
    // then
    assertEquals(ServerErrorCodes.SERVER_BUYER_NOT_FOUND, ex.getErrorCode());
  }

  @Test
  void shouldReturnBuyerGroupWithFindOne() {

    Company company = TestObjectsFactory.createCompany(CompanyType.BUYER);
    BuyerGroup buyerGroup = TestObjectsFactory.createBuyerGroup(company, "VND", "ARG");
    BuyerGroupDTO buyerGroupDTO = BuyerGroupDTOMapper.MAPPER.manualMap(buyerGroup);

    when(buyerGroupRepository.findById(buyerGroup.getPid())).thenReturn(Optional.of(buyerGroup));
    // when
    BuyerGroupDTO result = buyerGroupDTOService.findOne(buyerGroup.getPid());
    // then
    assertNotNull(result);
    assertEquals(result, buyerGroupDTO);
  }

  @Test
  void shouldReturnExceptionWhenBuyerGroupNotPresent() {

    Company company = TestObjectsFactory.createCompany(CompanyType.BUYER);
    BuyerGroup buyerGroup = TestObjectsFactory.createBuyerGroup(company, "VND", "ARG");
    Long buyerGroupPid = buyerGroup.getPid();

    when(buyerGroupRepository.findById(buyerGroup.getPid())).thenReturn(Optional.empty());

    // throws exception when
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class, () -> buyerGroupDTOService.findOne(buyerGroupPid));
    // then
    assertEquals(ServerErrorCodes.SERVER_ENTITY_NOT_EXIST, exception.getErrorCode());
  }

  @Test
  void shouldThrowExceptionOnNullCompanyPidForUpdate() {
    BuyerGroupDTO buyerGroupDTO = new BuyerGroupDTO();
    // throws exception when
    GenevaValidationException ex =
        assertThrows(
            GenevaValidationException.class,
            () -> buyerGroupDTOService.update(null, 1L, buyerGroupDTO));
    // then
    assertEquals(ServerErrorCodes.SERVER_INVALID_INPUT, ex.getErrorCode());
  }

  @Test
  void shouldThrowExceptionOnNullBuyerGroupPidForUpdate() {
    BuyerGroupDTO buyerGroupDTO = new BuyerGroupDTO();
    // throws exception when
    GenevaValidationException ex =
        assertThrows(
            GenevaValidationException.class,
            () -> buyerGroupDTOService.update(1L, null, buyerGroupDTO));
    // then
    assertEquals(ServerErrorCodes.SERVER_INVALID_INPUT, ex.getErrorCode());
  }

  @Test
  void shouldThrowExceptionWhenBuyerGroupNotFoundForUpdate() {
    Company company = TestObjectsFactory.createCompany(CompanyType.BUYER);
    BuyerGroup buyerGroup = TestObjectsFactory.createBuyerGroup(company, "VND", "ARG");
    BuyerGroupDTO buyerGroupDTO = BuyerGroupDTOMapper.MAPPER.manualMap(buyerGroup);
    Long companyPid = company.getPid();
    Long buyerGroupPid = buyerGroup.getPid();
    when(buyerGroupRepository.findById(any(Long.class))).thenReturn(Optional.empty());
    // throws exception when
    GenevaValidationException ex =
        assertThrows(
            GenevaValidationException.class,
            () -> buyerGroupDTOService.update(companyPid, buyerGroupPid, buyerGroupDTO));
    // then
    assertEquals(ServerErrorCodes.SERVER_ENTITY_NOT_EXIST, ex.getErrorCode());
  }

  @Test
  void shouldThrowExceptionOnCompanyPidsDoNotMatchForUpdate() {
    Company company = TestObjectsFactory.createCompany(CompanyType.BUYER);
    BuyerGroup buyerGroup = TestObjectsFactory.createBuyerGroup(company, "VND", "ARG");
    BuyerGroupDTO buyerGroupDTO = BuyerGroupDTOMapper.MAPPER.manualMap(buyerGroup);
    Long companyPid = company.getPid();
    Long buyerGroupPid = buyerGroup.getPid();

    Company company2 = TestObjectsFactory.createCompany(CompanyType.BUYER);
    when(buyerGroupRepository.findById(any(Long.class)))
        .thenReturn(Optional.of(TestObjectsFactory.createBuyerGroup(company2, "VND", "ARG")));
    // throws exception when
    GenevaValidationException ex =
        assertThrows(
            GenevaValidationException.class,
            () -> buyerGroupDTOService.update(companyPid, buyerGroupPid, buyerGroupDTO));
    // then
    assertEquals(ServerErrorCodes.SERVER_COMPANY_MUST_NOT_BE_CHANGED, ex.getErrorCode());
  }

  @Test
  void shouldReturnUpdatedDtoForUpdate() {
    Company company = TestObjectsFactory.createCompany(CompanyType.BUYER);
    BuyerGroup buyerGroup = TestObjectsFactory.createBuyerGroup(company, "VND", "ARG");
    BuyerGroupDTO buyerGroupDTOPayload = BuyerGroupDTOMapper.MAPPER.manualMap(buyerGroup);

    BuyerGroup existingBuyerGroup = TestObjectsFactory.createBuyerGroup(company, "USD", "USA");
    when(buyerGroupRepository.findById(any(Long.class)))
        .thenReturn(Optional.of(existingBuyerGroup));
    when(buyerGroupRepository.saveAndFlush(any())).thenReturn(buyerGroup);
    // when
    BuyerGroupDTO buyerGroupDTOUpdated =
        buyerGroupDTOService.update(company.getPid(), buyerGroup.getPid(), buyerGroupDTOPayload);
    // then
    assertEquals(buyerGroupDTOPayload, buyerGroupDTOUpdated);
  }
}
