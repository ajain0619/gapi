package com.nexage.admin.core.repository;

import com.nexage.admin.core.bidder.model.BdrExchangeCompany;
import com.nexage.admin.core.bidder.model.BdrExchangeCompanyPk;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BdrExchangeCompanyRepository
    extends JpaRepository<BdrExchangeCompany, BdrExchangeCompanyPk> {

  List<BdrExchangeCompany> findByExchangeCompanyPk_Company_Pid(Long pid);

  void deleteByExchangeCompanyPk_Company_PidAndExchangeCompanyPk_BidderExchange_Pid(
      Long companyPid, Long bdrexchangePid);
}
