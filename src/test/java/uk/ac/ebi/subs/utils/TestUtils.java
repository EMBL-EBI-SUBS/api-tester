package uk.ac.ebi.subs.utils;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import uk.ac.ebi.subs.data.objects.Submission;
import uk.ac.ebi.subs.data.objects.SubmissionStatus;
import uk.ac.ebi.subs.data.objects.SubmittableTemplate;
import uk.ac.ebi.subs.data.objects.ValidationResult;
import uk.ac.ebi.subs.data.structures.ValidationResultStatusAndLink;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Random;

public class TestUtils {

    public static String createSubmission(String token, String submissionsApiTemplatedUrl, String submitterEmail, String teamName) throws IOException {
        String submissionApiUrl = submissionsApiTemplatedUrl.replace("{teamName}", teamName);

        String content = TestJsonUtils.getSubmissionJson(submitterEmail, teamName);

        HttpResponse response = HttpUtils.httpPost(token, submissionApiUrl, content);

        Submission resource = HttpUtils.retrieveResourceFromResponse(response, Submission.class);
        String selfHref = resource.get_links().getSelf().getHref();
        return selfHref.replace("{?projection}", "");
    }


    public static String createSample(String token, String samplesApiBaseUrl, String submissionUrl, String alias) throws IOException {
        return createSubmittable(token, samplesApiBaseUrl, TestJsonUtils.getCreateSampleJson(submissionUrl, alias));
    }

    /**
     * @param token
     * @param samplesApiBaseUrl
     * @param submittableContent
     * @return URL of new resource
     * @throws IOException
     */
    public static String createSubmittable(String token, String samplesApiBaseUrl, String submittableContent) throws IOException {
        HttpResponse response = HttpUtils.httpPost(token, samplesApiBaseUrl, submittableContent);

        Assert.assertEquals(201, response.getStatusLine().getStatusCode());

        SubmittableTemplate resource = HttpUtils.retrieveResourceFromResponse(response, SubmittableTemplate.class);

        return resource.get_links().getSelf().getHref();
    }

    public static String createProject(String token, String projectsApiBaseUrl, String submissionUrl, String projectAlias) throws IOException {
        return createProject(token, projectsApiBaseUrl, submissionUrl, projectAlias, LocalDateTime.now().toString());
    }

    public static String createProject(String token, String projectsApiBaseUrl, String submissionUrl, String projectAlias, String releaseDate) throws IOException {

        String content = TestJsonUtils.getProjectJson(submissionUrl, projectAlias, releaseDate);

        return createSubmittable(token, projectsApiBaseUrl, content);
    }


    public static String createStudy(String token, String studiesApiBaseUrl, String submissionUrl, String studyAlias, String projectAlias, String teamName) throws IOException {
        String content =
                TestJsonUtils.getStudyJson(
                        submissionUrl,
                        studyAlias,
                        projectAlias,
                        teamName
                );


        return createSubmittable(token, studiesApiBaseUrl, content);
    }

    public static String createAssay(String token, String assayApiBaseUrl, String submissionUrl, String assayAlias, String studyAlias, String sampleAlias) throws IOException {
        String content =
                TestJsonUtils.getAssayJson(
                        submissionUrl,
                        assayAlias,
                        sampleAlias,
                        studyAlias
                );

        return createSubmittable(token, assayApiBaseUrl, content);
    }

    public static String createAssayData(String token, String assayDataApiBaseUrl, String submissionUrl, String assayDataAlias, String assayAlias, String fileName, String fileType) throws IOException {
        String content =
                TestJsonUtils.getAssayDataJson(
                        submissionUrl,
                        assayDataAlias,
                        assayAlias,
                        fileName,
                        fileType
                );

        return createSubmittable(token, assayDataApiBaseUrl, content);
    }

    public static String[] createNSubmissions(int n, String token, String submissionsApiBaseUrl, String submitterEmail, String teamName) throws IOException {
        String[] urls = new String[n];
        for (int i = 0; i < n; i++) {
            urls[i] = createSubmission(token, submissionsApiBaseUrl, submitterEmail, teamName);
        }
        return urls;
    }

