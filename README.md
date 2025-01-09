## Where to find documentation

https://karatelabs.github.io/karate/

## Intellij Setup

Next IDEA plugins are required to install:

- Cucumber for Java
- Gherkin
- Maven

## Reporting

- Karate-fw project supports two types of reports: cucumber and default karate. Both of them are generated in the `target` folder.
- In `KarateRunnerTest.java` after test execution, `runPostTestScript` function is executed which generates in target directory `combine-result.json`. This file includes all auto-generated .json files, so it can be used with Jira.

## How to run tests

Go to the folder where the pom.xml is located:
- Run all tests: `mvn clean test`
- Run tests by tag: `mvn clean test "-Dkarate.options=--tags @yourTag"`

## Environment

- Default environment is set to: 'staging'. This can be modified in karate-config.js file.
- Environment configurations are set in `config-{env}.yml`
  - Information in config-{env}.yml:
    - URLs for: publicApiV4, publicApiV5, externalApi, tasteWiseApi, privateApi, ingestionApi.
    - Credentials
- Running tests in specific environment: `mvn clean test "-Dkarate.env={env}"`

## Credentials

- Credentials are set in `config-{env}.yml` files. 
- Credentials can be modified in testData.json files if needed in certain tests.

## Demo API:
- https://petstore.swagger.io/#/
- https://dummyjson.com/docs

## Auto-generate tests
- In karate-fw you can find `TestFileGenerator.class`. You can run it in terminal: `java TestFileGenerator`
- How it works:
1. Introduce your JiraId, this will be used in order to tag your tests
2. Create your API option (if it is not created yet). In this option you will be able to configure environments for the api and also add new ones.
3. Choose authentication method for your tests
4. Choose between different methods like (GET, POST, PUT, ...)
5. Write the path to your endpoint. You should take into account that this is only the path of your test, not the complete URL. e.g.: demoURL: "https://dummyjson.com/". path for the test: "products"
6. You will be suggested a name for your test folder, if you don't like it, you can change it.
7. Select your test's parameters in case it is needed
8. Select if your test has a body
9. Your test should be ready in the directory: karate-fw/src/test/java/tests/yourApi/nameYouProvidedToTheFolder
 