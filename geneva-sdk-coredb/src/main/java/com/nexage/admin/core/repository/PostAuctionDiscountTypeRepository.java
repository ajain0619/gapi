package com.nexage.admin.core.repository;

import com.nexage.admin.core.model.postauctiondiscount.PostAuctionDiscountType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostAuctionDiscountTypeRepository
    extends JpaRepository<PostAuctionDiscountType, Long> {

  long countByPidIn(Iterable<Long> pids);

  List<PostAuctionDiscountType> findByName(String name);
}
