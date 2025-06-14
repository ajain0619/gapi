package com.nexage.admin.core.repository;

import com.nexage.admin.core.model.DealRtbProfileViewUsingFormulas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface DealRtbProfileViewRepository
    extends JpaRepository<DealRtbProfileViewUsingFormulas, Long>,
        JpaSpecificationExecutor<DealRtbProfileViewUsingFormulas> {}
