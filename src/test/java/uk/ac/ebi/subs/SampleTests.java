package uk.ac.ebi.subs;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.ac.ebi.subs.data.objects.Sample;
import uk.ac.ebi.subs.data.structures.PutSampleResponseObject;
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

    static String authUrl = propertiesManager.getAuthenticationUrl();
    static String aapUsername = propertiesManager.getAapUsername();
    static String aapPassword = propertiesManager.getAapPassword();

    static String token = "";
    static String submissionUrl = "";
    static String sampleUrl = "";

    static String sampleAlias = TestUtils.getRandomAlias();

    @BeforeClass
    public static void setUp() throws Exception {
        token = TestUtils.getJWTToken(authUrl, aapUsername, aapPassword);
        submissionUrl = TestUtils.createSubmission(token, submissionsApiBaseUrl, submitterEmail, teamName);
        sampleUrl = TestUtils.createSample(token, samplesApiBaseUrl, submissionUrl, sampleAlias);
    }

    @Test
    public void givenSubmissionExists_whenAddingSampleToIt_then201IsReceived() throws IOException {

        HttpPost request = new HttpPost(samplesApiBaseUrl);
        request.setHeaders(TestUtils.getContentTypeAcceptAndTokenHeaders(token));

        StringEntity payload = new StringEntity(TestJsonUtils.getCreateSampleJson(submissionUrl, TestUtils.getRandomAlias()));
        request.setEntity(payload);

        HttpResponse response = HttpClientBuilder.create().build().execute(request);

        assertThat(
                response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_CREATED)
        );
    }

    @Test
    public void givenSampleExists_whenUpdatingIt_then200IsReceived() throws IOException {

        HttpPut request = new HttpPut(sampleUrl);
        request.setHeaders(TestUtils.getContentTypeAcceptAndTokenHeaders(token));

        StringEntity payload = new StringEntity(TestJsonUtils.getUpdateSampleJson(submissionUrl, sampleAlias));
        request.setEntity(payload);

        HttpResponse response = HttpClientBuilder.create().build().execute(request);

        assertThat(
                response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK)
        );
    }

    @Test
    public void givenSampleExists_whenUpdatingItsContentsWithPUT_thenStatusShouldRemainTheSame() throws IOException {

        String localAlias = TestUtils.getRandomAlias();
        String localSample = TestUtils.createSample(token, samplesApiBaseUrl, submissionUrl, localAlias);

        HttpPut request = new HttpPut(localSample);
        request.setHeaders(TestUtils.getContentTypeAcceptAndTokenHeaders(token));

        StringEntity payload = new StringEntity(TestJsonUtils.getUpdateSampleJson(submissionUrl, localAlias));
        request.setEntity(payload);

        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        PutSampleResponseObject resource = TestUtils.retrieveResourceFromResponse(response, PutSampleResponseObject.class);

        assertThat(
                resource.get_embedded().getProcessingStatus().getStatus(), equalTo("Draft")
        );
    }

    @Test
    public void givenSampleExists_whenUpdatingItsContentsWithPATCH_thenStatusShouldRemainTheSame() throws IOException {

        String localAlias = TestUtils.getRandomAlias();
        String localSample = TestUtils.createSample(token, samplesApiBaseUrl, submissionUrl, localAlias);

        HttpPatch request = new HttpPatch(localSample);
        request.setHeaders(TestUtils.getContentTypeAcceptAndTokenHeaders(token));

        StringEntity payload = new StringEntity(TestJsonUtils.getUpdateSampleJson(submissionUrl, localAlias));
        request.setEntity(payload);

        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        PutSampleResponseObject resource = TestUtils.retrieveResourceFromResponse(response, PutSampleResponseObject.class);

        assertThat(
                resource.get_embedded().getProcessingStatus().getStatus(), equalTo("Draft")
        );
    }

    @Test
    public void givenSampleExists_whenUpdatingItsContentsWithPUT_thenTeamShouldRemainTheSame() throws IOException {

        String localAlias = TestUtils.getRandomAlias();
        String localSample = TestUtils.createSample(token, samplesApiBaseUrl, submissionUrl, localAlias);

        HttpPut request = new HttpPut(localSample);
        request.setHeaders(TestUtils.getContentTypeAcceptAndTokenHeaders(token));

        StringEntity payload = new StringEntity(TestJsonUtils.getUpdateSampleJson(submissionUrl, localAlias));
        request.setEntity(payload);

        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        PutSampleResponseObject resource = TestUtils.retrieveResourceFromResponse(response, PutSampleResponseObject.class);

        assertThat(
                resource.getTeam().getName(), equalTo(teamName)
        );
    }

    @Test
    public void givenSampleExists_whenUpdatingItsContentsWithPATCH_thenTeamShouldRemainTheSame() throws IOException {

        String localAlias = TestUtils.getRandomAlias();
        String localSample = TestUtils.createSample(token, samplesApiBaseUrl, submissionUrl, localAlias);

        HttpPatch request = new HttpPatch(localSample);
        request.setHeaders(TestUtils.getContentTypeAcceptAndTokenHeaders(token));

        StringEntity payload = new StringEntity(TestJsonUtils.getUpdateSampleJson(submissionUrl, localAlias));
        request.setEntity(payload);

        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        PutSampleResponseObject resource = TestUtils.retrieveResourceFromResponse(response, PutSampleResponseObject.class);

        assertThat(
                resource.getTeam().getName(), equalTo(teamName)
        );
    }

    @Test
    public void givenSampleExists_whenDeletingSampleRelationships_thenRetrievedResourceIsCorrect() throws IOException {

        HttpPut request = new HttpPut(sampleUrl);
        request.setHeaders(TestUtils.getContentTypeAcceptAndTokenHeaders(token));

        StringEntity payload = new StringEntity(TestJsonUtils.getDeleteSampleRelationshipsJson(submissionUrl, sampleAlias));
        request.setEntity(payload);

        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        Sample resource = TestUtils.retrieveResourceFromResponse(response, Sample.class);

        assertThat(
                resource.getSampleRelationships(), equalTo(new String[0])
        );
    }

    @AfterClass
    public static void tearDown() throws Exception {
        HttpDelete request = new HttpDelete(submissionUrl);
        request.setHeaders(TestUtils.getContentTypeAcceptAndTokenHeaders(token));
        HttpClientBuilder.create().build().execute(request);
    }
}
