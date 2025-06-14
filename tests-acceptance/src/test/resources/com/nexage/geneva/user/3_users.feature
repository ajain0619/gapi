Feature: user permission tests

  Scenario: update Seller company which does not have access to a company fails
    Given the user "adminathens1" has logged in with role "AdminSeller"
    When Seller selects the company with id "313" and type "Seller"
    When the user updates a company without permissions for the user from the json file "jsons/genevacrud/user/payload/update/UpdateCompany_payload.json"
    Then "Company update" failed with "401" response code and error message "You're not authorized to perform this operation"

  Scenario: update Seller company for a stakeholder with role user fails
    Given the user "cpi_seller" has logged in with role "UserSeller"
    And "Seller" companies are retrieved
    When the user updates a company from the json file "jsons/genevacrud/user/payload/update/UpdateCompany_payload.json"
    Then "Company update" failed with "401" response code and error message "You're not authorized to perform this operation"

  Scenario: update tag for a stakeholder with role user fails
    Given the user "tagarchiveuser" has logged in with role "UserSeller"
    And the user specifies the date range from "2014-09-01T00:00:00-04:00" to "2016-09-23T00:00:00-04:00"
    When the user selects the site "TagArchive_TestSite1" and the position "Banner" and the tag "ExchangeAssigned"
    And the user updates the publisher "exchange" tag from the json file "jsons/genevacrud/user/payload/update/UpdateTag_payload.json"
    Then "Site update" failed with "401" response code and error message "You're not authorized to perform this operation"

  Scenario: update site for a stakeholder with role user fails
    Given the user "cpi_seller" has logged in with role "UserSeller"
    Given the Seller sites for PSS user are retrieved
    And the PSS user selects the site "cpi_site"
    And the PSS user gets site update info from the json file "jsons/genevacrud/user/payload/update/UpdateSite_payload.json"
    Then "Site update" failed with "401" response code and error message "You're not authorized to perform this operation"

  Scenario: update site for a stakeholder with role admin is successful
    Given the user "admin1c" has logged in with role "AdminNexage"
    And the user selects the "Seller" company "cpi_seller"
    And the user selects the site "cpi_site"
    When the user updates site with data from the json file "jsons/genevacrud/user/payload/update/UpdateSite_payload2.json"
    Then request passed successfully

