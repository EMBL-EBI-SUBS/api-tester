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
import uk.ac.ebi.subs.utils.HttpUtils;
import uk.ac.ebi.subs.utils.TestJsonUtils;
import uk.ac.ebi.subs.utils.TestUtils;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

@Category({TestEnv.class, DevEnv.class})
public class UpdateProjectTests {

    private static PropertiesManager pm = PropertiesManager.getInstance();

    private static String token;
    private static String submissionUrl;
    private static String projectUrl;

    private static String projectAlias = TestUtils.getRandomAlias();

    @BeforeClass
    public static void setUp() throws Exception {
        token = TestUtils.getJWTToken(pm.getAuthenticationUrl(), pm.getAapUsername(), pm.getAapPassword());
        submissionUrl = TestUtils.createSubmission(token, pm.getSubmissionsApiTemplatedUrl(), pm.getSubmitterEmail(), pm.getTeamName());
        projectUrl = TestUtils.createProject(token, submissionUrl, projectAlias);
    }

    @Test
    public void givenProjectExists_whenUpdatingIt_then200IsReceived() throws IOException {

        HttpPut request = new HttpPut(projectUrl);
        request.setHeaders(HttpUtils.getContentTypeAcceptAndTokenHeaders(token));

        StringEntity payload = new StringEntity(TestJsonUtils.getProjectJson(projectAlias, "2017-04-17"));
        request.setEntity(payload);

        HttpResponse response = HttpClientBuilder.create().build().execute(request);

        System.out.println(HttpUtils.extractResponseBody(response));

        assertThat(
                response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK)
        );
    }


    @AfterClass
    public static void tearDown() throws Exception {
        HttpUtils.deleteResource(token, submissionUrl);
    }
}
