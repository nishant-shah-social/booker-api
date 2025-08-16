## Setup and running the tests

### With Docker

* Clone the repo. If already cloned checkout the main or desired branch.
* From the root folder, i.e. `bookerTest`, run the following command<br>
  ```docker build -f docker/Dockerfile -t booker-tests:latest .```
* This will generate a docker image `booker-tests` with tag latest
* Now run the docker container with following command<br> ```docker run --rm -p 8080:8080 booker-tests```
* It will execute the tests (all test classes will be executed parallely) and it will also publish the allure report on
  port 8080
* Open `http://localhost:8080` for visualizing the test report

### Without Docker on Local Machine

* Ensure java version >= 20 is installed
* Ensure maven is installed for dependancy management. On mac you can install it via `brew install maven`
* Ensure allure CLI is installed. On mac you can install it via `brew install allure`
* Open terminal and navigate to the root of the project, i.e. `bookerTest`
* Run `mvn clean test -DsuiteXmlFile=testng.xm`. It will first download dependancies and then execute the tests, and
  generate the Allure report artifacts in the /targets folder
* To view the allure report, run the command `allure serve target/allure-results`. this will open the report url in
  browser and report should be visible there.