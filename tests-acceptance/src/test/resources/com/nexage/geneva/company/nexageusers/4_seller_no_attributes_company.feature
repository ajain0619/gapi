Feature: Get Publisher company without Seller Attributes

  # Geneva patch kit Geneva_20160908_162.1.0.jar (and sprint 163)
  # fixes a server 500 response for this scenario
  Scenario: Get publisher data
    Given the user "admin1c" has logged in with role "AdminNexage"
    And the user selects the "Seller" company "Burst Media"
    When publisher data is retrieved
    Then request passed successfully
