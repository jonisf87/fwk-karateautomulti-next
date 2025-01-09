@postProducts_add @demoApi @regression
Feature: Test postProducts_add endpoint
  @JiraId
  Scenario Outline: postProducts_add
    * def testData = read('<testDataFile>')
    * def req = testData.request
    * def expResponse = testData.expectedResponse

    Given url urls.demoApiUrl
    And path "products/add"
    And request req.body
    When method POST
    Then match responseStatus == expResponse.status
    And match response contains deep expResponse.body

    Examples:
      | testDataFile                                                    |
      | classpath:tests/demoApi/postProducts_add/testData/200.json |
