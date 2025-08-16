## Purpose

Purpose is to document the strategy for automating the tests for booker API, so that we can catch issues in the booker
API fetures faster and sooner -- and can enable us to release new updates/fixes in the API faster.

## Scope

Automate following scenarios of the API

* Positive and negative scenarios for following APIs
    * GET /booking
    * PATCH /booking/:id
    * DELETE /booking/:id
* E2e flow served by the booker API
* Basic Performance and Security checks around the above API's

Providing complementary toolings and facility to be able
to run these tests seamlessly, e.g. dockerizing, CI/CD integration

## Out-Of-Scope

* Performance tests by simulating production like traffic/load
* No UI checks covered
* Direct Database level checks
* Advanced security checks

## Tools & Technologies To Be Used

* Java :- The programming lanuage and technology to be used
* RestAssured Framework:- Well-known API Test framework for readable and maintainable API interactions in DSL format
* TestNG:- Framework for easy organization, maintenance, execution and reporting of tests
* Allure:- For comprehensive reporting of tests
* Docker:- For seamless execution of tests
* IntelliJ:- IDE for coding

## Test Approach

Automated API testing will be implemented using Rest Assured in combination with Java and TestNG.
Tests will be organized into modular, maintainable components following Page Object-like design for APIs (a.k.a. Service
Object Pattern).

### Salient Features Of The Approach

* BaseTest to separate out common logic to be used in all test files.<br> Mainly comprises of setting up of RestClient
  and Request/Response specifications
* Utils to cover all the utilities that facilitate clean tests
    * Includes a RestClient which is a wrapper to handle different API requests
* Separation of concerns, e.g. TestData to be decoupled from the tests via external json files.<br>DataFactory to create dynamic test data required in the tests
* POJO:- For modeling API request body and deserializing the JSON response to Java objects.<br> As a result we dont have
  to manually construct JSONS and code becomes readable and maintainable.<br> Also, implicitly performs schema validation as well of the API's
* Separating out tests of each domain in a separate directory, e.g. booking.<br>Separating out Tests for each endpoint into a separate test class, so that its easy to manage the tests and investigate issues.
* Reporting to be visual and actionable. Allure reporting to be used for the same

### Execution Approach
Tests to be maintained in Git. It should be integrated with jenkins. To be executed against each PR of the bookerAPI. If its green then only allow to merge PR



