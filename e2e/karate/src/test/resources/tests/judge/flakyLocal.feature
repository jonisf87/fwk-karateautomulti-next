@judge-flaky @judge-demo
Feature: Judge demo - flaky local test

  Background:
    * def Files = Java.type('java.nio.file.Files')
    * def Path = Java.type('java.nio.file.Path')
    * def markerPath = Path.of('target', 'judge-flaky-marker.txt')
    * def seenBefore = Files.exists(markerPath)
    * if (!seenBefore) Files.writeString(markerPath, 'seen')

  Scenario: Fail once and pass on rerun to emulate flakiness
    Given url urls.demoApplicationUrl
    When method get
    Then status 200
    And match seenBefore == true
