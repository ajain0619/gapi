package com.nexage.app.util.validator.deals;

import static java.nio.charset.StandardCharsets.UTF_8;

import au.com.bytecode.opencsv.CSVParser;
import com.nexage.admin.core.model.DealInventoryType;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.util.XlsUtils;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.validator.routines.DomainValidator;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@Log4j2
public class DealInventoriesFileValidator {

  private static final int FILE_NAME_LIMIT = 255;
  private static final int FILE_SIZE_LIMIT = 5000000;
  private static final int NUMBER_OF_ENTRIES_LIMIT = 50000;

  private static String appBundleRegex = "^[a-zA-Z0-9\\\\.]*$";
  private static String appAliasRegex = "^[a-zA-Z0-9\\s\\'\\(\\)\\.\\-]*$";

  public void validateDealInventoriesFile(
      String fileName, DealInventoryType fileType, MultipartFile inventoriesFile) {

    if (FILE_NAME_LIMIT < fileName.length())
      throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_FILE_NAME);

    if (!fileName.endsWith(".csv") && !fileName.endsWith(".xls") && !fileName.endsWith(".xlsx"))
      throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_FILE_FORMAT);

    if (inventoriesFile.getSize() > FILE_SIZE_LIMIT) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_FILE_SIZE);
    }

    var entries = new String[] {""};

    try (var inputStream = inventoriesFile.getInputStream()) {
      if (fileName.endsWith(".csv")) {
        entries =
            new CSVParser(CSVParser.DEFAULT_SEPARATOR)
                .parseLineMulti(IOUtils.toString(inputStream, UTF_8));
        for (String element : entries) {
          if (isInValidEntry(element)) {
            throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_CSV_OR_EXCEL_FILE);
          }
        }
      } else if (fileName.endsWith(".xlsx")) {
        entries = XlsUtils.readXlsxFileData(inputStream).toArray(String[]::new);
      } else if (fileName.endsWith(".xls")) {
        entries = XlsUtils.readXlsFileData(inputStream).toArray(String[]::new);
      }
    } catch (Exception exception) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_CSV_OR_EXCEL_FILE);
    }

    if (entries.length > NUMBER_OF_ENTRIES_LIMIT) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_NUMBER_OF_ENTRIES);
    }

    List<String> invalidEntries;
    switch (fileType) {
      case DOMAIN:
        final var validatorInstance = DomainValidator.getInstance();
        invalidEntries =
            Arrays.stream(entries)
                .filter(domain -> !validatorInstance.isValid(domain))
                .collect(Collectors.toList());
        break;
      case APP_BUNDLE:
        invalidEntries =
            Arrays.stream(entries)
                .filter(app -> !app.matches(appBundleRegex))
                .collect(Collectors.toList());
        break;
      case APP_ALIAS:
        invalidEntries =
            Arrays.stream(entries)
                .filter(app -> !app.matches(appAliasRegex))
                .collect(Collectors.toList());
        break;
      default:
        throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_FILE_TYPE);
    }
    if (CollectionUtils.isNotEmpty(invalidEntries)) {
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_INVALID_ENTRIES,
          new Object[] {String.valueOf(invalidEntries.size()), String.join(",", invalidEntries)});
    }
  }

  private boolean isInValidEntry(String entry) {
    Pattern regex = Pattern.compile("[\\\n*@|%#!]");
    return (regex.matcher(entry).find());
  }
}
