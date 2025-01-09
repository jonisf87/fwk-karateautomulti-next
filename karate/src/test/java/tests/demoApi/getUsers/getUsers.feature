@getUsers @demoApi @regression
Feature: Test getUsers endpoint
  @jira-10
  Scenario Outline: getUsers
    * def testData = read('<testDataFile>')
    * def req = testData.request
    * def expResponse = testData.expectedResponse

    Given url urls.demoApiUrl
    And path "users"
    And param limit = req.params.limit
    And param skip = req.params.skip
    And param select = req.params.select
    When method GET
    Then match responseStatus == expResponse.status
    And match response contains deep expResponse.body

    Examples:
      | testDataFile                                                    |
      | classpath:tests/demoApi/getUsers/testData/200.json |
      | classpath:tests/demoApi/getUsers/testData/400_param_limit.json |
      | classpath:tests/demoApi/getUsers/testData/400_param_skip.json |
      | classpath:tests/demoApi/getUsers/testData/400_param_select.json |
