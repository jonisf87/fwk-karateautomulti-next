@putPet @petStoreApi @regression
Feature: Test putPet endpoint

  @id-4
  Scenario Outline: putPet
    * def testData = read('<testDataFile>')
    * def req = testData.request
    * def expResponse = testData.expectedResponse

    Given url urls.petStoreApiUrl
    And path "pet"
    And request req.body
    When method PUT
    Then match responseStatus == expResponse.status
    And match response contains deep expResponse.body

    Examples:
      | testDataFile                                         |
      | classpath:tests/petStoreApi/putPet/testData/200.json |
