package com.nexage.admin.core.repository;

import com.nexage.admin.core.model.BidderConfig;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BidderConfigRepository
    extends JpaRepository<BidderConfig, Long>, JpaSpecificationExecutor<BidderConfig> {

  /**
   * Finds companyPid for that bidder
   *
   * @return the companyPid corresponding to that bidder.
   */
  @Query("select b.companyPid from BidderConfig b where b.pid = ?1")
  Long findCompanyPidByPid(Long pid);

  long countByPidIn(Collection<Long> pids);

  List<BidderConfig> findByCompanyPid(Long companyPid);

  List<BidderConfig> findByTrafficStatus(boolean active);

  long countByPidAndCompany_buyerGroups_pidIn(Long bidderPid, Collection<Long> buyerGroupPids);

  long countByPidAndCompany_buyerSeats_seatIn(Long bidderPid, Collection<String> buyerSeats);

  @Query("SELECT bc.pid FROM BidderConfig bc WHERE bc.companyPid = :companyPid")
  Set<Long> findPidsByCompanyPid(@NotNull @Param("companyPid") Long companyPid);
}
