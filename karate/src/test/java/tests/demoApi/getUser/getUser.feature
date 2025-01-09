@getUser @demoApi @regression
Feature: Test getUser endpoint
  @JiraId
  Scenario Outline: getUser
    * def testData = read('<testDataFile>')
    * def req = testData.request
    * def expResponse = testData.expectedResponse

    Given url urls.demoApiUrl
    And path "user"
    And param id = req.params.id
    When method GET
    Then match responseStatus == expResponse.status
    And match response contains deep expResponse.body

    Examples:
      | testDataFile                                                    |
      | classpath:tests/demoApi/getUser/testData/200.json |
      | classpath:tests/demoApi/getUser/testData/400_param_id.json |
