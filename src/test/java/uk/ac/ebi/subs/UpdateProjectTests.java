package uk.ac.ebi.subs;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.ac.ebi.subs.utils.TestJsonUtils;
import uk.ac.ebi.subs.utils.TestUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class UpdateProjectTests {

    private static PropertiesManager pm = PropertiesManager.getInstance();

    private static String projectsApiBaseUrl = pm.getProjectsApiBaseUrl();

    private static String token;
    private static String submissionUrl;
    private static String projectUrl;
    private static String projectValidationResultsUrl;

    private static String projectAlias = TestUtils.getRandomAlias();

    @BeforeClass
    public static void setUp() throws Exception {
        token = TestUtils.getJWTToken(pm.getAuthenticationUrl(), pm.getAapUsername(), pm.getAapPassword());
        submissionUrl = TestUtils.createSubmission(token, pm.getSubmissionsApiBaseUrl(), pm.getSubmitterEmail(), pm.getTeamName());
        projectUrl = TestUtils.createProject(token, projectsApiBaseUrl, submissionUrl, projectAlias);
        projectValidationResultsUrl = projectUrl + "/validationResult";

    }

    @Test
    public void givenProjectExists_whenUpdatingIt_then200IsReceived() throws IOException {

        HttpPut request = new HttpPut(projectUrl);
        request.setHeaders(TestUtils.getContentTypeAcceptAndTokenHeaders(token));

        StringEntity payload = new StringEntity(TestJsonUtils.getProjectJson(submissionUrl, projectAlias, "2017-04-17"));
        request.setEntity(payload);

        HttpResponse response = HttpClientBuilder.create().build().execute(request);

        System.out.println(TestUtils.extractResponseBody(response));

        assertThat(
                response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK)
        );
    }


    @AfterClass
    public static void tearDown() throws Exception {

        HttpDelete request = new HttpDelete(submissionUrl);
        request.setHeaders(TestUtils.getContentTypeAcceptAndTokenHeaders(token));
        HttpClientBuilder.create().build().execute(request);
    }
}
