# API Tester
[![Build Status](https://travis-ci.org/EMBL-EBI-SUBS/api-tester.svg?branch=master)](https://travis-ci.org/EMBL-EBI-SUBS/api-tester)

The main goal of this tool is to test the basic correctness of the API. It will run as a late step in a Continuous Integration process, consuming the REST API after it has already been deployed.

A series of black box tests will be run focusing on:
- the HTTP response code
- other HTTP headers in the response
- the payload (JSON)

Each test will only be focused on a **single responsability**.
Another important aspect of the integration tests is adherence to the *Single Level of Abstraction Principle* – the logic within a test should be written at a high level.

## Running the tests
The Api Tester consists of a series of _black box_ tests that are run using Gradle.
You don't need to have Gradle installed as the project ships with it's own gradle wrapper.

### Authentication
The USI API relies on the [AAP Service](https://api.aap.tsi.ebi.ac.uk/docs/index.html) to secure it.
The AAP service (Authentication, Authorisation and Profile) provides a central repository for identities (Authentication), group management/permissions via domains (Authorisation) and attributes (Profile).

### Properties
To run the API tests the AAP authentication credentials are required. These may be provided either as properties in a properties file or as command line parameters.
The properties file takes precedence over the command line parameters.

All the other properties will default to point at the current USI development deployment unless overridden by defining an `application.properties` file in the project parent directory.
You can find an example properties file [here](application.properties.example).

It's possible to provide the AAP credentials through the command line and the remaining properties in the properties file or all in the properties file.
The AAP authentication credentials are the only required properties, without which the tests will NOT be able to run. 

Required properties:
````properties
aapUsername=username
aapPassword=password
````
Optional properties and their default values:
````properties
submitterEmail=api-tester@ebi.ac.uk
teamName=team-alpha

apiRoot=http://submission-dev.ebi.ac.uk/api/
submissionsApiBaseUrl=http://submission-dev.ebi.ac.uk/api/submissions/
samplesApiBaseUrl=http://submission-dev.ebi.ac.uk/api/samples/
samplesInSubmissionByIdUrl=http://submission-dev.ebi.ac.uk/api/samples/search/by-submission?submissionId=
studiesApiBaseUrl=http://submission-dev.ebi.ac.uk/api/studies/

authenticationUrl=https://explore.api.aap.tsi.ebi.ac.uk/auth
````

### Run the tests
As explained above, there are two ways of running the tests depending on the way you pass the AAP credentials.
1. Passing the credentials in the `application.properties` file:
    ````bash
     $ cd api-tester/
     $ ./gradlew test
    ````
2. Passing the credentials in the command line:
    ````bash
     $ cd api-tester/
     $ ./gradlew test -DaapUsername=username -DaaPassword=password
    ````

## License
This project is licensed under the Apache 2.0 License - see the [LICENSE.md](LICENSE.md) file for details.

## Acknowledgments
Thanks to [Eugen Paraschiv](https://twitter.com/baeldung) for the article on how to [Test a REST API with Java](http://www.baeldung.com/integration-testing-a-rest-api).

For details about how to use HttpClient Basic Authentication check [this article](http://www.baeldung.com/httpclient-4-basic-authentication) from the same author.
