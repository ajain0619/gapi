package com.nexage.admin.core.repository;

import com.nexage.admin.core.model.ReportDefinition;
import com.ssp.geneva.common.model.inventory.CompanyType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportDefinitionRepository extends JpaRepository<ReportDefinition, Long> {

  Optional<ReportDefinition> findById(String id);

  List<ReportDefinition> findByCompanyTypesContainingOrderByDisplayOrder(CompanyType companyType);
}
