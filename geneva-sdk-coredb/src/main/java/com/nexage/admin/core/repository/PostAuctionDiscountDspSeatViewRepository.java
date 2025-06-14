package com.nexage.admin.core.repository;

import com.nexage.admin.core.model.postauctiondiscount.PostAuctionDiscountDspSeatView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostAuctionDiscountDspSeatViewRepository
    extends JpaRepository<PostAuctionDiscountDspSeatView, Long> {}
