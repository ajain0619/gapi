Feature: Seller - Sites - Tags: create and update with transparency settings as pss user

  Background: Log in as a pss manager and set/update transparency settings
    Given the user "pssTransEnabledMgr" has logged in with role "ManagerSeller"
    When the user specifies the date range from "2017-08-17T00:00:00-04:00" to "2017-08-22T23:59:59-04:00"

  Scenario: Create exchange tag with site and publisher transparency set to Aliases
    When the user selects the site "aleksis_site" and the position "transparency_enabled_position"
    And the user creates a publisher "exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/create/PssExchangeTagWithSiteAndPublisherAliases_payload.json"
    Then request passed successfully with code "201"
    And siteAliasId is retrieved for tag "Zureks1_tag_transparency_enabled"
    And pubAliasId is retrieved for tag "Zureks1_tag_transparency_enabled"
    And returned "created exchange tag" data matches the following json file "jsons/genevacrud/tag/pss/expected_results/create/PssExchangeTagWithSiteAndPublisherAliases_ER.json"

  Scenario: Create exchange tag with site transparency type for pub and site set to Real
    When the user selects the site "aleksis_site" and the position "transparency_enabled_position"
    And the user creates a publisher "exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/create/PssExchangeTagWithSiteAndPublisherReal_payload.json"
    Then request passed successfully with code "201"
    And siteAliasId was not generated for tag "Zureks2_tag_transparency_enabled"
    And pubAliasId was not generated for tag "Zureks2_tag_transparency_enabled"
    And returned "created exchange tag" data matches the following json file "jsons/genevacrud/tag/pss/expected_results/create/PssExchangeTagWithSiteAndPublisherReal_ER.json"

  Scenario:  Create exchange tag with transparency for pub and site set to None
    When the user selects the site "aleksis_site" and the position "transparency_enabled_position"
    And the user creates a publisher "exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/create/PssExchangeTagWithSiteAndPublisherNone_payload.json"
    Then request passed successfully with code "201"
    And siteAliasId is retrieved for tag "Zureks3_tag_transparency_enabled"
    And pubAliasId is retrieved for tag "Zureks3_tag_transparency_enabled"
    And returned "created exchange tag" data matches the following json file "jsons/genevacrud/tag/pss/expected_results/create/PssExchangeTagWithSiteAndPublisherNone_ER.json"

  Scenario:  Create exchange tag with site transparency set to None and publisher transparency set to Alias
    When the user selects the site "aleksis_site" and the position "transparency_enabled_position"
    And the user creates a publisher "exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/create/PssExchangeTagWithSiteNoneAndPublisherAlias_payload.json"
    Then request passed successfully with code "201"
    And siteAliasId is retrieved for tag "Zureks4_tag_transparency_enabled"
    And pubAliasId is retrieved for tag "Zureks4_tag_transparency_enabled"
    And returned "created exchange tag" data matches the following json file "jsons/genevacrud/tag/pss/expected_results/create/PssExchangeTagWithSiteNoneAndPublisherAlias_ER.json"

  Scenario:  Create exchange tag with site transparency set to None and publisher transparency set to Real
    When the user selects the site "aleksis_site" and the position "transparency_enabled_position"
    And the user creates a publisher "exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/create/PssExchangeTagWithSiteNoneAndPublisherReal_payload.json"
    Then request passed successfully with code "201"
    And siteAliasId is retrieved for tag "Zureks5_tag_transparency_enabled"
    And pubAliasId was not generated for tag "Zureks5_tag_transparency_enabled"
    And returned "created exchange tag" data matches the following json file "jsons/genevacrud/tag/pss/expected_results/create/PssExchangeTagWithSiteNoneAndPublisherReal_ER.json"

  Scenario:  Create exchange tag with site transparency set to Real and publisher transparency set to None
    When the user selects the site "aleksis_site" and the position "transparency_enabled_position"
    And the user creates a publisher "exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/create/PssExchangeTagWithSiteRealAndPublisherNone_payload.json"
    Then request passed successfully with code "201"
    And siteAliasId was not generated for tag "Zureks6_tag_transparency_enabled"
    And pubAliasId is retrieved for tag "Zureks6_tag_transparency_enabled"
    And returned "created exchange tag" data matches the following json file "jsons/genevacrud/tag/pss/expected_results/create/PssExchangeTagWithSiteRealAndPublisherNone_ER.json"

  Scenario:  Create exchange tag with site transparency set to Alias and publisher transparency set to None
    When the user selects the site "aleksis_site" and the position "transparency_enabled_position"
    And the user creates a publisher "exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/create/PssExchangeTagWithSiteAliasAndPublisherNone_payload.json"
    Then request passed successfully with code "201"
    And siteAliasId is retrieved for tag "Zureks7_tag_transparency_enabled"
    And pubAliasId is retrieved for tag "Zureks7_tag_transparency_enabled"
    And returned "created exchange tag" data matches the following json file "jsons/genevacrud/tag/pss/expected_results/create/PssExchangeTagWithSiteAliasAndPublisherNone_ER.json"

  Scenario:  Create exchange tag with site transparency set to Real and publisher transparency set to Alias
    When the user selects the site "aleksis_site" and the position "transparency_enabled_position"
    And the user creates a publisher "exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/create/PssExchangeTagWithSiteRealAndPublisherAlias_payload.json"
    Then request passed successfully with code "201"
    And siteAliasId was not generated for tag "Zureks8_tag_transparency_enabled"
    And pubAliasId is retrieved for tag "Zureks8_tag_transparency_enabled"
    And returned "created exchange tag" data matches the following json file "jsons/genevacrud/tag/pss/expected_results/create/PssExchangeTagWithSiteRealAndPublisherAlias_ER.json"

  Scenario:  Create exchange tag with site transparency set to Alias and publisher transparency set to Real
    When the user selects the site "aleksis_site" and the position "transparency_enabled_position"
    And the user creates a publisher "exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/create/PssExchangeTagWithSiteAliasAndPublisherReal_payload.json"
    Then request passed successfully with code "201"
    And siteAliasId is retrieved for tag "Zureks9_tag_transparency_enabled"
    And pubAliasId was not generated for tag "Zureks9_tag_transparency_enabled"
    And returned "created exchange tag" data matches the following json file "jsons/genevacrud/tag/pss/expected_results/create/PssExchangeTagWithSiteAliasAndPublisherReal_ER.json"

  Scenario:  Create exchange tag without transparency settings for site and pub
    When the user selects the site "aleksis_site" and the position "transparency_enabled_position"
    And the user creates a publisher "exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/create/PssExchangeTagWithoutSitePubTransparencySettings_payload.json"
    Then request passed successfully with code "201"
    And siteAliasId was not generated for tag "Zureks97_tag_transparency_enabled"
    And pubAliasId was not generated for tag "Zureks97_tag_transparency_enabled"
    And returned "created exchange tag" data matches the following json file "jsons/genevacrud/tag/pss/expected_results/create/PssExchangeTagWithoutSitePubTransparencySettings_ER.json"

  Scenario:  Create exchange tag without transparency settings for publisher
    When the user selects the site "aleksis_site" and the position "transparency_enabled_position"
    And the user creates a publisher "exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/create/PssExchangeTagWithoutPublisherTransparencySettings_payload.json"
    Then request passed successfully with code "201"
    And siteAliasId is retrieved for tag "Zureks92_tag_transparency_enabled"
    And pubAliasId was not generated for tag "Zureks92_tag_transparency_enabled"
    And returned "created exchange tag" data matches the following json file "jsons/genevacrud/tag/pss/expected_results/create/PssExchangeTagWithoutPublisherTransparencySettings_ER.json"

  Scenario:  Create exchange tag without transparency settings for site
    When the user selects the site "aleksis_site" and the position "transparency_enabled_position"
    And the user creates a publisher "exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/create/PssExchangeTagWithoutSiteTransparencySettings_payload.json"
    Then request passed successfully with code "201"
    And siteAliasId was not generated for tag "Zureks99_tag_transparency_enabled"
    And pubAliasId was not generated for tag "Zureks99_tag_transparency_enabled"
    And returned "created exchange tag" data matches the following json file "jsons/genevacrud/tag/pss/expected_results/create/PssExchangeTagWithoutSiteTransparencySettings_ER.json"

  Scenario:  Update exchange tag set site transparency to Real and publisher transparency to Alias
    When the user selects the site "aleksis_site" and the position "transparency_enabled_position" and the tag "Update1_tag_transparency_enabled"
    And the user updates the publisher "exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/update/PssUpdateExchangeTagSiteRealPubAlias_payload.json"
    Then request passed successfully
    And siteAliasId was not generated for tag "Update1_tag_transparency_enabled"
    And pubAliasId is retrieved for tag "Update1_tag_transparency_enabled"
    Then returned "updated exchange tag" data matches the following json file "jsons/genevacrud/tag/pss/expected_results/update/PssUpdateExchangeTagSiteRealPubAlias_ER.json"

  @restoreCrudCoreDatabaseBefore

  Scenario:  Update exchange tag set site transparency to None and publisher transparency to Real
    When the user selects the site "aleksis_site" and the position "transparency_enabled_position" and the tag "Update1_tag_transparency_enabled"
    And the user updates the publisher "exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/update/PssUpdateExchangeTagSiteNonePubReal_payload.json"
    Then request passed successfully
    And siteAliasId is retrieved for tag "Update1_tag_transparency_enabled"
    And pubAliasId was not generated for tag "Update1_tag_transparency_enabled"
    Then returned "updated exchange tag" data matches the following json file "jsons/genevacrud/tag/pss/expected_results/update/PssUpdateExchangeTagSiteNonePubReal_ER.json"

  @restoreCrudCoreDatabaseBefore

  Scenario:  Update exchange tag set site transparency to Alias and publisher transparency to None
    When the user selects the site "aleksis_site" and the position "transparency_enabled_position" and the tag "Update1_tag_transparency_enabled"
    And the user updates the publisher "exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/update/PssUpdateExchangeTagSiteAliasPubNone_payload.json"
    Then request passed successfully
    And siteAliasId is retrieved for tag "Update1_tag_transparency_enabled"
    And pubAliasId is retrieved for tag "Update1_tag_transparency_enabled"
    Then returned "updated exchange tag" data matches the following json file "jsons/genevacrud/tag/pss/expected_results/update/PssUpdateExchangeTagSiteAliasPubNone_ER.json"

  Scenario:  Update exchange tag set site and publisher transparency to None
    When the user selects the site "aleksis_site" and the position "transparency_enabled_position" and the tag "Update2_tag_transparency_enabled"
    And siteAliasId is retrieved for tag "Update2_tag_transparency_enabled"
    And pubAliasId is retrieved for tag "Update2_tag_transparency_enabled"
    And the user updates the publisher "exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/update/PssUpdateExchangeTagSitePubNone_payload.json"
    Then request passed successfully
    And siteAliasId was not regenerated for tag "Update2_tag_transparency_enabled"
    And pubAliasId was not regenerated for tag "Update2_tag_transparency_enabled"
    Then returned "updated exchange tag" data matches the following json file "jsons/genevacrud/tag/pss/expected_results/update/PssUpdateExchangeTagSitePubNone_ER.json"

  @restoreCrudCoreDatabaseBefore

  Scenario:  Update exchange tag set site and publisher transparency to RealName
    When the user selects the site "aleksis_site" and the position "transparency_enabled_position" and the tag "Update2_tag_transparency_enabled"
    And the user updates the publisher "exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/update/PssUpdateExchangeTagSitePubReal_payload.json"
    Then request passed successfully
    And siteAliasId was not generated for tag "Update2_tag_transparency_enabled"
    And pubAliasId was not generated for tag "Update2_tag_transparency_enabled"
    Then returned "updated exchange tag" data matches the following json file "jsons/genevacrud/tag/pss/expected_results/update/PssUpdateExchangeTagSitePubReal_ER.json"

  @restoreCrudCoreDatabaseBefore

  Scenario:  Update exchange tag set site and publisher transparency to Aliases
    When the user selects the site "aleksis_site" and the position "transparency_enabled_position" and the tag "Update2_tag_transparency_enabled"
    And siteAliasId is retrieved for tag "Update2_tag_transparency_enabled"
    And pubAliasId is retrieved for tag "Update2_tag_transparency_enabled"
    And the user updates the publisher "exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/update/PssUpdateExchangeTagSitePubAliases_payload.json"
    Then request passed successfully
    And siteAliasId was not regenerated for tag "Update2_tag_transparency_enabled"
    And pubAliasId was not regenerated for tag "Update2_tag_transparency_enabled"
    Then returned "updated exchange tag" data matches the following json file "jsons/genevacrud/tag/pss/expected_results/update/PssUpdateExchangeTagSitePubAliases_ER.json"

  Scenario:  Update exchange tag without transparency settings for site
    When the user selects the site "aleksis_site" and the position "transparency_enabled_position" and the tag "Update1_tag_transparency_enabled"
    And the user updates the publisher "exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/update/PssUpdateExchangeTagWithoutSiteTransparencySettings_payload.json"
    Then request passed successfully
    And siteAliasId was not generated for tag "Update1_tag_transparency_enabled"
    And pubAliasId was not generated for tag "Update1_tag_transparency_enabled"
    Then returned "updated exchange tag" data matches the following json file "jsons/genevacrud/tag/pss/expected_results/update/PssUpdateExchangeTagWithoutSiteTransparencySettings_ER.json"

  @restoreCrudCoreDatabaseBefore

  Scenario:  Update exchange tag without transparency settings for publisher
    When the user selects the site "aleksis_site" and the position "transparency_enabled_position" and the tag "Update1_tag_transparency_enabled"
    And the user updates the publisher "exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/update/PssUpdateExchangeTagWithoutPubTransparencySettings_payload.json"
    Then request passed successfully
    And siteAliasId was not generated for tag "Update1_tag_transparency_enabled"
    And pubAliasId was not generated for tag "Update1_tag_transparency_enabled"
    Then returned "updated exchange tag" data matches the following json file "jsons/genevacrud/tag/pss/expected_results/update/PssUpdateExchangeTagWithoutPubTransparencySettings_ER.json"

  Scenario:  Update exchange tag regenerate alias id for site only
    When the user selects the site "aleksis_site" and the position "transparency_enabled_position" and the tag "Update2_tag_transparency_enabled"
    And pubAliasId is retrieved for tag "Update2_tag_transparency_enabled"
    And siteAliasId is retrieved for tag "Update2_tag_transparency_enabled"
    And the user updates the publisher "exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/update/PssUpdateExchangeTagRegenerateSiteAliasId_payload.json"
    Then request passed successfully
    And siteAliasId was regenerated for tag "Update2_tag_transparency_enabled"
    And pubAliasId was not regenerated for tag "Update2_tag_transparency_enabled"
    Then returned "updated exchange tag" data matches the following json file "jsons/genevacrud/tag/pss/expected_results/update/PssUpdateExchangeTagRegenerateSiteAliasId_ER.json"

  @restoreCrudCoreDatabaseBefore

  Scenario:  Update exchange tag and regenerate alias id for publisher only
    When the user selects the site "aleksis_site" and the position "transparency_enabled_position" and the tag "Update2_tag_transparency_enabled"
    And pubAliasId is retrieved for tag "Update2_tag_transparency_enabled"
    And siteAliasId is retrieved for tag "Update2_tag_transparency_enabled"
    And the user updates the publisher "exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/update/PssUpdateExchangeTagRegeneratePubAliasId_payload.json"
    Then request passed successfully
    And siteAliasId was not regenerated for tag "Update2_tag_transparency_enabled"
    And pubAliasId was regenerated for tag "Update2_tag_transparency_enabled"
    Then returned "updated exchange tag" data matches the following json file "jsons/genevacrud/tag/pss/expected_results/update/PssUpdateExchangeTagRegeneratePubAliasId_ER.json"

  @restoreCrudCoreDatabaseBefore

  Scenario:  Update exchange tag and regenerate alias id for site and publisher
    When the user selects the site "aleksis_site" and the position "transparency_enabled_position" and the tag "Update2_tag_transparency_enabled"
    And pubAliasId is retrieved for tag "Update2_tag_transparency_enabled"
    And siteAliasId is retrieved for tag "Update2_tag_transparency_enabled"
    And the user updates the publisher "exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/update/PssUpdateExchangeTagRegeneratePubSiteAliasId_payload.json"
    Then request passed successfully
    And siteAliasId was regenerated for tag "Update2_tag_transparency_enabled"
    And pubAliasId was regenerated for tag "Update2_tag_transparency_enabled"
    Then returned "updated exchange tag" data matches the following json file "jsons/genevacrud/tag/pss/expected_results/update/PssUpdateExchangeTagRegeneratePubSiteAliasId_ER.json"

  Scenario:  Negative - Create exchange tag with transparency type for pub and site set to Alias when transparency is disabled
    Given the user "pssTransDisabledMgr" has logged in with role "ManagerSeller"
    When the user selects the site "aleksis_site_disabled" and the position "transparency_disabled_position"
    And the user creates a publisher "exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/invalid/PssCreateTagWithAliasesWhenDisabled_payload.json"
    Then "Create exchange tag" failed with "401" response code

  Scenario: Negative - Create exchange tag with site transparency set to alias with alias name set to null
    When the user selects the site "aleksis_site" and the position "transparency_enabled_position"
    And the user creates a publisher "exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/invalid/PssCreateTagSiteAliasWithNameNull_payload.json"
    Then "Create exchange tag" failed with "400" response code

  Scenario: Negative - Create exchange tag with publisher transparency set to alias with alias name set to null
    When the user selects the site "aleksis_site" and the position "transparency_enabled_position"
    Given the user creates a publisher "exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/invalid/PssCreateTagPubAliasWithNameNull_payload.json"
    Then "Create exchange tag" failed with "400" response code

  Scenario: Negative - Create exchange tag with site transparency set to real with site alias name
    When the user selects the site "aleksis_site" and the position "transparency_enabled_position"
    Given the user creates a publisher "exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/invalid/PssCreateTagSiteRealWithAliasName_payload.json"
    Then "Create exchange tag" failed with "400" response code

  Scenario: Negative - Create exchange tag with publisher transparency set to real with publisher alias name
    When the user selects the site "aleksis_site" and the position "transparency_enabled_position"
    Given the user creates a publisher "exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/invalid/PssCreateTagPubRealWithAliasName_payload.json"
    Then "Create exchange tag" failed with "400" response code

  Scenario: Negative - Create exchange tag with site transparency set to none with site alias name
    When the user selects the site "aleksis_site" and the position "transparency_enabled_position"
    Given the user creates a publisher "exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/invalid/PssCreateTagSiteNoneWithAliasName_payload.json"
    Then "Create exchange tag" failed with "400" response code

  Scenario: Negative - Create exchange tag with publisher transparency set to none with publisher alias name
    When the user selects the site "aleksis_site" and the position "transparency_enabled_position"
    Given the user creates a publisher "exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/invalid/PssCreateTagPubNoneWithAliasName_payload.json"
    Then "Create exchange tag" failed with "400" response code

  Scenario: Negative - Update tag set transparency for site and publisher to Real when transparency is disabled
    Given the user "pssTransDisabledMgr" has logged in with role "ManagerSeller"
    And the user selects the site "aleksis_site_disabled" and the position "transparency_disabled_position" and the tag "Update1_tag_transparency_disabled"
    When the user updates the publisher "exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/invalid/PssUpdateTagPubAndSiteRealNamesWhenDisabled_payload.json"
    Then "Update exchange tag" failed with "401" response code

  Scenario: Negative - Update tag set site transparency set to alias with alias name set to null
    Given the user selects the site "aleksis_site" and the position "transparency_enabled_position" and the tag "Update1_tag_transparency_enabled"
    When the user updates the publisher "exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/invalid/PssUpdateTagSiteAliasWithNameNull_payload.json"
    Then "Update exchange tag" failed with "400" response code

  Scenario: Negative - Update tag set publisher transparency set to alias with alias name set to null
    Given the user selects the site "aleksis_site" and the position "transparency_enabled_position" and the tag "Update1_tag_transparency_enabled"
    And the user updates the publisher "exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/invalid/PssUpdateTagPubAliasWithNameNull_payload.json"
    Then "Update exchange tag" failed with "400" response code

  Scenario: Negative - Update tag with site transparency set to real with site alias name
    Given the user selects the site "aleksis_site" and the position "transparency_enabled_position" and the tag "Update1_tag_transparency_enabled"
    And the user updates the publisher "exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/invalid/PssUpdateTagSiteRealWithAliasName_payload.json"
    Then "Update exchange tag" failed with "400" response code

  Scenario: Negative - Update exchange tag with publisher transparency set to real with publisher alias name
    Given the user selects the site "aleksis_site" and the position "transparency_enabled_position" and the tag "Update1_tag_transparency_enabled"
    And the user updates the publisher "exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/invalid/PssUpdateTagPubRealWithAliasName_payload.json"
    Then "Update exchange tag" failed with "400" response code

  Scenario: Negative - Update exchange tag with site transparency set to none with site alias name
    Given the user selects the site "aleksis_site" and the position "transparency_enabled_position" and the tag "Update1_tag_transparency_enabled"
    And the user updates the publisher "exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/invalid/PssUpdateTagSiteNoneWithAliasName_payload.json"
    Then "Update site" failed with "400" response code

  Scenario: Negative - Update exchange tag with publisher transparency set to none with publisher alias name
    Given the user selects the site "aleksis_site" and the position "transparency_enabled_position" and the tag "Update1_tag_transparency_enabled"
    And the user updates the publisher "exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/invalid/PssUpdateTagPubNoneWithAliasName_payload.json"
    Then "Update exchange tag" failed with "400" response code

  Scenario: Clone exchange tag when site transparency is Real and publisher transparency is Real
    When the user selects the site "aleksis_site" and the position "transparency_enabled_position" and the tag "Update1_tag_transparency_enabled"
    And the user selects the target site "10000202" and the target position "100318"
    And the user clones a publisher "exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/clone/PssCloneExchangeTagSitePubReal_payload.json"
    Then request passed successfully
    And returned "cloned exchange tag" data matches the following json file "jsons/genevacrud/tag/pss/expected_results/clone/PssCloneExchangeTagSitePubReal_ER.json"
    And tag "COPY - Update1_tag_transparency_enabled" can be searched in the database

  Scenario: Clone exchange tag when site and publisher transparency is Aliases
    When the user selects the site "aleksis_site" and the position "transparency_enabled_position" and the tag "Update2_tag_transparency_enabled"
    And the user selects the target site "10000202" and the target position "100318"
    And the user clones a publisher "exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/clone/PssCloneExchangeTagSitePubAliases_payload.json"
    Then request passed successfully
    And returned "cloned exchange tag" data matches the following json file "jsons/genevacrud/tag/pss/expected_results/clone/PssCloneExchangeTagSitePubAliases_ER.json"
    And tag "COPY - Update2_tag_transparency_enabled" can be searched in the database
    And pubAliasId is retrieved for tag "COPY - Update2_tag_transparency_enabled"
    And siteAliasId is retrieved for tag "COPY - Update2_tag_transparency_enabled"

  Scenario: Clone exchange tag for site transparency and publisher transparency is Real and transparency is disabled
    Given the user "pssTransDisabledMgr" has logged in with role "ManagerSeller"
    When the user selects the site "aleksis_site_disabled" and the position "transparency_disabled_position" and the tag "Update1_tag_transparency_disabled"
    And the user selects the target site "10000203" and the target position "100319"
    And the user clones a publisher "exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/invalid/PssCloneExchangeTagSitePubRealDisabled_payload.json"
    Then request passed successfully
    And returned "cloned exchange tag" data matches the following json file "jsons/genevacrud/tag/pss/expected_results/clone/PssCloneExchangeTagSitePubRealDisabled_ER.json"
    And tag "COPY - Update1_tag_transparency_disabled" can be searched in the database
