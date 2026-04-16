Feature: postAddProduct

  Scenario: postAddProduct
    * if (typeof product === 'undefined') product = "{title: random title}"

    Given url urls.dummyJSON_API_products
    And path "/add"
    And request product
    When method POST