package uk.ac.ebi.subs.samples;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import uk.ac.ebi.subs.PropertiesManager;
import uk.ac.ebi.subs.categories.DevEnv;
import uk.ac.ebi.subs.categories.TestEnv;
import uk.ac.ebi.subs.data.objects.SubmittableTemplate;
import uk.ac.ebi.subs.data.objects.ValidationResult;
import uk.ac.ebi.subs.data.structures.PutSampleResponseObject;
import uk.ac.ebi.subs.data.structures.ValidationResultStatusAndLink;
import uk.ac.ebi.subs.utils.HttpUtils;
import uk.ac.ebi.subs.utils.TestJsonUtils;
import uk.ac.ebi.subs.utils.TestUtils;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.AnyOf.anyOf;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;

@Category({TestEnv.class, DevEnv.class})
public class SampleTests {

    private static PropertiesManager pm = PropertiesManager.getInstance();

    private static String teamName = pm.getTeamName();

    private static String token;
    private static String submissionUrl;
    private static String sampleUrl;
    private static String sampleValidationResultsUrl;

    private static String sampleAlias = TestUtils.getRandomAlias();

    @BeforeClass
    public static void setUp() throws Exception {
        token = TestUtils.getJWTToken(pm.getAuthenticationUrl(), pm.getAapUsername(), pm.getAapPassword());
        submissionUrl = TestUtils.createSubmission(token, pm.getSubmissionsApiTemplatedUrl(), pm.getSubmitterEmail(), teamName);
        sampleUrl = TestUtils.createSample(token, submissionUrl, sampleAlias);
        sampleValidationResultsUrl = sampleUrl + "/validationResult";
    }

