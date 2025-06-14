package com.nexage.admin.core.repository;

import com.nexage.admin.core.pubselfserve.CompanyPubSelfServeView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyPubSelfServeViewRepository
    extends JpaRepository<CompanyPubSelfServeView, Long> {}
