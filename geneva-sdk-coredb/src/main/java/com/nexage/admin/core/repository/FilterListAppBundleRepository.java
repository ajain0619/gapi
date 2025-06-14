package com.nexage.admin.core.repository;

import com.nexage.admin.core.model.filter.FilterListAppBundle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface FilterListAppBundleRepository
    extends JpaRepository<FilterListAppBundle, Integer>,
        JpaSpecificationExecutor<FilterListAppBundle> {}
