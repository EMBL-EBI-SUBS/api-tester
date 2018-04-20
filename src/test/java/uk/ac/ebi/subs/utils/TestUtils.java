package uk.ac.ebi.subs.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import uk.ac.ebi.subs.data.objects.Submission;
import uk.ac.ebi.subs.data.objects.SubmissionStatus;
import uk.ac.ebi.subs.data.objects.SubmittableTemplate;
import uk.ac.ebi.subs.data.objects.ValidationResult;
import uk.ac.ebi.subs.data.structures.ValidationResultStatusAndLink;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Random;

public class TestUtils {

    public static String extractResponseBody(HttpResponse response){
        String body = null;
        try {
            body = IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8.name());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return body;
    }

    public static <T> T retrieveResourceFromResponse(HttpResponse response, Class<T> clazz) throws IOException {

        String jsonFromResponse = EntityUtils.toString(response.getEntity());
        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        T object = mapper.readValue(jsonFromResponse, clazz);
        return object;
    }

    public static String createSubmission(String token, String submissionsApiTemplatedUrl, String submitterEmail, String teamName) throws IOException {
        String submissionApiUrl = submissionsApiTemplatedUrl.replace("{teamName}",teamName);

        HttpPost request = new HttpPost(submissionApiUrl);
        request.setHeaders(getContentTypeAcceptAndTokenHeaders(token));

        StringEntity payload = new StringEntity(TestJsonUtils.getSubmissionJson(submitterEmail, teamName));
        request.setEntity(payload);

        HttpResponse response = HttpClientBuilder.create().build().execute(request);

        Submission resource = retrieveResourceFromResponse(response, Submission.class);
        String selfHref = resource.get_links().getSelf().getHref();
        return selfHref.replace("{?projection}","");
    }

    public static String createSample(String token, String samplesApiBaseUrl, String submissionUrl, String alias) throws IOException {
        HttpPost request = new HttpPost(samplesApiBaseUrl);
        request.setHeaders(TestUtils.getContentTypeAcceptAndTokenHeaders(token));

        StringEntity payload = new StringEntity(TestJsonUtils.getCreateSampleJson(submissionUrl, alias));
        request.setEntity(payload);

        HttpResponse response =  HttpClientBuilder.create().build().execute(request);
        SubmittableTemplate resource = TestUtils.retrieveResourceFromResponse(response, SubmittableTemplate.class);
        return resource.get_links().getSelf().getHref();
    }

    public static String createSample(String token, String samplesApiBaseUrl, StringEntity payload) throws IOException {
        HttpPost request = new HttpPost(samplesApiBaseUrl);
        request.setHeaders(TestUtils.getContentTypeAcceptAndTokenHeaders(token));
        request.setEntity(payload);

        HttpResponse response =  HttpClientBuilder.create().build().execute(request);
        SubmittableTemplate resource = TestUtils.retrieveResourceFromResponse(response, SubmittableTemplate.class);
        return resource.get_links().getSelf().getHref();
    }

    public static String createProject(String token, String projectsApiBaseUrl, String submissionUrl, String projectAlias) throws IOException {
        return createProject(token, projectsApiBaseUrl, submissionUrl, projectAlias, LocalDateTime.now().toString());
    }

    public static String createProject(String token, String projectsApiBaseUrl, String submissionUrl, String projectAlias, String releaseDate) throws IOException {
        HttpPost request = new HttpPost(projectsApiBaseUrl);
        request.setHeaders(TestUtils.getContentTypeAcceptAndTokenHeaders(token));

        StringEntity payload = new StringEntity(TestJsonUtils.getProjectJson(submissionUrl, projectAlias, releaseDate));
        request.setEntity(payload);

        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        SubmittableTemplate resource = TestUtils.retrieveResourceFromResponse(response, SubmittableTemplate.class);

        return resource.get_links().getSelf().getHref();
    }

    public static String createStudy(String token, String studiesApiBaseUrl, String submissionUrl, String studyAlias, String projectAlias, String teamName) throws IOException {
        return createStudy(token, studiesApiBaseUrl, submissionUrl, studyAlias, projectAlias, LocalDateTime.now().toString(),teamName);
    }

