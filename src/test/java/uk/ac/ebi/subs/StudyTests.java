package uk.ac.ebi.subs;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.ac.ebi.subs.data.objects.ValidationResult;
import uk.ac.ebi.subs.utils.TestJsonUtils;
import uk.ac.ebi.subs.utils.TestUtils;

import java.io.IOException;

import static org.hamcrest.core.AnyOf.anyOf;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class StudyTests {

    private static PropertiesManager pm = PropertiesManager.getInstance();

    private static String submitterEmail = pm.getSubmitterEmail();
    private static String teamName = pm.getTeamName();
    private static String submissionsApiBaseUrl = pm.getSubmissionsApiBaseUrl();
    private static String studiesApiBaseUrl = pm.getStudiesApiBaseUrl();


    private static String authUrl = pm.getAuthenticationUrl();
    private static String aapUsername = pm.getAapUsername();
    private static String aapPassword = pm.getAapPassword();

    private static String token;
    private static String submissionUrl;
    private static String studyUrl;
    private static String studyValidationResultsUrl;

    private static String studyAlias = TestUtils.getRandomAlias();

    @BeforeClass
    public static void setUp() throws Exception {
        token = TestUtils.getJWTToken(authUrl, aapUsername, aapPassword);
        submissionUrl = TestUtils.createSubmission(token, submissionsApiBaseUrl, submitterEmail, teamName);
        studyUrl = TestUtils.createStudy(token, studiesApiBaseUrl, submissionUrl, studyAlias);
        studyValidationResultsUrl = studyUrl + "/validationResult";
    }

    @Test
    public void givenSubmissionExists_whenAddingStudyToIt_then201IsReceived() throws IOException {

        HttpPost request = new HttpPost(studiesApiBaseUrl);
        request.setHeaders(TestUtils.getContentTypeAcceptAndTokenHeaders(token));

        StringEntity payload = new StringEntity(TestJsonUtils.getStudyJson(submissionUrl, TestUtils.getRandomAlias(), "2017-04-17T11:03:08.114+0000"));
        request.setEntity(payload);

        HttpResponse response = HttpClientBuilder.create().build().execute(request);

        assertThat(
                response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_CREATED)
        );
    }

    @Test
    public void givenStudyExists_whenUpdatingIt_then200IsReceived() throws IOException {

        HttpPut request = new HttpPut(studyUrl);
        request.setHeaders(TestUtils.getContentTypeAcceptAndTokenHeaders(token));

        StringEntity payload = new StringEntity(TestJsonUtils.getStudyJson(submissionUrl, studyAlias, "2017-04-17T11:03:08.114+0000"));
        request.setEntity(payload);

        HttpResponse response = HttpClientBuilder.create().build().execute(request);

        assertThat(
                response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK)
        );
    }

    @Test
    public void givenStudyExists_whenGettingValidationResults_then200IsReceived() throws IOException {

        HttpUriRequest request = new HttpGet(studyValidationResultsUrl);
        request.setHeaders(TestUtils.getContentTypeAcceptAndTokenHeaders(token));

        HttpResponse response = HttpClientBuilder.create().build().execute(request);

        assertThat(
                response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK)
        );
    }

    @Test
    public void givenStudyExists_whenGettingValidationResults_thenStatusIsCompleteOrPending() throws IOException {

        String validationStatus = TestUtils.getValidationStatus(studyValidationResultsUrl, token);

        assertThat(
                validationStatus, anyOf(equalTo("Pending"), equalTo("Complete"))
        );
    }

    @Test
    public void givenStudyExists_whenGettingCoreValidationResult_thenValidationResultIsAvailable() throws IOException, InterruptedException {

        HttpUriRequest request = new HttpGet(studyValidationResultsUrl);
        request.setHeaders(TestUtils.getContentTypeAcceptAndTokenHeaders(token));

        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        ValidationResult resource = TestUtils.retrieveResourceFromResponse(response, ValidationResult.class);

        Thread.sleep(2000);

        assertThat(
                resource.getValidationResultsFromCore()[0].getValidationStatus(), equalTo("Pass")
        );
    }

    @Test
    public void givenStudyExists_whenGettingEnaValidationResult_thenValidationResultIsAvailable() throws IOException, InterruptedException {

        HttpUriRequest request = new HttpGet(studyValidationResultsUrl);
        request.setHeaders(TestUtils.getContentTypeAcceptAndTokenHeaders(token));

        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        ValidationResult resource = TestUtils.retrieveResourceFromResponse(response, ValidationResult.class);

        Thread.sleep(2000);

        assertThat(
                resource.getValidationResultsFromEna()[0].getValidationStatus(), equalTo("Pass")
        );
    }

    @AfterClass
    public static void tearDown() throws Exception {
        HttpDelete request = new HttpDelete(submissionUrl);
        request.setHeaders(TestUtils.getContentTypeAcceptAndTokenHeaders(token));
        HttpClientBuilder.create().build().execute(request);
    }
}