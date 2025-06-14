package com.nexage.app.web;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.SellerAttributes;
import com.nexage.app.services.CompanyService;
import com.nexage.app.web.support.BaseControllerItTest;
import com.nexage.app.web.support.TestObjectsFactory;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.error.model.CommonErrorCodes;
import com.ssp.geneva.common.model.inventory.CompanyType;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class CompanyControllerIT extends BaseControllerItTest {

  @InjectMocks private CompanyController companyController;
  @Mock private CompanyService companyService;

  @Qualifier("objectMapper")
  @Autowired
  private ObjectMapper mapper;

  @Autowired private ControllerExceptionHandler controllerExceptionHandler;

  private static final String UPDATE_COMPANY_URL = "/companies/{companyPID}";
  private static final String GET_BUYER_COMPANY_FOO_URL = "/companies/?type=BUYER&qf=name&qt=foo";
  private static final String GET_BUYER_COMPANY_FOO_URL_WITH_ERROR =
      "/companies/?type=BUYER&qf=notName&qt=foo";

  private static final long COMPANY_PID = 10201L;

  @BeforeEach
  public void setUp() {
    this.mockMvc =
        MockMvcBuilders.standaloneSetup(companyController)
            .setControllerAdvice(controllerExceptionHandler)
            .build();
  }

  @Test
  void testGetAllBuyersCompanies() throws Exception {
    ArrayList<Company> companies =
        Lists.newArrayList(TestObjectsFactory.createCompany(CompanyType.BUYER));
    Company first = companies.get(0);
    when(companyService.getAllCompaniesByType(any(CompanyType.class), any(), any()))
        .thenReturn(companies);
    mockMvc
        .perform(get("/companies/?type=BUYER"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].pid", is(first.getPid())));
  }

  @Test
  void jacksonMarshallerTest() throws Exception {

    ArgumentCaptor<Company> companyCaptor = ArgumentCaptor.forClass(Company.class);

    String jsonContent =
        "{\n"
            + "  \"id\": \"\",\n"
            + "  \"pid\": null,\n"
            + "  \"version\": 0,\n"
            + "  \"name\": \"MX8596_newSeller\",\n"
            + "  \"type\": \"SELLER\",\n"
            + "  \"website\": \"www.aol.com\"\n"
            + "}";

    when(companyService.createCompany(any())).thenAnswer(i -> i.getArguments()[0]);

    mockMvc
        .perform(put("/companies").content(jsonContent).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    verify(companyService).createCompany(companyCaptor.capture());

    assertEquals("USD", companyCaptor.getValue().getCurrency());
  }

  @Test
  void updateSellerCompanyWithTagLimitEqualsTo6() throws Exception {
    String payload = getData(ResourcePath.UPDATE_SELLER_10201_PSS_TAG_LIMIT_PAYLOAD.getFilePath());
    Company expectedCompany = mapper.readValue(payload, Company.class);
    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerAttributes.setLimitEnabled(true);
    sellerAttributes.setTagsPerPositionLimit(6);
    expectedCompany.setSellerAttributes(sellerAttributes);
    when(companyService.updateCompany(any(Company.class))).thenReturn(expectedCompany);

    MvcResult putCompany =
        mockMvc
            .perform(
                put(UPDATE_COMPANY_URL, COMPANY_PID)
                    .content(payload)
                    .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andReturn();

    Company putCompanyResult =
        mapper.readValue(putCompany.getResponse().getContentAsString(), Company.class);
    assertEquals(putCompanyResult, expectedCompany);
    assertEquals(
        expectedCompany.getSellerAttributes().getTagsPerPositionLimit(),
        putCompanyResult.getSellerAttributes().getTagsPerPositionLimit());
    assertEquals(
        expectedCompany.getSellerAttributes().isLimitEnabled(),
        putCompanyResult.getSellerAttributes().isLimitEnabled());
  }

  @Test
  void updateSellerCompanyWithLimitEnabledFlagToFalse() throws Exception {
    String payload = getData(ResourcePath.UPDATE_SELLER_LIMIT_ENABLED_FLAG_PAYLOAD.getFilePath());
    Company expectedCompany = mapper.readValue(payload, Company.class);
    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerAttributes.setLimitEnabled(false);
    expectedCompany.setSellerAttributes(sellerAttributes);
    when(companyService.updateCompany(any(Company.class))).thenReturn(expectedCompany);

    MvcResult putCompany =
        mockMvc
            .perform(
                put(UPDATE_COMPANY_URL, COMPANY_PID)
                    .content(payload)
                    .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andReturn();
    Company putCompanyResult =
        mapper.readValue(putCompany.getResponse().getContentAsString(), Company.class);
    assertEquals(putCompanyResult, expectedCompany);
    assertEquals(
        expectedCompany.getSellerAttributes().getTagsPerPositionLimit(),
        putCompanyResult.getSellerAttributes().getTagsPerPositionLimit());
    assertEquals(
        expectedCompany.getSellerAttributes().isLimitEnabled(),
        putCompanyResult.getSellerAttributes().isLimitEnabled());
  }

  @Test
  void updateSellerCompanyWithSiteLimitEqualstoOne() throws Exception {
    String payload = getData(ResourcePath.UPDATE_SELLER_TAG_LIMIT_PAYLOAD.getFilePath());
    Company expectedCompany = mapper.readValue(payload, Company.class);
    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerAttributes.setLimitEnabled(true);
    sellerAttributes.setTagsPerPositionLimit(1);
    expectedCompany.setSellerAttributes(sellerAttributes);
    when(companyService.updateCompany(any(Company.class))).thenReturn(expectedCompany);

    MvcResult putCompany =
        mockMvc
            .perform(
                put(UPDATE_COMPANY_URL, 105)
                    .content(payload)
                    .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andReturn();

    Company putCompanyResult =
        mapper.readValue(putCompany.getResponse().getContentAsString(), Company.class);
    assertEquals(putCompanyResult, expectedCompany);
    assertEquals(
        expectedCompany.getSellerAttributes().getTagsPerPositionLimit(),
        putCompanyResult.getSellerAttributes().getTagsPerPositionLimit());
    assertEquals(
        expectedCompany.getSellerAttributes().isLimitEnabled(),
        putCompanyResult.getSellerAttributes().isLimitEnabled());
  }

  private String getData(String name) throws IOException {
    return Resources.toString(
        Resources.getResource(com.nexage.app.web.CompanyControllerIT.class, name),
        Charset.forName("UTF-8"));
  }

  @Test
  @SneakyThrows
  void shouldGetLikeCompaniesWhenSearchingByName() {
    List<Company> company = createCompany();

    when(companyService.getAllCompaniesByType(any(CompanyType.class), any(), any()))
        .thenReturn(company);

    mockMvc
        .perform(get(GET_BUYER_COMPANY_FOO_URL))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].pid", is(company.get(0).getPid())))
        .andExpect(jsonPath("$[0].name", is(company.get(0).getName())));
  }

  @Test
  @SneakyThrows
  void shouldNotGetLikeCompaniesWhenSearchingQueryFieldNotName() {
    when(companyService.getAllCompaniesByType(any(), any(), any()))
        .thenThrow(new GenevaValidationException(CommonErrorCodes.COMMON_BAD_REQUEST));
    mockMvc
        .perform(get(GET_BUYER_COMPANY_FOO_URL_WITH_ERROR))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode", is(CommonErrorCodes.COMMON_BAD_REQUEST.getCode())))
        .andExpect(
            jsonPath(
                "$.errorMessage",
                is(
                    controllerExceptionHandler.getErrorMessage(
                        CommonErrorCodes.COMMON_BAD_REQUEST))));
  }

  private List<Company> createCompany() {
    Company companyOne = new Company();
    companyOne.setName("foo");
    companyOne.setType(CompanyType.BUYER);
    return List.of(companyOne);
  }

  @Getter
  enum ResourcePath {
    UPDATE_SELLER_10201_PSS_TAG_LIMIT_PAYLOAD(
        "/data/seller_controller/payload/update/updateSeller10201TagLimit_payload.json"),
    UPDATE_SELLER_TAG_LIMIT_PAYLOAD(
        "/data/seller_controller/payload/update/updateSellerTagLimit_payload.json"),
    UPDATE_SELLER_LIMIT_ENABLED_FLAG_PAYLOAD(
        "/data/seller_controller/payload/update/updateSellerLimitEnabledFlag_payload.json");
    private String filePath;

    ResourcePath(String filePath) {
      this.filePath = filePath;
    }
  }
}
