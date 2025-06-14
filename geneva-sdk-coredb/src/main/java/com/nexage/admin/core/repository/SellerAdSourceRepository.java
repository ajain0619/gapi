package com.nexage.admin.core.repository;

import com.nexage.admin.core.sparta.jpa.model.SellerAdSource;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerAdSourceRepository extends JpaRepository<SellerAdSource, Long> {

  List<SellerAdSource> findAllBySellerPid(long sellerPid);

  Optional<SellerAdSource> findBySellerPidAndAdSourcePid(long sellerPid, long adSourcePid);

  boolean existsBySellerPidAndAdSourcePid(long sellerPid, long adSourcePid);

  void deleteBySellerPidAndAdSourcePid(long sellerPid, long adSourcePid);
}
