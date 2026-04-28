@judge-environment-unavailable @judge-demo
Feature: Judge demo - environment unavailable

  Scenario: Fail when the target environment is unreachable
    Given url 'http://karate-demo.invalid/products'
    When method get
    Then status 200
