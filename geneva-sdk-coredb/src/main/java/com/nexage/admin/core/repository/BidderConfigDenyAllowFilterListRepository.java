package com.nexage.admin.core.repository;

import com.nexage.admin.core.model.filter.BidderConfigDenyAllowFilterList;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BidderConfigDenyAllowFilterListRepository
    extends JpaRepository<BidderConfigDenyAllowFilterList, Integer>,
        JpaSpecificationExecutor<BidderConfigDenyAllowFilterList> {

  List<BidderConfigDenyAllowFilterList> findAllByFilterListPid(Integer filterListPid);
}
