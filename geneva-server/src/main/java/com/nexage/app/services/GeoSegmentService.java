package com.nexage.app.services;

import com.nexage.admin.core.model.GeoSegment;
import java.util.List;

public interface GeoSegmentService {

  /**
   * @param query search term. Contains with ignore case-sensitivity way
   * @param filterType search type: -1 no filter, 0 - search only for countries, 1 - search only for
   *     states
   * @param limit max items to be returned
   * @param page number of requested page starts from number 1
   * @param sort field to be used for sorting
   * @param dir - asc or desc
   * @return retrieved data
   */
  List<GeoSegment> getAllGeoSegments(
      String query, Long filterType, Integer limit, Integer page, String sort, String dir);
}
