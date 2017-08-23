package uk.ac.ebi.subs;

import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import uk.ac.ebi.subs.data.SampleResource;
import uk.ac.ebi.subs.utils.TestJsonUtils;
import uk.ac.ebi.subs.utils.TestUtils;

import java.io.IOException;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SampleTests {

    static String submitterEmail = "api-tester@ebi.ac.uk";
    static String teamName = "api-tester";

    static String submissionsApiBaseUrl = "http://submission-dev.ebi.ac.uk/api/submissions/";
    static String submissionUrl = "";

    String samplesApiBaseUrl = "http://submission-dev.ebi.ac.uk/api/samples/";
    static String sampleUrl = "";

    @BeforeClass
    public static void setUp() throws Exception {
        TestUtils.createSubmission(submissionsApiBaseUrl, submitterEmail, teamName);
        submissionUrl = TestUtils.getFirstSubmissionUrlForTeam(teamName);
    }

    @Test
    public void a_givenSubmissionExists_whenAddingSampleToIt_then201IsReceived() throws IOException {
        HttpPost request = new HttpPost(samplesApiBaseUrl);

        Header contentType = new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/hal+json");
        Header accept = new BasicHeader(HttpHeaders.ACCEPT, "application/hal+json");
        Header[] headers = {contentType, accept};
        request.setHeaders(headers);

        StringEntity payload = new StringEntity(TestJsonUtils.getCreateSampleJson(submissionUrl));
        request.setEntity(payload);

        HttpResponse response = HttpClientBuilder.create().build().execute(request);

        assertThat(
                response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_CREATED)
        );

        sampleUrl = TestUtils.getFirstSampleUrlForTeam(teamName);
    }

    @Test
    public void b_givenSampleExists_whenUpdatingIt_then200IsReceived() throws IOException {
        HttpPut request = new HttpPut(sampleUrl);

        Header contentType = new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/hal+json");
        Header accept = new BasicHeader(HttpHeaders.ACCEPT, "application/hal+json");
        Header[] headers = {contentType, accept};
        request.setHeaders(headers);

        StringEntity payload = new StringEntity(TestJsonUtils.getUpdateSampleRelationshipsJson(submissionUrl));
        request.setEntity(payload);

        HttpResponse response = HttpClientBuilder.create().build().execute(request);

        assertThat(
                response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK)
        );
    }

    @Test
    public void c_givenSampleExists_whenDeletingSampleRelationships_thenRetrievedResourceIsCorrect() throws IOException {
        HttpPut request = new HttpPut(sampleUrl);

        Header contentType = new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/hal+json");
        Header accept = new BasicHeader(HttpHeaders.ACCEPT, "application/hal+json");
        Header[] headers = {contentType, accept};
        request.setHeaders(headers);

        StringEntity payload = new StringEntity(TestJsonUtils.getDeleteSampleRelationshipsJson(submissionUrl));
        request.setEntity(payload);

        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        SampleResource resource = TestUtils.retrieveResourceFromResponse(response, SampleResource.class);

        assertThat(
                resource.getSampleRelationships(), equalTo(null)
        );

    }

    @AfterClass
    public static void tearDown() throws Exception {
        HttpDelete request = new HttpDelete(submissionUrl);
        HttpResponse response = HttpClientBuilder.create().build().execute(request);
    }
}
