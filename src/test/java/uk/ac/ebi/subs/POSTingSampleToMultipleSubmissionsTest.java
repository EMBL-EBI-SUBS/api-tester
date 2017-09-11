package uk.ac.ebi.subs;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.ac.ebi.subs.data.structures.WrapperObject;
import uk.ac.ebi.subs.utils.TestUtils;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class POSTingSampleToMultipleSubmissionsTest {

    private static PropertiesManager propertiesManager = PropertiesManager.getInstance();

    private static String submitterEmail = propertiesManager.getSubmitterEmail();
    private static String teamName = propertiesManager.getTeamName();
    private static String submissionsApiBaseUrl = propertiesManager.getSubmissionsApiBaseUrl();
    private static String samplesApiBaseUrl = propertiesManager.getSamplesApiBaseUrl();
    private static String samplesInSubmissionByIdUrl = propertiesManager.getSamplesInSubmissionByIdUrl();

    private static String authUrl = propertiesManager.getAuthenticationUrl();
    private static String aapUsername = propertiesManager.getAapUsername();
    private static String aapPassword = propertiesManager.getAapPassword();

    private static String token = "";
    private static String[] submissionsUrls;

    @BeforeClass
    public static void setUp() throws Exception {
        token = TestUtils.getJWTToken(authUrl, aapUsername, aapPassword);
        submissionsUrls = TestUtils.createNSubmissions(2, token, submissionsApiBaseUrl, submitterEmail, teamName);
        TestUtils.createSample(token, samplesApiBaseUrl, submissionsUrls[0], "S1234");
    }

    @Test
    public void givenSampleExistsInASubmission_whenAddingItToOtherSubmission_thenItShouldExistInBothSubmissions() throws IOException {
        TestUtils.createSample(token, samplesApiBaseUrl, submissionsUrls[1], "S1234");

        HttpUriRequest request1 = new HttpGet(samplesInSubmissionByIdUrl + TestUtils.getIdFromUrl(submissionsUrls[0]));
        request1.setHeaders(TestUtils.getContentTypeAcceptAndTokenHeaders(token));
        HttpResponse response1 = HttpClientBuilder.create().build().execute(request1);
        WrapperObject resource1 = TestUtils.retrieveResourceFromResponse(response1, WrapperObject.class);

        HttpUriRequest request2 = new HttpGet(samplesInSubmissionByIdUrl + TestUtils.getIdFromUrl(submissionsUrls[1]));
        request2.setHeaders(TestUtils.getContentTypeAcceptAndTokenHeaders(token));
        HttpResponse response2 = HttpClientBuilder.create().build().execute(request2);
        WrapperObject resource2 = TestUtils.retrieveResourceFromResponse(response2, WrapperObject.class);

        assertThat(1, equalTo(resource1.getSamplesLength()));
        assertThat(1, equalTo(resource2.getSamplesLength()));

        assertThat(
                resource1.getNthSampleAlias(0), equalTo(resource2.getNthSampleAlias(0))
        );
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
}
