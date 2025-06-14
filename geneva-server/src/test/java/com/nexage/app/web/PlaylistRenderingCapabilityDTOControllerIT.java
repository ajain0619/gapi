package com.nexage.app.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nexage.app.dto.PlaylistRenderingCapabilityDTO;
import com.nexage.app.services.PlaylistRenderingCapabilityDTOService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith({SpringExtension.class})
@ContextConfiguration(locations = {"classpath:application-context-test.xml"})
@WebAppConfiguration
class PlaylistRenderingCapabilityDTOControllerIT {

  @Mock private PlaylistRenderingCapabilityDTOService playlistRenderingCapabilityDTOService;

  @InjectMocks
  private PlaylistRenderingCapabilityDTOController playlistRenderingCapabilityDTOController;

  private MockMvc mockMvc;

  @BeforeEach
  void setup() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(playlistRenderingCapabilityDTOController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .build();
  }

  @Test
  void shouldDelegatePageRequestToService() throws Exception {
    var playlistRenderingCapabilityDTO1 = new PlaylistRenderingCapabilityDTO();
    playlistRenderingCapabilityDTO1.setValue("cap1");
    var playlistRenderingCapabilityDTO2 = new PlaylistRenderingCapabilityDTO();
    playlistRenderingCapabilityDTO2.setValue("cap2");
    var page =
        new PageImpl<>(List.of(playlistRenderingCapabilityDTO1, playlistRenderingCapabilityDTO2));
    when(this.playlistRenderingCapabilityDTOService.getPage(any(Pageable.class))).thenReturn(page);

    mockMvc
        .perform(get("/v1/playlist-rendering-capabilities"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].value").value("cap1"))
        .andExpect(jsonPath("$.content[1].value").value("cap2"));
  }

  @Test
  void shouldDefaultPagingParams() throws Exception {
    mockMvc.perform(get("/v1/playlist-rendering-capabilities")).andExpect(status().isOk());

    verify(this.playlistRenderingCapabilityDTOService)
        .getPage(
            argThat(
                pageable ->
                    pageable.getPageNumber() == 0
                        && pageable.getPageSize() == 10
                        && pageable.getSort().stream().count() == 1
                        && pageable.getSort().getOrderFor("displayValue") != null));
  }

  @Test
  void shouldAllowCustomPagingParams() throws Exception {
    var req =
        get("/v1/playlist-rendering-capabilities")
            .queryParam("page", "1")
            .queryParam("size", "100")
            .queryParam("sort", "pid");

    mockMvc.perform(req).andExpect(status().isOk());

    verify(this.playlistRenderingCapabilityDTOService)
        .getPage(
            argThat(
                pageable ->
                    pageable.getPageNumber() == 1
                        && pageable.getPageSize() == 100
                        && pageable.getSort().stream().count() == 1
                        && pageable.getSort().getOrderFor("pid") != null));
  }
}
