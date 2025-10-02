Feature: Mock de productos

Background:
  * configure cors = true
  * print 'Cargando mock de productos'

# Escenario mock: responde a GET /products
Scenario: pathMatches('/products') && methodIs('get')
  * def response = [{ id: 1, name: 'Product A', price: 10.0 }, { id: 2, name: 'Product B', price: 20.0 }]
  * def responseStatus = 200
  * def responseHeaders = { 'Content-Type': 'application/json' }
