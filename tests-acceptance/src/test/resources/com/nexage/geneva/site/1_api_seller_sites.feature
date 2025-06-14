Feature: Api Seller Sites: get

  Background: log in as seller admin
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    And "Seller" companies are retrieved

  Scenario: get all the sites paginated
    When the seller pid is passed in to grab "1" sites
    Then request passed successfully
    And returned "sites" data matches the following json file "jsons/genevacrud/site/expected_results/GetSitesPaginated0_ER.json"
    And grab the second page of "1" sites "1" page
    Then request passed successfully
    And returned "sites" data matches the following json file "jsons/genevacrud/site/expected_results/GetSitesPaginated1_ER.json"

  Scenario: get all the sites that match the query term
    When make a request to read this company's sites with the query term "8A"
    Then request passed successfully
    And returned "sites" data matches the following json file "jsons/genevacrud/site/expected_results/GetSitesQTPaginated_ER.json"

  Scenario Outline: Confirm siteType and active filters are applied

    Given a request is made to list the sites for this company and site type "<site_type>" and status "<status>"
    Then request passed successfully
    And returned "sites" data has the key "totalElements" and value "<total_elements>"

    Examples:
      | site_type  | status   | total_elements |
      | MOBILE_WEB | ACTIVE   | 2              |
      | ANDROID    | ACTIVE   | 0              |
      | IOS        | ACTIVE   | 0              |
      | DESKTOP    | ACTIVE   | 0              |
      | MOBILE_WEB | INACTIVE | 0              |

  Scenario: get all the sites paginated and fields limited
    When the seller pid is passed in to get all sites with fetch field value "limited"
    Then request passed successfully
    And returned "sites" data matches the following json file "jsons/genevacrud/site/expected_results/GetSitesPaginatedLimited_ER.json"


