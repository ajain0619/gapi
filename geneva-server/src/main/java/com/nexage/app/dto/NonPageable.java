package com.nexage.app.dto;

import java.util.Collection;

/**
 * Defines basic operations for non-pageable data.
 *
 * @param <T> type of non-paged data
 */
public interface NonPageable<T> {

  /**
   * Returns a non-paged collection of objects.
   *
   * @return collection of objects
   */
  Collection<T> getContent();

  /**
   * Returns the total size of non-paged data.
   *
   * @return total size of data
   */
  int size();
}
