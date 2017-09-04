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
To run the API tests a couple of properties are required, the others will default to point at the current USI development deployment and can be overridden by defining an `application.properties` file in the project parent directory.
You can find an example properties file [here](application.properties.example).

The AAP authentication credentials are the only required properties, without which the tests will NOT be able to run. 
All the other properties, when not provided in the properties file, will default to the values in the [PropertiesManager](/src/main/java/uk/ac/ebi/subs/PropertiesManager.java).

Required properties:
````properties
aapUsername=username
aapPassword=password
````

### Run the tests
To run the tests, after having cloned this project, do the following:
````bash
 $ cd api-tester/
 $ ./gradlew test
````

## License
This project is licensed under the Apache 2.0 License - see the [LICENSE.md](LICENSE.md) file for details.

## Acknowledgments
Thanks to [Eugen Paraschiv](https://twitter.com/baeldung) for the article on how to [Test a REST API with Java](http://www.baeldung.com/integration-testing-a-rest-api).

For details about how to use HttpClient Basic Authentication check [this article](http://www.baeldung.com/httpclient-4-basic-authentication) from the same author.
