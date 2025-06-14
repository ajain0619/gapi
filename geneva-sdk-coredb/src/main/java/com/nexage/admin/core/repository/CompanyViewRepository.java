package com.nexage.admin.core.repository;

import com.nexage.admin.core.model.CompanyView;
import com.ssp.geneva.common.model.inventory.CompanyType;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyViewRepository
    extends JpaRepository<CompanyView, Long>, JpaSpecificationExecutor<CompanyView> {

  @Query(
      "SELECT CASE WHEN COUNT(1) = :pidCount THEN true ELSE false END FROM CompanyView cv WHERE cv.pid in (:pids) AND cv.type = :type")
  boolean matchCompanyTypeWith(
      @Param("pids") Set<Long> pids,
      @Param("pidCount") Long pidCount,
      @Param("type") CompanyType type);

  @Query(
      "SELECT CASE WHEN COUNT(1) = :pidCount THEN true ELSE false END FROM CompanyView cv WHERE cv.pid in (:pidlists)")
  boolean existsByPidIn(@Param("pidlists") Set<Long> pidlists, @Param("pidCount") Long pidCount);

  @Query(
      "SELECT CASE WHEN COUNT(cv) > 0  THEN true ELSE false END FROM CompanyView cv WHERE cv.pid = :sellerPid AND cv.sellerSeatPid = :sellerSeatPid")
  boolean isSellerAssociatedWithSellerSeat(
      @Param("sellerPid") Long sellerPid, @Param("sellerSeatPid") Long sellerSeatPid);

  @Query("SELECT c FROM CompanyView c WHERE c.type = :type")
  List<CompanyView> findCompaniesByType(@Param("type") CompanyType type);

  @Query("SELECT c FROM CompanyView c WHERE c.pid IN  (:ids)")
  List<CompanyView> findCompaniesByIds(@Param("ids") List<Long> ids);

  @Query(
      "SELECT cv FROM CompanyView cv  WHERE cv.type='SELLER' AND cv.sellerSeatPid = :sellerSeatPid")
  List<CompanyView> findCompaniesAssociatedWithSellerSeat(
      @Param("sellerSeatPid") long sellerSeatPid);
}
