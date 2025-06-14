package com.nexage.app.web.publisher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexage.admin.core.enums.TagType;
import com.nexage.app.dto.Status;
import com.nexage.app.dto.publisher.PublisherDTO;
import com.nexage.app.dto.publisher.PublisherTagDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.security.UserContext;
import com.nexage.app.services.PublisherSelfService;
import com.nexage.app.services.SellerLimitService;
import com.nexage.app.web.ControllerExceptionHandler;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.security.model.SpringUserDetails;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:application-context-test.xml"})
@WebAppConfiguration
class TagPerPlacementRateLimitIT {

  private static final String GET_TAG_URL =
      "/pss/{publisher}/site/{site}/position/{position}/tag/{tag}";
  private static final String CREATE_TAG_URL =
      "/pss/{publisher}/site/{site}/position/{position}/tag";
  private static final String UPDATE_TAG_URL =
      "/pss/{publisher}/site/{site}/position/{position}/tag/{tag}";
  private static final String UPDATE_PUBLISHER_URL = "/pss/{publisher}";

  private static final long PUBLISHER_PID = 10201;
  private static final long SITE_PID = 10000174;
  private static final long POSITION_PID = 100247;
  private static final long TAG_PID = 105;

  private MockMvc mockMvc;

  @Autowired private ControllerExceptionHandler controllerExceptionHandler;

  @Autowired
  @Qualifier("customViewLayerObjectMapper")
  private ObjectMapper mapper;

  @Mock private PublisherSelfService publisherSelfService;
  @Mock private UserContext userContext;
  @Mock private SellerLimitService sellerLimitService;
  @Mock private SpringUserDetails springUserDetails;
  @InjectMocks private PublisherSelfServeController publisherSelfServeController;

