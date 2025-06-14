package com.nexage.app.services;

import com.nexage.app.dto.IsoLanguageDTO;
import com.nexage.app.util.validator.SearchRequestParamConstraint;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IsoLanguageService {
  Page<IsoLanguageDTO> findAll(
      String qt,
      @SearchRequestParamConstraint(allowedParams = "languageName") Set<String> qf,
      Pageable pageable);
}
