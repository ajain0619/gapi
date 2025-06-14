package com.nexage.app.util.assemblers.sellingrule;

import com.nexage.admin.core.model.RuleDeployedCompany;
import com.nexage.admin.core.repository.RuleDeployedCompanyRepository;
import com.nexage.app.dto.sellingrule.PublisherAssignmentDTO;
import com.nexage.app.util.assemblers.NoContextAssembler;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PublisherRuleAssignmentAssembler extends NoContextAssembler {

  private final RuleDeployedCompanyRepository ruleDeployedCompanyRepository;

  public static final Set<String> DEFAULT_FIELDS = Set.of("pid", "name");

  public PublisherAssignmentDTO make(RuleDeployedCompany company) {
    return make(company, DEFAULT_FIELDS);
  }

  public PublisherAssignmentDTO make(RuleDeployedCompany company, Set<String> fields) {
    PublisherAssignmentDTO.PublisherAssignmentDTOBuilder builder = PublisherAssignmentDTO.builder();

    Set<String> fieldsToMap = (fields != null) ? fields : DEFAULT_FIELDS;

    for (String field : fieldsToMap) {
      switch (field) {
        case "pid":
          builder.pid(company.getPid());
          break;
        case "name":
          builder.name(company.getName());
          break;
        default:
          throw new RuntimeException("Unknown field '" + field + "'.");
      }
    }

    return builder.build();
  }

  public RuleDeployedCompany apply(PublisherAssignmentDTO pubAssignment) {
    return ruleDeployedCompanyRepository.getOne(pubAssignment.getPid());
  }
}
