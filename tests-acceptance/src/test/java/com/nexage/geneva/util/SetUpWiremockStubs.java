package com.nexage.geneva.util;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class SetUpWiremockStubs {

  final String NEXAGE =
      "{\"id\":8,\"name\":\"Nexage\",\"displayName\":\"Nexage\",\"application\":\"OneMobile\",\"type\":\"functional\",\"permission\":\"access\",\"organizationIds\":[63100,63103,63106,64465]}";
  final String SELLER =
      "{\"id\":9,\"name\":\"Seller\",\"displayName\":\"Seller\",\"application\":\"OneMobile\",\"type\":\"functional\",\"permission\":\"access\",\"organizationIds\":[63100,63103,63106,64465]}";
  final String BUYER =
      "{\"id\":10,\"name\":\"Buyer\",\"displayName\":\"Buyer\",\"application\":\"OneMobile\",\"type\":\"functional\",\"permission\":\"access\",\"organizationIds\":[63100,63103,63106,64465]}";
  final String SMARTEX =
      "{\"id\":11,\"name\":\"Smartex\",\"displayName\":\"Smartex\",\"application\":\"OneMobile\",\"type\":\"functional\",\"permission\":\"access\",\"organizationIds\":[63100,63103,63106,64465]}";
  final String YIELD =
      "{\"id\":12,\"name\":\"Yield\",\"displayName\":\"Yield\",\"application\":\"OneMobile\",\"type\":\"functional\",\"permission\":\"access\",\"organizationIds\":[63100,63103,63106,64465]}";
  final String SEATHOLDER =
      "{\"id\":13,\"name\":\"Seatholder\",\"displayName\":\"Seatholder\",\"application\":\"OneMobile\",\"type\":\"functional\",\"permission\":\"access\",\"organizationIds\":[63100,63103,63106,64465]}";
  final String SELLERSEAT =
      "{\"id\":14,\"name\":\"Sellerseat\",\"displayName\":\"Sellerseat\",\"application\":\"OneMobile\",\"type\":\"functional\",\"permission\":\"access\",\"organizationIds\":[63100,63103,63106,64465]}";
  final String API =
      "{\"id\":15,\"name\":\"Api\",\"displayName\":\"Api\",\"application\":\"OneMobile\",\"type\":\"functional\",\"permission\":\"access\",\"organizationIds\":[63100,63103,63106,64465]}";
  final String APIIIQ =
      "{\"id\":16,\"name\":\"ApiIIQ\",\"displayName\":\"ApiIIQ\",\"application\":\"OneMobile\",\"type\":\"functional\",\"permission\":\"access\",\"organizationIds\":[63100,63103,63106,64465]}";
  final String DEAL =
      "{\"id\":17,\"name\":\"Deal\",\"displayName\":\"Deal\",\"application\":\"OneMobile\",\"type\":\"functional\",\"permission\":\"access\",\"organizationIds\":[63100,63103,63106,64465]}";
  final String ADMIN =
      "{\"id\":18,\"name\":\"Admin\",\"displayName\":\"Admin\",\"application\":\"OneMobile\",\"type\":\"functional\",\"permission\":\"access\",\"organizationIds\":[63100,63103,63106,64465]}";
  final String MANAGER =
      "{\"id\":19,\"name\":\"Manager\",\"displayName\":\"Manager\",\"application\":\"OneMobile\",\"type\":\"functional\",\"permission\":\"access\",\"organizationIds\":[63100,63103,63106,64465]}";
  final String USER =
      "{\"id\":20,\"name\":\"User\",\"displayName\":\"User\",\"application\":\"OneMobile\",\"type\":\"functional\",\"permission\":\"access\",\"organizationIds\":[63100,63103,63106,64465]}";
  final String ADSCREENING =
      "{\"id\":21,\"name\":\"Adscreening\",\"displayName\":\"Adscreening\",\"application\":\"OneMobile\",\"type\":\"functional\",\"permission\":\"access\",\"organizationIds\":[63100,63103,63106,64465]}";
  final String NONE = "{}";
  final String ADMINNEXAGE = "AdminNexage";
  final String MANAGERYIELDNEXAGE = "ManagerYieldNexage";
  final String MANAGERSMARTEXNEXAGE = "ManagerSmartexNexage";
  final String MANAGERNEXAGE = "ManagerNexage";
  final String USERNEXAGE = "UserNexage";
  final String ADMINSELLER = "AdminSeller";
  final String MANAGERSELLER = "ManagerSeller";
  final String USERSELLER = "UserSeller";
  final String ADMINBUYER = "AdminBuyer";
  final String MANAGERBUYER = "ManagerBuyer";
  final String USERBUYER = "UserBuyer";
  final String ADMINSEATHOLDER = "AdminSeatHolder";
  final String MANAGERSEATHOLDER = "ManagerSeatHolder";
  final String USERSEATHOLDER = "UserSeatHolder";
  final String ADMINSELLERSEAT = "AdminSellerSeat";
  final String MANAGERSELLERSEAT = "ManagerSellerSeat";
  final String USERSELLERSEAT = "UserSellerSeat";
  final String APIROLE = "Api";
  final String APIROLESELLER = "ApiSeller";
  final String APIROLEBUYER = "ApiBuyer";
  final String APIIIQROLE = "ApiIIQ";
  final String DEALMANAGER = "DealManager";
  final String DUMMY = "Dummy";

  public void setUpWireMockLogin(String OneCUserName) {

    stubFor(
        post(urlMatching("/identity/oauth2/introspect.*"))
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withStatus(200)
                    .withBody("{\"active\":\"true\"}")));

    stubFor(
        post(urlMatching("/one-central/authorization-management/v2/entities/user/.*"))
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withStatus(200)
                    .withBody(
                        "{ \"entityType\": \"user\", \"entityId\": \""
                            + OneCUserName
                            + "\", \"id\": 1, \"name\": \""
                            + OneCUserName
                            + "\",  \"type\": \"string\", \"permission\": \"access\" }")));

    var stub =
        stubFor(
            get(urlPathMatching("/identity/oauth2/authorize?.*"))
                .willReturn(
                    aResponse()
                        .withStatus(302)
                        .withHeader("Content-Type", "application/json")
                        .withTransformers("CaptureStateTransformer")));

    stubFor(
        post(urlMatching("/identity/oauth2/access_token?.*"))
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withStatus(200)
                    .withTransformers("CaptureStateTransformer")));

    stubFor(
        get(urlMatching("/identity/oauth2/connect/jwk_uri?.*"))
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withStatus(200)
                    .withTransformers("CaptureStateTransformer")));

    stubFor(
        post(urlMatching("/identity/oauth2/access_token"))
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withStatus(200)
                    .withTransformers("CaptureStateTransformer")));
    String body =
        "{\"username\":\""
            + OneCUserName
            + "\",\"firstName\":\"SSP\",\"lastName\":\"DEV\",\"email\":\"ssp.dev.test.api@test.com\",\"internal\":false,\"systemUser\":true,\"impersonationMode\":false,\"impersonationActualUsername\":null,\""
            + "sessionToken\":null,\"sessionTokenType\":null,\"countryCd\":null,\"status\":\"ACTIVE\",\"cdid\":null,\"oktaShortId\":null,\"defaultOrgSapId\":0,\"defaultVideoOrgId\":0,\""
            + "entitlements\":[{\"id\":8,\"name\":\"oneAccess\",\"displayName\":null,\"application\":\"\",\"type\":\"functional\",\"permission\":\"access\",\"organizationIds\":[63100,63103,63106,64465]},"
            + "{\"id\":26120,\"name\":\"publisherHubAccess\",\"displayName\":null,\"application\":\"One4P.AO\",\"type\":\"functional\",\"permission\":\"access\",\"organizationIds\":[63100,63103,63106,64465]},"
            + "{\"id\":116314,\"name\":\"7882678040\",\"displayName\":null,\"application\":null,\"type\":\"agencyAdvertiserSapIds\",\"permission\":\"access\",\"organizationIds\":[63100]},"
            + "{\"id\":116317,\"name\":\"7891874795\",\"displayName\":null,\"application\":null,\"type\":\"agencyAdvertiserSapIds\",\"permission\":\"access\",\"organizationIds\":[63103]},"
            + "{\"id\":116320,\"name\":\"7887886815\",\"displayName\":null,\"application\":null,\"type\":\"agencyAdvertiserSapIds\",\"permission\":\"access\",\"organizationIds\":[63106]},"
            + "{\"id\":118087,\"name\":\"7836000378\",\"displayName\":null,\"application\":null,\"type\":\"agencyAdvertiserSapIds\",\"permission\":\"access\",\"organizationIds\":[64465]}],"
            + "\"agencyAdvertiserAssociations\":[{\"agency\":7882678040,\"advertiser\":-1},{\"agency\":7891874795,\"advertiser\":-1},{\"agency\":7887886815,\"advertiser\":-1},{\"agency\":7836000378,\"advertiser\":-1}],\"uri\":null,\"userPreferences\":[]}";
    stubFor(
        get(urlMatching("/one-central/user-authorization/v3/users/authorization"))
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withStatus(200)
                    .withBody(body)));
  }

  public void setUpWireMockLogin(String OneCUserName, String role) {

    stubFor(
        post(urlMatching("/identity/oauth2/introspect"))
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withStatus(200)
                    .withBody("{\"active\":\"true\"}")));

    stubFor(
        post(urlMatching("one-central/authorization-management/v2/entities/user/.*"))
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withStatus(200)
                    .withBody(
                        "{ \"entityType\": \"user\", \"entityId\": \""
                            + OneCUserName
                            + "\", \"id\": 1, \"name\": \""
                            + OneCUserName
                            + "\",  \"type\": \"string\", \"permission\": \"access\" }")));

    stubFor(
        get(urlMatching("/identity/oauth2/connect/jwk_uri?.*"))
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withStatus(200)
                    .withTransformers("CaptureStateTransformer")));

    stubFor(
        get(urlPathMatching("/identity/oauth2/authorize?.*"))
            .willReturn(
                aResponse()
                    .withStatus(302)
                    .withHeader("Content-Type", "application/json")
                    .withTransformers("CaptureStateTransformer")));

    stubFor(
        post(urlMatching("/identity/oauth2/access_token?.*"))
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withStatus(200)
                    .withTransformers("CaptureStateTransformer")));

    stubFor(
        post(urlMatching("/identity/oauth2/access_token"))
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withStatus(200)
                    .withTransformers("CaptureStateTransformer")));
    String entitlements = buildOneCentralEntitlements(role);
    String body =
        "{\"username\":\""
            + OneCUserName
            + "\",\"firstName\":\"SSP\",\"lastName\":\"DEV\",\"email\":\"ssp.dev.test.api@test.com\",\"internal\":false,\"systemUser\":true,\"impersonationMode\":false,\"impersonationActualUsername\":null,\""
            + "sessionToken\":null,\"sessionTokenType\":null,\"countryCd\":null,\"status\":\"ACTIVE\",\"cdid\":null,\"oktaShortId\":null,\"defaultOrgSapId\":0,\"defaultVideoOrgId\":0,\"entitlements\":["
            + entitlements
            + "],\"uri\":null,\"userPreferences\":[]}";
    stubFor(
        get(urlMatching("/one-central/user-authorization/v3/users/authorization"))
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withStatus(200)
                    .withBody(body)));
  }

  public void setUpWireMockB2BLogin(String oneCUserName, String role) {
    setUpWireMockLogin(oneCUserName, role);
  }

  public void setUpWireMockB2BLogin(String oneCUserName) {
    setUpWireMockLogin(oneCUserName);
  }

  public void setUpWireMockCreateUser(String userBody) {

    stubFor(
        post(urlMatching("/one-central/user-migration/v1/users"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(userBody)));
  }

  public void setUpWireMockFind1CUserByEmail(String email, String userBody) {
    stubFor(
        get(urlMatching(
                "/one-central/user-management/v6/users\\?email="
                    + URLEncoder.encode(email, StandardCharsets.UTF_8)
                    + "&searchSource=One-Central"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(userBody)));
  }

  public void setUpWireMockUpdateUser(String userName, String userBody) {

    stubFor(
        put(urlMatching("/one-central/user-management/v6/users/" + userName))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(userBody)));
  }

  public void setupWireMockCreateRole() {
    stubFor(
        post(urlPathMatching(
                "/one-central/authorization-management/v2/entities/user/(.+)/roles/([0-9]+)"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{}")));
  }

  public void setupWireMockGetRoles() {
    stubFor(
        get(urlPathMatching("/one-central/authorization-management/v2/entities/user/(.+)/roles"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        "{\"list\": [{\"id\": \"510335\", \"roleId\": 510335, \"type\": \"role1\"}]}")));
  }

  public void setupWireMockDeleteRoles() {
    stubFor(
        delete(
                urlPathMatching(
                    "/one-central/authorization-management/v2/entities/user/(.+)/roles/([0-9]+)"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        "{\"list\": [{\"id\": \"510335\", \"roleId\": 510335, \"type\": \"role1\"}]}")));
  }

  public void setUpWireMockObiPost(String wmObiResponse) {
    stubFor(
        post(urlMatching("/biz/jsonapi/ordermanagement/registerSyndicationOffer?.*"))
            .willReturn(aResponse().withStatus(200).withBody(wmObiResponse)));
  }

  public void setUpWireMockObiAuthorization(String authResponse) {
    stubFor(
        post(urlMatching("/biz/jsonapi/bps/getAuthToken?.*"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(authResponse)));
  }

  public void setUpWireMockLogout() {
    stubFor(
        get(urlMatching("identity/XUI/"))
            .willReturn(
                aResponse().withHeader("Content-Type", "application/json").withStatus(200)));
  }

  public void setUpWireMockDv360AuctionPackage(String auctionPackageResponse) {
    stubFor(
        post(urlMatching("/exchanges/6001/auctionPackages"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(auctionPackageResponse)));
  }

  public void setUpWireMockDv360Order(String orderResponse) {
    stubFor(
        post(urlMatching("/exchanges/6001/orders"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(orderResponse)));
  }

  public void setUpWireMockDv360Product(String orderId, String productResponse) {
    stubFor(
        post(urlMatching("/exchanges/6001/orders/" + orderId + "/products"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(productResponse)));
  }

  public void setUpWireMockDv360AuctionPackageError(
      int httpStatus, String contentType, String responseBody) {
    stubFor(
        post(urlMatching("/exchanges/6001/auctionPackages"))
            .willReturn(
                aResponse()
                    .withStatus(httpStatus)
                    .withHeader("Content-Type", contentType)
                    .withBody(responseBody)));
  }

  public void setUpCdkSteps(String creativeId, String creativeResponse) {
    stubFor(
        get(urlMatching("/publisher/creativereview/1.0/creatives/" + creativeId + ".*"))
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withStatus(200)
                    .withBody(creativeResponse)));
    stubFor(
        get(urlMatching(
                "/publisher/creativereview/1.0/creatives.*buyerCreativeId=" + creativeId + ".*"))
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withStatus(200)
                    .withBody(creativeResponse)));
  }

  public void setUpCdkSteps() {
    stubFor(
        get(urlMatching("/publisher/creativereview/1.0/creatives/.*"))
            .willReturn(
                aResponse().withHeader("Content-Type", "application/json").withStatus(200)));
    stubFor(
        get(urlMatching("/publisher/creativereview/1.0/creatives.*"))
            .willReturn(
                aResponse().withHeader("Content-Type", "application/json").withStatus(200)));
  }

  private String buildOneCentralEntitlements(String role) {
    String entitlements = null;
    switch (role) {
      case ADMINNEXAGE:
        entitlements = String.format("%s,%s,%s,%s", NEXAGE, ADMIN, MANAGER, USER);
        break;
      case MANAGERYIELDNEXAGE:
        entitlements = String.format("%s,%s,%s,%s,%s", YIELD, SMARTEX, MANAGER, USER, NEXAGE);
        break;
      case MANAGERSMARTEXNEXAGE:
        entitlements = String.format("%s,%s,%s,%s", SMARTEX, NEXAGE, MANAGER, USER);
        break;
      case MANAGERNEXAGE:
        entitlements = String.format("%s,%s,%s", NEXAGE, MANAGER, USER);
        break;
      case USERNEXAGE:
        entitlements = String.format("%s,%s", NEXAGE, USER);
        break;
      case ADMINSELLER:
        entitlements = String.format("%s,%s,%s,%s", SELLER, ADMIN, MANAGER, USER);
        break;
      case MANAGERSELLER:
        entitlements = String.format("%s,%s,%s", SELLER, MANAGER, USER);
        break;
      case USERSELLER:
        entitlements = String.format("%s,%s", SELLER, USER);
        break;
      case ADMINBUYER:
        entitlements = String.format("%s,%s,%s,%s", BUYER, ADMIN, MANAGER, USER);
        break;
      case MANAGERBUYER:
        entitlements = String.format("%s,%s,%s", BUYER, MANAGER, USER);
        break;
      case USERBUYER:
        entitlements = String.format("%s,%s", BUYER, USER);
        break;
      case ADMINSEATHOLDER:
        entitlements = String.format("%s,%s,%s,%s", SEATHOLDER, ADMIN, MANAGER, USER);
        break;
      case MANAGERSEATHOLDER:
        entitlements = String.format("%s,%s,%s", SEATHOLDER, MANAGER, USER);
        break;
      case USERSEATHOLDER:
        entitlements = String.format("%s,%s", SEATHOLDER, USER);
        break;
      case ADMINSELLERSEAT:
        entitlements = String.format("%s,%s,%s,%s", SELLERSEAT, ADMIN, MANAGER, USER);
        break;
      case MANAGERSELLERSEAT:
        entitlements = String.format("%s,%s,%s", SELLERSEAT, MANAGER, USER);
        break;
      case USERSELLERSEAT:
        entitlements = String.format("%s,%s", SELLERSEAT, USER);
        break;
      case APIROLE:
        entitlements = API;
      case APIROLESELLER:
        entitlements = String.format("%s,%s", API, SELLER);
        break;
      case APIROLEBUYER:
        entitlements = String.format("%s,%s", API, BUYER);
        break;
      case APIIIQROLE:
        entitlements = APIIIQ;
        break;
      case DEALMANAGER:
        entitlements = String.format("%s,%s,%s", DEAL, MANAGER, USER);
        break;
      case DUMMY:
        entitlements = String.format("%s", NONE);
    }
    return entitlements;
  }

  public void setUpWireMockXandrDeal(String deal) {
    stubFor(
        post(urlMatching("/deal"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(deal)));
  }

  public void setUpWireMockXandrDealError(int httpStatus, String contentType, String responseBody) {
    stubFor(
        post(urlMatching("/deal"))
            .willReturn(
                aResponse()
                    .withStatus(httpStatus)
                    .withHeader("Content-Type", contentType)
                    .withBody(responseBody)));
  }

  public void setUpWireMockXandrDealForPut(String deal) {
    stubFor(
        put(urlMatching("/deal"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(deal)));
  }

  public void setUpWireMockXandrDealErrorForPut(
      int httpStatus, String contentType, String responseBody) {
    stubFor(
        put(urlMatching("/deal"))
            .willReturn(
                aResponse()
                    .withStatus(httpStatus)
                    .withHeader("Content-Type", contentType)
                    .withBody(responseBody)));
  }

  public void setUpWireMockXandrDealAuth(String authResponse) {
    stubFor(
        post(urlPathMatching("/auth"))
            .withHeader("Content-Type", matching("application/json"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(authResponse)));
  }
}
