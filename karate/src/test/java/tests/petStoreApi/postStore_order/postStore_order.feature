@postStore_order @petStoreApi @regression
Feature: Test postStore_order endpoint

  @id-3
  Scenario Outline: postStore_order
    * def testData = read('<testDataFile>')
    * def req = testData.request
    * def expResponse = testData.expectedResponse

    Given url urls.petStoreApiUrl
    And path "store/order"
    And request req.body
    When method POST
    Then match responseStatus == expResponse.status
    And match response contains deep expResponse.body

    Examples:
      | testDataFile                                                  |
      | classpath:tests/petStoreApi/postStore_order/testData/200.json |
