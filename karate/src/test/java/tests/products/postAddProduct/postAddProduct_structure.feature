@structure @postAddProduct
Feature: Test POST add product
    @id-03
    Scenario Outline: POST add product structure
        * def testData = read('<testDataFile>')
        * def product = testData.request.body
       

        * def test = call read('classpath:tests/products/postAddProduct/postAddProduct.feature') {product: '#(product)'}

        Then match test.responseStatus == testData.expectedResponse.status
        And match test.response contains deep testData.expectedResponse.body

        Examples:
            | testDataFile                                                         |
            | classpath:tests/products/postAddProduct/test-data/structure/200.json |