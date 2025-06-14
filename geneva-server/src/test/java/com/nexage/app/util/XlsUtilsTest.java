package com.nexage.app.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.nexage.app.error.ServerErrorCodes;
import com.ssp.geneva.common.error.exception.GenevaAppRuntimeException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.core.io.ResourceLoader;

class XlsUtilsTest {
  List<String> xlsInputData =
      List.of("aol.com", "finance.yahoo.com", "news.aol.com", "www.xyz.com", "yahoo.com");
  private static final String XLS_FILE = "/data/deal_inventory_files/domains1.xls";

  private InputStream setupTargetData(String fileName) {
    return ResourceLoader.class.getResourceAsStream(fileName);
  }

  @Test
  void shouldReturnExceptionWhenSheetNameIsEmptyOrNull() {
    var thrown =
        assertThrows(
            GenevaAppRuntimeException.class, () -> XlsUtils.writeXlsFile(xlsInputData, ""));

    assertEquals(ServerErrorCodes.SERVER_CREATE_XLS_FILE_INTERNAL_ERROR, thrown.getErrorCode());
  }

  @Test
  void
      shouldWriteXlsFileWhenWriteXlsFileInvokedandValidateXlsFileByReadingAndComparingWithInputData() {
    ByteArrayOutputStream xlsFile = XlsUtils.writeXlsFile(xlsInputData, "sample sheet");
    assertNotNull(xlsFile);
    InputStream xlsInputStream = new ByteArrayInputStream(xlsFile.toByteArray());
    List<String> xlsFileData = XlsUtils.readXlsFileData(xlsInputStream);
    assertEquals(
        xlsFileData, xlsInputData, "Written data in XLS file should be equal to the Input data");
  }

  @Test
  void shouldReturnExceptionWhenReadXlsFileInvokedWithNoInputStream() {
    var thrown =
        assertThrows(GenevaAppRuntimeException.class, () -> XlsUtils.readXlsFileData(null));

    assertEquals(ServerErrorCodes.SERVER_READ_XLS_FILE_INTERNAL_ERROR, thrown.getErrorCode());
  }

  @Test
  void shouldReturnExceptionWhenReadXlsxFileInvokedWithNoInputStream() {
    var thrown =
        assertThrows(GenevaAppRuntimeException.class, () -> XlsUtils.readXlsxFileData(null));

    assertEquals(ServerErrorCodes.SERVER_READ_XLS_FILE_INTERNAL_ERROR, thrown.getErrorCode());
  }

  @Test
  void shouldReturnXlsFileDataAsListWhenReadXlsInvokedWithXlsFileAsInput() {
    InputStream xlsFile = setupTargetData(XLS_FILE);
    List<String> fileData = XlsUtils.readXlsFileData(xlsFile);
    assertEquals(
        fileData,
        xlsInputData,
        "The data from readXls method should be equal to the data in the original XLS file");
  }

  @ParameterizedTest
  @MethodSource("getQueryParamsForXlsxTest")
  void shouldReturnXlsxFileDataAsListWhenReadXlsxInvokedWithXlsxFileAsInput(
      String filePath, List<String> inputData) {
    InputStream xlsxFile = setupTargetData(filePath);
    List<String> fileData = XlsUtils.readXlsxFileData(xlsxFile);
    assertEquals(
        fileData,
        inputData,
        "The data from readXlsx method should be equal to the data in the original XLSX file");
  }

  private static Stream<Arguments> getQueryParamsForXlsxTest() {
    return Stream.of(
        Arguments.of(
            "/data/deal_inventory_files/domains2.xlsx",
            Arrays.asList("abc.com", "xyz.com", "vvk.com")),
        Arguments.of(
            "/data/deal_inventory_files/AppNames.xlsx",
            Arrays.asList("Samsung TV Plus", "Pluto TV", "927060395", "Paramount Plus")));
  }
}
