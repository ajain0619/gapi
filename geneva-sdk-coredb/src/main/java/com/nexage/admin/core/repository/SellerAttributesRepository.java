package com.nexage.admin.core.repository;

import com.nexage.admin.core.model.SellerAttributes;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SellerAttributesRepository extends JpaRepository<SellerAttributes, Long> {

  @EntityGraph(
      type = EntityGraph.EntityGraphType.FETCH,
      attributePaths = {"defaultRtbProfile"})
  List<SellerAttributes> findAll();

  Page<SellerAttributes> findAllBySellerPid(Long sellerPid, Pageable pageable);

  boolean existsBySellerPid(Long sellerPid);
}
