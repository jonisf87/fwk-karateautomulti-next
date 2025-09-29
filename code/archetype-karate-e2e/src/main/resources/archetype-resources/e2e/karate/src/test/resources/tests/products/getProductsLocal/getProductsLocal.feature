Feature: Obtener productos usando mock embebido

Background:
  * def mock = karate.start('classpath:mocks/products-mock.feature')
  * def waitForPort =
  """
  function(p){
    var Socket = Java.type('java.net.Socket');
    for (var i=0;i<20;i++){
      try { var s = new Socket('127.0.0.1', p); s.close(); return true; } catch(e) { java.lang.Thread.sleep(150); }
    }
    return false;
  }
  """
  * assert waitForPort(mock.port)
  * url mock.url
  * print 'Mock listo en', mock.url

Scenario: listar productos (mock)
  Given path 'products'
  When method get
  Then status 200
  And match response contains { id: 1, name: 'Product A' }