    public static String createStudy(String token, String studiesApiBaseUrl, String submissionUrl, String studyAlias, String projectAlias, String releaseDate, String teamName) throws IOException {
        HttpPost request = new HttpPost(studiesApiBaseUrl);
        request.setHeaders(TestUtils.getContentTypeAcceptAndTokenHeaders(token));

        StringEntity payload = new StringEntity(
                TestJsonUtils.getStudyJson(
                        submissionUrl,
                        studyAlias,
                        projectAlias,
                        releaseDate,
                        teamName
                )
        );
        request.setEntity(payload);

        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        SubmittableTemplate resource = TestUtils.retrieveResourceFromResponse(response, SubmittableTemplate.class);

        return resource.get_links().getSelf().getHref();
    }

    public static String[] createNSubmissions(int n, String token, String submissionsApiBaseUrl, String submitterEmail, String teamName) throws IOException {
        String[] urls = new String[n];
        for (int i = 0; i < n; i++) {
            urls[i] = createSubmission(token, submissionsApiBaseUrl, submitterEmail, teamName);
        }
        return urls;
    }

    public static Header[] getContentTypeAcceptAndTokenHeaders(String jwtToken) {
        Header contentType = new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/hal+json");
        Header accept = new BasicHeader(HttpHeaders.ACCEPT, "application/hal+json");
        Header token = new BasicHeader(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken);
        Header[] headers = {contentType, accept, token};
        return headers;
    }

