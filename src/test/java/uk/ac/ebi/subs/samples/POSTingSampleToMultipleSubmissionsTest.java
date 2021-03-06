package uk.ac.ebi.subs.samples;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import uk.ac.ebi.subs.PropertiesManager;
import uk.ac.ebi.subs.categories.DevEnv;
import uk.ac.ebi.subs.categories.TestEnv;
import uk.ac.ebi.subs.data.objects.SubmissionStatus;
import uk.ac.ebi.subs.utils.HttpUtils;
import uk.ac.ebi.subs.utils.TestJsonUtils;
import uk.ac.ebi.subs.utils.TestUtils;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static uk.ac.ebi.subs.utils.TestUtils.getRandomAlias;

@Category({TestEnv.class, DevEnv.class})
public class POSTingSampleToMultipleSubmissionsTest {

    private static PropertiesManager pm = PropertiesManager.getInstance();

    private static String token;
    private String[] submissionsUrls;
    private String alias;
    private String sampleUrl;


    @BeforeClass
    public static void setUp() throws Exception {
        token = TestUtils.getJWTToken(pm.getAuthenticationUrl(), pm.getAapUsername(), pm.getAapPassword());
    }

    @Before
    public void buildUp() throws Exception {
        submissionsUrls = TestUtils.createNSubmissions(2, token, pm.getSubmissionsApiTemplatedUrl(), pm.getSubmitterEmail(), pm.getTeamName());
        alias = getRandomAlias();
        sampleUrl = createSampleForSubmission(token, submissionsUrls[0], alias);
    }

    @Test
    public void givenSampleInSubmissionWithStatusDraft_whenAddingItToOtherSubmission_thenItShouldBeAccepted() throws IOException {

        String content = TestJsonUtils.getCreateSampleJson(alias);

        String sampleUrl = TestUtils.submittableCreationUrl("samples", submissionsUrls[1]);

        HttpResponse response = HttpUtils.httpPost(token, sampleUrl, content);
        assertThat(response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_CREATED));
    }

    @Test
    public void givenSampleInSubmissionWithStatusCompleted_whenAddingItToOtherSubmission_thenItShouldBeAccepted() throws IOException, InterruptedException {

        TestUtils.waitForValidationResults(token, sampleUrl);

        // Submit submission 0
        HttpResponse statusResponse = HttpUtils.httpGet(token, submissionsUrls[0] + "/submissionStatus");

        SubmissionStatus submissionStatus = HttpUtils.retrieveResourceFromResponse(statusResponse, SubmissionStatus.class);
        ;
        HttpResponse response = HttpUtils.httpPatch(token, submissionStatus.getSelfUrl(), "{\"status\" : \"Submitted\"}");
        assertThat(response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK));

        TestUtils.waitForCompletedSubmittable(token, sampleUrl);

        // Add same sample to submission 1
        String sampleContent = TestJsonUtils.getCreateSampleJson(alias);

        String sampleUrl = TestUtils.submittableCreationUrl("samples", submissionsUrls[1]);

        HttpResponse sampleResponse = HttpUtils.httpPost(token, sampleUrl, sampleContent);
        //System.out.println(EntityUtils.toString(sampleResponse.getEntity()));

        assertThat(sampleResponse.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_CREATED));
    }


    @After
    public void tearDown() throws Exception {
        HttpDelete request1 = new HttpDelete(submissionsUrls[0]);
        request1.setHeaders(HttpUtils.getContentTypeAcceptAndTokenHeaders(token));
        HttpClientBuilder.create().build().execute(request1);

        HttpDelete request2 = new HttpDelete(submissionsUrls[1]);
        request2.setHeaders(HttpUtils.getContentTypeAcceptAndTokenHeaders(token));
        HttpClientBuilder.create().build().execute(request2);
    }

    private static String createSampleForSubmission(String token, String submissionUrl, String alias) throws IOException {
        String content = TestJsonUtils.createSampleForSubmissionJson(alias);
        return TestUtils.createSubmittable(token, "samples", submissionUrl, content);
    }
}
