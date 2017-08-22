package uk.ac.ebi.subs;

import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.IOException;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SampleTests {

    static String submitterEmail = "api-tester@ebi.ac.uk";
    static String teamName = "api-tester";

    static String submissionsApiBaseUrl = "http://submission-dev.ebi.ac.uk/api/submissions/";
    static String submissionUrl = "";

    String samplesApiBaseUrl = "http://submission-dev.ebi.ac.uk/api/samples/";
    static String sampleUrl = "";

    @BeforeClass
    public static void setUp() throws Exception {
        TestUtils.createSubmission(submissionsApiBaseUrl, submitterEmail, teamName);
        submissionUrl = TestUtils.getFirstSubmissionUrlForTeam(teamName);
    }

    @Test
    public void a_givenSubmissionExists_whenAddingSampleToIt_then201IsReceived() throws IOException {
        HttpPost request = new HttpPost(samplesApiBaseUrl);

        Header contentType = new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/hal+json");
        Header accept = new BasicHeader(HttpHeaders.ACCEPT, "application/hal+json");
        Header[] headers = {contentType, accept};
        request.setHeaders(headers);

        StringEntity payload = new StringEntity("{\n" +
                "  \"alias\" : \"D1\",\n" +
                //"  \"archive\" : \"BioSamples\",\n" +
                "  \"title\" : \"NA12878_D1\",\n" +
                "  \"description\" : \"Material derived from cell line NA12878\",\n" +
                "  \"attributes\" : [ {\n" +
                "    \"name\" : \"Cell line type\",\n" +
                "    \"value\" : \"EBV-LCL cell line\",\n" +
                "    \"terms\" : [ {\n" +
                "      \"url\" : \"http://purl.obolibrary.org/obo/BTO_0003335\"\n" +
                "    } ]\n" +
                "  } ],\n" +
                "  \"sampleRelationships\" : [ {\n" +
                "    \"accession\" : \"SAME123392\",\n" +
                "    \"relationshipNature\" : \"Derived from\"\n" +
                "  } ],\n" +
                "  \"taxonId\" : 9606,\n" +
                "  \"taxon\" : \"Homo sapiens\",\n" +
                "  \"submission\" : \"" + submissionUrl + "\"\n" +
                "}");
        request.setEntity(payload);

        HttpResponse response = HttpClientBuilder.create().build().execute(request);

        assertThat(
                response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_CREATED)
        );

        sampleUrl = TestUtils.getFirstSampleUrlForTeam(teamName);
    }

    @Test
    public void b_givenSampleExists_whenUpdatingIt_then200IsReceived() throws IOException {
        HttpPut request = new HttpPut(sampleUrl);

        Header contentType = new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/hal+json");
        Header accept = new BasicHeader(HttpHeaders.ACCEPT, "application/hal+json");
        Header[] headers = {contentType, accept};
        request.setHeaders(headers);

        StringEntity payload = new StringEntity("{\n" +
                "  \"alias\" : \"D1\",\n" +
                //"  \"archive\" : \"BioSamples\",\n" +
                "  \"title\" : \"NA12878_D1\",\n" +
                "  \"description\" : \"Material derived from cell line NA12878\",\n" +
                "  \"attributes\" : [ {\n" +
                "    \"name\" : \"Cell line type\",\n" +
                "    \"value\" : \"EBV-LCL cell line\",\n" +
                "    \"terms\" : [ {\n" +
                "      \"url\" : \"http://purl.obolibrary.org/obo/BTO_0003335\"\n" +
                "    } ]\n" +
                "  } ],\n" +
                "  \"sampleRelationships\" : [ {\n" +
                "    \"accession\" : \"SAME123392\",\n" +
                "    \"relationshipNature\" : \"Derived from\"\n" +
                "  }, {\n" +
                "    \"alias\" : \"D0\",\n" +
                "    \"team\" : \"my-team\",\n" +
                "    \"relationshipNature\" : \"Child of\"\n" +
                "  } ],\n" +
                "  \"taxonId\" : 9606,\n" +
                "  \"taxon\" : \"Homo sapiens\",\n" +
                "  \"submission\" : \"" + submissionUrl + "\"\n" +
                "}");
        request.setEntity(payload);

        HttpResponse response = HttpClientBuilder.create().build().execute(request);

        assertThat(
                response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK)
        );
    }

    @AfterClass
    public static void tearDown() throws Exception {
        HttpDelete request = new HttpDelete(submissionUrl);
        HttpResponse response = HttpClientBuilder.create().build().execute(request);

        assertThat(
                response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_NO_CONTENT)
        );
    }
}
