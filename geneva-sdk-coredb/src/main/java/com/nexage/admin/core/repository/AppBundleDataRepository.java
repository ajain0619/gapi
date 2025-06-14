package com.nexage.admin.core.repository;

import com.nexage.admin.core.model.AppBundleData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AppBundleDataRepository
    extends JpaRepository<AppBundleData, Integer>, JpaSpecificationExecutor<AppBundleData> {

  /**
   * Fetches a single App by app bundle
   *
   * @param appBundleId{@link String}
   * @return {@link AppBundleData}
   */
  AppBundleData findByAppBundleId(String appBundleId);
}
