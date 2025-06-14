package com.nexage.app.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.admin.core.model.DoohScreen;
import com.ssp.geneva.server.screenmanagement.dto.DoohScreenDTO;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import spark.utils.Assert;

@ExtendWith(MockitoExtension.class)
class DoohScreenDTOMapperTest {

  private static final String VALID_SCREENS_JSON_NAME = "/schema/dooh/valid-screens.json";

  @Test
  void shouldMapJsonFileToDoohScreenDTOs() throws IOException {
    InputStream inputStream = ResourceLoader.class.getResourceAsStream(VALID_SCREENS_JSON_NAME);
    MultipartFile screensFile = new MockMultipartFile("screensFile", inputStream);

    List<DoohScreenDTO> screens = DoohScreenDTOMapper.MAPPER.map(screensFile);

    assertEquals(2, screens.size());
    screens.forEach(Assert::notNull);
  }

  @Test
  void shouldMapDoohScreenDTOsToDoohScreens() {
    List<DoohScreenDTO> doohScreenDTOs =
        Arrays.asList(createDoohScreenDTO(), createDoohScreenDTO());
    List<DoohScreen> doohScreens = DoohScreenDTOMapper.MAPPER.map(doohScreenDTOs);

    assertEquals(doohScreenDTOs.size(), doohScreens.size());
    doohScreens.forEach(doohScreen -> assertEquals("screenId", doohScreen.getSspScreenId()));
  }

  @Test
  void shouldReturnEmptyListWhenFileIsNull() throws IOException {
    MultipartFile multipartFile = null;
    assertTrue(DoohScreenDTOMapper.MAPPER.map(multipartFile).isEmpty());
  }

  @Test
  void shouldMapDoohScreensToDoohScreenDTOs() {
    Pageable pageable = PageRequest.of(0, 2);
    List<DoohScreen> doohScreens =
        Arrays.asList(
            createDoohScreen(
                1L,
                "111-AdstashScreen-BroadwaySt",
                111L,
                "HDF-Max-Res",
                "AdstashScreen-BroadwaySt",
                BigDecimal.valueOf(0.55)));
    Page<DoohScreen> screenPage = new PageImpl<>(doohScreens, pageable, doohScreens.size());
    screenPage.map(DoohScreenDTOMapper.MAPPER::map);
    assertEquals(1, screenPage.getTotalElements());
    assertEquals(screenPage.getContent().get(0).getPid(), doohScreens.get(0).getPid());
    assertEquals(screenPage.getContent().get(0).getSellerPid(), doohScreens.get(0).getSellerPid());
    assertEquals(
        screenPage.getContent().get(0).getSellerScreenId(), doohScreens.get(0).getSellerScreenId());
    assertEquals(
        screenPage.getContent().get(0).getSellerScreenName(),
        doohScreens.get(0).getSellerScreenName());
    assertEquals(
        screenPage.getContent().get(0).getFloorPrice(), doohScreens.get(0).getFloorPrice());
  }

  private DoohScreenDTO createDoohScreenDTO() {
    var doohScreenDTO = new DoohScreenDTO();
    doohScreenDTO.setSspScreenId("screenId");
    doohScreenDTO.setFloorPrice(BigDecimal.valueOf(0.25));
    return doohScreenDTO;
  }

  private DoohScreen createDoohScreen(
      Long pid,
      String sspScreenId,
      Long sellerPid,
      String sellerScreenName,
      String sellerScreenId,
      BigDecimal floorPrice) {
    var doohScreen = new DoohScreen();
    doohScreen.setPid(pid);
    doohScreen.setSspScreenId(sspScreenId);
    doohScreen.setSellerPid(sellerPid);
    doohScreen.setSellerScreenName(sellerScreenName);
    doohScreen.setSellerScreenId(sellerScreenId);
    doohScreen.setFloorPrice(floorPrice);
    return doohScreen;
  }
}
