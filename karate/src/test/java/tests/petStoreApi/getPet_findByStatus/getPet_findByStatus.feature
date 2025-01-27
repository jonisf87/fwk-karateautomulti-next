@getPet_findByStatus @petStoreApi @regression
Feature: Test getPet_findByStatus endpoint

  @id-2
  Scenario Outline: getPet_findByStatus
    * def testData = read('<testDataFile>')
    * def req = testData.request
    * def expResponse = testData.expectedResponse

    Given url urls.petStoreApiUrl
    And path "pet/findByStatus"
    And param status = req.params.status
    When method GET
    Then match responseStatus == expResponse.status
    And match response contains deep expResponse.body

    Examples:
      | testDataFile                                                                   |
      | classpath:tests/petStoreApi/getPet_findByStatus/testData/200.json              |
