Feature: Common login

  Scenario: login
    Given url urls.demoApiUrl
    And path "auth/login"
    And request { username: '#(credentials.demoApiAuthUsername)', password: '#(credentials.demoApiAuthPassword)' }
    When method post
    Then status 200
    And def token = response.accessToken