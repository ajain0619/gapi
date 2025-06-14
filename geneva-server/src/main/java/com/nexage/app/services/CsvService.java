package com.nexage.app.services;

import java.util.Map;
import org.springframework.core.io.InputStreamResource;

public interface CsvService {
  /**
   * Creates a csv of the passed in TreeMap and returns the InputStreamResource to be used as an
   * attachment header in the response
   *
   * @param treeMap The map of values to be made into a csv file format
   * @return InputStreamResource To be used in the attachment of the response entity
   */
  InputStreamResource create(Map<String, String> treeMap);
}
