Feature: webservice integration tests

  Scenario: Getting all queries
    Given I initiate a mock database with a query named "plop"
    When I call "queries"
    Then I am answered "plop" with a 200 http code

  Scenario: Posting a wrong query
    When I post "query" with body "{}"
    Then I am answered "No query in the body" with a 500 http code