    public static String getJWTToken(String authUrl, String username, String password) throws IOException {
        CredentialsProvider provider = new BasicCredentialsProvider();
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
        provider.setCredentials(AuthScope.ANY, credentials);

        HttpClient client = HttpClientBuilder.create().setDefaultCredentialsProvider(provider).build();
        HttpResponse response = client.execute(new HttpGet(authUrl));

        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            throw new CouldNotGetTokenException("ERROR: An error occurred when trying to obtain the AAP token.");
        }
        return EntityUtils.toString(response.getEntity());
    }

    public static String getRandomAlias() {
        Random random = new Random();
        String digit = String.format("%06d", random.nextInt(1000000));
        return "alias-" + digit;
    }

    public static String getValidationStatus(String validationResultsUrl, String token) throws IOException {
        HttpResponse response = HttpUtils.httpGet(token,validationResultsUrl);
        ValidationResult resource = HttpUtils.retrieveResourceFromResponse(response, ValidationResult.class);

        return resource.getValidationStatus();
    }

    public static ValidationResultStatusAndLink getValidationResultStatusAndLinkFromStudy(
            String studyValidationResultsUrl, String token) throws IOException {

        HttpResponse response = HttpUtils.httpGet(token,studyValidationResultsUrl);

        return HttpUtils.retrieveResourceFromResponse(response, ValidationResultStatusAndLink.class);
    }

    public static ValidationResult getValidationResultFromValidationResultStatus(String validationResultURI, String token)
            throws IOException {
        HttpResponse validationResultResponse = HttpUtils.httpGet(token,validationResultURI);

        return HttpUtils.retrieveResourceFromResponse(validationResultResponse, ValidationResult.class);
    }

    public static void waitForValidationResults(String token, String submittableUrl) throws IOException, InterruptedException {

        long maximumIntervalMillis = 30000;
        long startingTimeMillis = System.currentTimeMillis();

        while (System.currentTimeMillis() < startingTimeMillis + maximumIntervalMillis) {
            SubmittableTemplate resource = getSubmittableTemplate(token, submittableUrl);

            boolean validationIsNotPending = !resource.get_embedded().getValidationResult()
                    .getValidationStatus().equalsIgnoreCase("pending");

            if (validationIsNotPending) {

                return;
            }
            Thread.sleep(500);
        }

        throw new RuntimeException("Gave up waiting for validation results on " + submittableUrl);
    }

    public static ValidationResult getValidationResultForSubmittable(String submittableUrl, String token) throws IOException {
        SubmittableTemplate resource = getSubmittableTemplate(token, submittableUrl);
        String validationResultUrl = resource.get_links().getValidationResult().getHref();
;
        HttpResponse stubResultResponse = HttpUtils.httpGet(token,validationResultUrl);
        Assert.assertEquals(200, stubResultResponse.getStatusLine().getStatusCode());
        ValidationResult stubResult = HttpUtils.retrieveResourceFromResponse(stubResultResponse, ValidationResult.class);

        HttpResponse fullResultResponse = HttpUtils.httpGet(token,stubResult.get_links().getSelf().getHref());
        Assert.assertEquals(200, fullResultResponse.getStatusLine().getStatusCode());
        ValidationResult fullResult = HttpUtils.retrieveResourceFromResponse(fullResultResponse, ValidationResult.class);

        return fullResult;
    }

    public static void waitForCompletedSubmittable(String token, String submittableUrl) throws IOException, InterruptedException {

        long maximumIntervalMillis = 50000;
        long startingTimeMillis = System.currentTimeMillis();

        while (System.currentTimeMillis() < startingTimeMillis + maximumIntervalMillis) {
            SubmittableTemplate resource = getSubmittableTemplate(token, submittableUrl);

            boolean submittableIsCompleted = resource.get_embedded().getProcessingStatus().getStatus().equalsIgnoreCase("completed");

            if (submittableIsCompleted) {

                return;
            }
            Thread.sleep(500);
        }

        throw new RuntimeException("Gave up waiting for validation results on " + submittableUrl);
    }

    public static SubmittableTemplate getSubmittableTemplate(String token, String submittableUrl) throws IOException {
        HttpUriRequest submittableRequest = new HttpGet(submittableUrl);
        submittableRequest.setHeaders(HttpUtils.getContentTypeAcceptAndTokenHeaders(token));
        HttpResponse submittableResponse = HttpClientBuilder.create().build().execute(submittableRequest);

        Assert.assertEquals(200, submittableResponse.getStatusLine().getStatusCode());
        return HttpUtils.retrieveResourceFromResponse(submittableResponse, SubmittableTemplate.class);
    }

    public static void waitForCompletedSubmission(String token, String submissionUrl) throws IOException, InterruptedException {

        long maximumIntervalMillis = 50000;
        long startingTimeMillis = System.currentTimeMillis();

        String submissionStatusUrl = getStatusUrlForSubmission(token, submissionUrl);

        while (System.currentTimeMillis() < startingTimeMillis + maximumIntervalMillis) {

            HttpResponse statusResponse = HttpUtils.httpGet(token,submissionStatusUrl);
            Assert.assertEquals(200, statusResponse.getStatusLine().getStatusCode());

            SubmissionStatus resource = HttpUtils.retrieveResourceFromResponse(statusResponse, SubmissionStatus.class);

            boolean submissionIsCompleted = resource.getStatus().equalsIgnoreCase("completed");

            if (submissionIsCompleted) {

                return;
            }
            Thread.sleep(500);
        }

        throw new RuntimeException("Gave up waiting for submission to be completed " + submissionUrl);
    }

    public static void waitForUpdateableSubmission(String token, String submissionUrl) throws IOException, InterruptedException {

        long maximumIntervalMillis = 50000;
        long startingTimeMillis = System.currentTimeMillis();

        String submissionStatusUrl = getStatusUrlForSubmission(token, submissionUrl);

        while (System.currentTimeMillis() < startingTimeMillis + maximumIntervalMillis) {
            HttpResponse statusResponse = HttpUtils.httpGet(token,submissionStatusUrl);

            Assert.assertEquals(200, statusResponse.getStatusLine().getStatusCode());
            SubmissionStatus resource = HttpUtils.retrieveResourceFromResponse(statusResponse, SubmissionStatus.class);

            boolean submissionIsUpdateable = resource.getStatusUpdateUrl() != null;

            if (submissionIsUpdateable) {
                return;
            }
            Thread.sleep(500);
        }

        throw new RuntimeException("Gave up waiting for submission to be updateable " + submissionUrl);
    }

    public static String getStatusUrlForSubmission(String token, String submissionUrl) throws IOException {
        HttpResponse submissionResponse = HttpUtils.httpGet(token,submissionUrl);

        Assert.assertEquals(200, submissionResponse.getStatusLine().getStatusCode());
        Submission submissionResource = HttpUtils.retrieveResourceFromResponse(submissionResponse, Submission.class);
        return submissionResource.get_links().getSubmissionStatus().getHref();
    }

}

