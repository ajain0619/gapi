package com.nexage.app.web;

import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.nexage.app.dto.ContentRatingDTO;
import com.nexage.app.services.ContentRatingService;
import java.net.URI;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:application-context-test.xml"})
@WebAppConfiguration
class ContentRatingControllerIT {

  private MockMvc mockMvc;
  @Mock private ContentRatingService contentRatingService;
  @InjectMocks private ContentRatingController contentRatingController;

  @BeforeEach
  void setUp() {
    this.mockMvc =
        MockMvcBuilders.standaloneSetup(contentRatingController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setViewResolvers((viewName, locale) -> new MappingJackson2JsonView())
            .build();
  }

  @Test
  void shouldGetContentRatingData() throws Exception {
    Pageable pageRequest = PageRequest.of(0, 1);
    ContentRatingDTO contentRatingDTO = new ContentRatingDTO(2L, "NG");

    Page<ContentRatingDTO> dtoPage = new PageImpl<>(List.of(contentRatingDTO), pageRequest, 1);
    when(contentRatingService.findAll(
            nullable(String.class), nullable(Set.class), nullable(Pageable.class)))
        .thenReturn(dtoPage);
    this.mockMvc
        .perform(MockMvcRequestBuilders.get(URI.create("/v1/content-ratings")))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(jsonPath("content[0].pid").value(2))
        .andExpect(jsonPath("content[0].rating").value("NG"));
  }
}
