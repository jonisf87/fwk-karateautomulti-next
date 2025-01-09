@demo
Feature: Test get token with login feature

  @jiraId
  Scenario Outline: getTokenWithLogin feature
    * def testData = read('<testDataFile>')
    * def req = testData.request
    * def expResponse = testData.expectedResponse

    Given url urls.demoApiUrl
    And path "auth/login"
    And request { username: '#(credentials.demoApiAuthUsername)', password: '#(credentials.demoApiAuthPassword)' }
    When method post
    Then status 200
    And def token = response.accessToken
    
    Given url urls.demoApiUrl
    And path "auth/me"
    And header Authorization = 'Bearer ' + token
    When method GET
    Then match responseStatus == expResponse.status
    And match response contains deep expResponse.body

    Examples:
      | testDataFile                                                |
      | classpath:tests/demoApi/getTokenWithLogin/testData/200.json |          
    