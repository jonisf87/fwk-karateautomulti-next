@getProducts_categories @demoApi @regression
Feature: Test getProducts_categories endpoint
  @demo-3
  Scenario Outline: getProducts_categories
    * def testData = read('<testDataFile>')
    * def req = testData.request
    * def expResponse = testData.expectedResponse

    Given url urls.demoApiUrl
    And path "products/categories"
    When method GET
    Then match responseStatus == expResponse.status
    And match response contains deep expResponse.body

    Examples:
      | testDataFile                                                    |
      | classpath:tests/demoApi/getProducts_categories/testData/200.json |
