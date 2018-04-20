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
import org.junit.experimental.categories.Category;
import org.junit.runners.MethodSorters;
import uk.ac.ebi.subs.categories.DevEnv;
import uk.ac.ebi.subs.data.objects.SubmissionStatus;
import uk.ac.ebi.subs.data.objects.SubmittableTemplate;
import uk.ac.ebi.subs.utils.TestJsonUtils;
import uk.ac.ebi.subs.utils.TestUtils;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

@Category({DevEnv.class})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DraftToSubmittedTests {

    private static PropertiesManager pm = PropertiesManager.getInstance();
    private static String samplesApiBaseUrl = pm.getSamplesApiBaseUrl();

    private static String token;
    private static String submissionUrl;

    private static String sampleUrl;

    @BeforeClass
    public static void setUp() throws Exception {
        token = TestUtils.getJWTToken(pm.getAuthenticationUrl(), pm.getAapUsername(), pm.getAapPassword());
        submissionUrl = TestUtils.createSubmission(token, pm.getSubmissionsApiTemplatedUrl(), pm.getSubmitterEmail(), pm.getTeamName());
        sampleUrl = TestUtils.createSample(token, samplesApiBaseUrl, new StringEntity(TestJsonUtils.getSampleJson(submissionUrl, TestUtils.getRandomAlias())));

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
    public void givenSubmissionExists_whenCreatingASample_then201IsReceived() throws IOException {

        HttpPost request = new HttpPost(samplesApiBaseUrl);
        request.setHeaders(TestUtils.getContentTypeAcceptAndTokenHeaders(token));

        StringEntity payload = new StringEntity(TestJsonUtils.getSampleJson(submissionUrl, TestUtils.getRandomAlias()));
        request.setEntity(payload);

        HttpResponse response = HttpClientBuilder.create().build().execute(request);

        assertThat(
                response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_CREATED)
        );
    }

    @Test
    public void givenSubmissionExists_whenCreatingAProject_then201IsReceived() throws IOException {

        HttpPost request = new HttpPost(pm.getProjectsApiBaseUrl());
        request.setHeaders(TestUtils.getContentTypeAcceptAndTokenHeaders(token));

        StringEntity payload = new StringEntity(
                TestJsonUtils.getProjectJson(
                        submissionUrl,
                        TestUtils.getRandomAlias(),
                        LocalDateTime.now().toString()
                )
        );
        request.setEntity(payload);

        HttpResponse response = HttpClientBuilder.create().build().execute(request);

        assertThat(
                response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_CREATED)
        );
    }

    @Test
    public void givenSubmissionIsOK_whenPatchingSubmissionStatusSubmitted_then200IsReceived() throws IOException, InterruptedException {

        TestUtils.waitForUpdateableSubmission(submissionUrl,token);

        HttpUriRequest getRequest = new HttpGet(submissionUrl + "/submissionStatus");
        getRequest.setHeaders(TestUtils.getContentTypeAcceptAndTokenHeaders(token));

        HttpResponse getResponse = HttpClientBuilder.create().build().execute(getRequest);

        SubmissionStatus submissionStatus = TestUtils.retrieveResourceFromResponse(getResponse, SubmissionStatus.class);

        HttpPatch patchRequest = new HttpPatch(submissionStatus.getStatusUpdateUrl());
        patchRequest.setHeaders(TestUtils.getContentTypeAcceptAndTokenHeaders(token));
        patchRequest.setEntity(new StringEntity("{\"status\" : \"Submitted\"}"));

        HttpResponse response = HttpClientBuilder.create().build().execute(patchRequest);

        assertThat(
                response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK)
        );
    }

    @Test
    public void givenSubmissionIsSubmitted_whenGettingSampleAccession_thenAccessionIsRetrieved() throws Exception {

        TestUtils.waitForCompletedSubmittable(sampleUrl,token);

        HttpUriRequest getRequest = new HttpGet(sampleUrl);
        getRequest.setHeaders(TestUtils.getContentTypeAcceptAndTokenHeaders(token));

        HttpResponse response = HttpClientBuilder.create().build().execute(getRequest);
        SubmittableTemplate template = TestUtils.retrieveResourceFromResponse(response, SubmittableTemplate.class);

        assertThat(
                template.getAccession(), notNullValue()
        );
    }

    @Test
    public void givenSubmissionIsSubmitted_whenGettingSubmissionStatus_thenItsCorrect() throws IOException {

        HttpUriRequest getRequest = new HttpGet(submissionUrl + "/submissionStatus");
        getRequest.setHeaders(TestUtils.getContentTypeAcceptAndTokenHeaders(token));

        HttpResponse response = HttpClientBuilder.create().build().execute(getRequest);
        SubmissionStatus submissionStatus = TestUtils.retrieveResourceFromResponse(response, SubmissionStatus.class);

        assertThat(
                submissionStatus.getStatus(), equalTo("Submitted")
        );
    }

    @AfterClass
    public static void tearDown() throws Exception {

        HttpDelete request = new HttpDelete(submissionUrl);
        request.setHeaders(TestUtils.getContentTypeAcceptAndTokenHeaders(token));
        HttpClientBuilder.create().build().execute(request);
    }
}
