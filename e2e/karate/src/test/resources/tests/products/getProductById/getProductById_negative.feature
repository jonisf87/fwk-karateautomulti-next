@negative @getProductById
Feature: Test GET product by id
    @id-02
    Scenario Outline: GET product by id negative
        * def testData = read('<testDataFile>')
        * def productId = testData.request.params.productId

        * def test = call read('classpath:tests/products/getProductById/getProductById.feature') {productId: '#(productId)'}

        Then match test.responseStatus == testData.expectedResponse.status
        And match test.response contains deep testData.expectedResponse.body

        Examples:
            | testDataFile                                                                          |
            | classpath:tests/products/getProductById/test-data/negative/404_params_id_boolean.json |
            | classpath:tests/products/getProductById/test-data/negative/404_params_id_string.json  |