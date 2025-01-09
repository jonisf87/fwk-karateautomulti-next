@getRecipes @demoApi @regression
Feature: Test getRecipes endpoint
  @JiraId
  Scenario Outline: getRecipes
    * def testData = read('<testDataFile>')
    * def req = testData.request
    * def expResponse = testData.expectedResponse

    Given url urls.demoApiUrl
    And path "recipes"
    And param limit = req.params.limit
    And param skip = req.params.skip
    And param select = req.params.select
    When method GET
    Then match responseStatus == expResponse.status
    And match response contains deep expResponse.body

    Examples:
      | testDataFile                                                    |
      | classpath:tests/demoApi/getRecipes/testData/200.json |
      | classpath:tests/demoApi/getRecipes/testData/200_param_limit.json |
      | classpath:tests/demoApi/getRecipes/testData/200_param_skip.json |
      | classpath:tests/demoApi/getRecipes/testData/200_param_select.json |
