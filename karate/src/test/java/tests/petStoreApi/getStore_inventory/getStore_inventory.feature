@getStore_inventory @petStoreApi @regression
Feature: Test getStore_inventory endpoint

  @id-3
  Scenario Outline: getStore_inventory
    * def testData = read('<testDataFile>')
    * def req = testData.request
    * def expResponse = testData.expectedResponse

    Given url urls.petStoreApiUrl
    And path "store/inventory"
    When method GET
    Then match responseStatus == expResponse.status
    And match response contains deep expResponse.body

    Examples:
      | testDataFile                                                     |
      | classpath:tests/petStoreApi/getStore_inventory/testData/200.json |
