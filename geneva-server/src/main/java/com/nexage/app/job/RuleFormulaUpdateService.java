package com.nexage.app.job;

import com.nexage.admin.core.model.CompanyRule;
import com.nexage.admin.core.model.RuleDeployedPosition;
import com.nexage.admin.core.model.RuleFormulaPositionView;
import com.nexage.admin.core.model.placementformula.formula.impl.Group;
import com.nexage.admin.core.repository.RuleDeployedPositionRepository;
import com.nexage.admin.core.repository.RuleFormulaPositionViewRepository;
import com.nexage.admin.core.repository.RuleRepository;
import com.nexage.admin.core.specification.RuleSpecification;
import com.nexage.app.dto.sellingrule.formula.PlacementFormulaDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.util.assemblers.sellingrule.PlacementFormulaAssembler;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Transactional
public class RuleFormulaUpdateService extends AbstractPlacementFormulaUpdateService {

  private final RuleRepository ruleRepository;
  private final RuleFormulaPositionViewRepository ruleFormulaPositionViewRepository;
  private final PlacementFormulaAssembler placementFormulaAssembler;
  private final RuleDeployedPositionRepository ruleDeployedPositionRepository;

  public RuleFormulaUpdateService(
      RuleRepository ruleRepository,
      RuleFormulaPositionViewRepository ruleFormulaPositionViewRepository,
      PlacementFormulaAssembler placementFormulaAssembler,
      RuleDeployedPositionRepository ruleDeployedPositionRepository) {
    super("Rule");
    this.ruleRepository = ruleRepository;
    this.ruleFormulaPositionViewRepository = ruleFormulaPositionViewRepository;
    this.placementFormulaAssembler = placementFormulaAssembler;
    this.ruleDeployedPositionRepository = ruleDeployedPositionRepository;
  }

  @Override
  public List<Long> findAllToUpdate() {
    return ruleRepository.findRulesUpdateableWithNewlyApplicablePlacements();
  }

  @Override
  protected void compareAndUpdate(Long pid, PlacementFormulaAutoUpdateMetrics metrics) {
    long start = System.currentTimeMillis();
    CompanyRule rule =
        ruleRepository
            .findActualByPid(pid)
            .orElseThrow(
                () -> new GenevaValidationException(ServerErrorCodes.SERVER_COMPANY_NOT_FOUND));
    Set<Long> positionPidsInRule =
        rule.getDeployedPositions().stream()
            .map(RuleDeployedPosition::getPid)
            .collect(Collectors.toSet());
    metrics.addFindTime(System.currentTimeMillis() - start);

    PlacementFormulaDTO placementFormulaDto =
        placementFormulaAssembler.make(rule.getRuleFormula().getFormula());
    Group<RuleFormulaPositionView> group = placementFormulaAssembler.apply(placementFormulaDto);

    start = System.currentTimeMillis();
    List<RuleFormulaPositionView> ruleFormulaPositionViewList =
        ruleFormulaPositionViewRepository.findAll(
            RuleSpecification.withDefaultRtbProfiles(
                Collections.singleton(rule.getOwnerCompanyPid()), group));
    Set<Long> positionPidsFromFormula =
        ruleFormulaPositionViewList.stream()
            .map(RuleFormulaPositionView::getPid)
            .collect(Collectors.toSet());
    metrics.addSearchTimeAndFoundPlacements(
        System.currentTimeMillis() - start, ruleFormulaPositionViewList.size());

    if (!positionPidsInRule.equals(positionPidsFromFormula)) {
      metrics.incrementChanged();

      start = System.currentTimeMillis();
      Set<RuleDeployedPosition> positionsFromFormula =
          ruleFormulaPositionViewList.stream()
              .map(RuleFormulaPositionView::getPid)
              .map(ruleDeployedPositionRepository::getOne)
              .collect(Collectors.toSet());
      rule.setDeployedPositions(positionsFromFormula);
      ruleRepository.save(rule);
      metrics.addUpdateTime(System.currentTimeMillis() - start);
    } else {
      metrics.incrementNotChanged();
    }
  }
}
