package com.nexage.app.util.validator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.model.DealInventoryType;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.util.validator.deals.DealInventoriesFileValidator;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class DealInventoriesFileValidatorTest {

  @InjectMocks DealInventoriesFileValidator dealInventoriesFileValidator;

  @Mock private MultipartFile inventoryFile;

  private InputStream setupTargetData(String filePath) {
    return ResourceLoader.class.getResourceAsStream(filePath);
  }

  @Test
  void shouldReturnInvalidFileNameForInvalidFileNameInput() throws IOException {
    String fileName =
        "veryLongFileNameveryLongFileNameveryLongFileNameveryLongFileNameveryLongFileNameveryLongFileNameveryLongFileNameveryLongFileNameveryLongFileNameveryLongFileNameveryLongFileNameveryLongFileNameveryLongFileNameveryLongFileNameveryLongFileNameveryLongFileName.csv";
    DealInventoryType fileType = DealInventoryType.DOMAIN;

    var thrown =
        assertThrows(
            GenevaValidationException.class,
            () ->
                dealInventoriesFileValidator.validateDealInventoriesFile(
                    fileName, fileType, inventoryFile));

    assertEquals(ServerErrorCodes.SERVER_INVALID_FILE_NAME, thrown.getErrorCode());
  }

  @Test
  void shouldReturnInvalidFileFormatForInvalidFileFormat() throws IOException {
    String fileName = "domain.csm";
    DealInventoryType fileType = DealInventoryType.DOMAIN;

    var thrown =
        assertThrows(
            GenevaValidationException.class,
            () ->
                dealInventoriesFileValidator.validateDealInventoriesFile(
                    fileName, fileType, inventoryFile));

    assertEquals(ServerErrorCodes.SERVER_INVALID_FILE_FORMAT, thrown.getErrorCode());
  }

  @Test
  void shouldReturnInvalidFileSizeForValidMediaFile() throws IOException {
    String fileName = "domain.csv";
    DealInventoryType fileType = DealInventoryType.DOMAIN;

    Long largeFileSize = 5000099L;

    when(inventoryFile.getSize()).thenReturn(largeFileSize);
    var thrown =
        assertThrows(
            GenevaValidationException.class,
            () ->
                dealInventoriesFileValidator.validateDealInventoriesFile(
                    fileName, fileType, inventoryFile));

    assertEquals(ServerErrorCodes.SERVER_INVALID_FILE_SIZE, thrown.getErrorCode());
  }

  @Test
  void shouldReturnInvalidFileCsvForValidMediaFile() throws IOException {
    String fileName = "domain.csv";
    DealInventoryType fileType = DealInventoryType.DOMAIN;

    when(inventoryFile.getInputStream()).thenReturn(null);
    var thrown =
        assertThrows(
            GenevaValidationException.class,
            () ->
                dealInventoriesFileValidator.validateDealInventoriesFile(
                    fileName, fileType, inventoryFile));

    assertEquals(ServerErrorCodes.SERVER_INVALID_CSV_OR_EXCEL_FILE, thrown.getErrorCode());
  }

  @Test
  void shouldReturnInvalidEntriesForValidMediaFile() throws IOException {
    String fileName = "domain.csv";
    DealInventoryType fileType = DealInventoryType.DOMAIN;
    String data = "yahooinc.com/abc,aol.com,invalidDomain";

    when(inventoryFile.getInputStream()).thenReturn(IOUtils.toInputStream(data));

    var thrown =
        assertThrows(
            GenevaValidationException.class,
            () ->
                dealInventoriesFileValidator.validateDealInventoriesFile(
                    fileName, fileType, inventoryFile));

    assertEquals(ServerErrorCodes.SERVER_INVALID_ENTRIES, thrown.getErrorCode());
    assertEquals("2", thrown.getMessageParams()[0]);
    assertEquals("yahooinc.com/abc,invalidDomain", thrown.getMessageParams()[1]);
  }

  @Test
  void shouldReturnInvalidEntriesForInValidAppBundleEntries() throws IOException {
    String fileName = "appBundle.csv";
    DealInventoryType fileType = DealInventoryType.APP_BUNDLE;
    String data = "yahooinc.com,aol.com,123/Invalid.App.Bundle.com";

    when(inventoryFile.getInputStream()).thenReturn(IOUtils.toInputStream(data));

    var thrown =
        assertThrows(
            GenevaValidationException.class,
            () ->
                dealInventoriesFileValidator.validateDealInventoriesFile(
                    fileName, fileType, inventoryFile));

    assertEquals(ServerErrorCodes.SERVER_INVALID_ENTRIES, thrown.getErrorCode());
    assertEquals("1", thrown.getMessageParams()[0]);
    assertEquals("123/Invalid.App.Bundle.com", thrown.getMessageParams()[1]);
  }

  @Test
  void shouldReturnInvalidEntriesForInValidAppAliasEntries() throws IOException {
    String fileName = "appAlias.csv";
    DealInventoryType fileType = DealInventoryType.APP_ALIAS;
    String data =
        "yahooinc.com,aol.com,invalid/AppAlias.com,PraiseRichmond,OldSchoolCincy,com$.abc";

    when(inventoryFile.getInputStream()).thenReturn(IOUtils.toInputStream(data));

    var thrown =
        assertThrows(
            GenevaValidationException.class,
            () ->
                dealInventoriesFileValidator.validateDealInventoriesFile(
                    fileName, fileType, inventoryFile));

    assertEquals(ServerErrorCodes.SERVER_INVALID_ENTRIES, thrown.getErrorCode());
    assertEquals("2", thrown.getMessageParams()[0]);
    assertEquals("invalid/AppAlias.com,com$.abc", thrown.getMessageParams()[1]);
  }

  @Test
  void shouldNotReturnAnyErrorForValidDomainFile() throws IOException {
    String fileName = "domain.csv";
    DealInventoryType fileType = DealInventoryType.DOMAIN;
    String data = "yahooinc.com,aol.com";

    when(inventoryFile.getInputStream()).thenReturn(IOUtils.toInputStream(data));

    assertDoesNotThrow(
        () ->
            dealInventoriesFileValidator.validateDealInventoriesFile(
                fileName, fileType, inventoryFile));
  }

  @Test
  void shouldReturnInvalidEntriesForEmptyDomainFile() throws IOException {
    String fileName = "domain.csv";
    DealInventoryType fileType = DealInventoryType.DOMAIN;
    String data = "";

    when(inventoryFile.getInputStream()).thenReturn(IOUtils.toInputStream(data));

    var thrown =
        assertThrows(
            GenevaValidationException.class,
            () ->
                dealInventoriesFileValidator.validateDealInventoriesFile(
                    fileName, fileType, inventoryFile));

    assertEquals(ServerErrorCodes.SERVER_INVALID_ENTRIES, thrown.getErrorCode());
    assertEquals("1", thrown.getMessageParams()[0]);
    assertEquals(data, thrown.getMessageParams()[1]);
  }

  @Test
  void shouldNotReturnErrorForValidAppAlias() throws IOException {
    String fileName = "validAppAlias.csv";
    DealInventoryType fileType = DealInventoryType.APP_ALIAS;
    String data =
        "PraiseRichmond,OldSchoolCincy,com.abc,Men's Channel (Aggregated),sport-stimme.de,Boom 92 Houston";

    when(inventoryFile.getInputStream()).thenReturn(IOUtils.toInputStream(data));

    assertDoesNotThrow(
        () ->
            dealInventoriesFileValidator.validateDealInventoriesFile(
                fileName, fileType, inventoryFile));
  }

  @Test
  void shouldReturnErrorForInvalidFileContent() throws IOException {
    String fileName = "domain.csv";
    DealInventoryType fileType = DealInventoryType.DOMAIN;
    String data = "yahooinc.com\naol.com";

    when(inventoryFile.getInputStream()).thenReturn(IOUtils.toInputStream(data));

    var thrown =
        assertThrows(
            GenevaValidationException.class,
            () ->
                dealInventoriesFileValidator.validateDealInventoriesFile(
                    fileName, fileType, inventoryFile));

    assertEquals(ServerErrorCodes.SERVER_INVALID_CSV_OR_EXCEL_FILE, thrown.getErrorCode());
  }

  @ParameterizedTest
  @CsvSource({
    "'invalidDomain.xlsx', DOMAIN",
    "'invalidDomainFile.xls', DOMAIN",
  })
  void shouldReturnErrorForInvalidXLSFileContent(String fileName, DealInventoryType fileType)
      throws IOException {
    String filePath = "/data/deal_inventory_files/" + fileName;
    InputStream xlsFile = setupTargetData(filePath);

    when(inventoryFile.getInputStream()).thenReturn(xlsFile);
    var thrown =
        assertThrows(
            GenevaValidationException.class,
            () ->
                dealInventoriesFileValidator.validateDealInventoriesFile(
                    fileName, fileType, inventoryFile));

    assertEquals(ServerErrorCodes.SERVER_INVALID_CSV_OR_EXCEL_FILE, thrown.getErrorCode());
  }

  @Test
  void shouldReturnErrorForInvalidDomainNameInXLSFile() throws IOException {
    String filePath = "/data/deal_inventory_files/invaliddomainnames.xlsx";
    InputStream xlsFile = setupTargetData(filePath);
    String fileName = "invaliddomainnames.xlsx";
    DealInventoryType fileType = DealInventoryType.DOMAIN;

    when(inventoryFile.getInputStream()).thenReturn(xlsFile);

    var thrown =
        assertThrows(
            GenevaValidationException.class,
            () ->
                dealInventoriesFileValidator.validateDealInventoriesFile(
                    fileName, fileType, inventoryFile));

    assertEquals(ServerErrorCodes.SERVER_INVALID_ENTRIES, thrown.getErrorCode());
    assertEquals("1", thrown.getMessageParams()[0]);
    assertEquals("b.com/xyz", thrown.getMessageParams()[1]);
  }

  @ParameterizedTest
  @CsvSource({
    "'domains1.xls', DOMAIN",
    "'domains2.xlsx', DOMAIN",
    "'AppNames.xls', APP_ALIAS",
    "'AppNames.xlsx', APP_ALIAS",
    "'AppBundles.xls', APP_BUNDLE",
    "'AppBundles.xlsx', APP_BUNDLE",
  })
  void shouldNotReturnErrorForValidExcelFile(String fileName, DealInventoryType fileType)
      throws IOException {
    String filePath = "/data/deal_inventory_files/" + fileName;
    InputStream xlsFile = setupTargetData(filePath);

    when(inventoryFile.getInputStream()).thenReturn(xlsFile);

    assertDoesNotThrow(
        () ->
            dealInventoriesFileValidator.validateDealInventoriesFile(
                fileName, fileType, inventoryFile));
  }
}
