package com.nexage.app.services.impl;

import com.nexage.admin.core.model.feeadjustment.FeeAdjustment;
import com.nexage.admin.core.repository.FeeAdjustmentCompanyViewRepository;
import com.nexage.admin.core.repository.FeeAdjustmentRepository;
import com.nexage.admin.core.specification.FeeAdjustmentSpecification;
import com.nexage.app.dto.feeadjustment.FeeAdjustmentDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.mapper.feeadjustment.FeeAdjustmentDTOMapper;
import com.nexage.app.services.FeeAdjustmentService;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Log4j2
@Service("feeAdjustmentService")
@PreAuthorize("@loginUserContext.isOcAdminNexage() or @loginUserContext.isOcManagerNexage()")
@Transactional
public class FeeAdjustmentServiceImpl implements FeeAdjustmentService {

  private final FeeAdjustmentRepository feeAdjustmentRepository;

  private final FeeAdjustmentCompanyViewRepository feeAdjustmentCompanyViewRepository;

  /** {@inheritDoc} */
  @Override
  @PreAuthorize("@loginUserContext.isOcAdminNexage() or @loginUserContext.isOcManagerYieldNexage()")
  public FeeAdjustmentDTO create(FeeAdjustmentDTO feeAdjustmentDTO) {
    return FeeAdjustmentDTOMapper.MAPPER.map(
        feeAdjustmentRepository.saveAndFlush(
            FeeAdjustmentDTOMapper.MAPPER.map(
                feeAdjustmentDTO, new FeeAdjustment(), feeAdjustmentCompanyViewRepository)),
        false);
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize("@loginUserContext.isOcAdminNexage() or @loginUserContext.isOcManagerYieldNexage()")
  public FeeAdjustmentDTO update(FeeAdjustmentDTO feeAdjustmentDTO) {
    Optional<FeeAdjustment> optionalFeeAdjustment =
        feeAdjustmentRepository.findById(feeAdjustmentDTO.getPid());

    return FeeAdjustmentDTOMapper.MAPPER.map(
        feeAdjustmentRepository.saveAndFlush(
            FeeAdjustmentDTOMapper.MAPPER.map(
                feeAdjustmentDTO,
                optionalFeeAdjustment.orElseThrow(
                    () ->
                        new GenevaValidationException(
                            ServerErrorCodes.SERVER_FEE_ADJUSTMENT_NOT_FOUND)),
                feeAdjustmentCompanyViewRepository)),
        false);
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize("@loginUserContext.isOcAdminNexage() or @loginUserContext.isOcManagerYieldNexage()")
  public FeeAdjustmentDTO get(Long feeAdjustmentPid) {
    return FeeAdjustmentDTOMapper.MAPPER.map(
        feeAdjustmentRepository
            .findById(feeAdjustmentPid)
            .orElseThrow(
                () ->
                    new GenevaValidationException(
                        ServerErrorCodes.SERVER_FEE_ADJUSTMENT_NOT_FOUND)),
        false);
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize("@loginUserContext.isOcUserNexage()")
  public Page<FeeAdjustmentDTO> getAll(
      Set<String> qf, String qt, Boolean enabled, Pageable pageable) {
    return feeAdjustmentRepository
        .findAll(
            FeeAdjustmentSpecification.withQueryFieldsAndSearchTermAndEnabled(qf, qt, enabled),
            pageable)
        .map(feeAdjustment -> FeeAdjustmentDTOMapper.MAPPER.map(feeAdjustment, true));
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize("@loginUserContext.isOcAdminNexage() or @loginUserContext.isOcManagerYieldNexage()")
  public FeeAdjustmentDTO delete(Long feeAdjustmentPid) {
    if (!feeAdjustmentRepository.existsById(feeAdjustmentPid)) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_FEE_ADJUSTMENT_NOT_FOUND);
    }

    feeAdjustmentRepository.deleteById(feeAdjustmentPid);

    return FeeAdjustmentDTO.builder().pid(feeAdjustmentPid).build();
  }
}
