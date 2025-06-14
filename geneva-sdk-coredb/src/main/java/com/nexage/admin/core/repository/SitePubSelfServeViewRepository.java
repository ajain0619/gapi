package com.nexage.admin.core.repository;

import com.nexage.admin.core.pubselfserve.SitePubSelfServeView;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SitePubSelfServeViewRepository extends JpaRepository<SitePubSelfServeView, Long> {
  @Query(
      "SELECT sitePubSelfServeView FROM SitePubSelfServeView sitePubSelfServeView WHERE sitePubSelfServeView.pubPid = :pubPid and sitePubSelfServeView.status <> -1")
  List<SitePubSelfServeView> findAllByPubPidAndStatusNotDeleted(@Param("pubPid") long pubPid);
}
