package com.nexage.admin.core.repository;

import com.nexage.admin.core.model.AppAlias;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AppAliasRepository
    extends JpaRepository<AppAlias, Integer>, JpaSpecificationExecutor<AppAlias> {

  /**
   * Fetches a single App by app alias
   *
   * @param appAlias{@link String}
   * @return {@link AppAlias}
   */
  AppAlias findByAppAlias(String appAlias);
}
