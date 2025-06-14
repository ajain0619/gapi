# Created by rasangasamarasinghe at 09/03/2016
Feature: Seller - Sites - Tags: archive tags as Seller admin and check that they cannot be found
# Test Data
# Site TagArchive_TestSite1, position Banner
# Tags asscoaiated with tag archiving --> 7377(exchange unassigned), 7378(exhange assigned), 7379(mediation unassigned)
#                           			  and 7380(exchange unassigned without performance data in DW

  @restoreCrudDWDatabaseBefore
  # the following function updates the start and end dates, for the test tags, in the fact_revenue_adnet table
# to current-2days and current-1day, respectively
# the purpose is to get some meaningful stats for tags associated with the position to be archived
  Scenario: update fact_revenue_adnet table
    Then update fact_revenue_adnet start and end dates

#   todo Scenario: pss user can't archive, tags MX-256
#   #GET http://geneva.sbx/geneva/pss/10213/site/10000184/position/100289/tag/7377/tagPerformanceMetrics/archiveTransaction?_=1457727394978
#   #DELETE http://geneva.sbx/geneva/pss/105/site/950/position/13651/tag/7378?txid=798d1ea315909274222086cffc1992fe
#    Given the user "tagarchiveuser" has logged in
#    And the user specifies the date range from "2016-03-01T00:00:00-04:00" to "2016-03-23T00:00:00-04:00"
#    And the user selects the site "TagArchive_TestSite1" and the position "Banner" and the tag "<tagname>"
#    When the PSS user fails to gets performance metrics for selected tag

  Scenario Outline: pss manager can archive tags
    #GET http://geneva.sbx/geneva/pss/10213/site/10000184/position/100289/tag/7377/tagPerformanceMetrics/archiveTransaction?
    #DELETE http://geneva.sbx/geneva/pss/10213/site/10000184/position/100289/tag/7377?txid=
    Given the user "tagarchivemanager" has logged in with role "ManagerSeller"
    And the user specifies the date range from "2016-03-01T00:00:00-04:00" to "2016-03-23T00:00:00-04:00"
    And the user selects the site "TagArchive_TestSite1" and the position "Banner" and the tag "<tagname>"
    When the PSS user gets performance metrics for selected tag
    And performance metrics returned matches with database values
    And the_PSS_user_archives_the_selected_tag
    Then request passed successfully
    And returned "archive response" data matches the following json file "jsons/genevacrud/tag/pss/expected_results/archive/<filename>.json"

    Examples:
      | tagname                 | filename             |
      | ExchangeUnassigned      | ArchiveTagUnassigned |
      | ExchangeAssigned        | ArchiveTagAssigned   |
      | ExchangeNoPerfData      | ArchiveTagNoPerf     |
      | MediationNexageAdserver | MediationTagArchived |

  Scenario Outline: Trying to archive an already archived tag will fail
    Given the user "tagarchivemanager" has logged in with role "ManagerSeller"
    And the user specifies the date range from "2016-03-01T00:00:00-04:00" to "2016-03-23T00:00:00-04:00"
    When the user selects the site "TagArchive_TestSite1" and the position "Banner" and an archived tag "<tagid>"
    And the PSS user requests performance metrics for selected tag
    Then "Archived tag retrieval" failed with "404" response code

    Examples:
      | tagid |
      | 7377  |
      | 7378  |
      | 7379  |
      | 7380  |

  Scenario:Trying to archive a non existing tag will fail
    #GET http://geneva.sbx:8080/geneva/pss/105/site/963/position/14730/tag/7296
    Given the user "tagarchivemanager" has logged in with role "ManagerSeller"
    And the user specifies the date range from "2016-03-04T00:00:00-04:00" to "2016-03-11T00:00:00-04:00"
    When the user selects the site "TagArchive_TestSite1" and the position "Banner" and an archived tag "73777"
    And the PSS user requests performance metrics for selected tag
    Then "Archived tag retrieval" failed with "404" response code

  Scenario: tag summary will not contain the archived tag
    #GET http://geneva.sbx/geneva/publisher/105/tagsummary?start=2015-12-06T00%3A00%3A00-05%3A00&stop=2015-12-10T00%3A00%3A00-05%3A00&_=1457611004189
    Given the user "tagarchivemanager" has logged in with role "ManagerSeller"
    And the user specifies the date range as three days before today
    When the tag summaries are retrieved
    Then request passed successfully
    And response does not contain the given data
      | ExchangeUnassigned      |
      | ExchangeAssigned        |
      | ExchangeNoPerfData      |
      | MediationNexageAdserver |

  Scenario: get publisher tags will not contain the archived tag
    #GET http://geneva.sbx:8080/geneva/pss/105/site/963/position/14730/tag
    Given the user "tagarchivemanager" has logged in with role "ManagerSeller"
    And the user specifies the date range from "2016-03-04T00:00:00-04:00" to "2016-03-11T00:00:00-04:00"
    When the user selects the site "TagArchive_TestSite1" and the position "Banner"
    Then publisher tags are retrieved
    And request passed successfully
    And tag response doesn't contain the given tags
      | ExchangeUnassigned      |
      | ExchangeAssigned        |
      | ExchangeNoPerfData      |
      | MediationNexageAdserver |

  #tag 7377 is archived at the first case
  Scenario: get a publisher archived tag will fail
    #GET http://geneva.sbx:8080/geneva/pss/105/site/963/position/14730/tag/7296
    Given the user "tagarchivemanager" has logged in with role "ManagerSeller"
    And the user specifies the date range from "2016-03-04T00:00:00-04:00" to "2016-03-11T00:00:00-04:00"
    When the user selects the site "TagArchive_TestSite1" and the position "Banner" and an archived tag "7377"
    And publisher tag is retrieved
    Then "Archived tag retrieval" failed with "404" response code

  Scenario: get position will not contain archived tag
    #GET http://geneva.sbx:8080/geneva/pss/10213/site/10000184/position/100289
    Given the user "tagarchivemanager" has logged in with role "ManagerSeller"
    And the user selects the site "TagArchive_TestSite1"
    And position "Banner" is retrieved from database
    When the PSS user gets data for selected position
    Then response doesn't contain the given tags
      | ExchangeUnassigned      |
      | ExchangeAssigned        |
      | ExchangeNoPerfData      |
      | MediationNexageAdserver |

  Scenario: get detailed position will not contain archived tag
    #GET http://geneva.sbx:8080/geneva/pss/10213/site/10000184/position/100289/detailedPosition
    Given the user "tagarchivemanager" has logged in with role "ManagerSeller"
    And the user selects the site "TagArchive_TestSite1"
    And position "Banner" is retrieved from database
    When the PSS user gets detailed data for selected position
    Then response doesn't contain the given tags
      | ExchangeUnassigned      |
      | ExchangeAssigned        |
      | ExchangeNoPerfData      |
      | MediationNexageAdserver |

  Scenario: get all positions will not contain archived tag
    #GET http://geneva.sbx:8080/geneva/pss/10213/site/10000184/position
    Given the user "tagarchivemanager" has logged in with role "ManagerSeller"
    And the user selects the site "TagArchive_TestSite1"
    When the PSS user gets all positions
    Then response doesn't contain the given tags
      | ExchangeUnassigned |
      | ExchangeAssigned   |
      | ExchangeNoPerfData |

  Scenario: get pss site will not contain archived tag
    #GET http://geneva.sbx:8080/geneva/pss/10213/site/10000184
    Given the user "tagarchivemanager" has logged in with role "ManagerSeller"
    When the user selects the site "TagArchive_TestSite1"
    Then the site data is retrieved
    And response doesn't contain the given tags
      | ExchangeUnassigned      |
      | ExchangeAssigned        |
      | ExchangeNoPerfData      |
      | MediationNexageAdserver |

  Scenario: get pss all site will not contain archived tag
    #GET http://geneva.sbx:8080/geneva/pss/10213/site
    Given the user "tagarchivemanager" has logged in with role "ManagerSeller"
    When the user selects the site "TagArchive_TestSite1"
    Then the Seller sites for PSS user are retrieved
    And response doesn't contain the given tags
      | ExchangeUnassigned      |
      | ExchangeAssigned        |
      | ExchangeNoPerfData      |
      | MediationNexageAdserver |

  Scenario: get seller site data will not contain archived tag
    #GET http://geneva.sbx:8080/geneva/sellers/sites/10000184?#
    Given the user "tagarchivemanager" has logged in with role "ManagerSeller"
    When the user selects the site "TagArchive_TestSite1"
    Then the site data is retrieved
    And response doesn't contain the given tags
      | ExchangeUnassigned      |
      | ExchangeAssigned        |
      | ExchangeNoPerfData      |
      | MediationNexageAdserver |

  Scenario: get publisher complete metrics will not contain archived tags
    #GET http://geneva.sbx:8080/geneva/publisher/10213/dashboardsummary?start=2016-03-04T00:00:00-04:00&stop=2016-03-11T00:00:00-04:00
    Given the user "tagarchivemanager" has logged in with role "ManagerSeller"
    And the user specifies the date range as three days before today
    When the user retrieves publisher complete metrics
    And response does not contain the given data
      | ExchangeUnassigned      |
      | ExchangeAssigned        |
      | ExchangeNoPerfData      |
      | MediationNexageAdserver |

  #summary char changes frequently with changes to other tests
  @unstable
  Scenario: get publisher summary graph metrics will contain archived tag values
    #GET http://geneva.sbx:8080/geneva/publisher/10213/metrics/summarychart?start=2016-03-04T00:00:00-04:00&stop=2016-03-11T00:00:00-04:00&
    Given the user "tagarchivemanager" has logged in with role "ManagerSeller"
    And the user specifies the date range from "2016-03-04T00:00:00-04:00" to "2016-03-11T00:00:00-04:00"
    When the user retrieves publisher summary graph metrics
    Then returned "publisher summary graph metrics" data matches the following json file "jsons/genevacrud/tag/pss/expected_results/archive/SummarychartWithArchivedTag.json"

  Scenario Outline: get deployment info for tag in archived position will fail
    #GET http://geneva.sbx:8080/geneva/sellers/sites/10000184//tagdeployment/7377)
    Given the user "tagarchivemanager" has logged in with role "ManagerSeller"
    And the user selects the site "TagArchive_TestSite1"
    And tag "<tag name>" is retrieved from database
    When the PSS user tries to get deployment info for tag
    Then "tag deployment info search" failed with "404" response code

    Examples:
      | tag name                |
      | ExchangeUnassigned      |
      | ExchangeAssigned        |
      | ExchangeNoPerfData      |
      | MediationNexageAdserver |

  Scenario: get PSS tagsource tag will not contain archived tags
    #Get http://geneva.sbx:8080/geneva/pss/10213/adsource/7048/tag
    Given the user "tagarchivemanager" has logged in with role "ManagerSeller"
    When the user selects the adsource id "7048" tag
    Then returned " PSS tagsource tag" data matches the following json file "jsons/genevacrud/tag/pss/expected_results/archive/AdsourceTagWithArchived.json"

  Scenario: get PSS tagsource tag metric will not contain archived tags
    #Get http://geneva.sbx:8080/geneva/pss/10213/adsource/7048/tag/metric
    Given the user "tagarchivemanager" has logged in with role "ManagerSeller"
    When the user selects the adsource id "7048" tag metric
    Then returned " PSS tagsource tag" data matches the following json file "jsons/genevacrud/tag/pss/expected_results/archive/AdsourceTagWithArchived.json"
