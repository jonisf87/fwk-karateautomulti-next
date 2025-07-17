
Feature: Obtener productos del microservicio local

  @local
  Scenario: Obtener todos los productos
    Given url dummyJSON_API_products
    When method get
    Then status 200
    And match response == 
    """
    [
      { id: 1, name: 'Product A', price: 10.0 },
      { id: 2, name: 'Product B', price: 20.0 }
    ]
    """
