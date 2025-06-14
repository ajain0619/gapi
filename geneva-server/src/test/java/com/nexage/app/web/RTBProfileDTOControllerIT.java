package com.nexage.app.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.common.io.Resources;
import com.nexage.app.dto.RTBProfileDTO;
import com.nexage.app.services.RTBProfileDTOService;
import com.nexage.app.util.CustomViewLayerObjectMapper;
import com.nexage.app.web.support.TestObjectsFactory;
import java.io.IOException;
import java.nio.charset.Charset;
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
class RTBProfileDTOControllerIT {

  private static final String BASE_URL_TEMPLATE = "/v1/sellers/{sellerPid}/rtb-profiles";
  private static final Long SELLER_PID = 1L;

  @Autowired private ControllerExceptionHandler controllerExceptionHandler;
  @Autowired private CustomViewLayerObjectMapper mapper;

  @Mock private RTBProfileDTOService rtbProfileDTOService;

  private MockMvc mockMvc;

  @InjectMocks RTBProfileDTOController rtbProfileDTOController;

  @BeforeEach
  public void setUp() throws Exception {
    mockMvc =
        MockMvcBuilders.standaloneSetup(rtbProfileDTOController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setViewResolvers((viewName, locale) -> new MappingJackson2JsonView())
            .setControllerAdvice(controllerExceptionHandler)
            .build();
  }

  @Test
  void getRTBProfilesTest() throws Exception {

    List<RTBProfileDTO> rtbProfileDTOS = TestObjectsFactory.gimme(10, RTBProfileDTO.class);

    Page rtbProfilesDTO = new PageImpl(rtbProfileDTOS);

    when(rtbProfileDTOService.getRTBProfiles(any(), any(), any(), any()))
        .thenReturn(rtbProfilesDTO);

    mockMvc.perform(get("/v1/sellers/123/rtb-profiles")).andExpect(status().isOk());
  }

  @Test
  void updateRTBProfilesTest() throws Exception {
    long rtbPid = 1L;
    var payload = getData(ResourcePaths.UPDATE_RTB_PROFILES_PAYLOAD.filePath);
    var expected = getData(ResourcePaths.UPDATE_RTB_PROFILES_RESPONSE.filePath);
    var output = mapper.readValue(expected, RTBProfileDTO.class);

    when(rtbProfileDTOService.update(anyLong(), any(RTBProfileDTO.class), anyLong()))
        .thenReturn(output);
    var result =
        mockMvc
            .perform(
                put(BASE_URL_TEMPLATE + "/{rtbPid}", SELLER_PID, rtbPid)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(payload))
            .andExpect(status().isOk())
            .andReturn();
    var content = result.getResponse().getContentAsString();
    assertEquals(mapper.readTree(expected), mapper.readTree(content));
  }

  private String getData(String name) throws IOException {
    return Resources.toString(
        Resources.getResource(RTBProfileDTOControllerIT.class, name), Charset.forName("UTF-8"));
  }

  private enum ResourcePaths {
    UPDATE_RTB_PROFILES_PAYLOAD("/data/rtbprofiles/update_seller_rtb_profile_payload.json"),
    UPDATE_RTB_PROFILES_RESPONSE("/data/rtbprofiles/update_seller_rtb_profile_payload_ER.json");

    private String filePath;

    ResourcePaths(String filePath) {
      this.filePath = filePath;
    }
  }
}
