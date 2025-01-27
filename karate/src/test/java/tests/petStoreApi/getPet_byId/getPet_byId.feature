@getPet_byId @petStoreApi @regression
Feature: Test getPet_findByStatus endpoint

  @id-1
  Scenario Outline: getPet_byId
    * def testData = read('<testDataFile>')
    * def req = testData.request
    * def expResponse = testData.expectedResponse

    * def startTime = new Date().getTime()

    Given url urls.petStoreApiUrl
    And path "pet/" + req.params.id
    When method GET
    Then match responseStatus == expResponse.status
    And match response contains deep expResponse.body
    
    * if (new Date().getTime() - startTime > 3000) karate.fail('Tiempo de respuesta menor a 3 segundos')

    Examples:
      | testDataFile                                                                  |
      | classpath:tests/petStoreApi/getPet_byId/testData/200.json                     |
      | classpath:tests/petStoreApi/getPet_byId/testData/400_param_id_string.json     |
      | classpath:tests/petStoreApi/getPet_byId/testData/400_param_id_boolean.json    |
      | classpath:tests/petStoreApi/getPet_byId/testData/400_param_id_int_tooBig.json |
      | classpath:tests/petStoreApi/getPet_byId/testData/404_param_id_unknown.json    |
    
    