    @Test
    public void givenSubmissionExists_whenAddingSampleToIt_then201IsReceived() throws IOException {

        String content = TestJsonUtils.getCreateSampleJson(TestUtils.getRandomAlias());

        String sampleUrl = TestUtils.submittableCreationUrl("samples",submissionUrl);

        HttpResponse response = HttpUtils.httpPost(token, sampleUrl, content);

        assertThat(
                response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_CREATED)
        );
    }

    @Test
    public void givenSampleExists_whenUpdatingIt_then200IsReceived() throws IOException {

        HttpPut request = new HttpPut(sampleUrl);
        request.setHeaders(HttpUtils.getContentTypeAcceptAndTokenHeaders(token));

        StringEntity payload = new StringEntity(TestJsonUtils.getUpdateSampleJson(sampleAlias));
        request.setEntity(payload);

        HttpResponse response = HttpClientBuilder.create().build().execute(request);

        assertThat(
                response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK)
        );
    }

    @Test
    public void givenSampleExists_whenUpdatingItsContentsWithPUT_thenStatusShouldRemainTheSame() throws IOException {

        String localAlias = TestUtils.getRandomAlias();
        String localSample = TestUtils.createSample(token, submissionUrl, localAlias);

        HttpPut request = new HttpPut(localSample);
        request.setHeaders(HttpUtils.getContentTypeAcceptAndTokenHeaders(token));

        StringEntity payload = new StringEntity(TestJsonUtils.getUpdateSampleJson(localAlias));
        request.setEntity(payload);

        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        PutSampleResponseObject resource = HttpUtils.retrieveResourceFromResponse(response, PutSampleResponseObject.class);

        assertThat(
                resource.getEmbedded().getProcessingStatus().getStatus(), equalTo("Draft")
        );
    }

    @Test
    public void givenSampleExists_whenUpdatingItsContentsWithPATCH_thenStatusShouldRemainTheSame() throws IOException {

        String localAlias = TestUtils.getRandomAlias();
        String localSample = TestUtils.createSample(token, submissionUrl, localAlias);

        String content = TestJsonUtils.getUpdateSampleJson(localAlias);

        HttpResponse response = HttpUtils.httpPatch(token, localSample, content);
        PutSampleResponseObject resource = HttpUtils.retrieveResourceFromResponse(response, PutSampleResponseObject.class);

        assertThat(
                resource.getEmbedded().getProcessingStatus().getStatus(), equalTo("Draft")
        );
    }

    @Test
    public void givenSampleExists_whenUpdatingItsContentsWithPUT_thenTeamShouldRemainTheSame() throws IOException {

        String localAlias = TestUtils.getRandomAlias();
        String localSample = TestUtils.createSample(token, submissionUrl, localAlias);

        HttpPut request = new HttpPut(localSample);
        request.setHeaders(HttpUtils.getContentTypeAcceptAndTokenHeaders(token));

        StringEntity payload = new StringEntity(TestJsonUtils.getUpdateSampleJson(localAlias));
        request.setEntity(payload);

        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        PutSampleResponseObject resource = HttpUtils.retrieveResourceFromResponse(response, PutSampleResponseObject.class);

        assertThat(
                resource.getTeam().getName(), equalTo(teamName)
        );
    }

    @Test
    public void givenSampleExists_whenUpdatingItsContentsWithPATCH_thenTeamShouldRemainTheSame() throws IOException {

        String localAlias = TestUtils.getRandomAlias();
        String localSample = TestUtils.createSample(token, submissionUrl, localAlias);

        String content = TestJsonUtils.getUpdateSampleJson(localAlias);

        HttpResponse response = HttpUtils.httpPatch(token, localSample, content);
        PutSampleResponseObject resource = HttpUtils.retrieveResourceFromResponse(response, PutSampleResponseObject.class);

        assertThat(
                resource.getTeam().getName(), equalTo(teamName)
        );
    }

    @Test
    public void givenSampleExists_whenDeletingSampleRelationships_thenRetrievedResourceIsCorrect() throws IOException {

        HttpPut request = new HttpPut(sampleUrl);
        request.setHeaders(HttpUtils.getContentTypeAcceptAndTokenHeaders(token));

        StringEntity payload = new StringEntity(TestJsonUtils.getDeleteSampleRelationshipsJson(sampleAlias));
        request.setEntity(payload);

        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        SubmittableTemplate resource = HttpUtils.retrieveResourceFromResponse(response, SubmittableTemplate.class);

        assertThat(
                resource.getSampleRelationships(), equalTo(new String[0])
        );
    }

    @Test
    public void givenSampleExists_whenGettingValidationResults_then200IsReceived() throws IOException {

        HttpResponse response = HttpUtils.httpGet(token, sampleUrl);

        assertThat(
                response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK)
        );
    }

    @Test
    public void givenSampleExists_whenGettingValidationResults_thenStatusIsCompleteOrPending() throws IOException {

        String validationStatus = TestUtils.getValidationStatus(sampleValidationResultsUrl, token);

        assertThat(
                validationStatus, anyOf(equalTo("Pending"), equalTo("Complete"))
        );
    }

    @Test
    public void givenSampleExists_whenGettingTaxonomyValidationResult_thenValidationResultIsAvailable() throws IOException, InterruptedException {

        TestUtils.waitForValidationResults(token, sampleUrl);

        ValidationResultStatusAndLink validationResultStatusAndLink =
                TestUtils.getValidationResultStatusAndLinkFromStudy(sampleValidationResultsUrl, token);

        ValidationResult validationResult =
                TestUtils.getValidationResultFromValidationResultStatus(
                        validationResultStatusAndLink.getLinks().getSelf().getHref(), token);

        assertThat(
                validationResult.getValidationResultsFromTaxonomy()[0], notNullValue()
        );
    }

    @AfterClass
    public static void tearDown() throws Exception {
        HttpDelete request = new HttpDelete(submissionUrl);
        request.setHeaders(HttpUtils.getContentTypeAcceptAndTokenHeaders(token));
        HttpClientBuilder.create().build().execute(request);
    }
}
