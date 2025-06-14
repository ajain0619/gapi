package com.nexage.admin.core.repository;

import com.nexage.admin.core.sparta.jpa.model.PositionView;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PositionViewRepository
    extends JpaRepository<PositionView, Long>, JpaSpecificationExecutor<PositionView> {
  @Query(
      "SELECT NEW com.nexage.admin.core.sparta.jpa.model.PositionView(p.pid, p.name, p.status, p.version, p.screenLocation, p.positionAliasName, p.adSizeType) FROM Position p WHERE  p.sitePid = :sitePid")
  Page<PositionView> findAllPlacements(@Param("sitePid") Long sitePid, Pageable pageable);

  @Query(
      "SELECT NEW com.nexage.admin.core.sparta.jpa.model.PositionView(p.pid, p.name, p.status, p.version, p.screenLocation, p.positionAliasName, p.adSizeType) FROM Position p WHERE p.sitePid = :sitePid AND p.name LIKE %:qt%")
  Page<PositionView> searchPlacementsByName(
      @Param("sitePid") Long sitePid, @Param("qt") String qt, Pageable pageable);

  @Query(value = "SELECT p  FROM PositionView p WHERE p.pid IN :positionPids")
  List<PositionView> findAllByPidsIn(@Param("positionPids") Collection<Long> positionPids);
}
