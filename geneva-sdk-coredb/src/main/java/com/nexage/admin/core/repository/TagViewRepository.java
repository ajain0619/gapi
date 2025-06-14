package com.nexage.admin.core.repository;

import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.sparta.jpa.model.TagView;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagViewRepository extends JpaRepository<TagView, Long> {

  /**
   * Find a tag based on it's PID and statuses.
   *
   * @param pid tag pid
   * @param statuses list of statuses
   * @return tag
   */
  Optional<TagView> findByPidAndSitePidAndPositionPidAndStatusNotIn(
      Long pid, Long sitePid, Long positionPid, List<Status> statuses);

  /**
   * Find a list of tags based on tag PID, position PID and status.
   *
   * @param sitePid site PID
   * @param positionPid position PID
   * @param statuses list of statuses
   * @return list of tags
   */
  List<TagView> findBySitePidAndPositionPidAndStatusNotIn(
      Long sitePid, Long positionPid, List<Status> statuses);
}
