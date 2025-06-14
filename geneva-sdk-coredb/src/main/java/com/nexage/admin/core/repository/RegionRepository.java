package com.nexage.admin.core.repository;

import com.nexage.admin.core.sparta.jpa.model.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegionRepository extends JpaRepository<Region, Long> {}
