@getProducts @demoApi @regression
Feature: Test getProducts endpoint

  @demo-2
  Scenario Outline: getProducts
    * def testData = read('<testDataFile>')
    * def req = testData.request
    * def expResponse = testData.expectedResponse

    Given url urls.demoApiUrl
    And path "products"
    When method GET
    Then match responseStatus == expResponse.status
    And match response contains deep expResponse.body

    Examples:
      | testDataFile                                          |
      | classpath:tests/demoApi/getProducts/testData/200.json |