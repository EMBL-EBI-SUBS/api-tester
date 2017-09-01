package uk.ac.ebi.subs.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import uk.ac.ebi.subs.data.objects.Sample;
import uk.ac.ebi.subs.data.objects.Submission;

import java.io.IOException;
import java.util.Random;

public class TestUtils {

    public static <T> T retrieveResourceFromResponse(HttpResponse response, Class<T> clazz) throws IOException {

        String jsonFromResponse = EntityUtils.toString(response.getEntity());
        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return mapper.readValue(jsonFromResponse, clazz);
    }

    public static String createSubmission(String submissionsApiBaseUrl, String submitterEmail, String teamName) throws IOException {
        HttpPost request = new HttpPost(submissionsApiBaseUrl);
        request.setHeaders(getContentTypeAndAcceptHeaders());

        StringEntity payload = new StringEntity(TestJsonUtils.getSubmissionJson(submitterEmail, teamName));
        request.setEntity(payload);

        HttpResponse response = HttpClientBuilder.create().build().execute(request);

        Submission resource = retrieveResourceFromResponse(response, Submission.class);
        return resource.get_links().getSelf().getHref();
    }

    public static String createSample(String samplesApiBaseUrl, String submissionUrl, String alias) throws IOException {
        HttpPost request = new HttpPost(samplesApiBaseUrl);
        request.setHeaders(TestUtils.getContentTypeAndAcceptHeaders());

        StringEntity payload = new StringEntity(TestJsonUtils.getCreateSampleJson(submissionUrl, alias));
        request.setEntity(payload);

        HttpResponse response =  HttpClientBuilder.create().build().execute(request);
        Sample resource = TestUtils.retrieveResourceFromResponse(response, Sample.class);
        return resource.get_links().getSelf().getHref();
    }

    public static String[] createNSubmissions(int n, String submissionsApiBaseUrl, String submitterEmail, String teamName) throws IOException {
        String[] urls = new String[n];
        for (int i = 0; i < n; i++) {
            urls[i] = createSubmission(submissionsApiBaseUrl, submitterEmail, teamName);
        }
        return urls;
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

    public static String getRandomAlias() {
        Random random = new Random();
        String digit = String.format("%04d", random.nextInt(10000));
        return "alias-" + digit;
    }
}
