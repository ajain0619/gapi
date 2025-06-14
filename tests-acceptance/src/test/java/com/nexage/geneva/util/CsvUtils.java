package com.nexage.geneva.util;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class CsvUtils {

  private Logger logger = LoggerFactory.getLogger(CsvUtils.class);

  public List<String[]> readCsvFile(String file) throws Throwable {
    CSVReader reader = new CSVReader(new FileReader(new File(file)));
    return reader.readAll();
  }

  public void copyCsvFilesForEcpmUpdate(String source, String destination) throws Throwable {
    Path src = Paths.get(source);
    DirectoryStream<Path> directoryStream = Files.newDirectoryStream(src);
    try {
      for (Path sourcePaths : directoryStream) {
        Path file = sourcePaths.toAbsolutePath();
        CSVReader reader = new CSVReader(new FileReader(file.toString()));
        String[] headerRow = reader.readNext();
        List<String> headers = new ArrayList<>();
        for (int i = 0; i < headerRow.length; i++) {
          headers.add(headerRow[i]);
        }
        int index = headers.indexOf("Date");
        List<String> row = new ArrayList<>();
        if (index != -1) {
          String[] firstRow = reader.readNext();
          for (int i = 0; i < firstRow.length; i++) {
            row.add(firstRow[i]);
          }
          String newDate = getYesterdaysDate("MM/dd/yyyy");
          row.set(index, newDate);
        }
        String[] modifiedRow = new String[row.size()];
        modifiedRow = row.toArray(modifiedRow);

        List<String[]> allLines = new ArrayList<>();
        allLines.add(headerRow);
        allLines.add(modifiedRow);

        Path dest = Paths.get(destination);
        Path filename = sourcePaths.getFileName();
        Path finalLocation = dest.resolve(filename);

        writeCsvFile(allLines, finalLocation.toString());
        reader.close();
      }
    } catch (IOException e) {
      logger.error(e.getMessage());
    }
  }

  public void writeCsvFile(List<String[]> allRows, String destinationFile) {
    try {
      File f = new File(destinationFile);
      f.getParentFile().mkdirs();
      CSVWriter writer = new CSVWriter(new FileWriter(destinationFile));
      writer.writeAll(allRows);
      writer.close();
    } catch (IOException e) {
      logger.error(e.getMessage());
    }
  }

  public void copyCsvFilesToDestination(String source, String destination) throws Throwable {
    String absolutePath = new File(source).getAbsolutePath();
    Path src = Paths.get(absolutePath);
    Path dest = Paths.get(destination);
    try {
      if (!Files.isDirectory(dest)) {
        File file = new File(destination);
        file.mkdirs();
      }
      DirectoryStream<Path> directoryStream = Files.newDirectoryStream(src);
      for (Path path : directoryStream) {
        Files.copy(path, dest.resolve(path.getFileName()), StandardCopyOption.REPLACE_EXISTING);
      }
    } catch (IOException e) {
      logger.error(e.getMessage());
    }
  }

  public void deleteCsvFilesFromDestination(String destination) throws Throwable {
    Path dest = Paths.get(destination);
    try {
      if (Files.isDirectory(dest)) {
        File g = new File(destination);
        for (File f : g.listFiles()) {
          StringBuilder br = new StringBuilder();
          String tempString = br.append(destination).append("/").append(f.getName()).toString();
          Path Internaldest = Paths.get(tempString);
          if (Files.isDirectory(Internaldest)) {
            deleteCsvFilesFromDestination(tempString);
          }

          f.delete();
        }
      }
    } catch (IOException e) {
      logger.error(e.getMessage());
    }
  }

  private static String getYesterdaysDate(String pattern) {
    LocalDate today = LocalDate.now();
    String yesterday = today.minusDays(1).format(DateTimeFormatter.ofPattern(pattern));
    return yesterday;
  }

  public String getYesterdaysDate() throws Throwable {
    LocalDate today = LocalDate.now();
    String yesterday = today.minusDays(1).toString().concat(" 00:00:00");
    return yesterday;
  }
}
