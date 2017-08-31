package uk.ac.ebi.subs;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.ac.ebi.subs.data.objects.Sample;
import uk.ac.ebi.subs.utils.TestJsonUtils;
import uk.ac.ebi.subs.utils.TestUtils;

import java.io.IOException;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class SampleTests {

    static PropertiesManager propertiesManager = PropertiesManager.getInstance();

    static String submitterEmail = propertiesManager.getSubmitterEmail();
    static String teamName = propertiesManager.getTeamName();
    static String submissionsApiBaseUrl = propertiesManager.getSubmissionsApiBaseUrl();
    static String samplesApiBaseUrl = propertiesManager.getSamplesApiBaseUrl();

    static String submissionUrl = "";
    static String sampleUrl = "";

    @BeforeClass
    public static void setUp() throws Exception {
        submissionUrl = TestUtils.createSubmission(submissionsApiBaseUrl, submitterEmail, teamName);
        sampleUrl = TestUtils.createSample(samplesApiBaseUrl, submissionUrl);
    }

    @Test
    public void givenSubmissionExists_whenAddingSampleToIt_then201IsReceived() throws IOException {

        HttpPost request = new HttpPost(samplesApiBaseUrl);
        request.setHeaders(TestUtils.getContentTypeAndAcceptHeaders());

        StringEntity payload = new StringEntity(TestJsonUtils.getCreateSampleJson(submissionUrl, "test-alias"));
        request.setEntity(payload);

        HttpResponse response = HttpClientBuilder.create().build().execute(request);

        assertThat(
                response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_CREATED)
        );
    }

    @Test
    public void givenSampleExists_whenUpdatingIt_then200IsReceived() throws IOException {

        HttpPut request = new HttpPut(sampleUrl);
        request.setHeaders(TestUtils.getContentTypeAndAcceptHeaders());

        StringEntity payload = new StringEntity(TestJsonUtils.getUpdateSampleRelationshipsJson(submissionUrl));
        request.setEntity(payload);

        HttpResponse response = HttpClientBuilder.create().build().execute(request);

        assertThat(
                response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK)
        );
    }

    @Test
    public void givenSampleExists_whenDeletingSampleRelationships_thenRetrievedResourceIsCorrect() throws IOException {

        HttpPut request = new HttpPut(sampleUrl);
        request.setHeaders(TestUtils.getContentTypeAndAcceptHeaders());

        StringEntity payload = new StringEntity(TestJsonUtils.getDeleteSampleRelationshipsJson(submissionUrl));
        request.setEntity(payload);

        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        Sample resource = TestUtils.retrieveResourceFromResponse(response, Sample.class);

        assertThat(
                resource.getSampleRelationships(), equalTo(null)
        );
    }

    @AfterClass
    public static void tearDown() throws Exception {
        HttpDelete request = new HttpDelete(submissionUrl);
        HttpClientBuilder.create().build().execute(request);
    }
}
