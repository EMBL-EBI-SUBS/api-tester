# API Tester

The main goal of this tool is to test the basic correctness of the API. It will run as a late step in a Continuous Integration process, consuming the REST API after it has already been deployed.

A series of black box tests will be run focusing on:
- the HTTP response code
- other HTTP headers in the response
- the payload (JSON)

Each test will only be focused on a **single responsability**.

Another important aspect of the integration tests is adherence to the *Single Level of Abstraction Principle* – the logic within a test should be written at a high level.

## License
This project is licensed under the Apache 2.0 License - see the [LICENSE.md](LICENSE.md) file for details.


### Acknowledgments
Thanks to [Eugen Paraschiv](https://twitter.com/baeldung) for the article on how to [Test a REST API with Java](http://www.baeldung.com/integration-testing-a-rest-api).
