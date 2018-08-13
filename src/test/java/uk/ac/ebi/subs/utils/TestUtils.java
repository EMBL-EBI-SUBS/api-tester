package uk.ac.ebi.subs.utils;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import uk.ac.ebi.subs.data.objects.FileList;
import uk.ac.ebi.subs.data.objects.ProcessingStatus;
import uk.ac.ebi.subs.data.objects.ProcessingStatuses;
import uk.ac.ebi.subs.data.objects.Submission;
import uk.ac.ebi.subs.data.objects.SubmissionContents;
import uk.ac.ebi.subs.data.objects.SubmissionStatus;
import uk.ac.ebi.subs.data.objects.SubmittableTemplate;
import uk.ac.ebi.subs.data.objects.SubsFile;
import uk.ac.ebi.subs.data.objects.ValidationResult;
import uk.ac.ebi.subs.data.structures.Result;
import uk.ac.ebi.subs.data.structures.ValidationResultStatusAndLink;

import java.io.IOException;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class TestUtils {

    public static String createSubmission(String token, String submissionsApiTemplatedUrl, String submitterEmail, String teamName) throws IOException {
        String submissionApiUrl = submissionsApiTemplatedUrl.replace("{teamName}", teamName);

        String content = TestJsonUtils.getSubmissionJson(submitterEmail, teamName);

        HttpResponse response = HttpUtils.httpPost(token, submissionApiUrl, content);

        Submission resource = HttpUtils.retrieveResourceFromResponse(response, Submission.class);
        String selfHref = resource.getLinks().getSelf().getHref();
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

        return resource.getLinks().getSelf().getHref();
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

    public static String createMLStudy(String token, String studiesApiBaseUrl, String submissionUrl, String studyAlias, String projectAlias, String teamName) throws IOException {
        String content =
                TestJsonUtils.getMLStudyJson(
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

    public static SubmissionContents getSubmissionContent(String token, String submissionUrl) throws Exception {
        Submission submission = TestUtils.getSubmission(token, submissionUrl);
        HttpResponse submissionContentsResponse = HttpUtils.httpGet(token, submission.getLinks().getContents().getHref());
        return HttpUtils.retrieveResourceFromResponse(submissionContentsResponse, SubmissionContents.class);
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

            boolean validationIsNotPending = !resource.getEmbedded().getValidationResult()
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
        String validationResultUrl = resource.getLinks().getValidationResult().getHref();

        HttpResponse stubResultResponse = HttpUtils.httpGet(token,validationResultUrl);
        Assert.assertEquals(200, stubResultResponse.getStatusLine().getStatusCode());
        ValidationResult stubResult = HttpUtils.retrieveResourceFromResponse(stubResultResponse, ValidationResult.class);

        HttpResponse fullResultResponse = HttpUtils.httpGet(token,stubResult.getLinks().getSelf().getHref());
        Assert.assertEquals(200, fullResultResponse.getStatusLine().getStatusCode());
        ValidationResult fullResult = HttpUtils.retrieveResourceFromResponse(fullResultResponse, ValidationResult.class);

        return fullResult;
    }

    public static void waitForCompletedSubmittable(String token, String submittableUrl) throws IOException, InterruptedException {

        long maximumIntervalMillis = 50000;
        long startingTimeMillis = System.currentTimeMillis();

        while (System.currentTimeMillis() < startingTimeMillis + maximumIntervalMillis) {
            SubmittableTemplate resource = getSubmittableTemplate(token, submittableUrl);

            boolean submittableIsCompleted = resource.getEmbedded().getProcessingStatus().getStatus().equalsIgnoreCase("completed");

            if (submittableIsCompleted) {

                return;
            }
            Thread.sleep(500);
        }

        throw new RuntimeException("Gave up waiting for validation results on " + submittableUrl);
    }

    public static Submission getSubmission(String token, String submissionUrl) throws IOException {
        HttpUriRequest request = new HttpGet(submissionUrl);
        request.setHeaders(HttpUtils.getContentTypeAcceptAndTokenHeaders(token));
        HttpResponse response = HttpClientBuilder.create().build().execute(request);

        Assert.assertEquals(200, response.getStatusLine().getStatusCode());
        return HttpUtils.retrieveResourceFromResponse(response, Submission.class);
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

    public static void changeSubmissionStatusToSubmitted(String token, String submissionUrl) throws IOException {
        HttpResponse getResponse = HttpUtils.httpGet(token, submissionUrl);

        Submission submission = HttpUtils.retrieveResourceFromResponse(getResponse, Submission.class);

        HttpResponse response = HttpUtils.httpPut(token,submission.getLinks().getSubmissionStatusUpdate().getHref(),"{\"status\" : \"Submitted\"}");

        assertThat(
                response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK)
        );
    }

    public static Collection<ProcessingStatus> fetchProcessingStatuses(String token, String submissionUrl) throws IOException {
        Submission submission = TestUtils.getSubmission(token, submissionUrl);

        String processingStatusesUrl = submission.getLinks().getProcessingStatuses().getHref();

        List<ProcessingStatus> processingStatusList = new ArrayList<>();

        while (processingStatusesUrl != null) {
            HttpResponse response = HttpUtils.httpGet(token, processingStatusesUrl);
            ProcessingStatuses processingStatusesResource = HttpUtils.retrieveResourceFromResponse(
                    response,
                    ProcessingStatuses.class
            );

            processingStatusList.addAll(processingStatusesResource.getContent().getProcessingStatuses());

            if (processingStatusesResource.getLinks().getNext() != null) {
                processingStatusesUrl = processingStatusesResource.getLinks().getNext().getHref();
            }
            else {
                processingStatusesUrl = null;
            }
        }



        return processingStatusList;
    }

    public static void waitForUpdateableSubmissionStatus(String token, String submissionUrl) throws IOException, InterruptedException {

        long maximumIntervalMillis = 50000;
        long startingTimeMillis = System.currentTimeMillis();

        while (System.currentTimeMillis() < startingTimeMillis + maximumIntervalMillis) {
            HttpResponse statusResponse = HttpUtils.httpGet(token,submissionUrl);

            Assert.assertEquals(200, statusResponse.getStatusLine().getStatusCode());
            Submission resource = HttpUtils.retrieveResourceFromResponse(statusResponse, Submission.class);

            boolean submissionIsUpdateable = resource.getLinks().getSubmissionStatusUpdate() != null;

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
        return submissionResource.getLinks().getSubmissionStatus().getHref();
    }


    public static void assertNoErrorsInValidationResult(ValidationResult validationResult) {
        logMessages(validationResult);
        for (Result[] results : validationResult.getExpectedResults().values()) {
            for (Result result : results) {
                Assert.assertNotEquals("Error", result.getValidationStatus());
            }
        }
    }

    public static void logMessages(ValidationResult validationResult){
        for (Map.Entry<String,Result[]> entry : validationResult.getExpectedResults().entrySet()){
            String author = entry.getKey();
            Result[] results = entry.getValue();

            for (Result result : results){
                if (result.getMessage() != null){
                    String message = MessageFormat.format("Author:{0} Status:{1} Message:{2}",author,result.getValidationStatus(),result.getMessage());
                    System.out.println(message);
                }
            }
        }
    }

    public static void waitForFileValidationCompletion(String token, String submissionUrl) throws Exception{
        SubmissionContents submissionContents = TestUtils.getSubmissionContent(token, submissionUrl);

        long maximumIntervalMillis = 30000;
        long startingTimeMillis = System.currentTimeMillis();

        String fileListUrl = submissionContents.getLinks().getFiles().getHref();

        while (System.currentTimeMillis() < startingTimeMillis + maximumIntervalMillis) {
            HttpResponse fileListResponse = HttpUtils.httpGet(token, fileListUrl);
            FileList fileListResource = HttpUtils.retrieveResourceFromResponse(fileListResponse, FileList.class);

            List<SubsFile> files = fileListResource.getContents().getFiles();

            assertEquals(1, files.size());

            SubsFile file = files.get(0);
            if (file.getEmbedded() != null && file.getEmbedded().getValidationResult() != null) {
                ValidationResult vr = file.getEmbedded().getValidationResult();

                boolean validationIsNotPending = !vr.getValidationStatus().equalsIgnoreCase("pending");

                if (validationIsNotPending) {
                    assertEquals("Complete", vr.getValidationStatus());
                    return;
                }
            }
            Thread.sleep(500);
        }
        throw new RuntimeException("Gave up waiting for file validation results on " + fileListUrl);
    }

}

