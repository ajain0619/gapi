package com.nexage.admin.core.repository;

import com.nexage.admin.core.model.IsoLanguage;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface IsoLanguageRepository
    extends JpaRepository<IsoLanguage, Long>, JpaSpecificationExecutor<IsoLanguage> {
  List<IsoLanguage> findByLanguageCodeIn(Collection<String> languages);
}
