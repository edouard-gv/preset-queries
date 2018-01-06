Feature: webservice integration tests

  Scenario: Getting all queries
    When I call "queries"
    Then I am answered "name" with a 200 http code

  Scenario: Posting a wrong query
    When I post "query" with body "{}"
    Then I am answered "No query in the body" with a 500 http code
