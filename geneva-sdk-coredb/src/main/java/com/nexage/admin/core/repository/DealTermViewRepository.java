package com.nexage.admin.core.repository;

import com.nexage.admin.core.sparta.jpa.model.DealTermView;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DealTermViewRepository extends JpaRepository<DealTermView, Long> {

  /**
   * Fetch deal terms based on site and list of tags.
   *
   * @param sitePid site PID
   * @param tagPids tags PIDs
   * @return list of deal terms
   */
  List<DealTermView> findBySitePidAndTagPidInOrderByEffectiveDateDesc(
      Long sitePid, List<Long> tagPids);
}
