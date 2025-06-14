package com.nexage.admin.core.repository;

import com.nexage.admin.core.model.filter.FilterList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface FilterListRepository
    extends JpaRepository<FilterList, Long>, JpaSpecificationExecutor<FilterList> {}
