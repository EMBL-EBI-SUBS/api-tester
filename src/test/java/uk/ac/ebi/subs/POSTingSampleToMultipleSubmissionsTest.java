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
import org.apache.http.util.EntityUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runners.MethodSorters;
import uk.ac.ebi.subs.categories.DevEnv;
import uk.ac.ebi.subs.categories.TestEnv;
import uk.ac.ebi.subs.data.objects.SubmissionStatus;
import uk.ac.ebi.subs.data.objects.SubmittableTemplate;
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

    @BeforeClass
    public static void setUp() throws Exception {
        token = TestUtils.getJWTToken(pm.getAuthenticationUrl(), pm.getAapUsername(), pm.getAapPassword());
        submissionsUrls = TestUtils.createNSubmissions(2, token, pm.getSubmissionsApiTemplatedUrl(), pm.getSubmitterEmail(), pm.getTeamName());
        createSampleForSubmission(token, samplesApiBaseUrl, submissionsUrls[0], alias);
    }

    @Test
    public void A_givenSampleInSubmissionWithStatusDraft_whenAddingItToOtherSubmission_thenItShouldBeRejected() throws IOException {
        HttpPost request = new HttpPost(samplesApiBaseUrl);
        request.setHeaders(TestUtils.getContentTypeAcceptAndTokenHeaders(token));

        StringEntity payload = new StringEntity(TestJsonUtils.getCreateSampleJson(submissionsUrls[1], alias));
        request.setEntity(payload);

        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        assertThat(response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_BAD_REQUEST));

        String jsonFromResponse = EntityUtils.toString(response.getEntity());
        assertTrue(jsonFromResponse.contains("already_exists_and_not_completed"));
    }

    @Test
    public void B_givenSampleInSubmissionWithStatusCompleted_whenAddingItToOtherSubmission_thenItShouldBeAccepted() throws IOException, InterruptedException {

        Thread.sleep(2000); // Make sure validation results are all back

        // Submit submission 0
        HttpUriRequest getRequest = new HttpGet(submissionsUrls[0] + "/submissionStatus");
        getRequest.setHeaders(TestUtils.getContentTypeAcceptAndTokenHeaders(token));

        SubmissionStatus submissionStatus = TestUtils.retrieveResourceFromResponse(HttpClientBuilder.create().build().execute(getRequest), SubmissionStatus.class);

        HttpPatch patchRequest = new HttpPatch(submissionStatus.getSelfUrl());
        patchRequest.setHeaders(TestUtils.getContentTypeAcceptAndTokenHeaders(token));
        patchRequest.setEntity(new StringEntity("{\"status\" : \"Submitted\"}"));

        HttpResponse response = HttpClientBuilder.create().build().execute(patchRequest);
        assertThat(response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK));

        Thread.sleep(10000); // Make sure the submission gets completed

        // Add same sample to submission 1
        HttpPost request = new HttpPost(samplesApiBaseUrl);
        request.setHeaders(TestUtils.getContentTypeAcceptAndTokenHeaders(token));

        StringEntity payload = new StringEntity(TestJsonUtils.getCreateSampleJson(submissionsUrls[1], alias));
        request.setEntity(payload);

        HttpResponse sampleResponse = HttpClientBuilder.create().build().execute(request);
        //System.out.println(EntityUtils.toString(sampleResponse.getEntity()));

        assertThat(sampleResponse.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_CREATED));
    }

    @AfterClass
    public static void tearDown() throws Exception {
        HttpDelete request1 = new HttpDelete(submissionsUrls[0]);
        request1.setHeaders(TestUtils.getContentTypeAcceptAndTokenHeaders(token));
        HttpClientBuilder.create().build().execute(request1);

        HttpDelete request2 = new HttpDelete(submissionsUrls[1]);
        request2.setHeaders(TestUtils.getContentTypeAcceptAndTokenHeaders(token));
        HttpClientBuilder.create().build().execute(request2);
    }

    private static String createSampleForSubmission(String token, String samplesApiBaseUrl, String submissionUrl, String alias) throws IOException {
        HttpPost request = new HttpPost(samplesApiBaseUrl);
        request.setHeaders(TestUtils.getContentTypeAcceptAndTokenHeaders(token));

        StringEntity payload = new StringEntity(TestJsonUtils.createSampleForSubmissionJson(submissionUrl, alias));
        request.setEntity(payload);

        HttpResponse response =  HttpClientBuilder.create().build().execute(request);
        SubmittableTemplate resource = TestUtils.retrieveResourceFromResponse(response, SubmittableTemplate.class);
        return resource.get_links().getSelf().getHref();
    }
}
