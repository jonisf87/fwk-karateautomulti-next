@getAuthUser @demoApi @regression
Feature: Test get auth user

  Background:
    * def auth = call read('classpath:tests/demoApi/common/login.feature')

  @demo-1
  Scenario Outline: getAuthUser feature
    * def testData = read('<testDataFile>')
    * def req = testData.request
    * def expResponse = testData.expectedResponse

    Given url urls.demoApiUrl
    And path "auth/me"
    And header Authorization = 'Bearer ' + auth.response.accessToken
    When method GET
    Then match responseStatus == expResponse.status
    And match response contains deep expResponse.body

    Examples:
      | testDataFile                                          |
      | classpath:tests/demoApi/getAuthUser/testData/200.json |
      | classpath:tests/demoApi/getAuthUser/testData/200.json |

  @demo-2
  Scenario Outline: getAuthUser feature
    * def testData = read('<testDataFile>')
    * def req = testData.request
    * def expResponse = testData.expectedResponse

    Given url urls.demoApiUrl
    And path "auth/me"
    And header Authorization = 'Bearer ' + auth.response.accessToken
    When method GET
    Then match responseStatus == expResponse.status
    And match response contains deep expResponse.body

    Examples:
      | testDataFile                                          |
      | classpath:tests/demoApi/getAuthUser/testData/200.json |
      | classpath:tests/demoApi/getAuthUser/testData/200.json |
    
  @demo-3
  Scenario: getAuthUser feature
    * def testData = read('classpath:tests/demoApi/getAuthUser/testData/200.json')
    * def req = testData.request
    * def expResponse = testData.expectedResponse

    Given url urls.demoApiUrl
    And path "auth/me"
    And header Authorization = 'Bearer ' + auth.response.accessToken
    When method GET
    Then match responseStatus == expResponse.status
    And match response contains deep expResponse.body
