package com.nexage.app.web;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nexage.app.dto.seller.PlacementValidMemoDTO;
import com.nexage.app.services.PlacementValidMemoDTOService;
import com.nexage.app.web.placement.PlacementValidMemoDTOController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:application-context-test.xml"})
@WebAppConfiguration
class PlacementValidMemoDTOControllerIT {

  private MockMvc mockMvc;

  @InjectMocks private PlacementValidMemoDTOController placementsValidMemoController;

  @Mock private PlacementValidMemoDTOService placementValidMemoDTOService;

  @BeforeEach
  public void setUp() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(placementsValidMemoController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setViewResolvers((viewName, locale) -> new MappingJackson2JsonView())
            .build();
  }

  @Test
  void testGetValidPlacementMemo() throws Throwable {
    PlacementValidMemoDTO placementValidMemoDTO =
        new PlacementValidMemoDTO("memo test-s123-t68955", true);

    when(placementValidMemoDTOService.getValidPlacementMemo(
            any(Long.class), any(Long.class), any(String.class)))
        .thenReturn(placementValidMemoDTO);

    mockMvc
        .perform(
            get("/v1/sellers/1/sites/123/placements/valid-memos?memo={memo}", "banner placement"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("validMemo", startsWith(String.format("memo test-s%d-t", 123L))))
        .andExpect(jsonPath("unique", is(true)));
  }
}
