package com.nexage.app.web;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexage.admin.core.enums.AdSizeFilter;
import com.nexage.app.web.BuyerController.AdSizeJson;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class AdSizesTest {

  @Test
  void whenComparingAdSizes_thenListsOfAdSizesFromJsonFilesAndEnumMatch() throws IOException {
    List<AdSizeFilter> listFromCreateTimes = fetchAdSizesFilter("/static/json/creative-times.json");
    List<AdSizeFilter> listFromCreateSizes = fetchAdSizesFilter("/static/json/creative-sizes.json");
    List<AdSizeFilter> listFromEnum = Arrays.asList(AdSizeFilter.values());

    assertEquals(
        listFromCreateTimes,
        listFromCreateSizes,
        "Lists of ad sizes from the 2 json files should be the same");
    assertEquals(
        listFromCreateSizes,
        listFromEnum,
        "List of ad sizes from json file should be the same as from enum");
  }

  private List<AdSizeFilter> fetchAdSizesFilter(String pathToJson) throws IOException {
    try (InputStream json = BuyerController.class.getResourceAsStream(pathToJson)) {
      AdSizeJson[] arr = new ObjectMapper().readValue(json, AdSizeJson[].class);
      return Arrays.stream(arr)
          .map(adsize -> AdSizeFilter.fromActual(adsize.getText()))
          .collect(toList());
    }
  }
}
