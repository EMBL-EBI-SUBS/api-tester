# API Tester

The main goal of this tool is to test the basic correctness of the API. It will run as a late step in a Continuous Integration process, consuming the REST API after it has already been deployed.

A series of black box tests will be run focusing on:
- the HTTP response code
- other HTTP headers in the response
- the payload (JSON)

Each test will only be focused on a **single responsability**.

Another important aspect of the integration tests is adherence to the *Single Level of Abstraction Principle* – the logic within a test should be written at a high level.

## Running the tests
The tests are run using Gradle you don't need to have Gradle installed as the project ships with it's own gradle wrapper. To run the API tests 4 properties are required, these are set by default to point at the current USI development deployment and can be overridden by defining an `application.properties` file in the project root.

You can find an example properties [file](application.properties.example) there that can serve as a template.
If no properties file ii provided the tests will still run against the current USI development API. The default property values are:
````
submitterEmail=api-tester@ebi.ac.uk
teamName=api-tester
submissionsApiBaseUrl=http://submission-dev.ebi.ac.uk/api/submissions/
samplesApiBaseUrl=http://submission-dev.ebi.ac.uk/api/samples/
````
:warning: When running the tests without the default configuration make sure to use a different `teamName` as the tests rely on the uniqueness of this value.

To run the tests do the following:
````bash
 $ cd api-tester/
 $ ./gradlew test
````



## License
This project is licensed under the Apache 2.0 License - see the [LICENSE.md](LICENSE.md) file for details.

### Acknowledgments
Thanks to [Eugen Paraschiv](https://twitter.com/baeldung) for the article on how to [Test a REST API with Java](http://www.baeldung.com/integration-testing-a-rest-api).
