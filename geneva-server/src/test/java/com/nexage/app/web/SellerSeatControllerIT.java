package com.nexage.app.web;

import static com.nexage.admin.core.model.SellerSeat.DISABLED;
import static com.nexage.admin.core.model.SellerSeat.ENABLED;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.nexage.app.dto.SellerSeatDTO;
import com.nexage.app.dto.user.CompanyViewDTO;
import com.nexage.app.error.EntityConstraintViolationException;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.services.BeanValidationService;
import com.nexage.app.services.SellerSeatService;
import com.nexage.app.web.support.TestObjectsFactory;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.model.inventory.CompanyType;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:application-context-test.xml"})
@WebAppConfiguration
class SellerSeatControllerIT {

  private static final String BASE_URL = "/v1/seller-seats";
  private static final long DEFAULT_ID = 12345L;
  private static final SellerSeatDTO DEFAULT_SELLER_SEAT =
      TestObjectsFactory.createSellerSeatDTO(DEFAULT_ID, ENABLED);

  private MockMvc mockMvc;

  @Mock private SellerSeatService sellerSeatService;
  @Mock private BeanValidationService beanValidationService;
  @InjectMocks private SellerSeatController sellerSeatController;

  @Autowired private ControllerExceptionHandler controllerExceptionHandler;

  @BeforeEach
  public void setUp() {
    this.mockMvc =
        MockMvcBuilders.standaloneSetup(sellerSeatController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setViewResolvers((viewName, locale) -> new MappingJackson2JsonView())
            .setControllerAdvice(controllerExceptionHandler)
            .build();
  }

  @Test
  void shouldFetchSellerSeatById() throws Exception {
    given(sellerSeatService.getSellerSeat(DEFAULT_ID)).willReturn(DEFAULT_SELLER_SEAT);
    mockMvc
        .perform(get(BASE_URL + "/{pid}", DEFAULT_ID))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("pid", is(DEFAULT_SELLER_SEAT.getPid().intValue())))
        .andExpect(jsonPath("name", is(DEFAULT_SELLER_SEAT.getName())));
  }

  @Test
  void shouldFailToFetchSellerSeatByInvalidId() throws Exception {
    long nonExistingId = 111L;
    given(sellerSeatService.getSellerSeat(nonExistingId))
        .willThrow(new GenevaValidationException(ServerErrorCodes.SERVER_SELLER_SEAT_NOT_FOUND));
    mockMvc
        .perform(get(BASE_URL + "/{pid}", nonExistingId))
        .andExpect(status().isNotFound())
        .andExpect(
            jsonPath(
                "$.errorMessage",
                is(
                    controllerExceptionHandler.getErrorMessage(
                        ServerErrorCodes.SERVER_SELLER_SEAT_NOT_FOUND))));
  }

