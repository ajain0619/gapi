package com.nexage.app.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.bidder.model.BdrExchangeCompany;
import com.nexage.admin.core.repository.BdrExchangeCompanyRepository;
import com.nexage.app.dto.CompanyDTO;
import com.nexage.app.dto.bdr.BdrExchangeCompanyDTO;
import com.nexage.app.dto.bdr.BdrExchangeCompanyPkDTO;
import com.nexage.app.dto.bdr.BdrExchangeDTO;
import com.nexage.app.mapper.BdrExchangeCompanyDTOMapper;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExchangeCompanyServiceImplTest {

  @Mock private BdrExchangeCompanyRepository repository;
  @Mock private EntityManager entityManager;
  @InjectMocks private ExchangeCompanyServiceImpl service;

  @Test
  void shouldCreateBdrExchangeCompany() {
    // given
    BdrExchangeCompanyDTO bdrExchangeCompanyDto = createBdrExchangeCompanyDto(1L, 2L);
    BdrExchangeCompany bdrExchangeCompany =
        BdrExchangeCompanyDTOMapper.MAPPER.map(bdrExchangeCompanyDto);
    when(repository.saveAndFlush(bdrExchangeCompany)).then(returnsFirstArg());

    // when
    BdrExchangeCompanyDTO result = service.create(bdrExchangeCompanyDto);

    // then
    assertEquals(bdrExchangeCompanyDto, result);
    verify(repository).saveAndFlush(bdrExchangeCompany);
    verifyNoMoreInteractions(repository);
  }

  @Test
  void shouldGetBdrExchangeCompaniesForCompany() {
    // given
    long companyPid = 1L;
    List<BdrExchangeCompanyDTO> bdrExchangeCompanyDtos =
        List.of(createBdrExchangeCompanyDto(1L, 2L), createBdrExchangeCompanyDto(2L, 2L));
    List<BdrExchangeCompany> bdrExchangeCompanies =
        bdrExchangeCompanyDtos.stream()
            .map(BdrExchangeCompanyDTOMapper.MAPPER::map)
            .collect(Collectors.toList());
    when(repository.findByExchangeCompanyPk_Company_Pid(companyPid))
        .thenReturn(bdrExchangeCompanies);

    // when
    List<BdrExchangeCompanyDTO> result = service.getAllForSeatholder(companyPid);

    // then
    assertEquals(bdrExchangeCompanyDtos, result);
    verify(repository).findByExchangeCompanyPk_Company_Pid(companyPid);
    verifyNoMoreInteractions(repository);
  }

  @Test
  void shouldUpdateBdrExchangeCompany() {
    // given
    BdrExchangeCompanyDTO bdrExchangeCompanyDto = createBdrExchangeCompanyDto(1L, 2L);
    BdrExchangeCompany bdrExchangeCompany =
        BdrExchangeCompanyDTOMapper.MAPPER.map(bdrExchangeCompanyDto);
    when(repository.saveAndFlush(bdrExchangeCompany)).then(returnsFirstArg());

    // when
    BdrExchangeCompanyDTO result = service.update(bdrExchangeCompanyDto);

    // then
    assertEquals(bdrExchangeCompanyDto, result);
    verify(repository).saveAndFlush(bdrExchangeCompany);
    verifyNoMoreInteractions(repository);
  }

  @Test
  void shouldDeleteBdrExchangeCompany() {
    // given
    long seatHolderPid = 1L;
    long exchangePid = 2L;
    doNothing()
        .when(repository)
        .deleteByExchangeCompanyPk_Company_PidAndExchangeCompanyPk_BidderExchange_Pid(
            seatHolderPid, exchangePid);

    // when
    service.delete(seatHolderPid, exchangePid);

    // then
    verify(repository)
        .deleteByExchangeCompanyPk_Company_PidAndExchangeCompanyPk_BidderExchange_Pid(
            seatHolderPid, exchangePid);
    verifyNoMoreInteractions(repository);
  }

  private BdrExchangeCompanyDTO createBdrExchangeCompanyDto(long bdrExchangePid, long companyPid) {
    BdrExchangeDTO bdrExchangeDto = new BdrExchangeDTO();
    bdrExchangeDto.setPid(bdrExchangePid);
    CompanyDTO companyDto = new CompanyDTO();
    companyDto.setPid(companyPid);
    BdrExchangeCompanyDTO bdrExchangeCompanyDto = new BdrExchangeCompanyDTO();
    bdrExchangeCompanyDto.setExchangeCompanyPk(
        new BdrExchangeCompanyPkDTO(bdrExchangeDto, companyDto));
    return bdrExchangeCompanyDto;
  }
}
