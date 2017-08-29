package uk.ac.ebi.subs.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import uk.ac.ebi.subs.data.structures.WrapperObject;

import java.io.IOException;

public class TestUtils {

    public static <T> T retrieveResourceFromResponse(HttpResponse response, Class<T> clazz) throws IOException {

        String jsonFromResponse = EntityUtils.toString(response.getEntity());
        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return mapper.readValue(jsonFromResponse, clazz);
    }

    public static void createSubmission(String submissionsApiBaseUrl, String submitterEmail, String teamName) throws IOException {
        HttpPost request = new HttpPost(submissionsApiBaseUrl);
        request.setHeaders(getContentTypeAndAcceptHeaders());

        StringEntity payload = new StringEntity(TestJsonUtils.getSubmissionJson(submitterEmail, teamName));
        request.setEntity(payload);

        HttpClientBuilder.create().build().execute(request);
    }

    public static void createSample(String samplesApiBaseUrl, String submissionUrl) throws IOException {
        HttpPost request = new HttpPost(samplesApiBaseUrl);
        request.setHeaders(TestUtils.getContentTypeAndAcceptHeaders());

        StringEntity payload = new StringEntity(TestJsonUtils.getCreateSampleJson(submissionUrl));
        request.setEntity(payload);

        HttpClientBuilder.create().build().execute(request);
    }

    public static void createNSubmissions(int n, String submissionsApiBaseUrl, String submitterEmail, String teamName) throws IOException {
        for (int i = 0; i < n; i++) {
            createSubmission(submissionsApiBaseUrl, submitterEmail, teamName);
        }
    }

    public static String getFirstSubmissionUrlForTeam(String teamName) throws IOException {
        HttpUriRequest request = new HttpGet("http://submission-dev.ebi.ac.uk/api/submissions/search/by-team?teamName=" + teamName);
        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        WrapperObject resource = TestUtils.retrieveResourceFromResponse(response, WrapperObject.class);
        return resource.getFirstSubmissionUrl();
    }

    public static String[] getNSubmissionsUrlsForTeam(String teamName, int n) throws IOException {
        HttpUriRequest request = new HttpGet("http://submission-dev.ebi.ac.uk/api/submissions/search/by-team?teamName=" + teamName);
        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        WrapperObject resource = TestUtils.retrieveResourceFromResponse(response, WrapperObject.class);
        return resource.getNSubmissionsUrls(n);
    }

    public static String getFirstSampleUrlForTeam(String teamName) throws IOException {
        HttpUriRequest request = new HttpGet("http://submission-dev.ebi.ac.uk/api/samples/search/by-team?teamName=" + teamName);
        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        WrapperObject resource = TestUtils.retrieveResourceFromResponse(response, WrapperObject.class);
        return resource.getFirstSampleUrl();
    }

    public static Header[] getContentTypeAndAcceptHeaders() {
        Header contentType = new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/hal+json");
        Header accept = new BasicHeader(HttpHeaders.ACCEPT, "application/hal+json");
        Header[] headers = {contentType, accept};
        return headers;
    }

    public static String getIdFromUrl(String url) {
        String[] array = url.split("/");
        return array[array.length - 1];
    }
}
