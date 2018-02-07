Feature: webservice integration tests

  Scenario: Getting all queries
    Given I initiate a mock database with a query named "plop"
    When I call "queries"
    Then I am answered "plop" with a 200 http code

  Scenario: Posting a wrong query
    When I post "query" with body "{}" and token "rw-XobZPTuH59" in header
    Then I am answered "No configuration found in the query" with a 500 http code

  Scenario: creating a query
    When I post "query" with body "{'name':'should see this name', 'description':'should see this description', 'template':'should see this template too', 'configurationId':0}" and token "rw-XobZPTuH59" in header
    Then I am answered "should see this name" with a 200 http code
    And I am answered "should see this description"
    And I am answered "should see this template too"

  Scenario: checking R/W permissions, not enough rights
    When I post "query" with body "{'name':'n', 'description':'d', 'template':'t', 'configurationId':0}" and token "ro-pjha00ippK" in header
    Then I am answered "Unauthorized" with a 500 http code

  Scenario: checking R/W permissions, no token
    When I post "query" with body "{'name':'n', 'description':'d', 'template':'t', 'configurationId':0}" and token "null" in header
    Then I am answered "Please log-in again" with a 500 http code

  Scenario: checking R/W permissions, wrong token
    When I post "query" with body "{'name':'n', 'description':'d', 'template':'t', 'configurationId':0}" and token "rw-xxxxxx" in header
    Then I am answered "Please log-in again" with a 500 http code
