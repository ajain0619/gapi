package com.nexage.admin.core.repository;

import com.nexage.admin.core.model.InventoryAttribute;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryAttributeRepository
    extends JpaRepository<InventoryAttribute, Long>, JpaSpecificationExecutor<InventoryAttribute> {

  /**
   * Combine {@link InventoryAttribute} and {@link
   * com.nexage.admin.core.model.InventoryAttributeValue} into a single projection, based on
   * request. This query does not accept search based in any query param.
   *
   * @param companyPid The companyPid
   * @param pageable Pagination based on {@link Pageable}
   * @return {@link Page} of {@link InventoryAttribute} instances based on parameters.
   */
  @Query(
      value =
          "SELECT new com.nexage.admin.core.model.InventoryAttribute("
              + " a.pid, a.name, a.status, a.lastUpdate, a.assignedLevel, a.companyPid, COUNT(DISTINCT v), COUNT (CASE WHEN v.isEnabled = 1 THEN 1 ELSE NULL END)) "
              + "FROM InventoryAttribute a LEFT JOIN InventoryAttributeValue v"
              + " ON a.pid = v.attribute.pid WHERE (a.hasGlobalVisibility = 1 OR a.ownerCompany.pid = :companyPid) GROUP BY a")
  Page<InventoryAttribute> findAllByCompanyPid(
      @Param("companyPid") Long companyPid, Pageable pageable);

  /**
   * Combine {@link InventoryAttribute} and {@link
   * com.nexage.admin.core.model.InventoryAttributeValue} into a single projection, based on
   * request. This query does not accept search based in any query param.
   *
   * @param companyPid The companyPid.
   * @param pageable Pagination based on {@link Pageable}
   * @return {@link Page} of {@link InventoryAttribute} instances based on parameters.
   */
  @Query(
      value =
          "SELECT new com.nexage.admin.core.model.InventoryAttribute("
              + " a.pid, a.name, a.status, a.lastUpdate, a.assignedLevel, a.companyPid, COUNT(DISTINCT v), COUNT (CASE WHEN v.isEnabled = 1 THEN 1 ELSE NULL END)) "
              + "FROM InventoryAttribute a LEFT JOIN InventoryAttributeValue v"
              + " ON a.pid = v.attribute.pid WHERE (a.hasGlobalVisibility = 1 OR a.ownerCompany.pid = :companyPid) AND a.name LIKE :name  GROUP BY a")
  Page<InventoryAttribute> findByName(
      @Param("companyPid") Long companyPid, @Param("name") String name, Pageable pageable);

  /**
   * Checks if inventory attribute with specified name and prefix exists.
   *
   * @param name attribute name
   * @param prefix attribute prefix
   * @return {@code true} if attribute exists, {@code false} otherwise
   */
  boolean existsByNameAndPrefix(@Param("name") String name, @Param("prefix") String prefix);

  /**
   * Find inventory attribute with given company ID and ID.
   *
   * @param companyPid company ID
   * @param pid attribute ID
   * @return inventory attribute
   */
  Optional<InventoryAttribute> findByCompanyPidAndPid(Long companyPid, Long pid);

  @Query("SELECT a FROM InventoryAttribute a WHERE a.status = 1")
  List<InventoryAttribute> findByActiveStatus(Pageable pageable);

  @Query(
      "SELECT distinct a FROM InventoryAttribute a LEFT JOIN a.visibleCompanies c "
          + "WHERE (a.hasGlobalVisibility = 1 OR c.pid = :companyPid) AND a.status >= 0")
  List<InventoryAttribute> findAllVisibleForCompany(@Param("companyPid") long companyPid);

  @Query(
      "SELECT distinct a FROM InventoryAttribute a LEFT JOIN a.visibleCompanies c "
          + "WHERE (a.hasGlobalVisibility = 1 OR c.pid = :companyPid) AND a.pid = :pid AND a.status >= 0")
  InventoryAttribute findVisibleForCompanyByPid(
      @Param("companyPid") Long companyPid, @Param("pid") Long pid);

  @Query("SELECT a FROM InventoryAttribute a WHERE a.pid=:pid AND a.status IN (0, 1)")
  InventoryAttribute findNotDeletedByPid(@Param("pid") long pid);

  InventoryAttribute findByNameAndPrefix(String name, String prefix);

  @Query(
      "SELECT count(a) FROM InventoryAttribute a "
          + "WHERE  a.status > 0 AND a.hasGlobalVisibility = :visibility AND a.pid IN (:attributePids)")
  long countByGlobalVisibility(
      @Param("visibility") boolean visibility, @Param("attributePids") List<Long> attributePids);
}
