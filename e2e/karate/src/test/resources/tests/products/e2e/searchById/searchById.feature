@e2e @add
Feature: Test add and search product
    @e2e-00
    Scenario Outline: add and search product
        * def testData = read('<testDataFile>')

        * def getAllProducts = call read('classpath:tests/products/getAllProducts/getAllProducts.feature')
        * match getAllProducts.responseStatus == 200

        * def productId = getAllProducts.response.products[0].id
        * def getProductById = call read('classpath:tests/products/getProductById/getProductById.feature') {productId: '#(productId)'}


        Then match getProductById.responseStatus == testData.expectedResponse.status
        And match getProductById.response.id == productId
        And match getProductById.response.title == getAllProducts.response.products[0].title

        Examples:
            | testDataFile                                                         |
            | classpath:tests/products/e2e/searchById/test-data/structure/200.json |