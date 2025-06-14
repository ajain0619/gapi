Feature: create, update, hb partner associations for company, site and position

  Background: log in
    Given the user "admin1c" has logged in with role "AdminNexage"

  Scenario Outline: create a company with hb partner attributes
    When the user creates a company using pss from the json file "jsons/genevacrud/hbpartner/payload/<file_payload>.json"
    Then request passed successfully
    And returned "Company" data matches the following json file "jsons/genevacrud/hbpartner/expected_results/<file_ER>.json"

    Examples:
      | file_payload                                  | file_ER                                 |
      | CreateCompanyWithHbPartnerAttributes_payload  | CreateCompanyWithHbPartnerAttrbutes_ER  |

  Scenario: create and update new site with hb partner attributes
    Given the user selects the "Seller" company "PssSellerWithHbPartnerAttributes"
    When the PSS user creates a site from the json file "jsons/genevacrud/hbpartner/payload/CreateSiteWithHbPartnerAttributes_payload.json"
    Then request passed successfully with code "201"
    And returned "site" data matches the following json file "jsons/genevacrud/hbpartner/expected_results/CreateSiteWithHbPartnerAttributes_ER.json"
    And default site data is set
    Given the user selects the "Seller" company "PssSellerWithHbPartnerAttributes"
    And the Seller sites for PSS user are retrieved
    And the PSS user selects the site "hb_partner_site"
    When the PSS user gets site update info from the json file "jsons/genevacrud/hbpartner/payload/UpdateSiteWithHbPartnerAttributes_payload.json"
    Then request passed successfully
    When the PSS user updates site with data from the json file "jsons/genevacrud/hbpartner/payload/UpdateSiteWithHbPartnerAttributes_payload.json"
    Then request passed successfully
    And returned "site" data matches the following json file "jsons/genevacrud/hbpartner/expected_results/UpdateSiteWithHbPartnerAttributes_ER.json"

  Scenario: create position with hb partner attributes
    Given the user selects the "Seller" company "PssSellerWithHbPartnerAttributes"
    Given the PSS user selects the site "hb_partner_site"
    When the PSS user creates position from the json file "jsons/genevacrud/hbpartner/payload/CreatePositionWithHbPartnerAttributes_payload.json"
    Then request passed successfully with code "201"
    And returned "position" data matches the following json file "jsons/genevacrud/hbpartner/expected_results/CreatePositionWithHbPartnerAttributes_ER.json"
    And returned pss position can be searched out in database
    And pss position data in database is correct

  Scenario: Update position with hb partner attributes
    Given the user selects the "Seller" company "PssSellerWithHbPartnerAttributes"
    Given the PSS user selects the site "hb_partner_site"
    And position with name "banner_hb_partner" is selected
    When the PSS user updates selected position from the json file "jsons/genevacrud/hbpartner/payload/UpdatePositionWithHbPartnerAttributes_payload.json"
    Then request passed successfully
    And returned "position" data matches the following json file "jsons/genevacrud/hbpartner/expected_results/UpdatePositionWithHbPartnerAttributes_ER.json"
    And returned pss position can be searched out in database
    And pss position data in database is correct

  Scenario: update a company with hb partner attributes
    When set company "PssSellerWithHbPartnerAttributes"
    And the user updates a company using pss from the json file "jsons/genevacrud/hbpartner/payload/UpdateCompanyWithHbPartnerAttributes_payload.json"
    Then request passed successfully
    And returned "Company" data matches the following json file "jsons/genevacrud/hbpartner/expected_results/UpdateCompanyWithHbPartnerAttributes_ER.json"

  Scenario: update a company, delete hb partner attributes with child associations
    Given the user selects the "Seller" company "PssSellerWithHbPartnerAttributes"
    And the user updates a company using pss from the json file "jsons/genevacrud/hbpartner/payload/UpdateCompanyWithChildAssociations_payload.json"
    Then "update company" failed with "400" response code and error message "Unable to delete the S2S integration for this company, please delete S2S integration for its sites and/or placements."

  Scenario: update a site with child hb partner associations
    Given the user selects the "Seller" company "PssSellerWithHbPartnerAttributes"
    Given the PSS user selects the site "hb_partner_site"
    And position with name "banner_hb_partner" is selected
    When the PSS user updates selected position from the json file "jsons/genevacrud/hbpartner/payload/UpdatePositionWithHbPartnerAttributes_payload2.json"
    Then request passed successfully
    Given the PSS user selects the site "hb_partner_site"
    When the PSS user gets site update info from the json file "jsons/genevacrud/hbpartner/payload/UpdateSiteWithPositionAssociations_payload.json"
    Then request passed successfully
    When the PSS user updates site with data from the json file "jsons/genevacrud/hbpartner/payload/UpdateSiteWithPositionAssociations_payload.json"
    Then "update site" failed with "400" response code and error message "Unable to delete the S2S integration for this site, please delete S2S integration for its placements."

  Scenario: create default site with hb partner attributes and external site id empty sets the external site id to site name
    Given the user selects the "Seller" company "PssSellerWithHbPartnerAttributes"
    When the PSS user creates a site from the json file "jsons/genevacrud/hbpartner/payload/CreateSiteWithHbPartnerAttributes_payload2.json"
    Then request passed successfully with code "201"
    And returned "site" data matches the following json file "jsons/genevacrud/hbpartner/expected_results/CreateSiteWithHbPartnerAttributes_ER2.json"

  Scenario: create default position with hb partner attributes and external position id empty sets the external position id to site name
    Given the user selects the "Seller" company "PssSellerWithHbPartnerAttributes"
    Given the PSS user selects the site "hb_partner_site"
    When the PSS user creates position from the json file "jsons/genevacrud/hbpartner/payload/CreatePositionWithHbPartnerAttributes_payload2.json"
    Then request passed successfully with code "201"
    And returned "position" data matches the following json file "jsons/genevacrud/hbpartner/expected_results/CreatePositionWithHbPartnerAttributes_ER2.json"
    And returned pss position can be searched out in database
    And pss position data in database is correct

  Scenario: creating a 2nd default position fails if a default position already exists for the same hb partner & site
    Given the user selects the "Seller" company "PssSellerWithHbPartnerAttributes"
    Given the PSS user selects the site "hb_partner_site"
    When the PSS user creates position from the json file "jsons/genevacrud/hbpartner/payload/CreatePositionWithHbPartnerAttributes_payload3.json"
    Then "create default position" failed with "400" response code and error message "Only one default position allowed per partner for a site"

  Scenario: creating a 2nd non default position for the same site and the hb partner
    Given the user selects the "Seller" company "PssSellerWithHbPartnerAttributes"
    Given the PSS user selects the site "hb_partner_site"
    When the PSS user creates position from the json file "jsons/genevacrud/hbpartner/payload/CreatePositionWithHbPartnerAttributes_payload4.json"
    Then request passed successfully with code "201"
    And returned "position" data matches the following json file "jsons/genevacrud/hbpartner/expected_results/CreatePositionWithHbPartnerAttributes_ER4.json"
    And returned pss position can be searched out in database
    And pss position data in database is correct

  Scenario: create position with hb partner attributes with special characters for name and memo fields
    Given the user selects the "Seller" company "PssSellerWithHbPartnerAttributes"
    Given the PSS user selects the site "hb_partner_site"
    When the PSS user creates position from the json file "jsons/genevacrud/hbpartner/payload/CreatePositionWithSpecialCharactersforNameAndMemo_payload.json"
    Then request passed successfully with code "201"
    And returned "position" data matches the following json file "jsons/genevacrud/hbpartner/expected_results/CreatePositionWithSpecialCharactersforNameAndMemo_ER.json"
    And returned pss position can be searched out in database
    And pss position data in database is correct

  Scenario: create company and site with invalid hb partner association type
    When the user creates a company using pss from the json file "jsons/genevacrud/hbpartner/payload/CreateCompanyWithInvalidAssociationType_payload.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors "{"hbPartnerAttributes":"Invalid hb partner association type"}"
    Given the user selects the "Seller" company "PssSellerWithHbPartnerAttributes"
    When the PSS user creates a site from the json file "jsons/genevacrud/hbpartner/payload/CreateSiteWithInvalidAssociationType_payload.json"
    Then response failed with "400" response code, error message "Bad Request. Check your request parameters (json format, type..)" and field errors "{"hbPartnerAttributes":"Invalid hb partner association type"}"

  Scenario: create position with multi bidding hb partner attributes
    Given the user selects the "Seller" company "PssSellerWithHbPartnerAttributes"
    Given the PSS user selects the site "hb_partner_site"
    When the PSS user creates position from the json file "jsons/genevacrud/hbpartner/payload/CreatePositionWithMultiBiddingHbPartnerAttributes_payload.json"
    Then request passed successfully with code "201"
    And returned "position" data matches the following json file "jsons/genevacrud/hbpartner/expected_results/CreatePositionWithMultiBiddingHbPartnerAttributes_ER.json"

  Scenario: Update position with multi bidding hb partner attributes
    Given the user selects the "Seller" company "PssSellerWithHbPartnerAttributes"
    Given the PSS user selects the site "hb_partner_site"
    And position with name "create_placement_with_multibidding" is selected
    When the PSS user updates selected position from the json file "jsons/genevacrud/hbpartner/payload/UpdatePositionWithMultiBiddingHbPartnerAttributes_payload.json"
    Then request passed successfully
    And returned "position" data matches the following json file "jsons/genevacrud/hbpartner/expected_results/UpdatePositionWithMultiBiddingHbPartnerAttributes_ER.json"

  Scenario: Get position with multi bidding hb partner attributes
    Given the user selects the "Seller" company "PssSellerWithHbPartnerAttributes"
    Given the PSS user selects the site "hb_partner_site"
    And position with name "create_placement_with_multibidding" is selected
    And the PSS user gets data for selected position
    Then request passed successfully with code "200"
    And returned "placement" data matches the following json file "jsons/genevacrud/hbpartner/expected_results/GetPositionWithMultiBiddingHbPartnerAttributes_ER.json"

  Scenario: Get detailed position with multi bidding hb partner attributes
    Given the user selects the "Seller" company "PssSellerWithHbPartnerAttributes"
    Given the PSS user selects the site "hb_partner_site"
    And position with name "create_placement_with_multibidding" is selected
    And position by pid "100333" is retrieved from database
    When the PSS user gets detailed data for selected position
    Then request passed successfully with code "200"
    And returned "placement" data matches the following json file "jsons/genevacrud/hbpartner/expected_results/GetDetailedPositionWithMultiBiddingHbPartnerAttributes_ER.json"

  Scenario: create position with invalid multi bidding attributes fails with correct error message
    Given the user selects the "Seller" company "PssSellerWithHbPartnerAttributes"
    Given the PSS user selects the site "hb_partner_site"
    When the PSS user creates position from the json file "jsons/genevacrud/hbpartner/payload/<file_payload>.json"
    Then "create positionr" failed with "400" response code and error message "<fail_msg>"
    Examples:
      | file_payload                                                                   | fail_msg                                                                                                 |
      | CreatePositionWithMultiBiddingHbPartnerAttributesInvalidMultiImpressionBid     | "Bad Request. Check your request parameters (json format, type..)"                                       |
      | CreatePositionWithMultiBiddingHbPartnerAttributesInvalidCompetitiveSeparation  | "Bad Request. Check your request parameters (json format, type..)"                                       |
