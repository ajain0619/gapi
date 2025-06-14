package com.nexage.app.web.publisher;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nexage.admin.core.validator.CreateGroup;
import com.nexage.admin.core.validator.UpdateGroup;
import com.nexage.app.dto.publisher.PublisherPositionDTO;
import com.nexage.app.error.EntityConstraintViolationException;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.security.UserContext;
import com.nexage.app.services.BeanValidationService;
import com.nexage.app.services.PublisherSelfService;
import com.nexage.app.services.SellerLimitService;
import com.nexage.app.web.ControllerExceptionHandler;
import com.nexage.app.web.support.BaseControllerItTest;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.security.error.GenevaSecurityException;
import com.ssp.geneva.common.security.error.SecurityErrorCodes;
import com.ssp.geneva.common.security.model.SpringUserDetails;
import javax.validation.groups.Default;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

class InvalidPublisherSelfServeControllerIT extends BaseControllerItTest {
  private static final String CREATE_POSITION_URL = "/pss/{publisher}/site/{site}/position";
  private static final String UPDATE_POSITION_URL =
      "/pss/{publisher}/site/{site}/position/{position}";

  private MockMvc mockMvc;

  @Autowired ControllerExceptionHandler controllerExceptionHandler;

  @Mock private SellerLimitService sellerLimitService;
  @Mock private PublisherSelfService publisherSelfService;
  @Mock private UserContext userContext;
  @Mock private SpringUserDetails springUserDetails;
  @Mock private BeanValidationService beanValidationService;

  @InjectMocks private PublisherSelfServeController publisherSelfServeController;

  @BeforeEach
  void setUp() {
    when(userContext.getCurrentUser()).thenReturn(springUserDetails);
    this.mockMvc =
        MockMvcBuilders.standaloneSetup(publisherSelfServeController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setViewResolvers((viewName, locale) -> new MappingJackson2JsonView())
            .setControllerAdvice(controllerExceptionHandler)
            .build();
  }

  @Test
  @SneakyThrows
  void shouldReturnNotFoundWhenFetchingNonexistentPosition() {
    when(publisherSelfService.getPosition(anyLong(), anyLong(), anyLong(), anyBoolean()))
        .thenThrow(new GenevaValidationException(ServerErrorCodes.SERVER_POSITION_NOT_EXISTS));

    mockMvc
        .perform(
            get("/pss/{publisher}/site/{site}/position/{position}", 100, 100, 100)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(
            jsonPath(
                "$.errorMessage",
                is(
                    controllerExceptionHandler.getErrorMessage(
                        ServerErrorCodes.SERVER_POSITION_NOT_EXISTS))));
  }

  @Test
  @SneakyThrows
  void shouldReturnBadRequestStatusWhenCreatingPositionUsingInvalidJson() {
    doThrow(EntityConstraintViolationException.class)
        .when(beanValidationService)
        .validate(any(), eq(CreateGroup.class), eq(Default.class));

    mockMvc
        .perform(
            post(CREATE_POSITION_URL, 100, 100)
                .content("{}")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  @SneakyThrows
  void shouldReturnBadRequestStatusWhenCreatingPositionWithDuplicatedName() {
    when(sellerLimitService.isLimitEnabled(anyLong())).thenReturn(true);
    when(sellerLimitService.canCreatePositionsInSite(anyLong(), anyLong())).thenReturn(true);
    when(publisherSelfService.createPosition(
            anyLong(), any(PublisherPositionDTO.class), anyBoolean()))
        .thenThrow(new GenevaValidationException(ServerErrorCodes.SERVER_DUPLICATE_POSITION_NAME));

    mockMvc
        .perform(
            post(CREATE_POSITION_URL, 100, 100)
                .content("{}")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath(
                "$.errorMessage",
                is(
                    controllerExceptionHandler.getErrorMessage(
                        ServerErrorCodes.SERVER_DUPLICATE_POSITION_NAME))));
  }

  @Test
  @SneakyThrows
  void shouldReturnNotFoundStatusWhenUpdatingNonexistentPosition() {
    when(publisherSelfService.updatePosition(
            anyLong(), anyLong(), any(PublisherPositionDTO.class), anyBoolean()))
        .thenThrow(
            new GenevaValidationException(ServerErrorCodes.SERVER_POSITION_NOT_FOUND_IN_SITE));

    mockMvc
        .perform(
            put(UPDATE_POSITION_URL, 100, 100, 100246)
                .content("{\"pid\":100246}")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(
            jsonPath(
                "$.errorMessage",
                is(
                    controllerExceptionHandler.getErrorMessage(
                        ServerErrorCodes.SERVER_POSITION_NOT_FOUND_IN_SITE))));
  }

  @Test
  @SneakyThrows
  void shouldReturnBadRequestStatusWhenUpdatingPositionUsingInvalidPayload() {
    doThrow(EntityConstraintViolationException.class)
        .when(beanValidationService)
        .validate(any(), eq(UpdateGroup.class), eq(Default.class));

    mockMvc
        .perform(
            put(UPDATE_POSITION_URL, 100, 100, 100246)
                .content("{\"pid\":100246}")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  @SneakyThrows
  void shouldReturnNotAuthorizedStatusWhenCreatingPositionAndSelfServeIsNotEnabled() {
    when(sellerLimitService.isLimitEnabled(anyLong())).thenReturn(true);
    when(sellerLimitService.canCreatePositionsInSite(anyLong(), anyLong())).thenReturn(true);
    when(publisherSelfService.createPosition(
            anyLong(), any(PublisherPositionDTO.class), anyBoolean()))
        .thenThrow(new GenevaSecurityException(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED));

    mockMvc
        .perform(
            post(CREATE_POSITION_URL, 100, 100)
                .content("{}")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @SneakyThrows
  void shouldReturnNotAuthorizedStatusWhenUpdatingPositionAndSelfServeIsNotEnabled() {
    when(sellerLimitService.isLimitEnabled(anyLong())).thenReturn(true);
    when(sellerLimitService.canCreatePositionsInSite(anyLong(), anyLong())).thenReturn(true);
    when(publisherSelfService.updatePosition(
            anyLong(), anyLong(), any(PublisherPositionDTO.class), anyBoolean()))
        .thenThrow(new GenevaSecurityException(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED));

    mockMvc
        .perform(
            put(UPDATE_POSITION_URL, 100, 100, 100246)
                .content("{\"pid\":100246}")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized())
        .andExpect(
            jsonPath(
                "$.errorMessage",
                is(
                    controllerExceptionHandler.getErrorMessage(
                        SecurityErrorCodes.SECURITY_NOT_AUTHORIZED))));
  }
}
