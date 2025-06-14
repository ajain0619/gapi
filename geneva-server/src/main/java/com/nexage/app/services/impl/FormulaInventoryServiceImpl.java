package com.nexage.app.services.impl;

import static com.nexage.app.dto.sellingrule.formula.FormulaAttributeDTO.INVENTORY_ATTRIBUTE;

import com.nexage.admin.core.model.RuleFormulaPositionView;
import com.nexage.admin.core.model.placementformula.formula.impl.Group;
import com.nexage.admin.core.repository.AttributeCompanyVisibilityRepository;
import com.nexage.admin.core.repository.InventoryAttributeRepository;
import com.nexage.admin.core.repository.RuleFormulaPositionViewRepository;
import com.nexage.admin.core.specification.RuleSpecification;
import com.nexage.app.dto.sellingrule.FormulaInventoryDTO;
import com.nexage.app.dto.sellingrule.formula.FormulaGroupDTO;
import com.nexage.app.dto.sellingrule.formula.PlacementFormulaDTO;
import com.nexage.app.mapper.FormulaInventoryDTOMapper;
import com.nexage.app.mapper.PlacementFormulaDTOMapper;
import com.nexage.app.services.FormulaInventoryService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Log4j2
public class FormulaInventoryServiceImpl implements FormulaInventoryService {
  private final RuleFormulaPositionViewRepository ruleFormulaPositionViewRepository;
  private final InventoryAttributeRepository inventoryAttributeRepository;
  private final AttributeCompanyVisibilityRepository attributeCompanyVisibilityRepository;

  @Autowired
  public FormulaInventoryServiceImpl(
      RuleFormulaPositionViewRepository ruleFormulaPositionViewRepository,
      InventoryAttributeRepository inventoryAttributeRepository,
      AttributeCompanyVisibilityRepository attributeCompanyVisibilityRepository) {
    this.ruleFormulaPositionViewRepository = ruleFormulaPositionViewRepository;
    this.inventoryAttributeRepository = inventoryAttributeRepository;
    this.attributeCompanyVisibilityRepository = attributeCompanyVisibilityRepository;
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(readOnly = true)
  @PreAuthorize(
      "@loginUserContext.isOcAdminNexage() "
          + "or @loginUserContext.isOcManagerNexage() "
          + "or @loginUserContext.isOcManagerYieldNexage() "
          + "or @loginUserContext.isOcManagerSmartexNexage() "
          + "or ((@loginUserContext.isOcAdminSeller() or @loginUserContext.isOcManagerSeller()) "
          + "and @loginUserContext.doSameOrNexageAffiliation(#publisherPid))")
  public Page<FormulaInventoryDTO> getPlacementsByFormulaForPublisher(
      Long publisherPid, @Valid PlacementFormulaDTO formulaDto, Pageable pageable) {
    Page<RuleFormulaPositionView> foundPlacements =
        ruleFormulaPositionViewRepository.findAll(
            RuleSpecification.withDefaultRtbProfiles(
                Collections.singleton(publisherPid),
                PlacementFormulaDTOMapper.MAPPER.map(formulaDto)),
            pageable);
    List<FormulaInventoryDTO> FormulaInventoryDTOS =
        foundPlacements.stream()
            .map(placement -> FormulaInventoryDTOMapper.MAPPER.map(placement))
            .collect(Collectors.toList());

    return new PageImpl<>(FormulaInventoryDTOS, pageable, foundPlacements.getTotalElements());
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(readOnly = true)
  @PreAuthorize("@loginUserContext.isOcUserNexage()")
  public Page<FormulaInventoryDTO> getPlacementsByFormulaForDeals(
      @Valid PlacementFormulaDTO formulaDto, Pageable pageable) {
    Page<RuleFormulaPositionView> foundPlacements = findPlacementsByFormula(formulaDto, pageable);
    List<FormulaInventoryDTO> FormulaInventoryDTOS =
        foundPlacements.stream()
            .map(placement -> FormulaInventoryDTOMapper.MAPPER.map(placement))
            .collect(Collectors.toList());
    return new PageImpl<>(FormulaInventoryDTOS, pageable, foundPlacements.getTotalElements());
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(readOnly = true)
  @PreAuthorize("@loginUserContext.isOcUserNexage() or @loginUserContext.isOcUserSeller()")
  public Page<RuleFormulaPositionView> findPlacementsByFormula(
      @Valid PlacementFormulaDTO formulaDto, Pageable pageable) {
    List<FormulaGroupDTO> formulaGroups = formulaDto.getFormulaGroups();
    List<Long> attributePids = new ArrayList<>();

    if (!CollectionUtils.isEmpty(formulaGroups)) {
      formulaGroups.stream()
          .map(FormulaGroupDTO::getFormulaRules)
          .filter(formularules -> formularules != null && !formularules.isEmpty())
          .flatMap(Collection::stream)
          .forEach(
              eachFormula -> {
                if (eachFormula.getAttribute() == INVENTORY_ATTRIBUTE) {
                  attributePids.add(eachFormula.getAttributePid());
                }
              });
    }

    Page<RuleFormulaPositionView> result;
    Group<RuleFormulaPositionView> groupEntity = PlacementFormulaDTOMapper.MAPPER.map(formulaDto);

    if (CollectionUtils.isEmpty(attributePids)) {
      result =
          ruleFormulaPositionViewRepository.findAll(
              RuleSpecification.withDefaultRtbProfiles(Collections.emptyList(), groupEntity),
              pageable);
    } else {
      if (inventoryAttributeRepository.countByGlobalVisibility(true, attributePids) > 0) {
        result =
            ruleFormulaPositionViewRepository.findAll(
                RuleSpecification.withDefaultRtbProfiles(Collections.emptyList(), groupEntity),
                pageable);
      } else {
        List<Long> companyPids =
            attributeCompanyVisibilityRepository.findCompaniesForAttributes(attributePids);
        result =
            ruleFormulaPositionViewRepository.findAll(
                RuleSpecification.withDefaultRtbProfiles(companyPids, groupEntity), pageable);
      }
    }
    return result;
  }
}
