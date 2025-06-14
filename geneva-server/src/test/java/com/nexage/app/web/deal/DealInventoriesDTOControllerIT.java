package com.nexage.app.web.deal;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nexage.admin.core.model.DealInventoryType;
import com.nexage.app.dto.deals.DealInventoriesDTO;
import com.nexage.app.dto.deals.DealInventoryDownloadResponseDTO;
import com.nexage.app.services.deal.DealInventoriesService;
import com.nexage.app.util.XlsUtils;
import com.nexage.app.web.support.BaseControllerItTest;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

class DealInventoriesDTOControllerIT extends BaseControllerItTest {

  private MockMvc mockMvc;

  @InjectMocks private DealInventoriesDTOController controller;
  @Mock private DealInventoriesService service;

  @BeforeEach
  public void setUp() {

    mockMvc =
        MockMvcBuilders.standaloneSetup(controller)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setViewResolvers((viewName, locale) -> new MappingJackson2JsonView())
            .build();
  }

  @Test
  void shouldSuccesfullUploadValidFile() throws Throwable {
    String fileName = "domains.csv";
    DealInventoryType fileType = DealInventoryType.DOMAIN;
    MockMultipartFile inventoriesFile =
        new MockMultipartFile("inventoriesFile", "test.aol.com,yahooinc.com".getBytes());
    Long dealId = 1L;

    DealInventoriesDTO dealInventoriesDTO =
        new DealInventoriesDTO(123L, fileName, fileType, dealId);

    when(service.uploadDealInventories(fileName, fileType, inventoriesFile, dealId))
        .thenReturn(dealInventoriesDTO);

    mockMvc
        .perform(
            multipart("/v1/deals/inventories")
                .file(inventoriesFile)
                .param("fileName", fileName)
                .param("fileType", String.valueOf(fileType))
                .param("dealId", String.valueOf(dealId)))
        .andExpect(status().isCreated());
  }

  @Test
  void shouldDownloadDealInventoryFile() throws Throwable {
    long dealPid = 12;
    long filePid = 123;
    List<String> inventoryFileData = Arrays.asList("yahoo.com", "aol.com", "vvk.com");
    ByteArrayOutputStream xlsFile =
        XlsUtils.writeXlsFile(inventoryFileData, DealInventoryType.DOMAIN.toString());
    ByteArrayResource dealDomainInventoryFileByteResource =
        new ByteArrayResource(xlsFile.toByteArray());

    DealInventoryDownloadResponseDTO dealInventoryDownloadResponseDTO =
        new DealInventoryDownloadResponseDTO();
    dealInventoryDownloadResponseDTO.setFileName("domains.xls");
    dealInventoryDownloadResponseDTO.setInventoryFileByteResource(
        dealDomainInventoryFileByteResource);

    when(service.downloadDealInventories(filePid, dealPid))
        .thenReturn(dealInventoryDownloadResponseDTO);

    mockMvc
        .perform(
            get("/v1/deals/inventories?filePid=" + filePid + "&dealPid=" + dealPid)
                .accept(MediaType.APPLICATION_OCTET_STREAM))
        .andExpect(status().isOk())
        .andExpect(header().stringValues("Content-Disposition", "attachment;filename=domains.xls"))
        .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM));
  }

  @Test
  void shouldDeleteDealInventoriesSuccessfully() throws Exception {
    long filePid = 123L;
    long dealId = 1L;

    mockMvc
        .perform(
            delete("/v1/deals/inventories")
                .param("filePid", String.valueOf(filePid))
                .param("dealId", String.valueOf(dealId)))
        .andExpect(status().isNoContent());

    verify(service).deleteDealInventories(filePid, dealId);
  }
}
