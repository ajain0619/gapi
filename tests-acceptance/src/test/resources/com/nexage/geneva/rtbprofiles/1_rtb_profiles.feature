Feature:  RTBProfiles for seller

  Scenario: Get all RTBProfiles
    When the user "admin1c" has logged in with role "AdminNexage"
    Then the user grabs all RTBProfiles
    And the request returned status "OK"
    And returned "default_rtb_profiles" data matches the following json file "jsons/genevacrud/sellerrtbprofiles/expected_results/get_default_rtb_profiless_ER.json"

  Scenario: Get all RTBProfiles with Query Param
    When the user "admin1c" has logged in with role "AdminNexage"
    Then the user grabs all default RTBProfiles matching qt "My" with qf "name"
    And the request returned status "OK"
    And returned "default_rtb_profiles" data matches the following json file "jsons/genevacrud/sellerrtbprofiles/expected_results/get_default_rtb_profiles_QT_QF_ER.json"

  Scenario: update an RTB Profile
    When the user "admin1c" has logged in with role "AdminNexage"
    Then the user updates a seller rtb profile from the json file "jsons/genevacrud/sellerrtbprofiles/payload/update_seller_rtb_profile_payload.json"
    And the request returned status "OK"
