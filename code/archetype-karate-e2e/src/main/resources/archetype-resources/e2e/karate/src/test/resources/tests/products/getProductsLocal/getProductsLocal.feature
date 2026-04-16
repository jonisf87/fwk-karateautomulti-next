Feature: Obtener productos del microservicio local

  Background:
    * if (!urls) karate.fail('No se ha encontrado la variable "urls" en el contexto. Revisa la configuración de entorno y los ficheros YAML.')

  @local
  Scenario: Obtener todos los productos
    Given url urls.demoApplicationUrl
    When method get
    Then status 200
    And match response ==
    """
    [
      { id: 1, name: 'Product A', price: 10.0 },
      { id: 2, name: 'Product B', price: 20.0 }
    ]
    """
