package com.nexage.geneva.request;

import com.nexage.geneva.request.ignoredkeys.CompanyIgnoredKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CompaniesRequests {

  @Autowired private Request request;
  private final String companies = "/companies";
  private final String pss = "/pss/";
  private final String pssPublisher = "/pss/publisher/";

  public Request getGetSellerByPrefix() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern("/sellers?prefix=" + RequestParams.PREFIX);
  }

  public Request getGetBuyersByPrefix() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(companies + "?type=BUYER&prefix=" + RequestParams.PREFIX);
  }

  public Request getDeleteCompanyRequest() {
    return request
        .clear()
        .setDeleteStrategy()
        .setUrlPattern(companies + "/" + RequestParams.COMPANY_PID);
  }

  public Request getUpdateCompanyRequest() {
    return request
        .clear()
        .setPutStrategy()
        .setActualObjectIgnoredKeys(CompanyIgnoredKeys.actualAndExpectedObjectUpdate)
        .setExpectedObjectIgnoredKeys(CompanyIgnoredKeys.actualAndExpectedObjectUpdate)
        .setUrlPattern(companies + "/" + RequestParams.COMPANY_PID);
  }

  public Request getNewUpdateCompanyRequest() {
    return request
        .clear()
        .setPutStrategy()
        .setActualObjectIgnoredKeys(CompanyIgnoredKeys.actualAndExpectedNewObjectUpdate)
        .setExpectedObjectIgnoredKeys(CompanyIgnoredKeys.actualAndExpectedNewObjectUpdate)
        .setUrlPattern(companies + "/" + RequestParams.COMPANY_PID);
  }

  public Request getUpdatePublisherRequest() {
    return request
        .clear()
        .setPutStrategy()
        .setActualObjectIgnoredKeys(CompanyIgnoredKeys.actualObjectEligibleBidders)
        .setExpectedObjectIgnoredKeys(CompanyIgnoredKeys.expectedObjectEligibleBidders)
        .setUrlPattern(pss + RequestParams.COMPANY_PID);
  }

  public Request getGetPublisherRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setActualObjectIgnoredKeys(CompanyIgnoredKeys.actualObjectEligibleBidders)
        .setExpectedObjectIgnoredKeys(CompanyIgnoredKeys.expectedObjectEligibleBidders)
        .setUrlPattern(pss + RequestParams.COMPANY_PID);
  }

  public Request getCreatePssCompanyRequest() {
    return request.clear().setPostStrategy().setUrlPattern(pssPublisher);
  }

  public Request getDeletePssCompanyRequest() {
    return request
        .clear()
        .setDeleteStrategy()
        .setUrlPattern(pssPublisher + RequestParams.COMPANY_PID);
  }

  public Request getUpdatePssCompanyRequest() {
    return request.clear().setPutStrategy().setUrlPattern(pssPublisher + RequestParams.COMPANY_PID);
  }

  public Request getGetPssCompanyRequest() {
    return request.clear().setGetStrategy().setUrlPattern(pssPublisher + RequestParams.COMPANY_PID);
  }

  public Request getCreateBuyerRequest() {
    return request
        .clear()
        .setPutStrategy()
        .setActualObjectIgnoredKeys(CompanyIgnoredKeys.actualObjectCreateBuyer)
        .setExpectedObjectIgnoredKeys(CompanyIgnoredKeys.expectedObjectCreateBuyer)
        .setUrlPattern(companies + "/");
  }

  public Request getCreateSellerRequest() {
    return request
        .clear()
        .setPutStrategy()
        .setActualObjectIgnoredKeys(CompanyIgnoredKeys.actualAndExpectedCreateSeller)
        .setExpectedObjectIgnoredKeys(CompanyIgnoredKeys.actualAndExpectedCreateSeller)
        .setUrlPattern(companies + "/");
  }

  public Request getCreateExternalRequest() {
    return request
        .clear()
        .setPutStrategy()
        .setActualObjectIgnoredKeys(CompanyIgnoredKeys.actualAndExpectedCreateSeller)
        .setExpectedObjectIgnoredKeys(CompanyIgnoredKeys.actualAndExpectedCreateSeller)
        .setUrlPattern(companies + "/");
  }

  public Request getGetCompanyRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(companies + "/" + RequestParams.COMPANY_PID);
  }

  public Request getGetNewCompanyRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setActualObjectIgnoredKeys(CompanyIgnoredKeys.actualAndExpectedCreateSeller)
        .setExpectedObjectIgnoredKeys(CompanyIgnoredKeys.actualAndExpectedCreateSeller)
        .setUrlPattern(companies + "/" + RequestParams.COMPANY_PID);
  }

  public Request getGetAllBuyersRequest() {
    return request.clear().setGetStrategy().setUrlPattern(companies + "?type=BUYER");
  }

  public Request getGetAllSellersRequest() {
    return request.clear().setGetStrategy().setUrlPattern(companies + "?type=SELLER");
  }

  public Request getGetAllSeatHoldersRequest() {
    return request.clear().setGetStrategy().setUrlPattern(companies + "?type=SEATHOLDER");
  }

  public Request getCompaniesByTypeWithQueryFieldNameAndQueryTermContainingStringRequest() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(
            companies
                + "?type="
                + RequestParams.COMPANY_TYPE
                + "&qf="
                + RequestParams.QF
                + "&qt="
                + RequestParams.QT);
  }
}
