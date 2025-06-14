Feature:PSS - Sites: update site with revenue settings fields and validate tag dependent on this site

  Background: log in as nexage yield manager and select company
    Given the user specifies the date range from "2018-03-06T00:00:00-04:00" to "2019-03-06T23:59:59-04:00"
    Given the user "crudnexagemanageryield" has logged in with role "ManagerYieldNexage"
    Given the user selects the "Seller" company "transparency_enabled"
    And the user selects the site "aleksis_site"

  #Validate that site is not changed after calling site update info request and returned correct amount of tags (if there is an rtb / non rtb tags).
  #Site is not updated with new nexage revenue share and rtb fee.
  #https://jira.vzbuilders.com/browse/MX-16631 flaky deal_term
  Scenario: get site update info without update site and validate that tags remain unchanged
    And the user sets the site "aleksis_site" pid and the position "transparency_enabled_position" pid
    And the user creates a publisher "non-exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/create/CreatePssNonRtbTagAllNewFieldsNA_payload.json"
    Then request passed successfully with code "201"
    And the PSS user gets site update info from the json file "jsons/genevacrud/site/pss/payload/UpdateSiteUpdateInfo_payload.json"
    Then request passed successfully
    And returned "site info" data matches the following json file "jsons/genevacrud/site/pss/expected_results/SiteUpdateInfoBase_ER.json"
    When the PSS user selects the site "aleksis_site"
    Then returned "get site" data matches the following json file "jsons/genevacrud/site/pss/expected_results/GetSiteForUpdateInfo_ER.json"
    When publisher tags are retrieved
    Then request passed successfully
    And returned "publisher tag" data matches the following json file "jsons/genevacrud/tag/pss/expected_results/get/GetTagsWithoutUpdateSiteRevenue_ER.json"
    And the deal terms for specified site "aleksis_site" equals to "2"

    @restoreCrudCoreDatabaseBefore

  #There is a site with two rtb tags. One of them overrides nexage revenue share, rtb fee or both. Update a site with new nexage revenue share (rtb fee or both)
  #and make sure, that tag data is updated correctly
  #https://jira.vzbuilders.com/browse/MX-16631 flaky deal_term
  Scenario Outline: update a site with new revenue share, rtb fee or both when tags override some of these values or not
    And the user sets the site "aleksis_site" pid and the position "transparency_enabled_position" pid
    And the user sets the tag "Update1_tag_transparency_enabled" pid
    And the user updates the publisher "exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/update/<update_tag>.json"
    Then request passed successfully
    And the PSS user gets site update info from the json file "jsons/genevacrud/site/pss/payload/UpdateSiteUpdateInfo_payload.json"
    Then request passed successfully
    And returned "site info" data matches the following json file "jsons/genevacrud/site/pss/expected_results/<update_info_ER>.json"
    And the PSS user updates site with data from the json file "jsons/genevacrud/site/pss/payload/UpdateSiteUpdateInfo_payload.json"
    Then request passed successfully
    Then publisher tags are retrieved
    And request passed successfully
    And returned "publisher tag" data matches the following json file "jsons/genevacrud/tag/pss/expected_results/get/<get_tags_ER>.json"
    And the deal terms for specified site "aleksis_site" equals to "3"

    Examples:
      | update_tag                          | update_info_ER                      | get_tags_ER                               |
      | UpdateTagRevShareOverriding_payload | SiteUpdateInfoRevShareOverriding_ER | GetTagsOneWithRevShareOverriding_ER       |
      | UpdateTagRtbFeeOverriding_payload   | SiteUpdateInfoRtbFeeOverriding_ER   | GetTagsOneWithRtbFeeOverriding_ER         |
      | UpdateTagRevBothOverriding_payload  | SiteUpdateInfoRevBothOverriding_ER  | GetTagsOneWithRevShareRtbFeeOverriding_ER |


