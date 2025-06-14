package com.nexage.admin.core.repository;

import com.nexage.admin.core.model.Advertiser;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AdvertiserRepository extends JpaRepository<Advertiser, Long> {

  @Query(
      "SELECT advertiser FROM Advertiser advertiser WHERE advertiser.sellerId = :sellerId and advertiser.status <> 2")
  List<Advertiser> findAllBySellerIdAndStatusNotDeleted(@Param("sellerId") Long sellerId);

  List<Advertiser> findAllBySellerId(Long sellerId);
}
