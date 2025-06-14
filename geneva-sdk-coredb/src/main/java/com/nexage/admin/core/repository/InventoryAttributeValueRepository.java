package com.nexage.admin.core.repository;

import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.InventoryAttributeValue;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryAttributeValueRepository
    extends JpaRepository<InventoryAttributeValue, Long>,
        JpaSpecificationExecutor<InventoryAttributeValue> {

  /**
   * Fetches paged list of inventory attribute values for specified attribute and seller.
   *
   * @param company seller company
   * @param attributePid attribute PID
   * @param pageable pageable object
   * @return paged list of attribute values
   */
  @Query(
      value =
          "SELECT v FROM InventoryAttributeValue v JOIN v.attribute a WHERE a.pid=:attributePid AND a.ownerCompany=:company")
  Page<InventoryAttributeValue> findAllValuesForAttribute(
      @Param("company") Company company,
      @Param("attributePid") Long attributePid,
      Pageable pageable);

  @Query(
      "SELECT v FROM InventoryAttributeValue v JOIN v.attribute a WHERE a.pid = :attributePid AND v.isEnabled = TRUE")
  List<InventoryAttributeValue> findAllEnabledByInventoryAttributePid(
      @Param("attributePid") Long attributePid);
}
