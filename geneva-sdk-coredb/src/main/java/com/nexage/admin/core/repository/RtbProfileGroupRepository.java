package com.nexage.admin.core.repository;

import com.nexage.admin.core.model.RtbProfileGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RtbProfileGroupRepository extends JpaRepository<RtbProfileGroup, Long> {
  boolean existsByPidAndIsUICustomGroup(long pid, boolean isUICustomGroup);
}