  @Test
  void shouldUpdateSellerSeatById() throws Exception {
    SellerSeatDTO dto = TestObjectsFactory.createSellerSeatDTO(DEFAULT_ID, Boolean.FALSE);

    given(sellerSeatService.updateSellerSeat(DEFAULT_ID, DEFAULT_SELLER_SEAT)).willReturn(dto);

    MockHttpServletRequestBuilder request =
        put(BASE_URL.concat("/{sellerSeatPid}"), DEFAULT_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .content(dtoToJsonString(DEFAULT_SELLER_SEAT));
    mockMvc
        .perform(request)
        .andExpect(status().isOk())
        .andExpect(jsonPath("pid", is((int) DEFAULT_ID)))
        .andExpect(jsonPath("name", is(dto.getName())))
        .andExpect(jsonPath("status", is(dto.getStatus())))
        .andExpect(jsonPath("description", is(dto.getDescription())))
        .andExpect(jsonPath("version", is(dto.getVersion())));
  }

  @Test
  void shouldThrowAnExceptionWhenPidsAreNotCorresponding() throws Exception {
    SellerSeatDTO dto = TestObjectsFactory.createSellerSeatDTO(DEFAULT_ID, DISABLED);

    given(sellerSeatService.updateSellerSeat(DEFAULT_ID, DEFAULT_SELLER_SEAT)).willReturn(dto);

    long someOtherPid = 4342; // other then the one in json representation
    MockHttpServletRequestBuilder request =
        put(BASE_URL.concat("/{sellerSeatPid}"), someOtherPid)
            .contentType(MediaType.APPLICATION_JSON)
            .content(dtoToJsonString(DEFAULT_SELLER_SEAT));
    mockMvc.perform(request).andExpect(status().isNotFound());
  }

  @Test
  void shouldCreateSeller() throws Exception {
    SellerSeatDTO dto = TestObjectsFactory.createSellerSeatDTO(321L, ENABLED);

    given(sellerSeatService.createSellerSeat(dto)).willReturn(dto);

    MockHttpServletRequestBuilder request =
        post(BASE_URL).contentType(MediaType.APPLICATION_JSON).content(dtoToJsonString(dto));
    mockMvc
        .perform(request)
        .andExpect(status().isOk())
        .andExpect(jsonPath("pid").exists())
        .andExpect(jsonPath("pid", is(321)))
        .andExpect(jsonPath("name", is(dto.getName())))
        .andExpect(jsonPath("status", is(dto.getStatus())))
        .andExpect(jsonPath("description", is(dto.getDescription())))
        .andExpect(jsonPath("version", is(dto.getVersion())));
  }

  @Test
  void createSellerSeatNameIsNull() throws Exception {
    SellerSeatDTO dto = TestObjectsFactory.createSellerSeatDTO(DEFAULT_ID, DISABLED);
    dto.setName(null);
    verifyError(dto, HttpMethod.POST);
  }

  @Test
  void createSellerSeatVersionIsNull() throws Exception {
    SellerSeatDTO dto = TestObjectsFactory.createSellerSeatDTO(DEFAULT_ID, DISABLED);
    dto.setVersion(null);
    verifyError(dto, HttpMethod.POST);
  }

  @Test
  void createSellerSeatStatusIsNull() throws Exception {
    SellerSeatDTO dto = TestObjectsFactory.createSellerSeatDTO(DEFAULT_ID, DISABLED);
    dto.setStatus(null);
    verifyError(dto, HttpMethod.POST);
  }

  @Test
  void updateSellerSeatNameIsNull() throws Exception {
    SellerSeatDTO dto = TestObjectsFactory.createSellerSeatDTO(DEFAULT_ID, DISABLED);
    dto.setName(null);
    verifyError(dto, HttpMethod.PUT);
  }

  @Test
  void updateSellerSeatVersionIsNull() throws Exception {
    SellerSeatDTO dto = TestObjectsFactory.createSellerSeatDTO(DEFAULT_ID, DISABLED);
    dto.setVersion(null);
    verifyError(dto, HttpMethod.PUT);
  }

  @Test
  void updateSellerSeatStatusIsNull() throws Exception {
    SellerSeatDTO dto = TestObjectsFactory.createSellerSeatDTO(DEFAULT_ID, DISABLED);
    dto.setStatus(null);
    verifyError(dto, HttpMethod.PUT);
  }

  @Test
  void shouldGetAllSellerSeats() throws Exception {
    SellerSeatDTO secondSellerSeat = createSecondSellerSeat();
    List<SellerSeatDTO> sellerSeatsList = Lists.newArrayList(DEFAULT_SELLER_SEAT, secondSellerSeat);
    Page<SellerSeatDTO> sellerSeats = new PageImpl<>(sellerSeatsList);

    given(sellerSeatService.findAll(eq(false), any(), any(), any())).willReturn(sellerSeats);
    mockMvc
        .perform(get(BASE_URL))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("content.[0].pid", is(DEFAULT_SELLER_SEAT.getPid().intValue())))
        .andExpect(jsonPath("content.[0].name", is(DEFAULT_SELLER_SEAT.getName())))
        .andExpect(jsonPath("content.[1].pid", is(secondSellerSeat.getPid().intValue())))
        .andExpect(jsonPath("content.[1].name", is(secondSellerSeat.getName())));
  }

