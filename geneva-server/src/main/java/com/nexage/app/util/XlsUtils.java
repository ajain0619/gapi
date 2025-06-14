package com.nexage.app.util;

import com.nexage.app.error.ServerErrorCodes;
import com.ssp.geneva.common.error.exception.GenevaAppRuntimeException;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class XlsUtils {

  private XlsUtils() {}

  /**
   * Writes the data in XLS format and returns {@link ByteArrayOutputStream} file data
   *
   * @param data {@link List<String>}
   * @param sheetName {@link String}
   * @return {@link ByteArrayOutputStream} of type.
   */
  public static ByteArrayOutputStream writeXlsFile(List<String> data, String sheetName) {
    ByteArrayOutputStream xlsFileByteArrayOutputStream = null;
    try (var xlsFile = new HSSFWorkbook()) {
      var xlsFileSheet = xlsFile.createSheet(sheetName);

      var rowNum = 0;
      for (String cellData : data) {
        var row = xlsFileSheet.createRow(rowNum++);
        var cell = row.createCell(0);
        cell.setCellValue(cellData);
      }

      xlsFileByteArrayOutputStream = new ByteArrayOutputStream();
      xlsFile.write(xlsFileByteArrayOutputStream);
    } catch (Exception e) {
      throw new GenevaAppRuntimeException(ServerErrorCodes.SERVER_CREATE_XLS_FILE_INTERNAL_ERROR);
    }
    return xlsFileByteArrayOutputStream;
  }

  /**
   * Reads the data in XLS file and returns cells data {@link List<String>}
   *
   * @param inputStream {@link InputStream}
   * @return {@link List<String>} of type.
   */
  public static List<String> readXlsFileData(InputStream inputStream)
      throws GenevaValidationException {
    try (var wb = new HSSFWorkbook(inputStream)) {
      var sheet = wb.getSheetAt(0);
      validateSheet(sheet.iterator());
      return getCellValues(sheet.iterator());
    } catch (Exception e) {
      throw new GenevaAppRuntimeException(ServerErrorCodes.SERVER_READ_XLS_FILE_INTERNAL_ERROR);
    }
  }

  /**
   * Reads the data in XLSX file and returns cells data {@link List<String>}
   *
   * @param inputStream {@link InputStream}
   * @return {@link List<String>} of type.
   */
  public static List<String> readXlsxFileData(InputStream inputStream)
      throws GenevaValidationException {
    try (var wb = new XSSFWorkbook(inputStream)) {
      var sheet = wb.getSheetAt(0);
      validateSheet(sheet.iterator());
      return getCellValues(sheet.iterator());
    } catch (Exception e) {
      throw new GenevaAppRuntimeException(ServerErrorCodes.SERVER_READ_XLS_FILE_INTERNAL_ERROR);
    }
  }

  private static void validateSheet(Iterator<Row> iterator) {
    var itr = iterator;
    while (itr.hasNext()) {
      var row = itr.next();
      int numberOfCells = row.getPhysicalNumberOfCells();
      if (numberOfCells > 1)
        throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_CSV_OR_EXCEL_FILE);
    }
  }

  private static List<String> getCellValues(Iterator<Row> iterator) {
    var itr = iterator;
    var cellData = new ArrayList<String>();
    while (itr.hasNext()) {
      var row = itr.next();
      var cell = row.getCell(0);
      if (cell != null) {
        if (cell.getCellType() == CellType.STRING) {
          cellData.add(cell.getStringCellValue());
        } else if (cell.getCellType() == CellType.NUMERIC) {
          cellData.add(NumberToTextConverter.toText(cell.getNumericCellValue()));
        }
      }
    }
    return cellData;
  }
}
