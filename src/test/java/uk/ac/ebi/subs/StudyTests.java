package uk.ac.ebi.subs;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.ac.ebi.subs.data.objects.ValidationResult;
import uk.ac.ebi.subs.data.structures.ValidationResultStatusAndLink;
import uk.ac.ebi.subs.utils.TestJsonUtils;
import uk.ac.ebi.subs.utils.TestUtils;

import java.io.IOException;

import static org.hamcrest.core.AnyOf.anyOf;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

public class StudyTests {

    private static PropertiesManager pm = PropertiesManager.getInstance();

    private static String studiesApiBaseUrl = pm.getStudiesApiBaseUrl();

    private static String token;
    private static String submissionUrl;
    private static String studyUrl;
    private static String studyValidationResultsUrl;

    private static String studyAlias = TestUtils.getRandomAlias();

    @BeforeClass
    public static void setUp() throws Exception {
        token = TestUtils.getJWTToken(pm.getAuthenticationUrl(), pm.getAapUsername(), pm.getAapPassword());
        submissionUrl = TestUtils.createSubmission(token, pm.getSubmissionsApiBaseUrl(), pm.getSubmitterEmail(), pm.getTeamName());
        studyUrl = TestUtils.createStudy(token, studiesApiBaseUrl, submissionUrl, studyAlias);
        studyValidationResultsUrl = studyUrl + "/validationResult";
    }

    @Test
    public void givenSubmissionExists_whenAddingStudyToIt_then201IsReceived() throws IOException {

        HttpPost request = new HttpPost(studiesApiBaseUrl);
        request.setHeaders(TestUtils.getContentTypeAcceptAndTokenHeaders(token));

        StringEntity payload = new StringEntity(TestJsonUtils.getStudyJson(submissionUrl, TestUtils.getRandomAlias(), "2017-04-17"));
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

        StringEntity payload = new StringEntity(TestJsonUtils.getStudyJson(submissionUrl, studyAlias, "2017-04-17"));
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
    public void givenStudyExists_whenGettingCoreValidationResult_thenValidationResultStatusIsPass() throws IOException, InterruptedException {

        Thread.sleep(2000);

        ValidationResultStatusAndLink validationResultStatusAndLink =
                TestUtils.getValidationResultStatusAndLinkFromStudy(studyValidationResultsUrl, token);

        ValidationResult validationResult =
                TestUtils.getValidationResultFromValidationResultStatus(
                        validationResultStatusAndLink.get_links().getSelf().getHref(), token);

        assertThat(
                validationResult.getValidationResultsFromCore()[0].getValidationStatus(), equalTo("Pass")
        );
    }

    @Test
    public void givenStudyExists_whenGettingEnaValidationResult_thenValidationResultIsAvailable() throws IOException, InterruptedException {

        Thread.sleep(2000);

        ValidationResultStatusAndLink validationResultStatusAndLink =
                TestUtils.getValidationResultStatusAndLinkFromStudy(studyValidationResultsUrl, token);

        ValidationResult validationResult =
                TestUtils.getValidationResultFromValidationResultStatus(
                        validationResultStatusAndLink.get_links().getSelf().getHref(), token);

        assertThat(
                validationResult.getValidationResultsFromEna()[0], notNullValue()
        );
    }

    @AfterClass
    public static void tearDown() throws Exception {

        HttpDelete request = new HttpDelete(submissionUrl);
        request.setHeaders(TestUtils.getContentTypeAcceptAndTokenHeaders(token));
        HttpClientBuilder.create().build().execute(request);
    }
}
