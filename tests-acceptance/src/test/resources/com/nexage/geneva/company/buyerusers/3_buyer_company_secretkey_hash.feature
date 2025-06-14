Feature: update buyer company secret key as a buyer admin

  # test to verify new md5 hash value in reporting-api table is not updated when secret key is not updated
  Scenario: update Buyer company without new secret key
    Given the user "crudnexageadmin" has logged in with role "AdminNexage"
    And the user selects the "Buyer" company "debtestbuyer"
    Given the user "rptapibuyeradmin" has logged in with role "AdminBuyer"
    And the original secret key hash value is retrieved
    When the user updates a company from the json file "jsons/genevacrud/company/payload/buyer/UpdateWithoutSecretKey_payload1.json"
    Then request passed successfully
    When the user updates a company from the json file "jsons/genevacrud/company/payload/buyer/UpdateWithoutSecretKey_payload2.json"
    Then request passed successfully
    And returned "company update" data matches the following json file "jsons/genevacrud/company/expected_results/buyer/UpdateWithoutSecretKey_ER.json"
    Then secret key hash should not be changed
