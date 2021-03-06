package uk.ac.ebi.subs.core;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.hamcrest.CoreMatchers;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import uk.ac.ebi.subs.PropertiesManager;
import uk.ac.ebi.subs.categories.DevEnv;
import uk.ac.ebi.subs.categories.TestEnv;
import uk.ac.ebi.subs.data.objects.ApiError;
import uk.ac.ebi.subs.data.objects.Submission;
import uk.ac.ebi.subs.utils.HttpUtils;
import uk.ac.ebi.subs.utils.TestJsonUtils;
import uk.ac.ebi.subs.utils.TestUtils;

import java.io.IOException;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Category({TestEnv.class, DevEnv.class})
public class SubmissionTests {

    private static PropertiesManager pm = PropertiesManager.getInstance();

    private static String submitterEmail = pm.getSubmitterEmail();
    private static String teamName = pm.getTeamName();
    private static String submissionsApiBaseUrl = pm.getSubmissionsApiTemplatedUrl().replace("{teamName}", teamName);

    private static String token;
    private static String submissionUrl;

    @BeforeClass
    public static void setUp() throws Exception {
        token = TestUtils.getJWTToken(pm.getAuthenticationUrl(), pm.getAapUsername(), pm.getAapPassword());
        submissionUrl = TestUtils.createSubmission(token, submissionsApiBaseUrl, submitterEmail, teamName);
    }

    @Test
    public void givenSubmissionDoesNotExists_whenSubmissionStatusIsRetrieved_then404IsReceived() throws IOException {

        String randomSubmissionId = UUID.randomUUID().toString();
        HttpResponse response = HttpUtils.httpGet(token, submissionsApiBaseUrl + randomSubmissionId);

        assertThat(
                response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_NOT_FOUND)
        );
    }

    @Test
    public void whenSubmissionIsCreated_then201IsReceived() throws IOException {

        String content =  TestJsonUtils.getSubmissionJson(submitterEmail, teamName);

        HttpResponse response = HttpUtils.httpPost(token, submissionsApiBaseUrl, content);

        assertThat(
                response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_CREATED)
        );
    }

    @Test
    public void givenSubmissionExists_whenSubmissionIsRetrieved_then200IsReceived() throws IOException {

        HttpResponse response =  HttpUtils.httpGet(token, submissionUrl);

        assertThat(
                response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK)
        );
    }

    @Test
    public void givenRequestWithNoAcceptHeader_whenRequestIsExecuted_thenDefaultResponseContentTypeIsHalJson() throws IOException {

        String halJsonMimeType = "application/hal+json";
        HttpUriRequest request = new HttpGet(submissionUrl);
        request.setHeaders(HttpUtils.getContentTypeAndTokenHeaders(token));

        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        String mimeType = ContentType.getOrDefault(response.getEntity()).getMimeType();

        assertThat(
                mimeType, equalTo(halJsonMimeType)
        );
    }

    @Test
    public void givenSubmissionExists_whenSubmissionIsRetrieved_thenRetrievedResourceIsCorrect() throws IOException {
        HttpResponse response = HttpUtils.httpGet(token, submissionUrl);
        Submission resource = HttpUtils.retrieveResourceFromResponse(response, Submission.class);

        assertThat(
                resource.getSubmitter().getEmail(), equalTo(submitterEmail)
        );
    }

    @Test
    public void givenSubmissionExists_whenUpdatingSubmitterEmail_then400IsReceived() throws IOException {

        HttpPut request = new HttpPut(submissionUrl);
        request.setHeaders(HttpUtils.getContentTypeAcceptAndTokenHeaders(token));

        StringEntity payload =  new StringEntity(TestJsonUtils.getSubmissionJson("test@email.com", teamName));
        request.setEntity(payload);

        HttpResponse response = HttpClientBuilder.create().build().execute(request);

        assertThat(
                response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_BAD_REQUEST)
        );
    }

    @Test
    public void givenSubmissionExists_whenUpdatingSubmitterEmail_thenRetrievedResourceIsCorrect() throws IOException {

        HttpPut request = new HttpPut(submissionUrl);
        request.setHeaders(HttpUtils.getContentTypeAcceptAndTokenHeaders(token));

        StringEntity payload =  new StringEntity(TestJsonUtils.getSubmissionJson("test@email.com", teamName));
        request.setEntity(payload);

        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        ApiError apiError = HttpUtils.retrieveResourceFromResponse(response, ApiError.class);

        assertEquals(apiError.getStatus(), HttpStatus.SC_BAD_REQUEST);

        for (String errorMessage : apiError.getErrors()) {
            assertThat(errorMessage, CoreMatchers.startsWith("resource_locked"));
        }

        assertTrue(!apiError.getErrors().isEmpty());


    }

    @Test
    public void givenSubmissionExists_whenSubmissionStatusIsRetrieved_then200IsReceived() throws IOException {

        HttpResponse response = HttpUtils.httpGet(token, submissionUrl + "/submissionStatus");

        assertThat(
                response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK)
        );
    }

    @Test
    public void givenSubmissionExists_whenSubmissionStatusIsRetrieved_thenDefaultResponseContentTypeIsHalJson() throws IOException {

        String halJsonMimeType = "application/hal+json";
        HttpUriRequest request = new HttpGet(submissionUrl + "/submissionStatus");
        request.setHeaders(HttpUtils.getContentTypeAcceptAndTokenHeaders(token));

        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        String mimeType = ContentType.getOrDefault(response.getEntity()).getMimeType();

        assertThat(
                mimeType, equalTo(halJsonMimeType)
        );
    }

    @AfterClass
    public static void tearDown() throws Exception {
        HttpUtils.deleteResource(token, submissionUrl);
    }
}
