package com.nexage.app.services;

import com.nexage.admin.core.dto.SearchSummaryDTO;
import com.ssp.geneva.common.model.inventory.CompanyType;
import java.util.List;

public interface SearchService<T extends SearchSummaryDTO> {

  List<T> findCompanySearchDtosByTypeAndNamePrefix(String prefix, CompanyType type);

  List<T> findSearchSummaryDtosContaining(String prefix);
}