  @Test
  void shouldGetAllNotAssignableSellerSeats() throws Exception {
    List<SellerSeatDTO> sellerSeatsList = Lists.newArrayList(DEFAULT_SELLER_SEAT);
    Page<SellerSeatDTO> sellerSeats = new PageImpl<>(sellerSeatsList);

    given(sellerSeatService.findAll(eq(false), any(), any(), any())).willReturn(sellerSeats);
    mockMvc
        .perform(get(BASE_URL.concat("?assignable=false")))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("content.[0].pid", is(DEFAULT_SELLER_SEAT.getPid().intValue())))
        .andExpect(jsonPath("content.[0].name", is(DEFAULT_SELLER_SEAT.getName())));
  }

  @Test
  void shouldGetAllAssignableSellerSeats() throws Exception {
    DEFAULT_SELLER_SEAT.setSellers(
        Collections.singleton(new CompanyViewDTO(1L, "test", CompanyType.SELLER, false)));
    List<SellerSeatDTO> sellerSeatsList = Lists.newArrayList(DEFAULT_SELLER_SEAT);
    Page<SellerSeatDTO> sellerSeats = new PageImpl<>(sellerSeatsList);

    given(sellerSeatService.findAll(eq(true), any(), any(), any())).willReturn(sellerSeats);
    mockMvc
        .perform(get(BASE_URL.concat("?assignable=true")))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("content.[0].pid", is(DEFAULT_SELLER_SEAT.getPid().intValue())))
        .andExpect(jsonPath("content.[0].name", is(DEFAULT_SELLER_SEAT.getName())));
  }

  @Test
  void shouldGetLikeSellerSeatsWhenSearchingByName() throws Exception {
    SellerSeatDTO secondSellerSeat = createSecondSellerSeat();
    List<SellerSeatDTO> sellerSeatsList = Lists.newArrayList(DEFAULT_SELLER_SEAT, secondSellerSeat);

    Page<SellerSeatDTO> sellerSeats = new PageImpl<>(sellerSeatsList);

    given(sellerSeatService.findAll(eq(true), any(), any(), any())).willReturn(sellerSeats);

    mockMvc
        .perform(
            get("/v1/seller-seats?assignable=true&page=0&size=1000&qf=name&qt=Seat Name")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("content.[0].pid", is(DEFAULT_SELLER_SEAT.getPid().intValue())))
        .andExpect(jsonPath("content.[0].name", is(DEFAULT_SELLER_SEAT.getName())))
        .andExpect(jsonPath("content.[1].pid", is(secondSellerSeat.getPid().intValue())))
        .andExpect(jsonPath("content.[1].name", is(secondSellerSeat.getName())));
  }

  private void verifyError(SellerSeatDTO dto, HttpMethod httpMethod) throws Exception {
    MockHttpServletRequestBuilder request = MockMvcRequestBuilders.request(httpMethod, BASE_URL);
    doThrow(EntityConstraintViolationException.class).when(beanValidationService).validate(any());
    if (HttpMethod.PUT.equals(httpMethod)) {
      request =
          MockMvcRequestBuilders.request(
              httpMethod, BASE_URL.concat("/{sellerSeatPid}"), DEFAULT_ID);
    }
    mockMvc
        .perform(
            request
                .content(dtoToJsonString(dto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  private String dtoToJsonString(SellerSeatDTO dto) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.writeValueAsString(dto);
  }

  private SellerSeatDTO createSecondSellerSeat() {
    return new SellerSeatDTO() {
      {
        this.setPid(123L);
        this.setName("Second Seller Seat Name");
        this.setDescription("Second Seller Seat Description");
      }
    };
  }
}
