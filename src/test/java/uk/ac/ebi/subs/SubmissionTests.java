package uk.ac.ebi.subs;

import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.junit.Test;
import uk.ac.ebi.subs.data.SubmissionResource;

import java.io.IOException;
import java.util.UUID;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class SubmissionTests {

    String apiBaseUrl = "http://submission-dev.ebi.ac.uk/api/";
    String submissions = "submissions/";
    String submissionId = "a0cc6017-84d9-4d2b-9b75-7302036cf748";

    //@Test - Temp to avoid repetitive submission creation
    public void whenSubmissionIsCreated_then201IsReceived () throws IOException {

        HttpPost request = new HttpPost( apiBaseUrl + submissions);

        Header contentType = new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/hal+json");
        Header accept = new BasicHeader(HttpHeaders.ACCEPT, "application/hal+json");
        Header[] headers = {contentType, accept};
        request.setHeaders(headers);

        StringEntity payload = new StringEntity("{\n" +
                "  \"submitter\" : {\n" +
                "    \"email\" : \"test@api-tester\"\n" +
                "  },\n" +
                "  \"team\" : {\n" +
                "    \"name\" : \"api-tester\"\n" +
                "  }\n" +
                "}");
        request.setEntity(payload);

        HttpResponse response = HttpClientBuilder.create().build().execute(request);

        assertThat(
                response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_CREATED)
        );
    }

    @Test
    public void givenSubmissionExists_whenSubmissionIsRetrieved_then200IsReceived() throws IOException {

        HttpUriRequest request = new HttpGet( apiBaseUrl + submissions + submissionId);

        HttpResponse response = HttpClientBuilder.create().build().execute(request);

        assertThat(
                response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK)
        );
    }

    @Test
    public void givenRequestWithNoAcceptHeader_whenRequestIsExecuted_thenDefaultResponseContentTypeIsHalJson() throws IOException {

        String halJsonMimeType = "application/hal+json";
        HttpUriRequest request = new HttpGet(apiBaseUrl + submissions + submissionId);

        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        String mimeType = ContentType.getOrDefault(response.getEntity()).getMimeType();

        assertThat(
                halJsonMimeType, equalTo(mimeType)
        );
    }

    @Test
    public void givenSubmissionExists_whenSubmissionIsRetrieved_thenRetrievedResourceIsCorrect() throws IOException {

        HttpUriRequest request = new HttpGet( apiBaseUrl + submissions + submissionId);

        HttpResponse response = HttpClientBuilder.create().build().execute(request);

        SubmissionResource resource = RetrieveUtil.retrieveResourceFromResponse(response, SubmissionResource.class);

        assertThat(
                "test@api-tester", equalTo(resource.getSubmitter().getEmail())
        );
    }

    @Test
    public void givenSubmissionDoesNotExists_whenSubmissionStatusIsRetrieved_then404IsReceived() throws IOException {

        String randomSubmissionId = UUID.randomUUID().toString();
        HttpUriRequest request = new HttpGet( apiBaseUrl + "submissionStatuses/" + randomSubmissionId);

        HttpResponse response = HttpClientBuilder.create().build().execute(request);

        assertThat(
                response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_NOT_FOUND)
        );
    }
}
