# API Tester

The main goal of this tool is to test the basic correctness of the API. It will run as a late step in a Continuous Integration process, consuming the REST API after it has already been deployed.

A series of black box tests will be run focusing on:
- the HTTP response code
- other HTTP headers in the response
- the payload (JSON)

Each test will only be focused on a **single responsability**.

Another important aspect of the integration tests is adherence to the *Single Level of Abstraction Principle* – the logic within a test should be written at a high level.

## Running the tests
The tests are run using Gradle you don't need to have Gradle installed as the project ships with it's own gradle wrapper. To run the API tests a set of properties are required, these are set by default to point at the current USI development deployment and can be overridden by defining an `application.properties` file in the project root.

You can find an example properties file [here](application.properties.example) that can serve as a template.
If no properties file is provided the tests will NOT run. 
The AAP authentication credentials are the only required properties, without which the tests will NOT be able to run. 
All the other properties, when not provided in the properties file, will default to the values in the [PropertiesManager](/src/main/java/uk/ac/ebi/subs/PropertiesManager.java).

Required properties:
````
aapUsername=username
aapPassword=password
````

To run the tests do the following:
````bash
 $ cd api-tester/
 $ ./gradlew test
````

## License
This project is licensed under the Apache 2.0 License - see the [LICENSE.md](LICENSE.md) file for details.

### Acknowledgments
Thanks to [Eugen Paraschiv](https://twitter.com/baeldung) for the article on how to [Test a REST API with Java](http://www.baeldung.com/integration-testing-a-rest-api).

For details about how to use HttpClient Basic Authentication check [this article](http://www.baeldung.com/httpclient-4-basic-authentication) from the same author.
