package uk.ac.ebi.subs;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.ac.ebi.subs.data.SubmissionResource;
import uk.ac.ebi.subs.data.SubmissionStatusResource;
import uk.ac.ebi.subs.utils.TestUtils;

import java.io.IOException;
import java.util.UUID;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class SubmissionTests {

    static String submitterEmail = "api-tester@ebi.ac.uk";
    static String teamName = "api-tester";

    static String submissionsApiBaseUrl = "http://submission-dev.ebi.ac.uk/api/submissions/";
    static String submissionUrl = "";

    @BeforeClass
    public static void setUp() throws Exception {
        TestUtils.createSubmission(submissionsApiBaseUrl, submitterEmail, teamName);
        submissionUrl = TestUtils.getFirstSubmissionUrlForTeam(teamName);
    }

    @Test
    public void givenSubmissionDoesNotExists_whenSubmissionStatusIsRetrieved_then404IsReceived() throws IOException {

        String randomSubmissionId = UUID.randomUUID().toString();
        HttpUriRequest request = new HttpGet(submissionsApiBaseUrl + randomSubmissionId);

        HttpResponse response = HttpClientBuilder.create().build().execute(request);

        assertThat(
                response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_NOT_FOUND)
        );
    }

    @Test
    public void givenSubmissionExists_whenSubmissionIsRetrieved_then200IsReceived() throws IOException {

        HttpUriRequest request = new HttpGet(submissionUrl);

        HttpResponse response = HttpClientBuilder.create().build().execute(request);

        assertThat(
                response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK)
        );
    }

    @Test
    public void givenRequestWithNoAcceptHeader_whenRequestIsExecuted_thenDefaultResponseContentTypeIsHalJson() throws IOException {

        String halJsonMimeType = "application/hal+json";
        HttpUriRequest request = new HttpGet(submissionUrl);

        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        String mimeType = ContentType.getOrDefault(response.getEntity()).getMimeType();

        assertThat(
                mimeType, equalTo(halJsonMimeType)
        );
    }

    @Test
    public void givenSubmissionExists_whenSubmissionIsRetrieved_thenRetrievedResourceIsCorrect() throws IOException {

        HttpUriRequest request = new HttpGet(submissionUrl);

        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        SubmissionResource resource = TestUtils.retrieveResourceFromResponse(response, SubmissionResource.class);

        assertThat(
                resource.getSubmitter().getEmail(), equalTo(submitterEmail)
        );
    }

    @Test
    public void givenSubmissionExists_whenSubmissionStatusIsRetrieved_then200IsReceived() throws IOException {

        HttpUriRequest request = new HttpGet(submissionUrl + "/submissionStatus");

        HttpResponse response = HttpClientBuilder.create().build().execute(request);

        assertThat(
                response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK)
        );
    }

    @Test
    public void givenSubmissionExists_whenSubmissionStatusIsRetrieved_thenDefaultResponseContentTypeIsHalJson() throws IOException {

        String halJsonMimeType = "application/hal+json";
        HttpUriRequest request = new HttpGet(submissionUrl + "/submissionStatus");

        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        String mimeType = ContentType.getOrDefault(response.getEntity()).getMimeType();

        assertThat(
                mimeType, equalTo(halJsonMimeType)
        );
    }

    @Test
    public void givenSubmissionDraftExists_whenSubmissionStatusIsRetrieved_thenRetrievedResourceIsCorrect() throws IOException {

        HttpUriRequest request = new HttpGet(submissionUrl + "/submissionStatus");

        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        SubmissionStatusResource resource = TestUtils.retrieveResourceFromResponse(response, SubmissionStatusResource.class);

        assertThat(
                resource.getStatus(), equalTo("Draft")
        );
    }

    @AfterClass
    public static void tearDown() throws Exception {
        HttpDelete request = new HttpDelete(submissionUrl);
        HttpClientBuilder.create().build().execute(request);
    }
}
