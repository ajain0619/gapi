package com.nexage.app.services.validation.sellingrule;

import static com.nexage.admin.core.enums.RuleTargetType.AD_FORMAT_TYPE;
import static com.nexage.admin.core.enums.RuleTargetType.AD_SIZE;
import static com.nexage.admin.core.enums.RuleTargetType.BUYER_SEATS;
import static com.nexage.admin.core.enums.RuleTargetType.CONTENT_CHANNEL;
import static com.nexage.admin.core.enums.RuleTargetType.CONTENT_GENRE;
import static com.nexage.admin.core.enums.RuleTargetType.CONTENT_LANGUAGE;
import static com.nexage.admin.core.enums.RuleTargetType.CONTENT_LIVESTREAM;
import static com.nexage.admin.core.enums.RuleTargetType.CONTENT_RATING;
import static com.nexage.admin.core.enums.RuleTargetType.CONTENT_SERIES;
import static com.nexage.admin.core.enums.RuleTargetType.COUNTRY;
import static com.nexage.admin.core.enums.RuleTargetType.DEAL_CATEGORY;
import static com.nexage.admin.core.enums.RuleTargetType.DEVICE_TYPE;
import static com.nexage.admin.core.enums.RuleTargetType.EXACT_MATCH_ADVERTISER_DOMAIN;
import static com.nexage.admin.core.enums.RuleTargetType.MULTI_AD_SIZE;
import static com.nexage.admin.core.enums.RuleTargetType.WILDCARD_ADVERTISER_DOMAIN;
import static com.nexage.app.dto.sellingrule.SellingRuleValidator.validateTargetsAndIntendedActions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.nexage.admin.core.enums.RuleTargetType;
import com.nexage.admin.core.model.CompanyRule;
import com.nexage.admin.core.model.RuleDeployedCompany;
import com.nexage.admin.core.model.RuleDeployedPosition;
import com.nexage.admin.core.model.RuleDeployedSite;
import com.nexage.admin.core.model.RuleTarget;
import com.nexage.admin.core.repository.CompanyRuleRepository;
import com.nexage.admin.core.repository.RuleDeployedCompanyRepository;
import com.nexage.admin.core.repository.RuleDeployedPositionRepository;
import com.nexage.admin.core.repository.RuleDeployedSiteRepository;
import com.nexage.admin.core.specification.CompanyRuleSpecification;
import com.nexage.app.dto.sellingrule.RuleTargetDTO;
import com.nexage.app.dto.sellingrule.SellerRuleDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.security.LoginUserContext;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class SellerRuleValidator {
  private static final EnumSet<RuleTargetType> ALLOWED_TARGET_TYPES_FOR_API =
      EnumSet.of(
          BUYER_SEATS,
          COUNTRY,
          AD_SIZE,
          MULTI_AD_SIZE,
          EXACT_MATCH_ADVERTISER_DOMAIN,
          WILDCARD_ADVERTISER_DOMAIN,
          DEVICE_TYPE,
          CONTENT_CHANNEL,
          CONTENT_SERIES,
          CONTENT_RATING,
          CONTENT_GENRE,
          AD_FORMAT_TYPE,
          DEAL_CATEGORY,
          CONTENT_LIVESTREAM,
          CONTENT_LANGUAGE);

  private CompanyRuleRepository companyRuleRepository;
  private LoginUserContext userContext;
  private ObjectMapper objectMapper;
  private RuleDeployedCompanyRepository companyRepository;
  private RuleDeployedSiteRepository siteRepository;
  private RuleDeployedPositionRepository positionRepository;

  public SellerRuleValidator(
      CompanyRuleRepository companyRuleRepository,
      LoginUserContext userContext,
      ObjectMapper objectMapper,
      RuleDeployedCompanyRepository companyRepository,
      RuleDeployedSiteRepository siteRepository,
      RuleDeployedPositionRepository positionRepository) {
    this.companyRuleRepository = companyRuleRepository;
    this.userContext = userContext;
    this.objectMapper = objectMapper;
    this.companyRepository = companyRepository;
    this.siteRepository = siteRepository;
    this.positionRepository = positionRepository;
  }

  public void validateCommonPartForCreateAndUpdate(Long sellerPid, SellerRuleDTO inputRule) {
    // BUCKET target is reserved only for the internal (nexage) users
    if (inputRule.getTargets().stream().anyMatch(t -> t.getTargetType() == RuleTargetType.BUCKET)
        && !Boolean.TRUE.equals(userContext.isNexageUser())) {
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_RULE_WRONG_COMBINATION_OF_RULE_TYPE_AND_RULE_TARGET);
    }

    if (!sellerPid.equals(inputRule.getOwnerCompanyPid())) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_PIDS_MISMATCH);
    }

    inputRule.getTargets().stream()
        .filter(t -> t.getTargetType() == RuleTargetType.BUYER_SEATS)
        .findFirst()
        .ifPresent(e -> validateBuyerSeatsTargetData(e.getData()));
    validateTargetsAndIntendedActions(inputRule);
    if (inputRule.getRuleFormula() != null && inputRule.getAssignments() != null) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_RULE_ASSIGNMENTS_NOT_ALLOWED);
    }
  }

  public void validateDeployedTargetsAndUpdateRule(CompanyRule rule) {
    Long ownerCompanyPid = rule.getOwnerCompanyPid();

    List<RuleDeployedCompany> deployedCompanies =
        validateTargetsCollection(
            rule.getDeployedCompanies(),
            pids -> companyRepository.findAllById(pids),
            RuleDeployedCompany::getPid,
            RuleDeployedCompany::getPid,
            ownerCompanyPid);
    rule.setDeployedCompanies(Set.copyOf(deployedCompanies));

    List<RuleDeployedSite> deployedSites =
        validateTargetsCollection(
            rule.getDeployedSites(),
            pids -> siteRepository.findAllById(pids),
            RuleDeployedSite::getPid,
            RuleDeployedSite::getCompanyPid,
            ownerCompanyPid);
    rule.setDeployedSites(Set.copyOf(deployedSites));

    List<RuleDeployedPosition> deployedPositions =
        validateTargetsCollection(
            rule.getDeployedPositions(),
            pids -> positionRepository.findAllById(pids),
            RuleDeployedPosition::getPid,
            e -> e.getSite().getCompanyPid(),
            ownerCompanyPid);
    rule.setDeployedPositions(Set.copyOf(deployedPositions));
  }

  public void validateDuplicateName(CompanyRule companyRule) {
    Specification<CompanyRule> spec =
        CompanyRuleSpecification.withPublisherPidAndName(
            companyRule.getOwnerCompanyPid(), companyRule.getName());
    Optional<CompanyRule> companyRuleOptional = companyRuleRepository.findOne(spec);

    companyRuleOptional.ifPresent(
        r -> {
          if (!r.getPid().equals(companyRule.getPid())) {
            throw new GenevaValidationException(ServerErrorCodes.SERVER_RULE_DUPLICATE_NAME);
          }
        });
  }

  public void validateBidManagementAPIRule(CompanyRule sellerRule) {
    if (!hasOnlyAllowedTargetTypes(sellerRule)) {
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_CURRENT_RULE_NOT_ALLOWED_TARGET_TYPE);
    }

    if (sellerRule.getRuleFormula() != null) {
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_CANT_EDIT_OR_CREATE_RULES_WITH_RULE_FORMULA_THROUGH_API);
    }
  }

  public void validateBidManagementAPIRuleDTO(SellerRuleDTO inputRule) {
    if (!hasOnlyAllowedTargetTypes(inputRule)) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_NOT_ALLOWED_TARGET_TYPE_PROVIDED);
    }

    if (inputRule.getRuleFormula() != null) {
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_CANT_EDIT_OR_CREATE_RULES_WITH_RULE_FORMULA_THROUGH_API);
    }
  }

  private boolean hasOnlyAllowedTargetTypes(CompanyRule sellerRule) {
    return sellerRule.getRuleTargets().stream()
        .map(RuleTarget::getRuleTargetType)
        .allMatch(ALLOWED_TARGET_TYPES_FOR_API::contains);
  }

  private boolean hasOnlyAllowedTargetTypes(SellerRuleDTO inputRule) {
    return inputRule.getTargets().stream()
        .map(RuleTargetDTO::getTargetType)
        .allMatch(ALLOWED_TARGET_TYPES_FOR_API::contains);
  }

  private <T> Stream<T> streamFromNullable(Set<T> input) {
    return Stream.ofNullable(input).flatMap(Collection::stream);
  }

  private <T> List<T> validateTargetsCollection(
      Set<T> targets,
      Function<Set<Long>, List<T>> dbSource,
      ToLongFunction<T> pidMapper,
      ToLongFunction<T> ownerMapper,
      Long ownerCompanyPid) {

    Set<Long> pids =
        streamFromNullable(targets).mapToLong(pidMapper).boxed().collect(Collectors.toSet());
    List<T> dbEntries = Lists.newArrayList();

    if (!pids.isEmpty()) {
      dbEntries = dbSource.apply(pids);
      if (dbEntries.size() != pids.size()
          || !dbEntries.stream().mapToLong(ownerMapper).allMatch(e -> e == ownerCompanyPid)) {
        throw new GenevaValidationException(ServerErrorCodes.SERVER_RULE_DEPLOYED_TO_WRONG_TARGET);
      }
    }

    return dbEntries;
  }

  private void validateBuyerSeatsTargetData(String data) {
    try {
      List<AbstractBidderValidator.BidderSeat> targetData =
          Arrays.asList(objectMapper.readValue(data, AbstractBidderValidator.BidderSeat[].class));
      targetData.stream()
          .filter(bs -> Objects.isNull(bs.getBuyerCompany()))
          .findFirst()
          .ifPresent(
              e -> {
                throw new GenevaValidationException(
                    ServerErrorCodes.SERVER_RULE_TARGET_DATA_INVALID_JSON_FORMAT);
              });
    } catch (JsonProcessingException e) {
      log.warn("Failed to convert string data to object: {}", data, e);
    }
  }
}