    public static String getJWTToken (String authUrl, String username, String password) throws IOException {
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

    public static String getIdFromUrl(String url) {
        String[] array = url.split("/");
        return array[array.length - 1];
    }

    public static String getRandomAlias() {
        Random random = new Random();
        String digit = String.format("%06d", random.nextInt(1000000));
        return "alias-" + digit;
    }

    public static String getValidationStatus(String validationResultsUrl, String token) throws IOException {
        HttpUriRequest request = new HttpGet(validationResultsUrl);
        request.setHeaders(TestUtils.getContentTypeAcceptAndTokenHeaders(token));

        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        ValidationResult resource = TestUtils.retrieveResourceFromResponse(response, ValidationResult.class);

        return resource.getValidationStatus();
    }

    public static ValidationResultStatusAndLink getValidationResultStatusAndLinkFromStudy(
            String studyValidationResultsUrl, String token) throws IOException {
        HttpUriRequest createStudyRequest = new HttpGet(studyValidationResultsUrl);
        createStudyRequest.setHeaders(TestUtils.getContentTypeAcceptAndTokenHeaders(token));

        HttpResponse CreateStudyResponse = HttpClientBuilder.create().build().execute(createStudyRequest);

        return TestUtils.retrieveResourceFromResponse(CreateStudyResponse, ValidationResultStatusAndLink.class);
    }

    public static ValidationResult getValidationResultFromValidationResultStatus(String validationResultURI, String token)
            throws IOException {
        HttpUriRequest validationResultRequest = new HttpGet(validationResultURI);
        validationResultRequest.setHeaders(TestUtils.getContentTypeAcceptAndTokenHeaders(token));
        HttpResponse validationResultResponse = HttpClientBuilder.create().build().execute(validationResultRequest);

        return TestUtils.retrieveResourceFromResponse(validationResultResponse, ValidationResult.class);
    }

    public static void waitForValidationResults(String submittableUrl, String token ) throws IOException, InterruptedException {

        long maximumIntervalMillis = 5000;
        long startingTimeMillis = System.currentTimeMillis();

        while (System.currentTimeMillis() < startingTimeMillis + maximumIntervalMillis) {
            SubmittableTemplate resource = getSubmittableTemplate(submittableUrl, token);

            boolean validationIsNotPending = !resource.get_embedded().getValidationResult()
                    .getValidationStatus().equalsIgnoreCase("pending");

            if (validationIsNotPending){

                return;
            }
            Thread.sleep(500);
        }

        throw new RuntimeException("Gave up waiting for validation results on "+submittableUrl);
    }

    public static void waitForCompletedSubmittable(String submittableUrl, String token ) throws IOException, InterruptedException {

        long maximumIntervalMillis = 50000;
        long startingTimeMillis = System.currentTimeMillis();

        while (System.currentTimeMillis() < startingTimeMillis + maximumIntervalMillis) {
            SubmittableTemplate resource = getSubmittableTemplate(submittableUrl, token);

            boolean submittableIsCompleted = resource.get_embedded().getProcessingStatus().getStatus().equalsIgnoreCase("completed");

            if (submittableIsCompleted){

                return;
            }
            Thread.sleep(500);
        }

        throw new RuntimeException("Gave up waiting for validation results on "+submittableUrl);
    }

    public static SubmittableTemplate getSubmittableTemplate(String submittableUrl, String token) throws IOException {
        HttpUriRequest submittableRequest = new HttpGet(submittableUrl);
        submittableRequest.setHeaders(TestUtils.getContentTypeAcceptAndTokenHeaders(token));
        HttpResponse submittableResponse = HttpClientBuilder.create().build().execute(submittableRequest);

        Assert.assertEquals(200, submittableResponse.getStatusLine().getStatusCode());
        return TestUtils.retrieveResourceFromResponse(submittableResponse,SubmittableTemplate.class);
    }

    public static void waitForCompletedSubmission(String submissionUrl, String token ) throws IOException, InterruptedException {

        long maximumIntervalMillis = 50000;
        long startingTimeMillis = System.currentTimeMillis();

        String submissionStatusUrl = getStatusUrlForSubmission(submissionUrl, token);

        while (System.currentTimeMillis() < startingTimeMillis + maximumIntervalMillis) {
            HttpUriRequest statusRequest = new HttpGet(submissionStatusUrl);
            statusRequest.setHeaders(TestUtils.getContentTypeAcceptAndTokenHeaders(token));
            HttpResponse statusResponse = HttpClientBuilder.create().build().execute(statusRequest);

            Assert.assertEquals(200, statusResponse.getStatusLine().getStatusCode());
            SubmissionStatus resource = TestUtils.retrieveResourceFromResponse(statusResponse,SubmissionStatus.class);

            boolean submissionIsCompleted= resource.getStatus().equalsIgnoreCase("completed");

            if (submissionIsCompleted){

                return;
            }
            Thread.sleep(500);
        }

        throw new RuntimeException("Gave up waiting for submission to be completed "+submissionUrl);
    }

    public static void waitForUpdateableSubmission(String submissionUrl, String token ) throws IOException, InterruptedException {

        long maximumIntervalMillis = 50000;
        long startingTimeMillis = System.currentTimeMillis();

        String submissionStatusUrl = getStatusUrlForSubmission(submissionUrl, token);

        while (System.currentTimeMillis() < startingTimeMillis + maximumIntervalMillis) {
            HttpUriRequest statusRequest = new HttpGet(submissionStatusUrl);
            statusRequest.setHeaders(TestUtils.getContentTypeAcceptAndTokenHeaders(token));
            HttpResponse statusResponse = HttpClientBuilder.create().build().execute(statusRequest);

            Assert.assertEquals(200, statusResponse.getStatusLine().getStatusCode());
            SubmissionStatus resource = TestUtils.retrieveResourceFromResponse(statusResponse,SubmissionStatus.class);

            boolean submissionIsUpdateable = resource.getStatusUpdateUrl() != null;


            if (submissionIsUpdateable){

                return;
            }
            Thread.sleep(500);
        }

        throw new RuntimeException("Gave up waiting for submission to be updateable "+submissionUrl);
    }

    public static String getStatusUrlForSubmission(String submissionUrl, String token) throws IOException {
        HttpUriRequest submissionRequest = new HttpGet(submissionUrl);
        submissionRequest.setHeaders(TestUtils.getContentTypeAcceptAndTokenHeaders(token));
        HttpResponse submissionResponse = HttpClientBuilder.create().build().execute(submissionRequest);

        Assert.assertEquals(200, submissionResponse.getStatusLine().getStatusCode());
        Submission submissionResource = TestUtils.retrieveResourceFromResponse(submissionResponse,Submission.class);
        return submissionResource.get_links().getSubmissionStatus().getHref();
    }

}

class CouldNotGetTokenException extends RuntimeException {

    public CouldNotGetTokenException(String message) {
        super(message);
    }
}