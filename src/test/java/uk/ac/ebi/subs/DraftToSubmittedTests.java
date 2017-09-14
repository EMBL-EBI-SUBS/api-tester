package uk.ac.ebi.subs;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import uk.ac.ebi.subs.data.objects.SubmissionStatus;
import uk.ac.ebi.subs.utils.TestJsonUtils;
import uk.ac.ebi.subs.utils.TestUtils;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DraftToSubmittedTests {

    private static PropertiesManager pm = PropertiesManager.getInstance();

    private static String token;
    private static String submissionUrl;

    @BeforeClass
    public static void setUp() throws Exception {
        token = TestUtils.getJWTToken(pm.getAuthenticationUrl(), pm.getAapUsername(), pm.getAapPassword());
        submissionUrl = TestUtils.createSubmission(token, pm.getSubmissionsApiBaseUrl(), pm.getSubmitterEmail(), pm.getTeamName());
    }

    @Test
    public void givenSubmissionExists_whenCreatingASample_then201IsReceived() throws IOException {

        HttpPost request = new HttpPost(pm.getSamplesApiBaseUrl());
        request.setHeaders(TestUtils.getContentTypeAcceptAndTokenHeaders(token));

        StringEntity payload = new StringEntity(TestJsonUtils.getSampleJson(submissionUrl, TestUtils.getRandomAlias()));
        request.setEntity(payload);

        assertThat(
                HttpClientBuilder.create().build().execute(request).getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_CREATED)
        );
    }

    @Test
    public void givenSubmissionExists_whenCreatingAStudy_then201IsReceived() throws IOException {

        HttpPost request = new HttpPost(pm.getStudiesApiBaseUrl());
        request.setHeaders(TestUtils.getContentTypeAcceptAndTokenHeaders(token));

        StringEntity payload = new StringEntity(TestJsonUtils.getStudyJson(submissionUrl, TestUtils.getRandomAlias(), LocalDateTime.now().toString()));
        request.setEntity(payload);

        assertThat(
                HttpClientBuilder.create().build().execute(request).getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_CREATED)
        );
    }

    @Test
    public void givenSubmissionDraftExists_whenSubmissionStatusIsRetrieved_thenSubmissionStatusIsDraft() throws IOException {

        HttpUriRequest request = new HttpGet(submissionUrl + "/submissionStatus");
        request.setHeaders(TestUtils.getContentTypeAcceptAndTokenHeaders(token));

        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        SubmissionStatus submissionStatus = TestUtils.retrieveResourceFromResponse(response, SubmissionStatus.class);

        assertThat(
                submissionStatus.getStatus(), equalTo("Draft")
        );
    }

    @Test
    public void givenSubmissionIsOK_whenPatchingSubmissionStatusSubmitted_then200IsReceived() throws IOException, InterruptedException {

        Thread.sleep(2000); // Make sure validation results are all back

        HttpUriRequest getRequest = new HttpGet(submissionUrl + "/submissionStatus");
        getRequest.setHeaders(TestUtils.getContentTypeAcceptAndTokenHeaders(token));

        SubmissionStatus submissionStatus = TestUtils.retrieveResourceFromResponse(HttpClientBuilder.create().build().execute(getRequest), SubmissionStatus.class);

        HttpPatch patchRequest = new HttpPatch(submissionStatus.getStatusUpdateUrl());
        patchRequest.setHeaders(TestUtils.getContentTypeAcceptAndTokenHeaders(token));
        patchRequest.setEntity(new StringEntity("{\"status\" : \"Submitted\"}"));

        HttpResponse response = HttpClientBuilder.create().build().execute(patchRequest);

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
