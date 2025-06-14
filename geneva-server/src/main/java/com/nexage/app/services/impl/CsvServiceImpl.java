package com.nexage.app.services.impl;

import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.services.CsvService;
import com.ssp.geneva.common.error.exception.GenevaAppRuntimeException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Log4j2
@Transactional(readOnly = true)
public class CsvServiceImpl implements CsvService {
  /** {@inheritDoc} */
  @Override
  public InputStreamResource create(Map<String, String> map) {
    List<List<String>> csvBody = new ArrayList<>();
    map.forEach((firstEntry, secondEntry) -> csvBody.add(Arrays.asList(firstEntry, secondEntry)));

    ByteArrayInputStream byteArrayOutputStream;

    try (ByteArrayOutputStream out = new ByteArrayOutputStream();
        CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), CSVFormat.DEFAULT)) {
      csvBody.stream()
          .forEach(
              entry -> {
                try {
                  csvPrinter.printRecord(entry);
                } catch (IOException exception) {
                  log.error("Exception reading CSV file input entry", exception);
                  throw new GenevaAppRuntimeException(
                      ServerErrorCodes.SERVER_FILE_SYSTEM_READ_ERROR);
                }
              });

      csvPrinter.flush();

      byteArrayOutputStream = new ByteArrayInputStream(out.toByteArray());
    } catch (IOException exception) {
      log.error("Exception creating CSV file resource", exception);
      throw new GenevaAppRuntimeException(ServerErrorCodes.SERVER_FILE_SYSTEM_WRITE_ERROR);
    }

    return new InputStreamResource(byteArrayOutputStream);
  }
}
