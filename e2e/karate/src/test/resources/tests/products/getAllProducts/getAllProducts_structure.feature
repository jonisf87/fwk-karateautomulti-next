@structure
Feature: Test GET all products
    @id-00
    Scenario Outline: GET all products structure
        * def testData = read('<testDataFile>')

        * def test = call read('classpath:tests/products/getAllProducts/getAllProducts.feature')

        Then match test.responseStatus == testData.expectedResponse.status
        And match test.response contains deep testData.expectedResponse.body

        Examples:
            | testDataFile                                                         |
            | classpath:tests/products/getAllProducts/test-data/structure/200.json |