package com.nexage.admin.core.repository;

import com.nexage.admin.core.model.BrandProtectionTagValues;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BrandProtectionTagValuesRepository
    extends JpaRepository<BrandProtectionTagValues, Long> {}
