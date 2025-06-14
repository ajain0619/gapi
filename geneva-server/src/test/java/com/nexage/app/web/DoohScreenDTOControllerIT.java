package com.nexage.app.web;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.common.collect.Lists;
import com.nexage.app.services.DoohScreenService;
import com.ssp.geneva.server.screenmanagement.dto.DoohScreenDTO;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:application-context-test.xml"})
@WebAppConfiguration
class DoohScreenDTOControllerIT {

  private MockMvc mockMvc;
  @Mock private DoohScreenService doohScreenService;
  private DoohScreenDTOController doohScreenDTOController;

  @BeforeEach
  public void setUp() {
    doohScreenDTOController = new DoohScreenDTOController(doohScreenService);
    mockMvc =
        MockMvcBuilders.standaloneSetup(doohScreenDTOController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setViewResolvers((viewName, locale) -> new MappingJackson2JsonView())
            .build();
  }

  @Test
  void whenCreateDoohScreensShouldReturnCreatedResponse() throws Exception {
    var sellerPid = 1216L;
    var screens = new MockMultipartFile("screens", "[]".getBytes());
    when(doohScreenService.replaceDoohScreens(sellerPid, null)).thenReturn(5);
    mockMvc
        .perform(multipart("/v1/sellers/{sellerPid}/screens", sellerPid).file(screens))
        .andExpect(status().isCreated());
  }

  @Test
  void shouldGetAllDoohScreens() throws Exception {

    List<DoohScreenDTO> expected =
        Lists.newArrayList(
            new DoohScreenDTO() {
              {
                this.setSellerScreenId("812-screen-id-1");
                this.setSellerScreenName("Billboard");
              }
            },
            new DoohScreenDTO() {
              {
                this.setSellerScreenId("812-screen-id-2");
                this.setSellerScreenName("NY-Metro-Billboard");
              }
            });
    Page<DoohScreenDTO> doohScreenDTOs = new PageImpl<>(expected);

    given(doohScreenService.getDoohScreens(any(), any())).willReturn(doohScreenDTOs);
    mockMvc
        .perform(get("/v1/sellers/812/screens"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(
            jsonPath(
                "content.[0].sellerScreenId",
                is(doohScreenDTOs.getContent().get(0).getSellerScreenId())))
        .andExpect(
            jsonPath(
                "content.[0].sellerScreenName",
                is(doohScreenDTOs.getContent().get(0).getSellerScreenName())))
        .andExpect(
            jsonPath(
                "content.[1].sellerScreenId",
                is(doohScreenDTOs.getContent().get(1).getSellerScreenId())))
        .andExpect(
            jsonPath(
                "content.[1].sellerScreenName",
                is(doohScreenDTOs.getContent().get(1).getSellerScreenName())));
  }
}
