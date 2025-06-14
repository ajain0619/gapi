package com.nexage.app.services.impl;

import static com.nexage.app.web.support.TestObjectsFactory.setFieldReflectively;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.google.common.base.Joiner;
import com.nexage.admin.core.dto.AdSourceSummaryDTO;
import com.nexage.admin.core.dto.BidderSummaryDTO;
import com.nexage.admin.core.enums.BidderFormat;
import com.nexage.admin.core.enums.BlockListInclusion;
import com.nexage.admin.core.enums.GlobalConfigProperty;
import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.enums.VerificationType;
import com.nexage.admin.core.enums.site.Type;
import com.nexage.admin.core.model.AdSource;
import com.nexage.admin.core.model.BidderConfig;
import com.nexage.admin.core.model.BidderDeviceType;
import com.nexage.admin.core.model.BidderSubscription;
import com.nexage.admin.core.model.BuyerGroup;
import com.nexage.admin.core.model.BuyerSeat;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.ExchangeProduction;
import com.nexage.admin.core.model.ExchangeRegional;
import com.nexage.admin.core.model.ExternalDataProvider;
import com.nexage.admin.core.phonecast.PhoneCastConfigService;
import com.nexage.admin.core.repository.AdSourceRepository;
import com.nexage.admin.core.repository.BidderConfigRepository;
import com.nexage.admin.core.repository.BuyerGroupRepository;
import com.nexage.admin.core.repository.BuyerSeatRepository;
import com.nexage.admin.core.repository.CompanyRepository;
import com.nexage.admin.core.repository.DeviceTypeRepository;
import com.nexage.admin.core.repository.ExchangeProductionRepository;
import com.nexage.admin.core.repository.ExchangeRegionalRepository;
import com.nexage.admin.core.repository.ExternalDataProviderRepository;
import com.nexage.admin.core.sparta.jpa.model.AdSourceLogoFileReference;
import com.nexage.admin.core.util.UUIDGenerator;
import com.nexage.app.dto.buyer.BuyerGroupDTO;
import com.nexage.app.dto.buyer.BuyerSeatDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.mapper.BuyerGroupDTOMapper;
import com.nexage.app.services.AdSourceService;
import com.nexage.app.services.BidderConfigService;
import com.nexage.app.services.FileSystemService;
import com.nexage.app.util.EnvironmentUtil;
import com.nexage.app.util.assemblers.buyer.BuyerGroupAssembler;
import com.nexage.app.util.assemblers.buyer.BuyerSeatAssembler;
import com.nexage.app.web.support.TestObjectsFactory;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.error.model.CommonErrorCodes;
import com.ssp.geneva.common.model.inventory.CompanyType;
import com.ssp.geneva.common.settings.service.GlobalConfigService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.hibernate.StaleStateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class BuyerServiceImplTest {

  private static final String LOGO_BASE_URL =
      "https://adserve.nexage.com/ads/uploaded/pvol1/buyer_logo/";
  private static final String LOGO_FILENAME1 = "7061-1507318471319.jpg";
  private static final String LOGO_FILENAME2 = "666-1507314017407.jpg";
  private static final String LOGO_UPLOAD_DIR = "/opt/creative/buyer_logo";
  private static final String RELOAD_MESSAGE =
      "Data record has been updated since last loaded. Please reload it and try again";

  @Mock private CompanyRepository companyRepository;
  @Mock private BuyerGroupRepository buyerGroupRepository;
  @Mock private AdSourceRepository adSourceRepository;
  @Mock private AdSourceService adSourceService;
  @Mock private BuyerSeatRepository buyerSeatRepository;
  @Mock private BuyerGroupAssembler buyerGroupAssembler;
  @Mock private BuyerSeatAssembler buyerSeatAssembler;
  @Mock private PhoneCastConfigService phoneCastConfigService;
  @Mock private GlobalConfigService globalConfigService;
  @Mock private EnvironmentUtil environmentUtil;
  @Mock private BidderConfigRepository bidderConfigRepository;
  @Mock private BidderConfigService bidderConfigService;
  @Mock private ExchangeProductionRepository exchangeProductionRepository;
  @Mock private ExchangeRegionalRepository exchangeRegionalRepository;
  @Mock private ExternalDataProviderRepository externalDataProviderRepository;
  @Mock private DeviceTypeRepository deviceTypeRepository;

  @InjectMocks private BuyerServiceTestImpl buyerService;

  @BeforeEach
  void setup() {
    when(environmentUtil.isAwsEnvironment()).thenReturn(false);
    when(globalConfigService.getStringValue(GlobalConfigProperty.BUYER_LOGO_BASE_URL))
        .thenReturn(LOGO_BASE_URL);
    when(globalConfigService.getStringValue(GlobalConfigProperty.BUYER_LOGO_DIR))
        .thenReturn(LOGO_UPLOAD_DIR);

    buyerService.init();
  }

  @Test
  void shouldGetLogoUrlOnGetAllAdSourceSummaries() {
    // given
    AdSourceSummaryDTO dbAdSourceSummary1 = TestObjectsFactory.createAdSourceSummary();
    dbAdSourceSummary1.setLogo(LOGO_FILENAME1);
    AdSourceSummaryDTO dbAdSourceSummary2 = TestObjectsFactory.createAdSourceSummary();
    dbAdSourceSummary2.setLogo(LOGO_FILENAME2);
    when(adSourceService.getAdSourceSummariesForGeneva())
        .thenReturn(Arrays.asList(dbAdSourceSummary1, dbAdSourceSummary2));

    // when
    List<AdSourceSummaryDTO> actualSummaries = buyerService.getAllAdSourceSummaries();

    // then
    assertEquals(LOGO_BASE_URL + LOGO_FILENAME1, actualSummaries.get(0).getLogoUrl());
    assertEquals(LOGO_BASE_URL + LOGO_FILENAME2, actualSummaries.get(1).getLogoUrl());
  }

  @Test
  void shouldGetLogoUrlOnGetAdSource() {
    // given
    var pid = 12345L;
    AdSource dbAdSource = TestObjectsFactory.createAdSource();
    dbAdSource.setPid(pid);
    dbAdSource.setLogo(LOGO_FILENAME1);
    when(adSourceRepository.findById(pid)).thenReturn(Optional.of(dbAdSource));

    // when
    AdSource actualAdSource = buyerService.getAdSource(pid);

    // then
    assertEquals(LOGO_BASE_URL + LOGO_FILENAME1, actualAdSource.getLogoUrl());
  }

  @Test
  void shouldUpdateAdSourceWhenLogoIsValid() {
    // given
    var adSourcePid = 12345L;
    var extension = "jpg";
    byte[] image = "image_content_would_go_here".getBytes();

    Company dbCompany = prepareCompany();

    AdSource dbAdSource = TestObjectsFactory.createAdSource();
    dbAdSource.setPid(adSourcePid);
    when(adSourceRepository.findById(anyLong())).thenReturn(Optional.of(dbAdSource));

    AdSource inputAdSource = TestObjectsFactory.createAdSource();
    inputAdSource.setPid(adSourcePid);

    when(adSourceRepository.save(any())).thenReturn(inputAdSource);

    AdSourceLogoFileReference logoFileReference = new AdSourceLogoFileReference();
    logoFileReference.setExtension(extension);
    logoFileReference.setData(image);

    inputAdSource.setLogoFileReference(logoFileReference);

    // when
    AdSource updatedAdSource =
        buyerService.updateAdSource(dbCompany.getPid(), inputAdSource, adSourcePid);

    // then
    assertEquals(image, buyerService.image);
    Pattern pattern = Pattern.compile(adSourcePid + "-[0-9]+\\." + extension);
    Matcher matcher = pattern.matcher(updatedAdSource.getLogo());
    assertTrue(matcher.matches());
    assertEquals(LOGO_BASE_URL + updatedAdSource.getLogo(), updatedAdSource.getLogoUrl());
    assertEquals(updatedAdSource.getLogo(), buyerService.logoPath);
    assertNull(updatedAdSource.getLogoFileReference());
  }

  @Test
  void shouldThrowExceptionOnUpdateAdSourceWhenLogoExtensionIsInvalid() {
    // given
    var adSourcePid = 12345L;
    var extension = "";
    byte[] image = "image_content_would_go_here".getBytes();

    Company dbCompany = prepareCompany();
    long companyPid = dbCompany.getPid();

    AdSource dbAdSource = TestObjectsFactory.createAdSource();
    dbAdSource.setPid(adSourcePid);
    when(adSourceRepository.findById(anyLong())).thenReturn(Optional.of(dbAdSource));

    AdSource inputAdSource = TestObjectsFactory.createAdSource();
    inputAdSource.setPid(adSourcePid);

    AdSourceLogoFileReference logoFileReference = new AdSourceLogoFileReference();
    logoFileReference.setExtension(extension);
    logoFileReference.setData(image);

    inputAdSource.setLogoFileReference(logoFileReference);

    // throws exception when
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> buyerService.updateAdSource(companyPid, inputAdSource, adSourcePid));

    // then
    assertEquals(ServerErrorCodes.SERVER_INVALID_ADSOURCE_LOGO, exception.getErrorCode());
  }

  @Test
  void shouldThrowExceptionOnUpdateAdSourceWhenLogoImageIsInvalid() {
    // given
    var adSourcePid = 12345L;
    var extension = "gif";
    byte[] image = new byte[0];

    Company dbCompany = prepareCompany();

    AdSource dbAdSource = TestObjectsFactory.createAdSource();
    dbAdSource.setPid(adSourcePid);
    when(adSourceRepository.findById(anyLong())).thenReturn(Optional.of(dbAdSource));

    AdSource inputAdSource = TestObjectsFactory.createAdSource();
    inputAdSource.setPid(adSourcePid);

    AdSourceLogoFileReference logoFileReference = new AdSourceLogoFileReference();
    logoFileReference.setExtension(extension);
    logoFileReference.setData(image);

    inputAdSource.setLogoFileReference(logoFileReference);

    // throws exception when
    Long companyPid = dbCompany.getPid();
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> buyerService.updateAdSource(companyPid, inputAdSource, adSourcePid));

    // then
    assertEquals(ServerErrorCodes.SERVER_INVALID_ADSOURCE_LOGO, exception.getErrorCode());
  }

  @Test
  void shouldDeleteLogoOnUpdateAdSourceWhenLogoIsNull() {
    // given
    var adSourcePid = 12345L;

    Company dbCompany = prepareCompany();

    AdSource dbAdSource = TestObjectsFactory.createAdSource();
    dbAdSource.setPid(adSourcePid);
    when(adSourceRepository.findById(anyLong())).thenReturn(Optional.of(dbAdSource));

    AdSource inputAdSource = TestObjectsFactory.createAdSource();
    inputAdSource.setPid(adSourcePid);

    when(adSourceRepository.save(any())).thenReturn(inputAdSource);
    inputAdSource.setLogo(null);

    // when
    AdSource updatedAdSource =
        buyerService.updateAdSource(dbCompany.getPid(), inputAdSource, adSourcePid);

    // then
    assertNull(updatedAdSource.getLogo());
    assertNull(updatedAdSource.getLogoUrl());
  }

  @Test
  void shouldThrowExceptionWhenAttemptingToUpdateAdSourceWithStatusDeleted() {
    AdSource inputAdSource = TestObjectsFactory.createAdSource();
    inputAdSource.setStatus(Status.DELETED);
    var company = new Company();
    company.setPid(1L);
    inputAdSource.setCompany(company);
    inputAdSource.setPid(1L);
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () -> buyerService.updateAdSource(1L, inputAdSource, 1L));
    assertEquals(CommonErrorCodes.COMMON_BAD_REQUEST, exception.getErrorCode());
  }

  @Test
  void shouldThrowExceptionWhenBidderConfigNotFound() {
    // given
    when(bidderConfigRepository.findByCompanyPid(anyLong())).thenReturn(List.of());
    when(companyRepository.existsById(anyLong())).thenReturn(true);

    // when
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> buyerService.getAllBidderConfigsByCompanyPid(1L));

    // then
    assertEquals(ServerErrorCodes.SERVER_BIDDER_CONFIG_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void shouldCreateAdSourceWhenLogoIsValid() {
    // given
    var adSourcePid = 12345L;
    var extension = "jpg";
    byte[] image = "image_content_would_go_here".getBytes();

    Company dbCompany = prepareCompany();

    AdSource dbAdSource = TestObjectsFactory.createAdSource();
    dbAdSource.setPid(adSourcePid);

    AdSource inputAdSource = TestObjectsFactory.createAdSource();
    inputAdSource.setPid(adSourcePid);

    when(adSourceRepository.save(any())).thenReturn(inputAdSource);

    AdSourceLogoFileReference logoFileReference = new AdSourceLogoFileReference();
    logoFileReference.setExtension(extension);
    logoFileReference.setData(image);

    inputAdSource.setLogoFileReference(logoFileReference);

    // when
    AdSource newAdSource = buyerService.createAdSource(dbCompany.getPid(), inputAdSource);

    // then
    assertEquals(image, buyerService.image);
    Pattern pattern = Pattern.compile(adSourcePid + "-[0-9]+\\." + extension);
    Matcher matcher = pattern.matcher(newAdSource.getLogo());
    assertTrue(matcher.matches());
    assertEquals(LOGO_BASE_URL + newAdSource.getLogo(), newAdSource.getLogoUrl());
    assertEquals(newAdSource.getLogo(), buyerService.logoPath);
    assertNull(newAdSource.getLogoFileReference());
  }

  @Test
  void shouldThrowExceptionWhenCompanyDoesNotExist() {
    when(companyRepository.findById(anyLong())).thenReturn(Optional.empty());
    AdSource inputAdSource = TestObjectsFactory.createAdSource();
    var exception =
        assertThrows(
            GenevaValidationException.class, () -> buyerService.createAdSource(1L, inputAdSource));
    assertEquals(
        ServerErrorCodes.SERVER_CREATE_ADSOURCE_COMPANY_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void shouldThrowExceptionOnUpdateBuyerGroupWhenEntityToBeUpdatedDoesNotExist() {
    // given
    var companyPid = 1L;
    var buyerGroupPid = 1L;
    BuyerGroupDTO dto =
        new BuyerGroupDTO(
            buyerGroupPid, "name", "sfdcLineId", "sfdcIoId", "EUR", "DEU", true, 0, companyPid);
    when(buyerGroupRepository.findById(buyerGroupPid)).thenReturn(Optional.empty());

    // throws exception when
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> buyerService.updateBuyerGroup(companyPid, buyerGroupPid, dto));

    // then
    assertEquals(ServerErrorCodes.SERVER_ENTITY_NOT_EXIST, exception.getErrorCode());
  }

  @Test
  void shouldThrowExceptionOnUpdateBuyerSeatWhenEntityToBeUpdatedDoesNotExist() {
    // given
    var companyPid = 1L;
    var buyerGroupPid = 1L;
    var seatPid = 1L;
    BuyerSeatDTO dto =
        new BuyerSeatDTO(1L, "name-1", "seat-1", true, buyerGroupPid, 0, companyPid, true, 1L);
    when(buyerSeatRepository.findById(seatPid)).thenReturn(Optional.empty());

    // throws exception when
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> buyerService.updateBuyerSeat(companyPid, seatPid, dto));

    // then
    assertEquals(ServerErrorCodes.SERVER_ENTITY_NOT_EXIST, exception.getErrorCode());
  }

  @Test
  void shouldThrowExceptionOnUpdateBuyerGroupWhenVersionDoesNotMatch() {
    // given
    Company company = TestObjectsFactory.createCompany(CompanyType.BUYER);
    long companyPid = company.getPid();
    var version = 0;
    BuyerGroup buyerGroup = TestObjectsFactory.createBuyerGroup(company, "VND", "ARG");
    long buyerGroupPid = buyerGroup.getPid();
    setFieldReflectively(buyerGroup, "version", version);
    BuyerGroupDTO dto = BuyerGroupDTOMapper.MAPPER.manualMap(buyerGroup);
    setFieldReflectively(dto, "version", version + 1);
    when(buyerGroupRepository.findById(buyerGroup.getPid())).thenReturn(Optional.of(buyerGroup));

    // throws exception when
    StaleStateException exception =
        assertThrows(
            StaleStateException.class,
            () -> buyerService.updateBuyerGroup(companyPid, buyerGroupPid, dto));

    // then
    assertEquals(RELOAD_MESSAGE, exception.getMessage());
  }

  @Test
  void shouldThrowExceptionOnUpdateBuyerSeatWhenVersionDoesNotMatch() {
    // given
    Company company = TestObjectsFactory.createCompany(CompanyType.BUYER);
    long companyPid = company.getPid();
    BuyerGroup buyerGroup = TestObjectsFactory.createBuyerGroup(company, "VND", "ARG");
    var version = 0;
    BuyerSeat seat =
        TestObjectsFactory.createBuyerSeat("seat-1", "name-1", true, buyerGroup, company, true, 1L);
    long buyerSeatPid = seat.getPid();
    setFieldReflectively(seat, "version", version);
    BuyerSeatDTO dto = new BuyerSeatDTO(seat);
    setFieldReflectively(dto, "version", version + 1);
    when(buyerSeatRepository.findById(seat.getPid())).thenReturn(Optional.of(seat));

    // throws exception when
    StaleStateException exception =
        assertThrows(
            StaleStateException.class,
            () -> buyerService.updateBuyerSeat(companyPid, buyerSeatPid, dto));

    // then
    assertEquals(RELOAD_MESSAGE, exception.getMessage());
  }

  @Test
  void shouldThrowExceptionOnUpdateBuyerGroupWhenCompanyPidDoesNotMatch() {
    // given
    Company company = TestObjectsFactory.createCompany(CompanyType.BUYER);
    BuyerGroup buyerGroup = TestObjectsFactory.createBuyerGroup(company, "VND", "ARG");
    BuyerGroupDTO dto = BuyerGroupDTOMapper.MAPPER.manualMap(buyerGroup);
    when(buyerGroupRepository.findById(buyerGroup.getPid())).thenReturn(Optional.of(buyerGroup));
    Long changedCompanyPid = company.getPid() + 1;

    // throws exception when
    Long buyerGroupPid = buyerGroup.getPid();
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> buyerService.updateBuyerGroup(changedCompanyPid, buyerGroupPid, dto));

    // then
    assertEquals(ServerErrorCodes.SERVER_COMPANY_MUST_NOT_BE_CHANGED, exception.getErrorCode());
  }

  @Test
  void shouldReturnUpdatedBuyerGroup() {
    // given
    Company company = TestObjectsFactory.createCompany(CompanyType.BUYER);
    BuyerGroup buyerGroup = TestObjectsFactory.createBuyerGroup(company, "VND", "ARG");
    BuyerGroupDTO dto = BuyerGroupDTOMapper.MAPPER.manualMap(buyerGroup);

    when(buyerGroupRepository.findById(buyerGroup.getPid())).thenReturn(Optional.of(buyerGroup));
    when(buyerGroupAssembler.apply(any(BuyerGroup.class), any(BuyerGroupDTO.class)))
        .thenReturn(buyerGroup);
    when(buyerGroupRepository.saveAndFlush(any(BuyerGroup.class))).thenReturn(buyerGroup);

    // when
    BuyerGroupDTO updated =
        buyerService.updateBuyerGroup(company.getPid(), buyerGroup.getPid(), dto);

    // then
    assertEquals(dto, updated);

    verify(buyerGroupRepository).findById(buyerGroup.getPid());
    verify(buyerGroupRepository).saveAndFlush(any(BuyerGroup.class));
    verify(buyerGroupAssembler).apply(any(BuyerGroup.class), any(BuyerGroupDTO.class));
    verifyNoMoreInteractions(buyerGroupRepository);
    verifyNoMoreInteractions(buyerGroupAssembler);
  }

  @Test
  void shouldReturnCreatedBuyerGroup() {
    // given
    Company company = TestObjectsFactory.createCompany(CompanyType.BUYER);
    BuyerGroup buyerGroup = TestObjectsFactory.createBuyerGroup(company, "VND", "ARG");
    BuyerGroupDTO dto = BuyerGroupDTOMapper.MAPPER.manualMap(buyerGroup);

    when(buyerGroupAssembler.transientEntity(company.getPid(), dto)).thenReturn(buyerGroup);
    when(buyerGroupRepository.save(buyerGroup)).thenReturn(buyerGroup);

    // when
    BuyerGroupDTO created = buyerService.createBuyerGroup(company.getPid(), dto);

    // then
    assertEquals(dto, created);

    verify(buyerGroupRepository).save(buyerGroup);
    verify(buyerGroupAssembler).transientEntity(company.getPid(), dto);
    verifyNoMoreInteractions(buyerGroupRepository);
    verifyNoMoreInteractions(buyerGroupAssembler);
  }

  @Test
  void shouldReturnAllBuyerGroupsForCompany() {
    // given
    Company company = TestObjectsFactory.createCompany(CompanyType.BUYER);
    BuyerGroup buyerGroup1 = TestObjectsFactory.createBuyerGroup(company, "VND", "ARG");
    BuyerGroupDTO dto1 = BuyerGroupDTOMapper.MAPPER.manualMap(buyerGroup1);
    BuyerGroup buyerGroup2 = TestObjectsFactory.createBuyerGroup(company, "USD", "USA");
    BuyerGroupDTO dto2 = BuyerGroupDTOMapper.MAPPER.manualMap(buyerGroup2);
    List<BuyerGroup> buyerGroups = List.of(buyerGroup1, buyerGroup2);

    when(buyerGroupRepository.findAllByCompanyPid(company.getPid())).thenReturn(buyerGroups);

    // when
    List<BuyerGroupDTO> buyerGroupsDtos =
        buyerService.getAllBuyerGroupsForCompany(company.getPid());

    // then
    assertEquals(dto1, buyerGroupsDtos.get(0));
    assertEquals(dto2, buyerGroupsDtos.get(1));

    verify(buyerGroupRepository).findAllByCompanyPid(company.getPid());
    verifyNoMoreInteractions(buyerGroupRepository);
  }

  @Test
  void shouldThrowExceptionOnUpdateBuyerSeatWhenCompanyPidDoesNotMatch() {
    // given
    Company company = TestObjectsFactory.createCompany(CompanyType.BUYER);
    BuyerGroup buyerGroup = TestObjectsFactory.createBuyerGroup(company, "VND", "ARG");
    BuyerSeat seat =
        TestObjectsFactory.createBuyerSeat("seat-1", "name-1", true, buyerGroup, company, true, 1L);
    BuyerSeatDTO dto = new BuyerSeatDTO(seat);
    when(buyerSeatRepository.findById(seat.getPid())).thenReturn(Optional.of(seat));
    Long changedCompanyPid = company.getPid() + 1;

    // throws exception when
    Long seatPid = seat.getPid();
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> buyerService.updateBuyerSeat(changedCompanyPid, seatPid, dto));

    // then
    assertEquals(ServerErrorCodes.SERVER_COMPANY_MUST_NOT_BE_CHANGED, exception.getErrorCode());
  }

  @Test
  void shouldCreateDefaultBidderConfigOnCreateBidderConfigWhenPayloadIsNull() {
    // given
    Company company = new Company();
    company.setPid(7L);
    BidderConfig toPersist = new BidderConfig();
    BidderConfig expected = new BidderConfig();
    when(companyRepository.findById(7L)).thenReturn(Optional.of(company));
    when(bidderConfigService.createDefaultBidderConfigForBuyer(company)).thenReturn(toPersist);
    when(bidderConfigService.saveFlushAndRefreshBidderConfig(same(toPersist))).thenReturn(expected);

    // when
    BidderConfig actual = buyerService.createBidderConfig(7L, null);

    // then
    assertEquals(expected, actual);
  }

  @Test
  void
      shouldThrowExceptionWhenCreatingBidderConfigDefaultBidCurrencyMismatchedWithBuyersCurrency() {
    // given
    Company dbCompany = TestObjectsFactory.createCompany(CompanyType.BUYER);
    dbCompany.setCurrency("USD");
    when(companyRepository.findById(anyLong())).thenReturn(Optional.of(dbCompany));
    BidderConfig bidderConfig = new BidderConfig();
    bidderConfig.setDefaultBidCurrency("EUR");

    // throws exception when
    Long dbCompanyPid = dbCompany.getPid();
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> buyerService.createBidderConfig(dbCompanyPid, bidderConfig));

    // then
    assertEquals(
        ServerErrorCodes.SERVER_BIDDER_CONFIG_INVALID_BID_CURRENCY, exception.getErrorCode());
  }

  @Test
  void shouldSaveBidderConfigWhenCreatingBidderConfigDefaultBidCurrencyMatchesWithBuyersCurrency() {
    // given
    Company dbCompany = TestObjectsFactory.createCompany(CompanyType.BUYER);
    dbCompany.setCurrency("EUR");
    when(companyRepository.findById(anyLong())).thenReturn(Optional.of(dbCompany));
    BidderConfig bidderConfig = createDefaultBidderConfigForBuyer(dbCompany);
    bidderConfig.setDefaultBidCurrency("EUR");
    bidderConfig.setFormatType(BidderFormat.OpenRTBv2_3);
    when(bidderConfigService.saveFlushAndRefreshBidderConfig(bidderConfig))
        .thenReturn(bidderConfig);

    // when
    BidderConfig returnedBidderConfig =
        buyerService.createBidderConfig(dbCompany.getPid(), bidderConfig);

    // then
    assertEquals(bidderConfig, returnedBidderConfig);
    verify(bidderConfigService).saveFlushAndRefreshBidderConfig(bidderConfig);
  }

  @Test
  void
      shouldThrowExceptionWhenUpdatingBidderConfigDefaultBidCurrencyMismatchedWithBuyersCurrency() {
    // given
    Company dbCompany = TestObjectsFactory.createCompany(CompanyType.BUYER);
    dbCompany.setCurrency("USD");
    when(companyRepository.findById(dbCompany.getPid())).thenReturn(Optional.of(dbCompany));
    BidderConfig bidderConfig = createDefaultBidderConfigForBuyer(dbCompany);
    bidderConfig.setDefaultBidCurrency("EUR");
    bidderConfig.setPid(1L);
    bidderConfig.setCompanyPid(dbCompany.getPid());
    bidderConfig.setFormatType(BidderFormat.OpenRTBv2_3);
    when(bidderConfigRepository.findById(bidderConfig.getPid()))
        .thenReturn(Optional.of(bidderConfig));

    // throws exception when
    Long bidderConfigPid = bidderConfig.getPid();
    Long dbCompanyPid = dbCompany.getPid();
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> buyerService.updateBidderConfig(bidderConfigPid, bidderConfig, dbCompanyPid));

    // then
    assertEquals(
        ServerErrorCodes.SERVER_BIDDER_CONFIG_INVALID_BID_CURRENCY, exception.getErrorCode());
  }

  @Test
  void
      shouldSaveBidderConfigWhenCreatingBidderConfigDefaultDomainFilterAllowListMatchesWithDomainFilterWhiteList() {
    // given
    Company dbCompany = TestObjectsFactory.createCompany(CompanyType.BUYER);
    dbCompany.setCurrency("EUR");
    when(companyRepository.findById(anyLong())).thenReturn(Optional.of(dbCompany));
    BidderConfig bidderConfig = createDefaultBidderConfigForBuyer(dbCompany);
    bidderConfig.setDefaultBidCurrency("EUR");
    bidderConfig.setFormatType(BidderFormat.OpenRTBv2_3);
    bidderConfig.setDomainFilterAllowList(true);
    when(bidderConfigService.saveFlushAndRefreshBidderConfig(bidderConfig))
        .thenReturn(bidderConfig);

    // when
    BidderConfig returnedBidderConfig =
        buyerService.createBidderConfig(dbCompany.getPid(), bidderConfig);

    // then
    assertEquals(bidderConfig, returnedBidderConfig);
    verify(bidderConfigService).saveFlushAndRefreshBidderConfig(bidderConfig);
  }

  @Test
  void shouldThrowExceptionWhenUpdatingBidderConfigMismatchedWithDomainFilterList() {
    // given
    Company dbCompany = TestObjectsFactory.createCompany(CompanyType.BUYER);
    when(companyRepository.findById(dbCompany.getPid())).thenReturn(Optional.of(dbCompany));
    BidderConfig bidderConfig = createDefaultBidderConfigForBuyer(dbCompany);
    bidderConfig.setPid(1L);
    bidderConfig.setCompanyPid(dbCompany.getPid());
    bidderConfig.setFormatType(BidderFormat.OpenRTBv2_3);
    bidderConfig.setDomainFilterAllowList(true);
    when(bidderConfigRepository.findById(bidderConfig.getPid()))
        .thenReturn(Optional.of(bidderConfig));

    // throws exception when
    long bidderConfigPid = bidderConfig.getPid();
    long buyerPid = dbCompany.getPid();
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> buyerService.updateBidderConfig(bidderConfigPid, bidderConfig, buyerPid));

    // then
    assertEquals(
        ServerErrorCodes.SERVER_BIDDER_CONFIG_NO_EXCHANGE_REGIONS, exception.getErrorCode());
  }

  @Test
  void
      shouldSaveBidderConfigWhenCreatingBidderConfigDefaultDomainFilterAllowListWithoutDomainFilterWhiteList() {
    // given
    Company dbCompany = TestObjectsFactory.createCompany(CompanyType.BUYER);
    when(companyRepository.findById(anyLong())).thenReturn(Optional.of(dbCompany));
    BidderConfig bidderConfig = createDefaultBidderConfigForBuyer(dbCompany);
    bidderConfig.setFormatType(BidderFormat.OpenRTBv2_3);
    bidderConfig.setDomainFilterAllowList(true);
    when(bidderConfigService.saveFlushAndRefreshBidderConfig(bidderConfig))
        .thenReturn(bidderConfig);

    // when
    BidderConfig returnedBidderConfig =
        buyerService.createBidderConfig(dbCompany.getPid(), bidderConfig);

    // then
    assertEquals(bidderConfig, returnedBidderConfig);
    verify(bidderConfigService).saveFlushAndRefreshBidderConfig(bidderConfig);
  }

  @Test
  void shouldThrowExceptionWhenUpdatingBidderConfigDomainFilterList() {
    // given
    Company dbCompany = TestObjectsFactory.createCompany(CompanyType.BUYER);
    long companyPid = dbCompany.getPid();
    BidderConfig bidderConfig = createDefaultBidderConfigForBuyer(dbCompany);
    long bidderConfigPid = 1L;
    bidderConfig.setPid(bidderConfigPid);
    bidderConfig.setCompanyPid(dbCompany.getPid());
    bidderConfig.setFormatType(BidderFormat.OpenRTBv2_3);
    bidderConfig.setDomainFilterAllowList(true);
    when(bidderConfigRepository.findById(bidderConfig.getPid()))
        .thenReturn(Optional.of(bidderConfig));

    // throws exception when
    assertThrows(
        GenevaValidationException.class,
        () -> buyerService.updateBidderConfig(bidderConfigPid, bidderConfig, companyPid));
  }

  @Test
  void shouldGetBidderConfigWithAllowedDeviceTypes() {
    // given
    Company dbCompany = TestObjectsFactory.createCompany(CompanyType.BUYER);
    BidderConfig bidderConfig = new BidderConfig();
    bidderConfig.setPid(1L);
    bidderConfig.setCompanyPid(dbCompany.getPid());
    bidderConfig.setFormatType(BidderFormat.OpenRTBv2_3);
    BidderDeviceType bidderDeviceType = initializeBidderDeviceType(bidderConfig, 3);
    BidderDeviceType bidderDeviceType2 = initializeBidderDeviceType(bidderConfig, 4);
    bidderConfig.setAllowedDeviceTypes(Set.of(bidderDeviceType, bidderDeviceType2));
    when(bidderConfigRepository.findById(bidderConfig.getPid()))
        .thenReturn(Optional.of(bidderConfig));

    // when
    BidderConfig bidderConfigFromGet =
        buyerService.getBidderConfig(dbCompany.getPid(), bidderConfig.getPid());

    // then
    Set<BidderDeviceType> allowedDeviceTypes = bidderConfigFromGet.getAllowedDeviceTypes();
    assertEquals(2, allowedDeviceTypes.size());
    Set<Integer> deviceTypeIds =
        allowedDeviceTypes.stream()
            .map(BidderDeviceType::getDeviceTypeId)
            .collect(Collectors.toSet());
    assertTrue(deviceTypeIds.containsAll(Set.of(3, 4)));
  }

  @Test
  void shouldThrowExceptionOnGetBidderConfigWhenBidderConfigNotFound() {
    final var buyerPid = 123L;
    final var bidderConfigPid = 123L;
    when(bidderConfigRepository.findById(bidderConfigPid)).thenReturn(Optional.empty());

    var exception =
        assertThrows(
            GenevaValidationException.class,
            () -> buyerService.getBidderConfig(buyerPid, bidderConfigPid));
    assertEquals(ServerErrorCodes.SERVER_BIDDER_CONFIG_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void shouldDeleteBidderConfigWhenBidderConfigExists() {
    final var bidderConfigPid = 123L;
    final Company dbCompany = TestObjectsFactory.createCompany(CompanyType.BUYER);
    final BidderConfig bidderConfig = createDefaultBidderConfigForBuyer(dbCompany);
    when(bidderConfigRepository.findById(bidderConfigPid)).thenReturn(Optional.of(bidderConfig));

    buyerService.deleteBidderConfig(bidderConfigPid);

    verify(bidderConfigRepository).delete(bidderConfig);
  }

  @Test
  void shouldThrowExceptionOnDeleteBidderConfigWhenBidderConfigNotFound() {
    final var bidderConfigPid = 123L;
    when(bidderConfigRepository.findById(bidderConfigPid)).thenReturn(Optional.empty());

    assertThrows(
        GenevaValidationException.class, () -> buyerService.deleteBidderConfig(bidderConfigPid));
  }

  @Test
  void shouldThrowExceptionWithInvalidAllowedContentEncodingType() {
    // given
    Company dbCompany = TestObjectsFactory.createCompany(CompanyType.BUYER);
    when(companyRepository.findById(anyLong())).thenReturn(Optional.of(dbCompany));
    BidderConfig bidderConfig = createDefaultBidderConfigForBuyer(dbCompany);
    bidderConfig.setCompanyPid(dbCompany.getPid());
    bidderConfig.setFormatType(BidderFormat.OpenRTBv2_3);
    bidderConfig.setAllowedContentEncoding("ZIP");
    Long companyPid = dbCompany.getPid();

    // when
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> buyerService.createBidderConfig(companyPid, bidderConfig));

    // then
    assertEquals(
        ServerErrorCodes.SERVER_BIDDER_CONFIG_UNKNOWN_CONTENT_ENCODING_TYPE,
        exception.getErrorCode());
  }

  @Test
  void shouldCreateWithValidAllowedContentEncodingType() {
    // given
    Company dbCompany = TestObjectsFactory.createCompany(CompanyType.BUYER);
    when(companyRepository.findById(anyLong())).thenReturn(Optional.of(dbCompany));
    BidderConfig bidderConfig = createDefaultBidderConfigForBuyer(dbCompany);
    bidderConfig.setCompanyPid(dbCompany.getPid());
    bidderConfig.setFormatType(BidderFormat.OpenRTBv2_3);
    bidderConfig.setAllowedContentEncoding("GZIP");
    when(bidderConfigService.saveFlushAndRefreshBidderConfig(bidderConfig))
        .thenReturn(bidderConfig);

    // when
    BidderConfig returnedBidderConfig =
        buyerService.createBidderConfig(dbCompany.getPid(), bidderConfig);

    // then
    assertEquals(bidderConfig, returnedBidderConfig);
    verify(bidderConfigService).saveFlushAndRefreshBidderConfig(bidderConfig);
  }

  @Test
  void shouldCreateWithValidAllowedAndUniqContentEncodingType() {
    // given
    Company dbCompany = TestObjectsFactory.createCompany(CompanyType.BUYER);
    when(companyRepository.findById(anyLong())).thenReturn(Optional.of(dbCompany));
    BidderConfig bidderConfig = createDefaultBidderConfigForBuyer(dbCompany);
    bidderConfig.setCompanyPid(dbCompany.getPid());
    bidderConfig.setFormatType(BidderFormat.OpenRTBv2_3);
    bidderConfig.setAllowedContentEncoding("gzip,GZIP");
    when(bidderConfigService.saveFlushAndRefreshBidderConfig(bidderConfig))
        .thenReturn(bidderConfig);

    // when
    BidderConfig returnedBidderConfig =
        buyerService.createBidderConfig(dbCompany.getPid(), bidderConfig);

    // then
    assertEquals("GZIP", returnedBidderConfig.getAllowedContentEncoding());
    verify(bidderConfigService).saveFlushAndRefreshBidderConfig(bidderConfig);
  }

  @Test
  void shouldUpdateWithValidAllowedContentEncodingType() {
    // given
    String bidderAlias = "bidder-alias";
    Company dbCompany = TestObjectsFactory.createCompany(CompanyType.BUYER);
    ExternalDataProvider edp = new ExternalDataProvider(100L);

    BidderConfig bidderConfig = createDefaultBidderConfigForBuyer(dbCompany);
    bidderConfig.setPid(1L);
    bidderConfig.setCompanyPid(dbCompany.getPid());
    bidderConfig.setFormatType(BidderFormat.OpenRTBv2_3);
    bidderConfig.setExchangeRegionals(Set.of(new ExchangeRegional()));
    bidderConfig.setAllowedContentEncoding("GZIP");
    bidderConfig.setSubscriptions(
        Set.of(
            BidderConfig.SubscriptionInfo.builder()
                .dataProviderPid(edp.getPid())
                .requiresDataToBid(true)
                .bidderAlias(bidderAlias)
                .build()));

    when(bidderConfigRepository.findById(bidderConfig.getPid()))
        .thenReturn(Optional.of(bidderConfig));
    when(companyRepository.findById(anyLong())).thenReturn(Optional.of(dbCompany));
    when(externalDataProviderRepository.findAll()).thenReturn(List.of(edp));
    when(bidderConfigService.saveFlushAndRefreshBidderConfig(bidderConfig))
        .thenReturn(bidderConfig);

    // when
    BidderConfig returnedBidderConfig =
        buyerService.updateBidderConfig(bidderConfig.getPid(), bidderConfig, dbCompany.getPid());

    // then
    BidderSubscription bs = returnedBidderConfig.getBidderSubscriptions().iterator().next();
    assertEquals(bidderConfig, returnedBidderConfig);
    assertEquals("GZIP", returnedBidderConfig.getAllowedContentEncoding());
    verify(bidderConfigService).saveFlushAndRefreshBidderConfig(bidderConfig);
  }

  @Test
  void shouldThrowExceptionWhileUpdatingBidderConfigWithInvalidContentEncoding() {
    // given
    Company dbCompany = TestObjectsFactory.createCompany(CompanyType.BUYER);
    long companyPid = dbCompany.getPid();
    BidderConfig bidderConfig = createDefaultBidderConfigForBuyer(dbCompany);
    long bidderConfigPid = 1L;
    bidderConfig.setPid(bidderConfigPid);
    bidderConfig.setCompanyPid(dbCompany.getPid());
    bidderConfig.setFormatType(BidderFormat.OpenRTBv2_3);
    bidderConfig.setAllowedContentEncoding("GZIP,BZ");

    // throws exception when
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> buyerService.updateBidderConfig(bidderConfigPid, bidderConfig, companyPid));

    // then
    assertEquals(
        ServerErrorCodes.SERVER_BIDDER_CONFIG_UNKNOWN_CONTENT_ENCODING_TYPE,
        exception.getErrorCode());
  }

  @Test
  void shouldCreateBidderConfigWithAllowedDeviceTypes() {
    // given
    Company dbCompany = TestObjectsFactory.createCompany(CompanyType.BUYER);
    when(companyRepository.findById(anyLong())).thenReturn(Optional.of(dbCompany));
    BidderConfig bidderConfig = createDefaultBidderConfigForBuyer(dbCompany);
    bidderConfig.setCompanyPid(dbCompany.getPid());
    bidderConfig.setFormatType(BidderFormat.OpenRTBv2_3);
    BidderDeviceType bidderDeviceType = initializeBidderDeviceType(bidderConfig, 2);
    BidderDeviceType bidderDeviceType2 = initializeBidderDeviceType(bidderConfig, 4);
    bidderConfig.setAllowedDeviceTypes(Set.of(bidderDeviceType, bidderDeviceType2));
    when(bidderConfigService.saveFlushAndRefreshBidderConfig(bidderConfig))
        .thenReturn(bidderConfig);

    // when
    BidderConfig returnedBidderConfig =
        buyerService.createBidderConfig(dbCompany.getPid(), bidderConfig);

    // then
    assertEquals(bidderConfig, returnedBidderConfig);
    verify(bidderConfigService).saveFlushAndRefreshBidderConfig(bidderConfig);
  }

  @Test
  void shouldUpdateBidderConfigWithAllowedDeviceTypesAndBidderSubscriptions() {
    // given
    String bidderAlias = "bidder-alias";
    Company dbCompany = TestObjectsFactory.createCompany(CompanyType.BUYER);
    ExternalDataProvider edp = new ExternalDataProvider(100L);

    BidderConfig bidderConfig = createDefaultBidderConfigForBuyer(dbCompany);
    bidderConfig.setPid(1L);
    bidderConfig.setCompanyPid(dbCompany.getPid());
    bidderConfig.setFormatType(BidderFormat.OpenRTBv2_3);
    bidderConfig.setExchangeRegionals(Set.of(new ExchangeRegional()));
    BidderDeviceType bidderDeviceType = initializeBidderDeviceType(bidderConfig, 4);
    BidderDeviceType bidderDeviceType2 = initializeBidderDeviceType(bidderConfig, 6);
    bidderConfig.setAllowedDeviceTypes(Set.of(bidderDeviceType, bidderDeviceType2));
    bidderConfig.setSubscriptions(
        Set.of(
            BidderConfig.SubscriptionInfo.builder()
                .dataProviderPid(edp.getPid())
                .requiresDataToBid(true)
                .bidderAlias(bidderAlias)
                .build()));

    when(bidderConfigRepository.findById(bidderConfig.getPid()))
        .thenReturn(Optional.of(bidderConfig));
    when(companyRepository.findById(anyLong())).thenReturn(Optional.of(dbCompany));
    when(externalDataProviderRepository.findAll()).thenReturn(List.of(edp));
    when(deviceTypeRepository.existsById(4)).thenReturn(true);
    when(deviceTypeRepository.existsById(6)).thenReturn(true);
    when(bidderConfigService.saveFlushAndRefreshBidderConfig(bidderConfig))
        .thenReturn(bidderConfig);

    // when
    BidderConfig returnedBidderConfig =
        buyerService.updateBidderConfig(bidderConfig.getPid(), bidderConfig, dbCompany.getPid());

    // then
    BidderSubscription bs = returnedBidderConfig.getBidderSubscriptions().iterator().next();
    assertEquals(bidderConfig, returnedBidderConfig);
    assertEquals(1, bidderConfig.getBidderSubscriptions().size());
    assertEquals(edp, bs.getExternalDataProvider());
    assertEquals(bidderAlias, bs.getBidderAlias());
    assertTrue(bs.isRequiresDataToBid());
    verify(bidderConfigService).saveFlushAndRefreshBidderConfig(bidderConfig);
    verify(externalDataProviderRepository).findAll();
  }

  @Test
  void shouldGetAllBuyerSeats() {
    Company company = TestObjectsFactory.createCompany(CompanyType.BUYER);
    BuyerGroup buyerGroup = TestObjectsFactory.createBuyerGroup(company, "VND", "ARG");

    List<BuyerSeat> returnFromDatabase = new ArrayList<>();
    List<BuyerSeatDTO> buyerSeatDtos = new ArrayList<>();

    BuyerSeat seat1 =
        TestObjectsFactory.createBuyerSeat("seat-1", "name-1", true, buyerGroup, company, true, 1L);
    returnFromDatabase.add(seat1);
    buyerSeatDtos.add(new BuyerSeatDTO(seat1));

    BuyerSeat seat2 =
        TestObjectsFactory.createBuyerSeat("seat-2", "name-2", true, buyerGroup, company, true, 1L);
    returnFromDatabase.add(seat2);
    buyerSeatDtos.add(new BuyerSeatDTO(seat2));

    when(buyerSeatRepository.findAll(any(Specification.class))).thenReturn(returnFromDatabase);
    when(buyerSeatAssembler.make(seat1)).thenReturn(buyerSeatDtos.get(0));
    when(buyerSeatAssembler.make(seat2)).thenReturn(buyerSeatDtos.get(1));

    assertEquals(
        buyerSeatDtos,
        buyerService.getAllBuyerSeatsForCompanyAndName(company.getPid(), null, null, null));
  }

  @Test
  void shouldCreateBuyerSeat() {
    // given
    var companyPid = 1L;
    var name = "Test123";
    var buyerSeatDTO =
        new BuyerSeatDTO() {
          {
            setName(name);
          }
        };
    var buyerSeat = new BuyerSeat();
    when(buyerSeatAssembler.transientEntity(companyPid, buyerSeatDTO)).thenReturn(buyerSeat);
    when(buyerSeatRepository.save(any())).thenReturn(buyerSeat);
    when(buyerSeatAssembler.make(buyerSeat)).thenReturn(buyerSeatDTO);

    // when
    buyerService.createBuyerSeat(companyPid, buyerSeatDTO);

    // then
    verify(buyerSeatAssembler).transientEntity(companyPid, buyerSeatDTO);
    verify(buyerSeatRepository).save(buyerSeat);
    verify(buyerSeatAssembler).make(buyerSeat);
  }

  @Test
  void shouldUpdateBuyerSeat() {
    // given
    var companyPid = 1L;
    var seatPid = 2L;
    var version = 123;
    var seatDTO =
        new BuyerSeatDTO() {
          {
            setPid(seatPid);
            setVersion(version);
          }
        };
    var seat =
        new BuyerSeat() {
          {
            setPid(seatPid);
            setCompany(
                new Company() {
                  {
                    setPid(companyPid);
                  }
                });
            setVersion(version);
          }
        };
    when(buyerSeatRepository.findById(seatPid)).thenReturn(Optional.of(seat));
    when(buyerSeatAssembler.apply(seat, seatDTO)).thenReturn(seat);
    when(buyerSeatRepository.saveAndFlush(seat)).thenReturn(seat);
    when(buyerSeatAssembler.make(seat)).thenReturn(seatDTO);

    // when
    buyerService.updateBuyerSeat(companyPid, seatPid, seatDTO);

    // then
    verify(buyerSeatRepository).findById(seatPid);
    verify(buyerSeatAssembler).apply(seat, seatDTO);
    verify(buyerSeatRepository).saveAndFlush(seat);
    verify(buyerSeatAssembler).make(seat);
  }

  @Test
  void shouldGetAllExchangeProductions() {
    // given
    List<ExchangeProduction> exchangeProductions =
        List.of(new ExchangeProduction(1, 123, "ex1"), new ExchangeProduction(2, 234, "ex2"));
    when(exchangeProductionRepository.findAll()).thenReturn(exchangeProductions);

    // when
    List<ExchangeProduction> returnedExchangeProductions = buyerService.getAllExchangeProductions();

    // then
    assertEquals(exchangeProductions, returnedExchangeProductions);
  }

  private BidderDeviceType initializeBidderDeviceType(
      BidderConfig bidderConfig, Integer deviceType) {
    BidderDeviceType bidderDeviceType = new BidderDeviceType();
    bidderDeviceType.setDeviceTypeId(deviceType);
    bidderDeviceType.setBidderConfig(bidderConfig);
    return bidderDeviceType;
  }

  @Test
  void shouldGetBuyerSeatsByCallingFindByCompanyPidAndNameContainingIgnoreCase() {
    Company company = TestObjectsFactory.createCompany(CompanyType.BUYER);
    BuyerGroup buyerGroup = TestObjectsFactory.createBuyerGroup(company, "VND", "ARG");

    List<BuyerSeat> returnFromDatabase = new ArrayList<>();
    List<BuyerSeatDTO> buyerSeatDtos = new ArrayList<>();

    BuyerSeat seat1 =
        TestObjectsFactory.createBuyerSeat("seat-1", "name-1", true, buyerGroup, company, true, 1L);
    returnFromDatabase.add(seat1);
    buyerSeatDtos.add(new BuyerSeatDTO(seat1));

    Set<String> qf = Set.of("name");
    when(buyerSeatRepository.findAll(any(Specification.class))).thenReturn(returnFromDatabase);
    when(buyerSeatAssembler.make(seat1)).thenReturn(buyerSeatDtos.get(0));

    assertEquals(
        buyerSeatDtos,
        buyerService.getAllBuyerSeatsForCompanyAndName(
            company.getPid(), null, qf, seat1.getName()));
  }

  @Test
  void shouldGetAllExchangeRegions() {
    // given
    List<ExchangeRegional> exchangeRegionals =
        List.of(
            new ExchangeRegional(1L, "id1"),
            new ExchangeRegional(2L, "id2"),
            new ExchangeRegional(3L, "id3"));
    when(exchangeRegionalRepository.findAll()).thenReturn(exchangeRegionals);

    // when
    List<ExchangeRegional> returnedExchangeRegionals = buyerService.getAllExchangeRegions();

    // then
    assertEquals(exchangeRegionals, returnedExchangeRegionals);
  }

  @Test
  void shouldGetAllBidderSummaries() {
    // given
    List<BidderSummaryDTO> bidderSummaryDTOs =
        List.of(
            new BidderSummaryDTO(1L, "summary1"),
            new BidderSummaryDTO(2L, "summary2"),
            new BidderSummaryDTO(3L, "summary3"));
    when(bidderConfigService.getBidderSummaries()).thenReturn(bidderSummaryDTOs);

    // when
    List<BidderSummaryDTO> returnedBidderSummaryDTOs = buyerService.getAllBidderSummaries();

    // then
    assertEquals(bidderSummaryDTOs, returnedBidderSummaryDTOs);
  }

  @Test
  void shouldThrowExceptionWhenCompanyPidDoesNotBelongToRequestedBidderConfigOnGetBidderConfig() {
    // given
    var buyerPid = 1L;
    var otherBuyerPid = 2L;
    var bidderConfigPid = 3L;
    var bidderConfig = new BidderConfig();
    bidderConfig.setCompanyPid(otherBuyerPid);
    when(bidderConfigRepository.findById(bidderConfigPid)).thenReturn(Optional.of(bidderConfig));

    // throws exception when
    assertThrows(
        GenevaValidationException.class,
        () -> buyerService.getBidderConfig(buyerPid, bidderConfigPid));
  }

  @Test
  void shouldThrowExceptionOnAdSourcePidMismatch() {
    // given
    long adSourcePid = 1L;
    long mismatchedAdSourcePid = 2L;
    long companyPid = 1L;

    AdSource inputAdSource = new AdSource();
    inputAdSource.setPid(adSourcePid);

    // when
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> buyerService.updateAdSource(companyPid, inputAdSource, mismatchedAdSourcePid));

    // then
    assertEquals(ServerErrorCodes.SERVER_AD_SOURCE_ID_INVALID, exception.getErrorCode());
  }

  @Test
  void shouldSetSmartQpsFieldsToDbValuesWhenMissingInUpdatedConfig() {
    // given
    Company company = TestObjectsFactory.createCompany(CompanyType.BUYER);

    BidderConfig updatedBidderConfig = createDefaultBidderConfigForBuyer(company);
    updatedBidderConfig.setPid(1L);
    updatedBidderConfig.setCompanyPid(company.getPid());
    updatedBidderConfig.setFormatType(BidderFormat.OpenRTBv2_3);
    updatedBidderConfig.setExchangeRegionals(Set.of(new ExchangeRegional()));

    BidderConfig existingBidderConfig = createDefaultBidderConfigForBuyer(company);
    existingBidderConfig.setPid(1L);
    existingBidderConfig.setCompanyPid(company.getPid());
    existingBidderConfig.setFormatType(BidderFormat.OpenRTBv2_3);
    existingBidderConfig.setSmartQpsOutboundEnabled(true);
    existingBidderConfig.setThrottleRate(0.3);

    when(bidderConfigRepository.findById(updatedBidderConfig.getPid()))
        .thenReturn(Optional.of(existingBidderConfig));
    when(companyRepository.findById(anyLong())).thenReturn(Optional.of(company));
    when(bidderConfigService.saveFlushAndRefreshBidderConfig(updatedBidderConfig))
        .thenReturn(updatedBidderConfig);

    // when
    BidderConfig returnedBidderConfig =
        buyerService.updateBidderConfig(
            updatedBidderConfig.getPid(), updatedBidderConfig, company.getPid());

    // then
    assertEquals(updatedBidderConfig, returnedBidderConfig);
    assertTrue(returnedBidderConfig.getSmartQpsOutboundEnabled());
    assertEquals(0.3, returnedBidderConfig.getThrottleRate());
    verify(bidderConfigService).saveFlushAndRefreshBidderConfig(updatedBidderConfig);
  }

  @ParameterizedTest
  @MethodSource("getInvalidTrafficFilterConfig")
  void shouldThrowExceptionWhenCreatingBidderConfigWithInvalidTrafficFilterConfig(
      Boolean smartQpsOutboundEnabled,
      Double throttleRate,
      Integer limit,
      ServerErrorCodes expectedErrorCode) {
    // given
    Company company = TestObjectsFactory.createCompany(CompanyType.BUYER);
    when(companyRepository.findById(anyLong())).thenReturn(Optional.of(company));
    Long companyPid = company.getPid();

    BidderConfig bidderConfig = createDefaultBidderConfigForBuyer(company);
    bidderConfig.setFormatType(BidderFormat.OpenRTBv2_3);
    bidderConfig.setSmartQpsOutboundEnabled(smartQpsOutboundEnabled);
    bidderConfig.setThrottleRate(throttleRate);
    bidderConfig.setRequestRateFilter(limit);

    // throws exception when
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> buyerService.createBidderConfig(companyPid, bidderConfig));

    // then
    assertEquals(expectedErrorCode, exception.getErrorCode());
  }

  @ParameterizedTest
  @MethodSource("getValidTrafficFilterConfig")
  void shouldCreateBidderConfigWhenTrafficFilterConfigIsValid(
      Boolean smartQpsOutboundEnabled, Double throttleRate, Integer limit) {
    Company company = TestObjectsFactory.createCompany(CompanyType.BUYER);
    when(companyRepository.findById(anyLong())).thenReturn(Optional.of(company));

    BidderConfig bidderConfig = createDefaultBidderConfigForBuyer(company);
    bidderConfig.setFormatType(BidderFormat.OpenRTBv2_3);
    bidderConfig.setSmartQpsOutboundEnabled(smartQpsOutboundEnabled);
    bidderConfig.setThrottleRate(throttleRate);
    bidderConfig.setRequestRateFilter(limit);
    when(bidderConfigService.saveFlushAndRefreshBidderConfig(bidderConfig))
        .thenReturn(bidderConfig);

    // when
    BidderConfig createdBidderConfig =
        buyerService.createBidderConfig(company.getPid(), bidderConfig);

    // then
    assertEquals(smartQpsOutboundEnabled, createdBidderConfig.getSmartQpsOutboundEnabled());
    assertEquals(throttleRate, createdBidderConfig.getThrottleRate());
    assertEquals(limit, createdBidderConfig.getRequestRateFilter());
  }

  @ParameterizedTest
  @MethodSource("getInvalidTrafficFilterConfig")
  void shouldThrowExceptionWhenUpdatingBidderConfigWithInvalidTrafficFilterConfig(
      Boolean smartQpsOutboundEnabled,
      Double throttleRate,
      Integer limit,
      ServerErrorCodes expectedErrorCode) {
    // given
    Company company = TestObjectsFactory.createCompany(CompanyType.BUYER);
    Long companyPid = company.getPid();

    BidderConfig updatedBidderConfig = createDefaultBidderConfigForBuyer(company);
    updatedBidderConfig.setPid(1L);
    updatedBidderConfig.setCompanyPid(companyPid);
    updatedBidderConfig.setFormatType(BidderFormat.OpenRTBv2_3);
    updatedBidderConfig.setExchangeRegionals(Set.of(new ExchangeRegional()));
    updatedBidderConfig.setSmartQpsOutboundEnabled(smartQpsOutboundEnabled);
    updatedBidderConfig.setThrottleRate(throttleRate);
    updatedBidderConfig.setRequestRateFilter(limit);

    BidderConfig existingBidderConfig = createDefaultBidderConfigForBuyer(company);
    existingBidderConfig.setPid(1L);
    existingBidderConfig.setCompanyPid(companyPid);
    existingBidderConfig.setFormatType(BidderFormat.OpenRTBv2_3);
    existingBidderConfig.setSmartQpsOutboundEnabled(false);
    existingBidderConfig.setThrottleRate(0.0);

    // throws exception when
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> buyerService.updateBidderConfig(1L, updatedBidderConfig, companyPid));

    // then
    assertEquals(expectedErrorCode, exception.getErrorCode());
  }

  @ParameterizedTest
  @MethodSource("getValidTrafficFilterConfig")
  void shouldUpdateBidderConfigWhenTrafficFilterConfigIsValid(
      Boolean smartQpsOutboundEnabled, Double throttleRate, Integer limit) {
    // given
    Company company = TestObjectsFactory.createCompany(CompanyType.BUYER);

    BidderConfig updatedBidderConfig = createDefaultBidderConfigForBuyer(company);
    updatedBidderConfig.setPid(1L);
    updatedBidderConfig.setCompanyPid(company.getPid());
    updatedBidderConfig.setFormatType(BidderFormat.OpenRTBv2_3);
    updatedBidderConfig.setExchangeRegionals(Set.of(new ExchangeRegional()));
    updatedBidderConfig.setSmartQpsOutboundEnabled(smartQpsOutboundEnabled);
    updatedBidderConfig.setThrottleRate(throttleRate);
    updatedBidderConfig.setRequestRateFilter(limit);

    BidderConfig existingBidderConfig = createDefaultBidderConfigForBuyer(company);
    existingBidderConfig.setPid(1L);
    existingBidderConfig.setCompanyPid(company.getPid());
    existingBidderConfig.setFormatType(BidderFormat.OpenRTBv2_3);
    existingBidderConfig.setSmartQpsOutboundEnabled(false);
    existingBidderConfig.setThrottleRate(0.0);

    when(bidderConfigRepository.findById(updatedBidderConfig.getPid()))
        .thenReturn(Optional.of(existingBidderConfig));
    when(companyRepository.findById(anyLong())).thenReturn(Optional.of(company));
    when(bidderConfigService.saveFlushAndRefreshBidderConfig(updatedBidderConfig))
        .thenReturn(updatedBidderConfig);

    // when
    BidderConfig returnedBidderConfig =
        buyerService.updateBidderConfig(
            updatedBidderConfig.getPid(), updatedBidderConfig, company.getPid());

    // then
    boolean expectedEnabled = smartQpsOutboundEnabled != null ? smartQpsOutboundEnabled : false;
    double expectedThrottleRate = throttleRate != null ? throttleRate : 0.0;

    assertEquals(updatedBidderConfig, returnedBidderConfig);
    assertEquals(expectedEnabled, returnedBidderConfig.getSmartQpsOutboundEnabled());
    assertEquals(expectedThrottleRate, returnedBidderConfig.getThrottleRate());
    assertEquals(limit, returnedBidderConfig.getRequestRateFilter());
    verify(bidderConfigService).saveFlushAndRefreshBidderConfig(updatedBidderConfig);
  }

  private Company prepareCompany() {
    Company dbCompany = TestObjectsFactory.createCompany(CompanyType.BUYER);
    when(companyRepository.findById(anyLong())).thenReturn(Optional.of(dbCompany));

    return dbCompany;
  }

  private BidderConfig createDefaultBidderConfigForBuyer(Company company) {
    BidderConfig bidderConfig = new BidderConfig();
    bidderConfig.setCompany(company);
    bidderConfig.setTrafficStatus(false);
    bidderConfig.setAllowedTraffic(Joiner.on(",").join(EnumSet.allOf(Type.class)));

    bidderConfig.setIncludeLists(BlockListInclusion.ADAPTIVE);
    bidderConfig.setDefaultBidCurrency(company.getCurrency());
    bidderConfig.setDefaultBidUnit(0);
    bidderConfig.setRequestRateFilter(-1);
    bidderConfig.setAuctionTypeFilter(0);
    bidderConfig.setScriptAllowedFilter(false);
    bidderConfig.setVersion(1);
    bidderConfig.setVerificationType(VerificationType.STANDARD);
    bidderConfig.setBidRequestCpm(BigDecimal.ZERO);
    bidderConfig.setId((String) new UUIDGenerator().generate());
    bidderConfig.setCreationDate(new Date());
    return bidderConfig;
  }

  private static Stream<Arguments> getInvalidTrafficFilterConfig() {
    return Stream.of(
        Arguments.of(false, -3.4, -1, ServerErrorCodes.SERVER_BIDDER_CONFIG_INVALID_THROTTLE_RATE),
        Arguments.of(false, 1.2, -1, ServerErrorCodes.SERVER_BIDDER_CONFIG_INVALID_THROTTLE_RATE),
        Arguments.of(
            false, 0.75, -1, ServerErrorCodes.SERVER_BIDDER_CONFIG_THROTTLE_RATE_NOT_ALLOWED),
        Arguments.of(true, 0.0, -1, ServerErrorCodes.SERVER_BIDDER_CONFIG_INVALID_FILTER_CONFIG));
  }

  private static Stream<Arguments> getValidTrafficFilterConfig() {
    return Stream.of(
        Arguments.of(false, 0.0, -1),
        Arguments.of(true, 0.6, -1),
        Arguments.of(true, 0.75, 3000),
        Arguments.of(null, null, -1),
        Arguments.of(null, null, 1000));
  }

  private static class BuyerServiceTestImpl extends BuyerServiceImpl {
    private String logoPath;
    private byte[] image;

    public BuyerServiceTestImpl(
        AdSourceService adSourceService,
        AdSourceRepository adSourceRepository,
        BidderConfigService bidderConfigService,
        BidderConfigRepository bidderConfigRepository,
        ExchangeRegionalRepository exchangeRegionalRepository,
        ExchangeProductionRepository exchangeProductionRepository,
        FileSystemService fileSystemService,
        CompanyRepository companyRepository,
        BuyerGroupRepository buyerGroupRepository,
        BuyerSeatRepository buyerSeatRepository,
        BuyerGroupAssembler buyerGroupAssembler,
        ExternalDataProviderRepository externalDataProviderRepository,
        BuyerSeatAssembler buyerSeatAssembler,
        DeviceTypeRepository deviceTypeRepository,
        PhoneCastConfigService phoneCastConfigService,
        GlobalConfigService globalConfigService,
        EnvironmentUtil environmentUtil) {
      super(
          adSourceService,
          adSourceRepository,
          bidderConfigService,
          bidderConfigRepository,
          exchangeRegionalRepository,
          exchangeProductionRepository,
          fileSystemService,
          companyRepository,
          buyerGroupRepository,
          buyerSeatRepository,
          buyerGroupAssembler,
          externalDataProviderRepository,
          buyerSeatAssembler,
          deviceTypeRepository,
          phoneCastConfigService,
          globalConfigService,
          environmentUtil);
    }

    @Override
    void writeLogoToDisc(String logoPath, byte[] image) {
      this.logoPath = logoPath;
      this.image = image;
    }

    @Override
    void checkAndCreateLogoUploadDir() {}
  }
}
