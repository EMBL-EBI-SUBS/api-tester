package uk.ac.ebi.subs.core;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
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
import uk.ac.ebi.subs.data.objects.ValidationResult;
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
public class StudyTests {

    private static PropertiesManager pm = PropertiesManager.getInstance();

    private static String token;
    private static String submissionUrl;
    private static String studyUrl;
    private static String studyValidationResultsUrl;

    private static String studyAlias = TestUtils.getRandomAlias();
    private static String projectAlias = TestUtils.getRandomAlias();

    @BeforeClass
    public static void setUp() throws Exception {
        token = TestUtils.getJWTToken(pm.getAuthenticationUrl(), pm.getAapUsername(), pm.getAapPassword());
        submissionUrl = TestUtils.createSubmission(token, pm.getSubmissionsApiTemplatedUrl(), pm.getSubmitterEmail(), pm.getTeamName());
        TestUtils.createProject(token, submissionUrl, projectAlias);

        studyUrl = TestUtils.createStudy(token, "enaStudies", submissionUrl, studyAlias, projectAlias, pm.getTeamName());
        studyValidationResultsUrl = studyUrl + "/validationResult";
    }

    @Test
    public void givenSubmissionExists_whenAddingStudyToIt_then201IsReceived() throws IOException {

        String content =
                TestJsonUtils.getStudyJson(
                        "Study.json",
                        TestUtils.getRandomAlias(),
                        projectAlias,
                        pm.getTeamName()

                );

        String studyUrl = TestUtils.submittableCreationUrl("enaStudies",submissionUrl);

        HttpResponse response = HttpUtils.httpPost(token, studyUrl,content);

        assertThat(
                response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_CREATED)
        );
    }

    @Test
    public void givenStudyExists_whenUpdatingIt_then200IsReceived() throws IOException {

        HttpPut request = new HttpPut(studyUrl);
        request.setHeaders(HttpUtils.getContentTypeAcceptAndTokenHeaders(token));

        StringEntity payload = new StringEntity(
                TestJsonUtils.getStudyJson(
                        "Study.json",
                        studyAlias,
                        projectAlias,
                        pm.getTeamName()
                )
        );
        request.setEntity(payload);

        HttpResponse response = HttpClientBuilder.create().build().execute(request);

        assertThat(
                response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK)
        );
    }

    @Test
    public void givenStudyExists_whenGettingValidationResults_then200IsReceived() throws IOException {
        HttpResponse response = HttpUtils.httpGet(token, studyValidationResultsUrl);

        assertThat(
                response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK)
        );
    }

    @Test
    public void givenStudyExists_whenGettingValidationResults_thenStatusIsCompleteOrPending() throws IOException {

        String validationStatus = TestUtils.getValidationStatus(studyValidationResultsUrl, token);

        assertThat(
                validationStatus, anyOf(equalTo("Pending"), equalTo("Complete"))
        );
    }

    @Test
    public void givenStudyExists_whenGettingCoreValidationResult_thenValidationResultStatusIsPass() throws IOException, InterruptedException {

        TestUtils.waitForValidationResults(token, studyUrl);

        ValidationResultStatusAndLink validationResultStatusAndLink =
                TestUtils.getValidationResultStatusAndLinkFromStudy(studyValidationResultsUrl, token);

        ValidationResult validationResult =
                TestUtils.getValidationResultFromValidationResultStatus(
                        validationResultStatusAndLink.getLinks().getSelf().getHref(), token);

        assertThat(
                validationResult.getValidationResultsFromCore()[0].getValidationStatus(), equalTo("Pass")
        );
    }

    @Test
    public void givenStudyExists_whenGettingEnaValidationResult_thenValidationResultIsAvailable() throws IOException, InterruptedException {

        TestUtils.waitForValidationResults(token, studyUrl);

        ValidationResultStatusAndLink validationResultStatusAndLink =
                TestUtils.getValidationResultStatusAndLinkFromStudy(studyValidationResultsUrl, token);

        ValidationResult validationResult =
                TestUtils.getValidationResultFromValidationResultStatus(
                        validationResultStatusAndLink.getLinks().getSelf().getHref(), token);

        assertThat(
                validationResult.getValidationResultsFromEna()[0], notNullValue()
        );
    }

    @AfterClass
    public static void tearDown() throws Exception {
        HttpUtils.deleteResource(token, submissionUrl);
    }

}
