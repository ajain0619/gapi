package com.nexage.app.services.impl;

import com.nexage.admin.core.model.GeoSegment;
import com.nexage.admin.core.repository.GeoSegmentRepository;
import com.nexage.app.services.GeoSegmentService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service("geoSegmentService")
public class GeoSegmentServiceImpl implements GeoSegmentService {

  private final GeoSegmentRepository geoSegmentRepository;

  /** {@inheritDoc} */
  @Override
  @PreAuthorize(
      "@loginUserContext.isOcUserNexage() or @loginUserContext.isOcAdminSeller() "
          + "or @loginUserContext.isOcManagerSeller() or @loginUserContext.isOcUserSeller()")
  public List<GeoSegment> getAllGeoSegments(
      String query, Long filterType, Integer limit, Integer page, String sort, String dir) {

    Direction direction = Direction.fromString(dir);
    PageRequest pageRequest = getPageRequest(limit, page, sort, direction);

    if (ignoreFiltering(filterType)) {
      return geoSegmentRepository.findByNameContainingIgnoreCase(query, pageRequest).getContent();
    } else {
      return geoSegmentRepository
          .findByNameContainingIgnoreCaseAndType(query, filterType, pageRequest)
          .getContent();
    }
  }

  private boolean ignoreFiltering(Long filterType) {
    return filterType == -1;
  }

  private PageRequest getPageRequest(
      Integer limit, Integer page, String sort, Direction direction) {
    // in spring PageRequest starts pagination from a page = 0
    int springPageNumber = page - 1;
    return PageRequest.of(springPageNumber, limit, direction, sort);
  }
}
