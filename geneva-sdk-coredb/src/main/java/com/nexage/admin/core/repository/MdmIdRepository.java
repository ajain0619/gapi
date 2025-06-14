package com.nexage.admin.core.repository;

import com.nexage.admin.core.model.CompanyMdmView;
import com.nexage.admin.core.model.MdmId;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MdmIdRepository extends JpaRepository<MdmId, Long> {

  @Query("SELECT c FROM CompanyMdmView c WHERE c.pid IN :companyPids")
  Page<CompanyMdmView> findMdmIdsForCompaniesIn(Set<Long> companyPids, Pageable pageable);
}
