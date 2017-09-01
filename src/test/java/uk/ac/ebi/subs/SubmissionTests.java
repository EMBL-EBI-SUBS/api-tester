package uk.ac.ebi.subs;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.ac.ebi.subs.data.objects.SubmissionStatus;
import uk.ac.ebi.subs.data.objects.Submission;
import uk.ac.ebi.subs.data.structures.ErrorWrapperObject;
import uk.ac.ebi.subs.utils.TestJsonUtils;
import uk.ac.ebi.subs.utils.TestUtils;

import java.io.IOException;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class SubmissionTests {

    static PropertiesManager propertiesManager = PropertiesManager.getInstance();

    static String submitterEmail = propertiesManager.getSubmitterEmail();
    static String teamName = propertiesManager.getTeamName();
    static String submissionsApiBaseUrl = propertiesManager.getSubmissionsApiBaseUrl();

    static String authUrl = propertiesManager.getAuthenticationUrl();
    static String aapUsername = propertiesManager.getAapUsername();
    static String aapPassword = propertiesManager.getAapPassword();

    static String token = "";
    static String submissionUrl = "";

    @BeforeClass
    public static void setUp() throws Exception {
        token = TestUtils.getJWTToken(authUrl, aapUsername, aapPassword);
        submissionUrl = TestUtils.createSubmission(token, submissionsApiBaseUrl, submitterEmail, teamName);
    }

    @Test
    public void givenSubmissionDoesNotExists_whenSubmissionStatusIsRetrieved_then404IsReceived() throws IOException {

        String randomSubmissionId = UUID.randomUUID().toString();
        HttpUriRequest request = new HttpGet(submissionsApiBaseUrl + randomSubmissionId);
        request.setHeaders(TestUtils.getContentTypeAcceptAndTokenHeaders(token));

        HttpResponse response = HttpClientBuilder.create().build().execute(request);

        assertThat(
                response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_NOT_FOUND)
        );
    }

    @Test
    public void whenSubmissionIsCreated_then201IsReceived() throws IOException {

        HttpPost request = new HttpPost(submissionsApiBaseUrl);
        request.setHeaders(TestUtils.getContentTypeAcceptAndTokenHeaders(token));

        StringEntity payload = new StringEntity(TestJsonUtils.getSubmissionJson(submitterEmail, teamName));
        request.setEntity(payload);

        HttpResponse response = HttpClientBuilder.create().build().execute(request);

        Submission resource = TestUtils.retrieveResourceFromResponse(response, Submission.class);

        assertThat(
                resource.get_links().getSelf().getHref(), startsWith("http://submission-dev.ebi.ac.uk/api/submissions/")
        );
    }

    @Test
    public void givenSubmissionExists_whenSubmissionIsRetrieved_then200IsReceived() throws IOException {

        HttpUriRequest request = new HttpGet(submissionUrl);
        request.setHeaders(TestUtils.getContentTypeAcceptAndTokenHeaders(token));

        HttpResponse response = HttpClientBuilder.create().build().execute(request);

        assertThat(
                response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK)
        );
    }

    @Test
    public void givenRequestWithNoAcceptHeader_whenRequestIsExecuted_thenDefaultResponseContentTypeIsHalJson() throws IOException {

        String halJsonMimeType = "application/hal+json";
        HttpUriRequest request = new HttpGet(submissionUrl);
        request.setHeaders(TestUtils.getContentTypeAcceptAndTokenHeaders(token));

        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        String mimeType = ContentType.getOrDefault(response.getEntity()).getMimeType();

        assertThat(
                mimeType, equalTo(halJsonMimeType)
        );
    }

    @Test
    public void givenSubmissionExists_whenSubmissionIsRetrieved_thenRetrievedResourceIsCorrect() throws IOException {

        HttpUriRequest request = new HttpGet(submissionUrl);
        request.setHeaders(TestUtils.getContentTypeAcceptAndTokenHeaders(token));

        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        Submission resource = TestUtils.retrieveResourceFromResponse(response, Submission.class);

        assertThat(
                resource.getSubmitter().getEmail(), equalTo(submitterEmail)
        );
    }

    @Test
    public void givenSubmissionExists_whenUpdatingSubmitterEmail_then400IsReceived() throws IOException {

        HttpPut request = new HttpPut(submissionUrl);
        request.setHeaders(TestUtils.getContentTypeAcceptAndTokenHeaders(token));

        StringEntity payload = new StringEntity(TestJsonUtils.getSubmissionJson("test@email.com", teamName));
        request.setEntity(payload);

        HttpResponse response = HttpClientBuilder.create().build().execute(request);

        assertThat(
                response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_BAD_REQUEST)
        );
    }

    @Test
    public void givenSubmissionExists_whenUpdatingSubmitterEmail_thenRetrievedResourceIsCorrect() throws IOException {

        HttpPut request = new HttpPut(submissionUrl);
        request.setHeaders(TestUtils.getContentTypeAcceptAndTokenHeaders(token));

        StringEntity payload = new StringEntity(TestJsonUtils.getSubmissionJson("test@email.com", teamName));
        request.setEntity(payload);

        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        ErrorWrapperObject resource = TestUtils.retrieveResourceFromResponse(response, ErrorWrapperObject.class);

        assertThat(
                resource.getFirstErrorMessage(), equalTo("resource_locked")
        );
    }

    @Test
    public void givenSubmissionExists_whenSubmissionStatusIsRetrieved_then200IsReceived() throws IOException {

        HttpUriRequest request = new HttpGet(submissionUrl + "/submissionStatus");
        request.setHeaders(TestUtils.getContentTypeAcceptAndTokenHeaders(token));

        HttpResponse response = HttpClientBuilder.create().build().execute(request);

        assertThat(
                response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK)
        );
    }

    @Test
    public void givenSubmissionExists_whenSubmissionStatusIsRetrieved_thenDefaultResponseContentTypeIsHalJson() throws IOException {

        String halJsonMimeType = "application/hal+json";
        HttpUriRequest request = new HttpGet(submissionUrl + "/submissionStatus");
        request.setHeaders(TestUtils.getContentTypeAcceptAndTokenHeaders(token));

        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        String mimeType = ContentType.getOrDefault(response.getEntity()).getMimeType();

        assertThat(
                mimeType, equalTo(halJsonMimeType)
        );
    }

    @Test
    public void givenSubmissionDraftExists_whenSubmissionStatusIsRetrieved_thenRetrievedResourceIsCorrect() throws IOException {

        HttpUriRequest request = new HttpGet(submissionUrl + "/submissionStatus");
        request.setHeaders(TestUtils.getContentTypeAcceptAndTokenHeaders(token));

        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        SubmissionStatus resource = TestUtils.retrieveResourceFromResponse(response, SubmissionStatus.class);

        assertThat(
                resource.getStatus(), equalTo("Draft")
        );
    }

    @AfterClass
    public static void tearDown() throws Exception {
        HttpDelete request = new HttpDelete(submissionUrl);
        request.setHeaders(TestUtils.getContentTypeAcceptAndTokenHeaders(token));
        HttpClientBuilder.create().build().execute(request);
    }
}
