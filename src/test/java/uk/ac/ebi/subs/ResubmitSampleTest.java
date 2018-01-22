package uk.ac.ebi.subs;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.subs.data.objects.SubmissionStatus;
import uk.ac.ebi.subs.data.objects.SubmittableTemplate;
import uk.ac.ebi.subs.utils.TestJsonUtils;
import uk.ac.ebi.subs.utils.TestUtils;

import java.io.IOException;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class ResubmitSampleTest {
    private static PropertiesManager pm = PropertiesManager.getInstance();
    private static String teamName = pm.getTeamName();
    private static String samplesApiBaseUrl = pm.getSamplesApiBaseUrl();
    private static String token;

    @Before
    public void setUp() throws Exception {
        token = TestUtils.getJWTToken(pm.getAuthenticationUrl(), pm.getAapUsername(), pm.getAapPassword());
    }

    @Test
    public void givenSampleAlreadySubmitted_whenSubmittingAgainWithoutAccession_thenSampleGetsSameAccession() throws IOException, InterruptedException {
        String sampleAlias = TestUtils.getRandomAlias();

        // --- First submission --- //
        String firstSubmissionUrl = TestUtils.createSubmission(token, pm.getSubmissionsApiBaseUrl(), pm.getSubmitterEmail(), teamName);
        String firstSampleUrl = createSampleForSubmission(token, samplesApiBaseUrl, firstSubmissionUrl, sampleAlias);
        Thread.sleep(2000); // Make sure validation results are all back

        // Submit
        HttpUriRequest getRequest = new HttpGet(firstSubmissionUrl + "/submissionStatus");
        getRequest.setHeaders(TestUtils.getContentTypeAcceptAndTokenHeaders(token));
        SubmissionStatus submissionStatus = TestUtils.retrieveResourceFromResponse(HttpClientBuilder.create().build().execute(getRequest), SubmissionStatus.class);

        HttpPatch patchRequest = new HttpPatch(submissionStatus.getStatusUpdateUrl());
        patchRequest.setHeaders(TestUtils.getContentTypeAcceptAndTokenHeaders(token));
        patchRequest.setEntity(new StringEntity("{\"status\" : \"Submitted\"}"));
        HttpClientBuilder.create().build().execute(patchRequest);

        System.out.println("Sleeping long enough for first sample to get submitted and accessioned (40s).");
        Thread.sleep(40000); // Make sure first sample is submitted

        // --- Second submission --- //
        String submissionUrl2 = TestUtils.createSubmission(token, pm.getSubmissionsApiBaseUrl(), pm.getSubmitterEmail(), teamName);
        String secondSampleUrl = createSampleForSubmission(token, samplesApiBaseUrl, submissionUrl2, sampleAlias);
        Thread.sleep(2000); // Make sure validation results are all back

        // Submit
        HttpUriRequest getRequest2 = new HttpGet(submissionUrl2 + "/submissionStatus");
        getRequest2.setHeaders(TestUtils.getContentTypeAcceptAndTokenHeaders(token));
        SubmissionStatus submissionStatus2 = TestUtils.retrieveResourceFromResponse(HttpClientBuilder.create().build().execute(getRequest2), SubmissionStatus.class);

        HttpPatch patchRequest2 = new HttpPatch(submissionStatus2.getStatusUpdateUrl());
        patchRequest2.setHeaders(TestUtils.getContentTypeAcceptAndTokenHeaders(token));
        patchRequest2.setEntity(new StringEntity("{\"status\" : \"Submitted\"}"));
        HttpClientBuilder.create().build().execute(patchRequest2);
        Thread.sleep(2000);

        // --- Get samples --- //
        HttpUriRequest getRequest_sample1 = new HttpGet(firstSampleUrl);
        getRequest_sample1.setHeaders(TestUtils.getContentTypeAcceptAndTokenHeaders(token));
        HttpResponse response_sample1 = HttpClientBuilder.create().build().execute(getRequest_sample1);
        SubmittableTemplate sample_1 = TestUtils.retrieveResourceFromResponse(response_sample1, SubmittableTemplate.class);

        HttpUriRequest getRequest_sample2 = new HttpGet(secondSampleUrl);
        getRequest_sample2.setHeaders(TestUtils.getContentTypeAcceptAndTokenHeaders(token));
        HttpResponse response_sample2 = HttpClientBuilder.create().build().execute(getRequest_sample2);
        SubmittableTemplate sample_2 = TestUtils.retrieveResourceFromResponse(response_sample2, SubmittableTemplate.class);

        assertThat(
                sample_2.getAccession(), equalTo(sample_1.getAccession())
        );
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
