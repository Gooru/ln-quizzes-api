Quizzes API Functional Tests
=============
This document describe how to work with Quizzes API functional tests.

The functional tests are implemented using [Frisby](http://frisbyjs.com) which is a REST API testing framework built on [NodeJS](https://nodejs.org). Frisby requires both NodeJS and NPM to be installed on your system.

Functional tests are stored in the GIT folder `integration/spec/quizzes-api`. The first step is to go to the `integration` folder and install Frisby. This will create a folder for node_modules, this node_modules folder should NOT be stored in GIT.

To install Frisby run in your terminal `npm install --save-dev frisby`

Frisby is built on top of the Jasmine BDD framework, and uses the jasmine-node test runner to run spec tests.

To install jasmine-node run in your terminal `npm install -g jasmine-node`

All the test suites are implemented in the folder `spec/quizzes-api`. To include a new suite just add a new file in that folder. The suggested naming convention is to append a `_spec` to the filename, like `mytests_spec.js` and `moretests_spec.js`

For a more detailed documentation please refer to the Frisby web page http://frisbyjs.com

## Test against local environment
To run the tests locally you should:
* Go to the integration folder: `cd <quizzes-project-folder>/integration`
* Run a single test `jasmine-node spec/quizzes-api/<test-name.js>`
* Run all tests in parallel `jasmine-node spec/quizzes-api`
* Run all tests sequentially `./run-test.sh`

This command will run the tests against http://localhost:8080

## Test against a remote environment
To the run the tests against a remote environment or against a local environment in a different port than the default you should:
* Go to the integration folder: `cd <quizzes-project-folder>/integration`
* Then use this command 
`jasmine-node --config QUIZZES_SERVER_URL http://qa.api.quizzes.edify.cr spec/quizzes-api`

The parameter `--config QUIZZES_SERVER_URL` is used to define the host where the Quizzes API is living.

## Continuous Integration
The jasmine-node test runner has an option that generates test run reports in JUnit XML format. This format is compaitlble with most CI servers, including Hudson, Jenkins, Bamboo, Pulse, and others.

To run the tests in a CI process just use this command `jasmine-node --config QUIZZES_SERVER_URL http://qa.api.quizzes.edify.cr spec/quizzes-api --junitreport`