package com.nexage.admin.core.repository;

import com.nexage.admin.core.model.AdSource;
import com.nexage.admin.core.model.AdSource.SelfServeEnablement;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AdSourceRepository extends JpaRepository<AdSource, Long> {

  @Query("SELECT a FROM AdSource a WHERE a.status >= 0")
  List<AdSource> findAllNonDeleted();

  @Query("SELECT a FROM AdSource a WHERE a.status = 1 order by a.name")
  List<AdSource> findAllActiveOrderedByName();

  @Query("SELECT a FROM AdSource a WHERE a.status >= 0 AND a.pid IN :pids")
  List<AdSource> findNonDeletedByPidIn(@Param("pids") Collection<Long> pids);

  @Query("SELECT a FROM AdSource a JOIN a.company c WHERE c.pid = :companyPid and a.status >= 0")
  List<AdSource> findNonDeletedByCompanyPid(@Param("companyPid") Long companyPid);

  @Query(
      "SELECT a FROM AdSource a WHERE a.status >= 0 AND a.selfServeEnablement = :selfServeEnablement")
  List<AdSource> findNonDeletedBySelfServeEnablement(
      @Param("selfServeEnablement") SelfServeEnablement selfServeEnablement);
}
