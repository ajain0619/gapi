package com.nexage.app.util.validator.deals;

import static java.nio.charset.StandardCharsets.UTF_8;

import au.com.bytecode.opencsv.CSVParser;
import com.nexage.admin.core.model.CompanyView;
import com.nexage.admin.core.model.SiteView;
import com.nexage.admin.core.sparta.jpa.model.PositionView;
import com.nexage.app.error.ServerErrorCodes;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@Log4j2
public class DealSpecificInventoriesFileParser {

  private static final int FILE_SIZE_LIMIT = 5000000;
  private static final int NUMBER_OF_ENTRIES_LIMIT = 50000;

  private static final int SELLER_ID_POS = 0;
  private static final int APP_OR_SITE_ID_POS = 1;
  private static final int PLACEMENT_ID_POS = 2;

  private boolean isValidHeader(int sellerIdIndex, int appOrSiteIdIndex, int placementIdIndex) {
    return (sellerIdIndex != -1 && appOrSiteIdIndex != -1 && placementIdIndex != -1);
  }

  private List<String> getEntityIds(
      String[] entries, int sellerIdIndex, int appOrSiteIdIndex, int placementIdIndex) {

    var sellerId = "";
    var appOrSiteId = "";
    var placementId = "";

    if (entries.length > sellerIdIndex) {
      sellerId = entries[sellerIdIndex];
    }
    if (entries.length > appOrSiteIdIndex) {
      appOrSiteId = entries[appOrSiteIdIndex];
    }
    if (entries.length > placementIdIndex) {
      placementId = entries[placementIdIndex];
    }

    return List.of(sellerId, appOrSiteId, placementId);
  }

  public List[] processSpecificInventoriesFile(MultipartFile inventoriesFile) {

    if (inventoriesFile.getSize() > FILE_SIZE_LIMIT) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_FILE_SIZE);
    }

    String[] entries = null;
    var noOfEntries = 0;

    List<PositionView> positionViews = new ArrayList<>();
    List<SiteView> siteViews = new ArrayList<>();
    List<CompanyView> companyViews = new ArrayList<>();

    try (var inputStream = inventoriesFile.getInputStream()) {
      var reader = new BufferedReader(new InputStreamReader(inputStream, UTF_8));
      String line = reader.readLine();
      // Parse header and find the indexes of seller, app/site, placement Id
      entries = StringUtils.stripAll(new CSVParser(CSVParser.DEFAULT_SEPARATOR).parseLine(line));
      List<String> headers = Arrays.asList(entries);
      int sellerIdIndex = headers.indexOf("Seller Id");
      int appOrSiteIdIndex = headers.indexOf("App/Site Id");
      int placementIdIndex = headers.indexOf("Placement Id");
      if (!isValidHeader(sellerIdIndex, appOrSiteIdIndex, placementIdIndex)) {
        log.error("Header is missing in csv file : {}", inventoriesFile.getName());
        throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_CSV_OR_EXCEL_FILE);
      }
      while (reader.ready() && ++noOfEntries <= NUMBER_OF_ENTRIES_LIMIT) {
        line = reader.readLine();
        entries = StringUtils.stripAll(new CSVParser(CSVParser.DEFAULT_SEPARATOR).parseLine(line));
        var values = getEntityIds(entries, sellerIdIndex, appOrSiteIdIndex, placementIdIndex);
        if (!StringUtils.isEmpty(values.get(PLACEMENT_ID_POS))) {
          var siteView =
              new SiteView(
                  Long.parseLong(values.get(APP_OR_SITE_ID_POS)),
                  null,
                  Long.parseLong(values.get(SELLER_ID_POS)),
                  null);
          positionViews.add(
              PositionView.builder()
                  .pid(Long.parseLong(values.get(PLACEMENT_ID_POS)))
                  .sitePid(siteView.getPid())
                  .siteView(siteView)
                  .build());
        } else if (!StringUtils.isEmpty(values.get(APP_OR_SITE_ID_POS))) {
          siteViews.add(
              new SiteView(
                  Long.parseLong(values.get(APP_OR_SITE_ID_POS)),
                  null,
                  Long.parseLong(values.get(SELLER_ID_POS)),
                  null));
        } else if (!StringUtils.isEmpty(values.get(SELLER_ID_POS))) {
          companyViews.add(new CompanyView(Long.parseLong(values.get(SELLER_ID_POS)), null));
        } else {
          throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_CSV_OR_EXCEL_FILE);
        }
      }
    } catch (Exception exception) {
      log.error(
          "failed to parse csv file : {} : {}", inventoriesFile.getName(), exception.getMessage());
      throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_CSV_OR_EXCEL_FILE);
    }

    if (noOfEntries > NUMBER_OF_ENTRIES_LIMIT) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_NUMBER_OF_ENTRIES);
    }

    return new List[] {companyViews, siteViews, positionViews};
  }
}
