package uk.ac.ebi.subs.core;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.hamcrest.core.AnyOf;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runners.MethodSorters;
import uk.ac.ebi.subs.PropertiesManager;
import uk.ac.ebi.subs.categories.DevEnv;
import uk.ac.ebi.subs.data.objects.SubmissionStatus;
import uk.ac.ebi.subs.data.objects.SubmittableTemplate;
import uk.ac.ebi.subs.utils.HttpUtils;
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
        sampleUrl = TestUtils.createSubmittable(token, samplesApiBaseUrl, TestJsonUtils.getSampleJson(submissionUrl, TestUtils.getRandomAlias()));

    }

    @Test
    public void givenSubmissionDraftExists_whenSubmissionStatusIsRetrieved_thenSubmissionStatusIsDraft() throws IOException {
        HttpResponse response = HttpUtils.httpGet(token, submissionUrl + "/submissionStatus");
        SubmissionStatus submissionStatus = HttpUtils.retrieveResourceFromResponse(response, SubmissionStatus.class);

        assertThat(
                submissionStatus.getStatus(), equalTo("Draft")
        );
    }

    @Test
    public void givenSubmissionExists_whenCreatingASample_then201IsReceived() throws IOException {
        String content =  TestJsonUtils.getSampleJson(submissionUrl, TestUtils.getRandomAlias());

        HttpResponse response = HttpUtils.httpPost(token, samplesApiBaseUrl,content);

        assertThat(
                response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_CREATED)
        );
    }

    @Test
    public void givenSubmissionExists_whenCreatingAProject_then201IsReceived() throws IOException {
        String content =  
                TestJsonUtils.getProjectJson(
                        submissionUrl,
                        TestUtils.getRandomAlias(),
                        LocalDateTime.now().toString()
        );

        HttpResponse response = HttpUtils.httpPost(token, pm.getProjectsApiBaseUrl(),content);

        assertThat(
                response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_CREATED)
        );
    }

    @Test
    public void givenSubmissionIsOK_whenPatchingSubmissionStatusSubmitted_then200IsReceived() throws IOException, InterruptedException {

        TestUtils.waitForUpdateableSubmission(token, submissionUrl);

        TestUtils.changeSubmissionStatusToSubmitted(token,submissionUrl);
    }

    @Test
    public void givenSubmissionIsSubmitted_whenGettingSampleAccession_thenAccessionIsRetrieved() throws Exception {

        TestUtils.waitForCompletedSubmittable(token, sampleUrl);

        HttpResponse response =  HttpUtils.httpGet(token, sampleUrl);
        SubmittableTemplate template = HttpUtils.retrieveResourceFromResponse(response, SubmittableTemplate.class);

        assertThat(
                template.getAccession(), notNullValue()
        );
    }

    @Test
    public void givenSubmissionIsSubmitted_whenGettingSubmissionStatus_thenItsCorrect() throws IOException {
        HttpResponse response = HttpUtils.httpGet(token, submissionUrl + "/submissionStatus");
        SubmissionStatus submissionStatus = HttpUtils.retrieveResourceFromResponse(response, SubmissionStatus.class);

        assertThat(
                submissionStatus.getStatus(), AnyOf.anyOf(equalTo("Submitted"),equalTo("Completed"))
        );
    }

    @AfterClass
    public static void tearDown() throws Exception {
        HttpUtils.deleteResource(token, submissionUrl);
    }
}
