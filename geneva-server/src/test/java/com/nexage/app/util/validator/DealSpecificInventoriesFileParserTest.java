package com.nexage.app.util.validator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.model.CompanyView;
import com.nexage.admin.core.model.SiteView;
import com.nexage.admin.core.sparta.jpa.model.PositionView;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.util.validator.deals.DealSpecificInventoriesFileParser;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class DealSpecificInventoriesFileParserTest {

  @InjectMocks DealSpecificInventoriesFileParser dealSpecificInventoriesFileParser;

  @Mock private MultipartFile inventoryFile;

  List<CompanyView> companyViews = null;
  List<SiteView> siteViews = null;
  List<PositionView> positionViews = null;

  @Test
  void shouldReturnInvalidFileSizeForFileSizeAbove5MB() {
    Long largeFileSize = 5000008L;
    when(inventoryFile.getSize()).thenReturn(largeFileSize);
    var thrown =
        assertThrows(
            GenevaValidationException.class,
            () -> dealSpecificInventoriesFileParser.processSpecificInventoriesFile(inventoryFile));

    assertEquals(ServerErrorCodes.SERVER_INVALID_FILE_SIZE, thrown.getErrorCode());
  }

  @Test
  void shouldReturnInvalidFileCsvForInvalidTemplate() throws IOException {

    InputStream input =
        this.getClass()
            .getResourceAsStream(
                "/data/deal_specific_bulk_inventories_files/invalid_inventory_file.csv");

    when(inventoryFile.getInputStream()).thenReturn(input);
    var thrown =
        assertThrows(
            GenevaValidationException.class,
            () -> dealSpecificInventoriesFileParser.processSpecificInventoriesFile(inventoryFile));

    assertEquals(ServerErrorCodes.SERVER_INVALID_CSV_OR_EXCEL_FILE, thrown.getErrorCode());
  }

  @Test
  void shouldReturnInvalidFileEntriesForEntiresAbove50k() throws IOException {
    InputStream input =
        this.getClass()
            .getResourceAsStream("/data/deal_specific_bulk_inventories_files/50k_plus_entries.csv");

    when(inventoryFile.getInputStream()).thenReturn(input);
    var thrown =
        assertThrows(
            GenevaValidationException.class,
            () -> dealSpecificInventoriesFileParser.processSpecificInventoriesFile(inventoryFile));

    assertEquals(ServerErrorCodes.SERVER_INVALID_NUMBER_OF_ENTRIES, thrown.getErrorCode());
  }

  @Test
  void shouldProcessFileForValidInventories() throws IOException {
    InputStream input =
        this.getClass()
            .getResourceAsStream(
                "/data/deal_specific_bulk_inventories_files/valid_inventory_file.csv");

    when(inventoryFile.getInputStream()).thenReturn(input);

    List[] out = dealSpecificInventoriesFileParser.processSpecificInventoriesFile(inventoryFile);

    companyViews = out[0];
    siteViews = out[1];
    positionViews = out[2];

    assertEquals(1, companyViews.size());
    assertEquals(29225, companyViews.get(0).getPid());

    assertEquals(1, siteViews.size());
    assertEquals(336611, siteViews.get(0).getPid());
    assertEquals(58821, siteViews.get(0).getCompany().getPid());

    assertEquals(1, positionViews.size());
    assertEquals(1220213, positionViews.get(0).getPid());
    assertEquals(275779, positionViews.get(0).getSiteView().getPid());
    assertEquals(57316, positionViews.get(0).getSiteView().getCompany().getPid());
  }
}
