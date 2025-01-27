@getRecipes @demoApi @regression
Feature: Test getRecipes endpoint

  @demo-4
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
      | testDataFile                                                                |
      | classpath:tests/demoApi/getRecipes/testData/200.json                        |
      # param - limit
      | classpath:tests/demoApi/getRecipes/testData/400_param_limit_boolean.json    |
      | classpath:tests/demoApi/getRecipes/testData/400_param_limit_string.json     |
      # param - select
      | classpath:tests/demoApi/getRecipes/testData/400_param_select_boolean.json   |
      | classpath:tests/demoApi/getRecipes/testData/400_param_select_int.json       |
      # param - skip
      | classpath:tests/demoApi/getRecipes/testData/400_param_skip_boolean.json     |
      | classpath:tests/demoApi/getRecipes/testData/400_param_skip_string.json      |

