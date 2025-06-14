package com.nexage.app.util.assemblers;

import com.nexage.admin.core.dto.AdSourceSummaryDTO;
import com.nexage.admin.core.dto.AdSourceSummaryDTO.ParameterConfig;
import com.nexage.admin.core.model.AdSource;
import com.nexage.app.dto.publisher.PublisherBuyerDTO;
import com.nexage.app.dto.publisher.PublisherBuyerDTO.AuthenticationType;
import com.nexage.app.dto.publisher.PublisherBuyerDTO.BidEnabled;
import com.nexage.app.dto.publisher.PublisherBuyerDTO.DecisionMakerEnabled;
import com.nexage.app.dto.publisher.PublisherBuyerDTO.SelfServeEnablement;
import com.nexage.app.util.assemblers.context.PublisherBuyerContext;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class PublisherBuyerAssembler
    extends Assembler<PublisherBuyerDTO, AdSourceSummaryDTO, PublisherBuyerContext> {

  public static final Set<String> DEFAULT_FIELDS =
      Set.of(
          "pid",
          "exchange",
          "name",
          "primaryId",
          "primaryName",
          "secondaryId",
          "secondaryName",
          "primaryIdDefault",
          "primaryNameDefault",
          "secondaryIdDefault",
          "secondaryNameDefault",
          "getPrimaryIdRequired",
          "getPrimaryNameRequired",
          "getSecondaryIdRequired",
          "getSecondaryNameRequired",
          "paramMetadata",
          "authenticationType",
          "selfServeEnablement",
          "bidEnabled",
          "decisionMakerEnabled",
          "logoUrl");

  public PublisherBuyerDTO make(
      final PublisherBuyerContext context, AdSourceSummaryDTO adSourceSummary) {
    return make(context, adSourceSummary, DEFAULT_FIELDS);
  }

  public PublisherBuyerDTO make(
      final PublisherBuyerContext context, AdSourceSummaryDTO adSourceSummary, Set<String> fields) {
    PublisherBuyerDTO.Builder publisherBuyerBuilder = PublisherBuyerDTO.newBuilder();

    Set<String> fieldsToMap = (fields != null) ? fields : DEFAULT_FIELDS;

    for (String field : fieldsToMap) {
      switch (field) {
        case "pid":
          publisherBuyerBuilder.withPid(adSourceSummary.getPid());
          break;
        case "exchange":
          publisherBuyerBuilder.withExchange(adSourceSummary.getExchange());
          break;
        case "name":
          publisherBuyerBuilder.withName(adSourceSummary.getName());
          break;
        case "primaryId":
          String pidAlias = getParameterAlias(adSourceSummary, "pid");
          if (!StringUtils.isBlank(pidAlias)) {
            publisherBuyerBuilder.withPrimaryId(pidAlias);
          }
          break;
        case "primaryName":
          String pnameAlias = getParameterAlias(adSourceSummary, "pname");
          if (!StringUtils.isBlank(pnameAlias)) {
            publisherBuyerBuilder.withPrimaryName(pnameAlias);
          }
          break;
        case "secondaryId":
          String sidAlias = getParameterAlias(adSourceSummary, "sid");
          if (!StringUtils.isBlank(sidAlias)) {
            publisherBuyerBuilder.withSecondaryId(sidAlias);
          }
          break;
        case "secondaryName":
          String snameAlias = getParameterAlias(adSourceSummary, "sname");
          if (!StringUtils.isBlank(snameAlias)) {
            publisherBuyerBuilder.withSecondaryName(snameAlias);
          }
          break;
        case "primaryIdDefault":
          String primaryIdDefault = adSourceSummary.getPrimaryIdDefault();
          if (!StringUtils.isBlank(primaryIdDefault)) {
            publisherBuyerBuilder.withPrimaryIdDefault(primaryIdDefault);
          }
          break;
        case "primaryNameDefault":
          String primaryNameDefault = adSourceSummary.getPrimaryNameDefault();
          if (!StringUtils.isBlank(primaryNameDefault)) {
            publisherBuyerBuilder.withPrimaryNameDefault(primaryNameDefault);
          }
          break;
        case "secondaryIdDefault":
          String sidDefault = adSourceSummary.getSecondaryIdDefault();
          if (!StringUtils.isBlank(sidDefault)) {
            publisherBuyerBuilder.withSecondaryIdDefault(sidDefault);
          }
          break;
        case "secondaryNameDefault":
          String snameDefault = adSourceSummary.getSecondaryNameDefault();
          if (!StringUtils.isBlank(snameDefault)) {
            publisherBuyerBuilder.withSecondaryNameDefault(snameDefault);
          }
          break;
        case "getPrimaryIdRequired":
          publisherBuyerBuilder.withPrimaryIdRequired(adSourceSummary.isPrimaryIdRequired());
          break;
        case "getPrimaryNameRequired":
          publisherBuyerBuilder.withPrimaryNameRequired(adSourceSummary.isPrimaryNameRequired());
          break;
        case "getSecondaryIdRequired":
          publisherBuyerBuilder.withSecondaryIdRequired(adSourceSummary.isSecondaryIdRequired());
          break;
        case "getSecondaryNameRequired":
          publisherBuyerBuilder.withSecondaryNameRequired(
              adSourceSummary.isSecondaryNameRequired());
          break;
        case "paramMetadata":
          publisherBuyerBuilder.withParamMetadata(adSourceSummary.getParamMetadata());
          break;
        case "authenticationType":
          int authType = adSourceSummary.getReportAuthType().asInt();
          publisherBuyerBuilder.withAuthenticationType(AuthenticationType.fromInt(authType));
          break;
        case "selfServeEnablement":
          int selfServEn = adSourceSummary.getSelfServeEnablement().asInt();
          if (AdSource.SelfServeEnablement.NONE.equals(adSourceSummary.getSelfServeEnablement())
              && context.isEnabledForPublisher()) {
            publisherBuyerBuilder.withSelfServeEnablement(SelfServeEnablement.ADDITIONAL);
          } else
            publisherBuyerBuilder.withSelfServeEnablement(SelfServeEnablement.fromInt(selfServEn));
          break;
        case "bidEnabled":
          int bidEnab = adSourceSummary.getBidEnabled().asInt();
          publisherBuyerBuilder.withBidEnabled(BidEnabled.fromInt(bidEnab));
          break;
        case "decisionMakerEnabled":
          int decionMakerEnab = adSourceSummary.getDecisionMakerEnabled().asInt();
          publisherBuyerBuilder.withDecisonMakerEnabled(
              DecisionMakerEnabled.fromInt(decionMakerEnab));
          break;

        case "logoUrl":
          publisherBuyerBuilder.withLogoUrl(adSourceSummary.getLogoUrl());
          break;
      }
    }
    return publisherBuyerBuilder.build();
  }

  private String getParameterAlias(AdSourceSummaryDTO adSourceSummary, String name) {
    String value = null;

    if (adSourceSummary.getParameterConfig() == null) return null;
    for (ParameterConfig config : adSourceSummary.getParameterConfig()) {
      if (name.equals(config.getName())) {
        return config.getAlias();
      }
    }
    return value;
  }

  public AdSourceSummaryDTO apply(
      final PublisherBuyerContext context,
      AdSourceSummaryDTO adSourceSummary,
      PublisherBuyerDTO publisherBuyer) {
    return adSourceSummary;
  }
}
