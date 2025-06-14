package com.nexage.app.util.assemblers.sellingrule;

import com.nexage.admin.core.model.RuleDeployedSite;
import com.nexage.admin.core.repository.RuleDeployedSiteRepository;
import com.nexage.app.dto.sellingrule.SiteAssignmentDTO;
import com.nexage.app.util.assemblers.NoContextAssembler;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SiteRuleAssignmentAssembler extends NoContextAssembler {
  private final PublisherRuleAssignmentAssembler publisherRuleAssignmentAssembler;
  private final RuleDeployedSiteRepository ruleDeployedSiteRepository;

  public static final Set<String> DEFAULT_FIELDS = Set.of("pid", "name", "publisherRuleAssignment");

  public SiteAssignmentDTO make(RuleDeployedSite site) {
    return make(site, DEFAULT_FIELDS);
  }

  public SiteAssignmentDTO make(RuleDeployedSite site, Set<String> fields) {
    SiteAssignmentDTO.SiteAssignmentDTOBuilder builder = SiteAssignmentDTO.builder();

    Set<String> fieldsToMap = (fields != null) ? fields : DEFAULT_FIELDS;

    for (String field : fieldsToMap) {
      switch (field) {
        case "pid":
          builder.pid(site.getPid());
          break;
        case "name":
          builder.name(site.getName());
          break;
        case "publisherRuleAssignment":
          builder.publisherAssignment(publisherRuleAssignmentAssembler.make(site.getCompany()));
          break;
        default:
          throw new RuntimeException("Unknown field '" + field + "'.");
      }
    }

    return builder.build();
  }

  public RuleDeployedSite apply(SiteAssignmentDTO siteAssignmentDto) {
    return ruleDeployedSiteRepository.getOne(siteAssignmentDto.getPid());
  }
}
