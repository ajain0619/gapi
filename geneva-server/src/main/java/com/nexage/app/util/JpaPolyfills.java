package com.nexage.app.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JpaPolyfills {

  /**
   * Given a Pageable recreates it by modifying the sort property This util is necessary because JPA
   * wrongly adds an extra prefix if the sort predicate reside in the Table being joined to
   *
   * <p>For example if sort=revenue,DESC, JPA will add [ORDER BY rootTableAlias.revenue DESC] in the
   * final SQL even though an alias was specified during the column SELECT. This only happens when
   * COALESCE or CASE WHEN are used in a column SELECT
   *
   * @param pageable
   * @param aliasMap A map that maps a sort property to a table alias
   * @return Pageable with modified sort properties
   * @see com.nexage.admin.core.repository.PositionRepository
   */
  public static Pageable createPageRequestWithTableAliases(
      Pageable pageable, Map<String, String> aliasMap) {
    if (aliasMap == null || aliasMap.isEmpty() || pageable.getSort() == null) {
      return pageable;
    }
    Iterator<Sort.Order> it = pageable.getSort().iterator();
    List<Sort.Order> orders = new ArrayList<>();
    while (it.hasNext()) {
      Sort.Order sortOrder = it.next();
      String newPropPrefix =
          aliasMap.get(sortOrder.getProperty()) == null
              ? ""
              : aliasMap.get(sortOrder.getProperty()) + ".";
      Sort.Order newSortOrder =
          new Sort.Order(sortOrder.getDirection(), newPropPrefix + sortOrder.getProperty());
      orders.add(newSortOrder);
    }
    Sort s = Sort.by(orders);
    return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), s);
  }

  /**
   * Returns a new Pageable consisting of the sort order list of the current Pageable combined with
   * the given property.
   *
   * @param pageable
   * @param property Property to be added at the end of the sort list
   * @return Pageable with extended sort list
   */
  public static Pageable addSortBy(Pageable pageable, String property) {
    return PageRequest.of(
        pageable.getPageNumber(),
        pageable.getPageSize(),
        pageable.getSort().and(Sort.by(property)));
  }
}
