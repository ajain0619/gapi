package com.nexage.admin.core.repository;

import com.nexage.admin.core.model.CrsTagMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CrsTagMappingRepository extends JpaRepository<CrsTagMapping, Long> {
  @Query("DELETE from CrsTagMapping c WHERE c.pid = ?1")
  @Modifying
  void deleteByPid(long pid);
}
