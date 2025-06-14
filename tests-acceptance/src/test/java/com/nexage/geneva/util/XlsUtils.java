package com.nexage.geneva.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.util.NumberToTextConverter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class XlsUtils {

  public static List<String> readContent(String fileName) throws Throwable {
    return readContent(TestUtils.getResourceAsInputStream(fileName));
  }

  public static List<String> readContent(InputStream inputStream) {
    try (var workbook = new HSSFWorkbook(inputStream)) {
      return readCellData(workbook.getSheetAt(0));
    } catch (IOException exception) {
      throw new RuntimeException("Error while reading the xls file");
    }
  }

  private static List<String> readCellData(HSSFSheet sheet) {
    var cellData = new ArrayList<String>();
    sheet
        .iterator()
        .forEachRemaining(
            row -> {
              var cell = row.getCell(0);
              if (cell != null) {
                if (cell.getCellType() == CellType.STRING) {
                  cellData.add(cell.getStringCellValue());
                } else if (cell.getCellType() == CellType.NUMERIC) {
                  cellData.add(NumberToTextConverter.toText(cell.getNumericCellValue()));
                }
              }
            });
    return cellData;
  }
}
