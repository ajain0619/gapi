package com.nexage.app.services;

import com.nexage.admin.core.model.feeadjustment.FeeAdjustment;
import com.nexage.app.dto.feeadjustment.FeeAdjustmentDTO;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FeeAdjustmentService {

  /**
   * Create a {@link FeeAdjustment} JPA persistence entity for this {@link FeeAdjustmentDTO} object.
   *
   * @param feeAdjustmentDTO
   * @return A new {@link FeeAdjustmentDTO} object representing the new {@link FeeAdjustment} JPA
   *     persistence entity.
   */
  FeeAdjustmentDTO create(FeeAdjustmentDTO feeAdjustmentDTO);

  /**
   * Update the {@link FeeAdjustment} JPA persistence entity corresponding to this {@link
   * FeeAdjustmentDTO} object (looks up the entity using the pid field of the {@link
   * FeeAdjustmentDTO} object).
   *
   * @param feeAdjustmentDTO
   * @return A new {@link FeeAdjustmentDTO} object representing the new state of the {@link
   *     FeeAdjustment} JPA persistence entity.
   */
  FeeAdjustmentDTO update(FeeAdjustmentDTO feeAdjustmentDTO);

  /**
   * Retrieve the {@link FeeAdjustmentDTO} object for the {@link FeeAdjustment} JPA persistence
   * entity with the specified pid.
   *
   * @param feeAdjustmentPid The pid of the {@link FeeAdjustment} entity to retrieve.
   * @return A {@link FeeAdjustmentDTO} object representing the state of the {@link FeeAdjustment}
   *     JPA persistence entity.
   */
  FeeAdjustmentDTO get(Long feeAdjustmentPid);

  /**
   * Retrieve a page of {@link FeeAdjustmentDTO} available objects based on the query/page
   * parameters. Note that this service is intended to provide a mechanism to discover any available
   * {@link FeeAdjustment} entities which are available.
   *
   * @param qf A unique {@link Set} of fields.
   * @param qt The term to be found.
   * @param enabled The value to match on the "enabled" field.
   * @param pageable The {@link Pageable} pagination parameters.
   * @return {@link Page} of {@link FeeAdjustmentDTO} objects based on the query/page parameters.
   */
  Page<FeeAdjustmentDTO> getAll(Set<String> qf, String qt, Boolean enabled, Pageable pageable);

  /**
   * Delete the {@link FeeAdjustment} entity with the specified pid.
   *
   * @param feeAdjustmentPid The pid of the {@link FeeAdjustment} entity to delete.
   * @return A {@link FeeAdjustmentDTO} object with only the pid of the object that was deleted.
   */
  FeeAdjustmentDTO delete(Long feeAdjustmentPid);
}
