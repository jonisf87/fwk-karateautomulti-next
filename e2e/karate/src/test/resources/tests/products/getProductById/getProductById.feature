Feature: getProductById

  Scenario: getProductById
    * if (typeof productId === 'undefined') productId = 1

    Given url urls.dummyJSON_API_products
    And path productId
    When method GET