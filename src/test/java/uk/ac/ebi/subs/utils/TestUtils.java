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

        Header contentType = new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/hal+json");
        Header accept = new BasicHeader(HttpHeaders.ACCEPT, "application/hal+json");
        Header[] headers = {contentType, accept};
        request.setHeaders(headers);

        StringEntity payload = new StringEntity(TestJsonUtils.getCreateSubmissionJson(submitterEmail, teamName));
        request.setEntity(payload);

        HttpClientBuilder.create().build().execute(request);
    }

    public static String getFirstSubmissionUrlForTeam(String teamName) throws IOException {
        HttpUriRequest request = new HttpGet("http://submission-dev.ebi.ac.uk/api/submissions/search/by-team?teamName=" + teamName);
        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        WrapperObject resource = TestUtils.retrieveResourceFromResponse(response, WrapperObject.class);
        return resource.getFirstSubmissionUrl();
    }

    public static String getFirstSampleUrlForTeam(String teamName) throws IOException {
        HttpUriRequest request = new HttpGet("http://submission-dev.ebi.ac.uk/api/samples/search/by-team?teamName=" + teamName);
        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        WrapperObject resource = TestUtils.retrieveResourceFromResponse(response, WrapperObject.class);
        return resource.getFirstSampleUrl();
    }
}