  @BeforeEach
  public void setUp() {
    given(userContext.getCurrentUser()).willReturn(springUserDetails);
    this.mockMvc =
        MockMvcBuilders.standaloneSetup(publisherSelfServeController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setViewResolvers((viewName, locale) -> new MappingJackson2JsonView())
            .setControllerAdvice(controllerExceptionHandler)
            .build();
  }

  @Test
  @SneakyThrows
  void shouldFetchTag() {
    // given
    PublisherTagDTO expectedTag = getTag();
    String expectedTagJson = mapper.writeValueAsString(expectedTag);

    given(publisherSelfService.getTag(anyLong(), anyLong(), anyLong(), anyLong()))
        .willReturn(expectedTag);

    // when
    MvcResult result =
        mockMvc
            .perform(get(GET_TAG_URL, PUBLISHER_PID, SITE_PID, POSITION_PID, TAG_PID))
            .andExpect(status().isOk())
            .andReturn();

    // then
    assertEquals(expectedTagJson, result.getResponse().getContentAsString());
  }

  @Test
  @SneakyThrows
  void shouldCreateTag() {
    // given
    String payload = mapper.writeValueAsString(getNewTag());

    PublisherTagDTO expectedTag = getTag();
    String expectedJson = mapper.writeValueAsString(expectedTag);

    when(sellerLimitService.canCreateTagsInPosition(PUBLISHER_PID, SITE_PID, POSITION_PID))
        .thenReturn(true);

    given(
            publisherSelfService.createTag(
                eq(PUBLISHER_PID), eq(SITE_PID), eq(POSITION_PID), any(PublisherTagDTO.class)))
        .willReturn(expectedTag);

    // when
    MvcResult result =
        mockMvc
            .perform(
                post(CREATE_TAG_URL, PUBLISHER_PID, SITE_PID, POSITION_PID)
                    .content(payload)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andReturn();

    // then
    assertEquals(expectedJson, result.getResponse().getContentAsString());
  }

  @Test
  @SneakyThrows
  void shouldThrowBadRequestStatusWhenCreatingTagThrows() {
    // given
    given(
            publisherSelfService.createTag(
                eq(PUBLISHER_PID), eq(SITE_PID), eq(POSITION_PID), any(PublisherTagDTO.class)))
        .willThrow(
            new GenevaValidationException(ServerErrorCodes.SERVER_TAGS_PER_POSITION_LIMIT_REACHED));

    // when/then
    mockMvc
        .perform(
            post(CREATE_TAG_URL, PUBLISHER_PID, SITE_PID, POSITION_PID)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
        .andExpect(status().isBadRequest());
  }

  @Test
  @SneakyThrows
  void shouldUpdateTag() {
    // given
    PublisherTagDTO updatedTag = getUpdatedTag();
    String payload = mapper.writeValueAsString(updatedTag);

    given(
            publisherSelfService.updateTag(
                eq(PUBLISHER_PID), eq(SITE_PID), eq(POSITION_PID), any(PublisherTagDTO.class)))
        .willReturn(updatedTag);

    // when
    MvcResult result =
        mockMvc
            .perform(
                put(UPDATE_TAG_URL, PUBLISHER_PID, SITE_PID, POSITION_PID, TAG_PID)
                    .content(payload)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

    // then
    assertEquals(payload, result.getResponse().getContentAsString());
  }

  @Test
  @SneakyThrows
  void shouldReturnBadRequestStatusWhenTagPidMismatchesUrlParam() {
    // given
    String payload = mapper.writeValueAsString(getUpdatedTag());

    // when
    mockMvc
        .perform(
            put(UPDATE_TAG_URL, PUBLISHER_PID, SITE_PID, POSITION_PID, TAG_PID + 1)
                .content(payload)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  @SneakyThrows
  void shouldReturnBadRequestWhenTagUpdateThrows() {
    // given
    String payload = mapper.writeValueAsString(getUpdatedTag());

    given(
            publisherSelfService.updateTag(
                eq(PUBLISHER_PID), eq(SITE_PID), eq(POSITION_PID), any(PublisherTagDTO.class)))
        .willThrow(
            new GenevaValidationException(ServerErrorCodes.SERVER_TAG_AND_PROFILE_NOT_MATCH));

    // when/then
    mockMvc
        .perform(
            put(UPDATE_TAG_URL, PUBLISHER_PID, SITE_PID, POSITION_PID, TAG_PID)
                .content(payload)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  @SneakyThrows
  void shouldUpdatePublisher() {
    // given
    PublisherDTO publisher = getPublisher();
    String payload = mapper.writeValueAsString(publisher);

    given(publisherSelfService.updatePublisher(eq(PUBLISHER_PID), any(PublisherDTO.class)))
        .willReturn(publisher);

    // when
    MvcResult result =
        mockMvc
            .perform(
                put(UPDATE_PUBLISHER_URL, PUBLISHER_PID)
                    .content(payload)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

    // then
    assertEquals(payload, result.getResponse().getContentAsString());
  }

  @Test
  @SneakyThrows
  void shouldReturnBadRequestStatusWhenRequestBodyIsMissing() {
    mockMvc
        .perform(put(UPDATE_PUBLISHER_URL, PUBLISHER_PID).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  @SneakyThrows
  void shouldReturnBadRequestWhenUpdatingPublisherThrows() {
    // given
    PublisherDTO publisher = getPublisher();
    String payload = mapper.writeValueAsString(publisher);

    given(publisherSelfService.updatePublisher(eq(PUBLISHER_PID), any(PublisherDTO.class)))
        .willThrow(
            new GenevaValidationException(
                ServerErrorCodes.SERVER_COMPANY_DEFAULT_RTB_PROFILE_ENABLED_FLAG_CANT_BE_UPDATED));

    // when/then
    mockMvc
        .perform(
            put(UPDATE_PUBLISHER_URL, PUBLISHER_PID)
                .content(payload)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andReturn();
  }

  private PublisherTagDTO getNewTag() {
    return getTagBuilder().build();
  }

  private PublisherTagDTO getTag() {
    return getTagBuilder().withPid(TAG_PID).build();
  }

  private PublisherTagDTO getUpdatedTag() {
    return getTagBuilder().withPid(TAG_PID).withName("tagname").build();
  }

  private PublisherTagDTO.Builder getTagBuilder() {
    return PublisherTagDTO.newBuilder().withStatus(Status.ACTIVE).withTagType(TagType.RTB);
  }

  private PublisherDTO getPublisher() {
    return PublisherDTO.newBuilder()
        .withPid(PUBLISHER_PID)
        .withId("8a858a330137372da0982da27bb70005")
        .withName("publishername")
        .build();
  }
}
