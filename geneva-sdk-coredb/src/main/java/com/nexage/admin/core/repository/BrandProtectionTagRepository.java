package com.nexage.admin.core.repository;

import com.nexage.admin.core.model.BrandProtectionTag;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BrandProtectionTagRepository
    extends JpaRepository<BrandProtectionTag, Long>, JpaSpecificationExecutor<BrandProtectionTag> {
  long countByCategoryPidAndPidIn(Long categoryPid, Set<Long> pidValues);
}
