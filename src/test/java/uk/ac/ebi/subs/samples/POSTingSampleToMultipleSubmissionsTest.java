package uk.ac.ebi.subs.samples;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runners.MethodSorters;
import uk.ac.ebi.subs.PropertiesManager;
import uk.ac.ebi.subs.categories.DevEnv;
import uk.ac.ebi.subs.categories.TestEnv;
import uk.ac.ebi.subs.data.objects.SubmissionStatus;
import uk.ac.ebi.subs.data.objects.SubmittableTemplate;
import uk.ac.ebi.subs.utils.HttpUtils;
import uk.ac.ebi.subs.utils.TestJsonUtils;
import uk.ac.ebi.subs.utils.TestUtils;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertTrue;
import static uk.ac.ebi.subs.utils.TestUtils.getRandomAlias;

@Category({TestEnv.class, DevEnv.class})
@FixMethodOrder(MethodSorters.NAME_ASCENDING) // The order of test execution matters here
public class POSTingSampleToMultipleSubmissionsTest {

    private static PropertiesManager pm = PropertiesManager.getInstance();
    private static String samplesApiBaseUrl = pm.getSamplesApiBaseUrl();

    private static String token;
    private static String[] submissionsUrls;
    private static String alias = getRandomAlias();
    private static String sampleUrl;

    @BeforeClass
    public static void setUp() throws Exception {
        token = TestUtils.getJWTToken(pm.getAuthenticationUrl(), pm.getAapUsername(), pm.getAapPassword());
        submissionsUrls = TestUtils.createNSubmissions(2, token, pm.getSubmissionsApiTemplatedUrl(), pm.getSubmitterEmail(), pm.getTeamName());
        sampleUrl = createSampleForSubmission(token, samplesApiBaseUrl, submissionsUrls[0], alias);
    }

    @Test
    public void A_givenSampleInSubmissionWithStatusDraft_whenAddingItToOtherSubmission_thenItShouldBeRejected() throws IOException {

        String content = TestJsonUtils.getCreateSampleJson(submissionsUrls[1], alias);


        HttpResponse response = HttpUtils.httpPost(token, samplesApiBaseUrl, content);
        assertThat(response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_BAD_REQUEST));

        String jsonFromResponse = EntityUtils.toString(response.getEntity());
        assertTrue(jsonFromResponse.contains("already_exists_and_not_completed"));
    }

    @Test
    public void B_givenSampleInSubmissionWithStatusCompleted_whenAddingItToOtherSubmission_thenItShouldBeAccepted() throws IOException, InterruptedException {

        TestUtils.waitForValidationResults(token, sampleUrl);

        // Submit submission 0
        HttpResponse statusResponse = HttpUtils.httpGet(token, submissionsUrls[0] + "/submissionStatus");

        SubmissionStatus submissionStatus = HttpUtils.retrieveResourceFromResponse(statusResponse, SubmissionStatus.class);
        ;
        HttpResponse response = HttpUtils.httpPatch(token, submissionStatus.getSelfUrl(), "{\"status\" : \"Submitted\"}");
        assertThat(response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK));

        TestUtils.waitForCompletedSubmittable(token, sampleUrl);

        // Add same sample to submission 1
        String sampleContent = TestJsonUtils.getCreateSampleJson(submissionsUrls[1], alias);


        HttpResponse sampleResponse = HttpUtils.httpPost(token, samplesApiBaseUrl, sampleContent);
        //System.out.println(EntityUtils.toString(sampleResponse.getEntity()));

        assertThat(sampleResponse.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_CREATED));
    }

    @AfterClass
    public static void tearDown() throws Exception {
        HttpDelete request1 = new HttpDelete(submissionsUrls[0]);
        request1.setHeaders(HttpUtils.getContentTypeAcceptAndTokenHeaders(token));
        HttpClientBuilder.create().build().execute(request1);

        HttpDelete request2 = new HttpDelete(submissionsUrls[1]);
        request2.setHeaders(HttpUtils.getContentTypeAcceptAndTokenHeaders(token));
        HttpClientBuilder.create().build().execute(request2);
    }

    private static String createSampleForSubmission(String token, String samplesApiBaseUrl, String submissionUrl, String alias) throws IOException {
        String content = TestJsonUtils.createSampleForSubmissionJson(submissionUrl, alias);
        return TestUtils.createSubmittable(token,samplesApiBaseUrl,content);
    }
}
