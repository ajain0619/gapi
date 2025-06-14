package com.nexage.admin.core.repository;

import com.nexage.admin.core.pubselfserve.AdsourcePubSelfServeView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdsourcePubSelfServeViewRepository
    extends JpaRepository<AdsourcePubSelfServeView, Long> {}
