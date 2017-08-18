package uk.ac.ebi.subs;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Test;

import java.io.IOException;
import java.util.UUID;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class SubmissionTest {

    String apiBaseUrl = "http://submission-dev.ebi.ac.uk/api/";

    @Test
    public void givenSubmissionDoesNotExists_whenSubmissionStatusIsRetrieved_then404IsReceived() throws IOException {

        String submissionId = UUID.randomUUID().toString();
        HttpUriRequest request = new HttpGet( apiBaseUrl + "submissionStatuses/" + submissionId);

        HttpResponse response = HttpClientBuilder.create().build().execute(request);

        assertThat(
                response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_NOT_FOUND)
        );
    }
}
