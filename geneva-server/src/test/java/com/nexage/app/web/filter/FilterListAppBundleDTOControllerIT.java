package com.nexage.app.web.filter;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.nexage.app.dto.filter.FilterListAppBundleDTO;
import com.nexage.app.dto.filter.MediaStatusDTO;
import com.nexage.app.services.filter.FilterListAppBundleDTOService;
import com.nexage.app.util.CustomObjectMapper;
import com.nexage.app.web.ControllerExceptionHandler;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang.math.RandomUtils;
import org.hibernate.validator.HibernateValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.support.SpringWebConstraintValidatorFactory;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:application-context-test.xml"})
@WebAppConfiguration
class FilterListAppBundleDTOControllerIT extends SpringWebConstraintValidatorFactory {

  @Autowired private ControllerExceptionHandler controllerExceptionHandler;
  private MockMvc mockMvc;
  @Autowired private MockServletContext servletContext;
  @InjectMocks private FilterListAppBundleDTOController controller;
  @Mock private FilterListAppBundleDTOService service;

  private ObjectMapper objectMapper;
  private final Integer FILTER_LIST_ID = RandomUtils.nextInt();
  private final Integer PID = RandomUtils.nextInt();
  private final Long BUYER_PID = RandomUtils.nextLong();

  @BeforeEach
  void setUp() {
    objectMapper = new CustomObjectMapper();
    objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    mockMvc =
        MockMvcBuilders.standaloneSetup(controller)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setViewResolvers((viewName, locale) -> new MappingJackson2JsonView())
            .setValidator(
                createLocalValidatorFactoryBean(new GenericWebApplicationContext(servletContext)))
            .setControllerAdvice(controllerExceptionHandler)
            .build();
  }

  @Test
  void shouldGetAppBundleFilterList_returnsDTO() throws Throwable {

    Set<String> queryField = ImmutableSet.of("app");
    String queryTerm = "test";
    Sort sort = Sort.by(Sort.Direction.ASC, "app");
    Pageable pageable = PageRequest.of(0, 1, sort);

    FilterListAppBundleDTO dto = createDto();

    Page<FilterListAppBundleDTO> pageDto =
        new PageImpl<>(Collections.singletonList(dto), pageable, 1);
    when(service.getFilterListAppBundles(
            BUYER_PID, FILTER_LIST_ID, pageable, queryField, queryTerm))
        .thenReturn(pageDto);

    mockMvc
        .perform(
            get(
                "/v1/buyers/{buyerId}/filter-lists/{filterListId}/app-bundles?page=0&size=1&sort=app&qf=app&qt=test",
                BUYER_PID,
                FILTER_LIST_ID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].filterListId", is(FILTER_LIST_ID)));
  }

  @Test
  void shouldDeleteAppBundleFilterList_returnStatusOk() throws Throwable {

    Set<Integer> filterListAppBundlePIDs = ImmutableSet.of(FILTER_LIST_ID);
    FilterListAppBundleDTO dto = createDto();
    List<FilterListAppBundleDTO> test = ImmutableList.of(dto);

    when(service.deleteFilterListAppBundles(BUYER_PID, FILTER_LIST_ID, filterListAppBundlePIDs))
        .thenReturn(test);

    mockMvc
        .perform(
            delete(
                    "/v1/buyers/{buyerId}/filter-lists/{filterListId}/app-bundles",
                    BUYER_PID,
                    FILTER_LIST_ID)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("[1]"))
        .andExpect(status().isOk());
  }

  private FilterListAppBundleDTO createDto() {
    FilterListAppBundleDTO dto = new FilterListAppBundleDTO();
    dto.setPid(PID);
    dto.setFilterListId(FILTER_LIST_ID);
    dto.setApp("com.test.app");
    dto.setStatus(MediaStatusDTO.VALID);
    return dto;
  }

  private LocalValidatorFactoryBean createLocalValidatorFactoryBean(
      GenericWebApplicationContext context) {
    LocalValidatorFactoryBean validatorFactoryBean = new LocalValidatorFactoryBean();
    validatorFactoryBean.setApplicationContext(context);
    validatorFactoryBean.setConstraintValidatorFactory(this);
    validatorFactoryBean.setProviderClass(HibernateValidator.class);
    validatorFactoryBean.afterPropertiesSet();
    return validatorFactoryBean;
  }
}
