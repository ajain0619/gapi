package com.nexage.admin.core.repository;

import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.model.PlaylistRenderingCapability;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Provides operations for interacting with sdk_capability table via {@link
 * PlaylistRenderingCapability}.
 */
@Repository
public interface PlaylistRenderingCapabilityRepository
    extends JpaRepository<PlaylistRenderingCapability, Long> {

  /**
   * Count number of sdk_capability with a given status and value in the given set.
   *
   * @param status the status to filter by
   * @param values the values to count the number of rows for
   * @return the count of sdk_capability with value in the given set
   */
  int countByStatusAndValueIn(Status status, Set<String> values);

  /**
   * Find page of {@link PlaylistRenderingCapability} with a given status.
   *
   * @param pageable the specification of the page to find
   * @param status the status to filter by
   * @return the found page of {@link PlaylistRenderingCapability}
   */
  Page<PlaylistRenderingCapability> findAllByStatus(Pageable pageable, Status status);
}
