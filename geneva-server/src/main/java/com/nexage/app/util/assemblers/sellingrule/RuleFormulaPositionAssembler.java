package com.nexage.app.util.assemblers.sellingrule;

import com.nexage.admin.core.model.RuleFormulaPositionView;
import com.nexage.app.dto.publisher.PublisherSiteDTO;
import com.nexage.app.dto.sellingrule.RuleFormulaPositionDTO;
import com.nexage.app.util.assemblers.NoContextAssembler;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Component;

/**
 * public class RuleFormulaPositionDTO {
 *
 * <p>private Long companyId;
 *
 * <p>private String companyName;
 *
 * <p>private Long sitePid;
 *
 * <p>private String siteName;
 *
 * <p>private PublisherSite.SiteType siteType;
 *
 * <p>private Long placementId;
 *
 * <p>private String placementName;
 *
 * <p>private String placementMemo;
 *
 * <p>private PlacementCategory type;
 *
 * <p>private String adSize;
 */
@Component
public class RuleFormulaPositionAssembler extends NoContextAssembler {
  public static final Set<String> DEFAULT_FIELDS =
      Set.of(
          "companyId",
          "companyName",
          "sitePid",
          "siteName",
          "siteType",
          "placementId",
          "placementName",
          "placementMemo",
          "placementType",
          "height",
          "width");

  public static final Set<String> PUBLISHER_INVENTORY_FIELDS =
      Set.of(
          "sitePid",
          "siteName",
          "siteType",
          "placementId",
          "placementName",
          "placementMemo",
          "placementType",
          "height",
          "width");

  public static final Set<String> DEAL_INVENTORY_FIELDS =
      Set.of(
          "companyId",
          "companyName",
          "sitePid",
          "siteName",
          "siteType",
          "placementId",
          "placementName",
          "placementMemo",
          "placementType",
          "height",
          "width");

  private static final Set ALL_FIELDS =
      Set.of(
          ArrayUtils.addAll(
              PUBLISHER_INVENTORY_FIELDS.toArray(new String[0]), "companyId", "companyName"));

  public List<RuleFormulaPositionDTO> make(
      List<RuleFormulaPositionView> ruleFormulaPositionViewList, Set<String> fields) {
    List<RuleFormulaPositionDTO> result = new LinkedList<>();
    for (RuleFormulaPositionView ruleFormulaPositionView : ruleFormulaPositionViewList) {
      result.add(make(ruleFormulaPositionView, fields));
    }
    return result;
  }

  public RuleFormulaPositionDTO make(RuleFormulaPositionView ruleFormulaPositionView) {
    return make(ruleFormulaPositionView, DEFAULT_FIELDS);
  }

  private RuleFormulaPositionDTO make(
      RuleFormulaPositionView ruleFormulaPositionView, Set<String> fields) {
    RuleFormulaPositionDTO.Builder builder = RuleFormulaPositionDTO.newBuilder();

    Set<String> fieldsToMap = (fields != null) ? fields : DEFAULT_FIELDS;

    for (String field : fieldsToMap) {
      switch (field) {
        case "companyId":
          builder.withCompanyId(ruleFormulaPositionView.getSite().getCompany().getPid());
          break;
        case "companyName":
          builder.withCompanyName(ruleFormulaPositionView.getSite().getCompany().getName());
          break;
        case "sitePid":
          builder.withSitePid(ruleFormulaPositionView.getSite().getPid());
          break;
        case "siteName":
          builder.withSiteName(ruleFormulaPositionView.getSite().getName());
          break;
        case "siteType":
          builder.withSiteType(
              PublisherSiteDTO.SiteType.valueOf(
                  ruleFormulaPositionView.getSite().getType().toString()));
          break;
        case "placementId":
          builder.withPlacementId(ruleFormulaPositionView.getPid());
          break;
        case "placementName":
          builder.withPlacementName(ruleFormulaPositionView.getName());
          break;
        case "placementMemo":
          builder.withPlacementMemo(ruleFormulaPositionView.getMemo());
          break;
        case "placementType":
          builder.withPlacementType(ruleFormulaPositionView.getType());
          break;
        case "height":
          builder.withHeight(ruleFormulaPositionView.getHeight());
          break;
        case "width":
          builder.withWidth(ruleFormulaPositionView.getWidth());
          break;
        default:
          throw new RuntimeException("Unknown field '" + field + "'.");
      }
    }

    return builder.build();
  }
}
