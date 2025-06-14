package com.nexage.admin.core.repository;

import com.nexage.admin.core.model.AttributeCompanyVisibility;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AttributeCompanyVisibilityRepository
    extends JpaRepository<AttributeCompanyVisibility, Long> {

  @Query(
      "SELECT distinct(acv.companyPid) FROM AttributeCompanyVisibility acv "
          + "WHERE acv.attributePid IN (:attributePids)")
  List<Long> findCompaniesForAttributes(@Param("attributePids") List<Long> attributePids);
}
