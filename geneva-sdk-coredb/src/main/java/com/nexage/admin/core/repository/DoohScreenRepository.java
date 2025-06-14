package com.nexage.admin.core.repository;

import com.nexage.admin.core.model.DoohScreen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DoohScreenRepository
    extends JpaRepository<DoohScreen, Long>, JpaSpecificationExecutor<DoohScreen> {}
