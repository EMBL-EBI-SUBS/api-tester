package uk.ac.ebi.subs.core;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
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

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

@Category({TestEnv.class, DevEnv.class})
public class CreateProjectTest {

    private static PropertiesManager pm = PropertiesManager.getInstance();

    private static String token;
    private static String submissionUrl;

    @BeforeClass
    public static void setUp() throws Exception {
        token = TestUtils.getJWTToken(pm.getAuthenticationUrl(), pm.getAapUsername(), pm.getAapPassword());
        submissionUrl = TestUtils.createSubmission(token, pm.getSubmissionsApiTemplatedUrl(), pm.getSubmitterEmail(), pm.getTeamName());

    }

    @Test
    public void givenSubmissionExists_whenAddingProjectToIt_then201IsReceived() throws IOException {
        String content = TestJsonUtils.getProjectJson(TestUtils.getRandomAlias(), "2017-04-17");

        String projectsUrl = TestUtils.submittableCreationUrl("projects",submissionUrl);

        HttpResponse response = HttpUtils.httpPost(token, projectsUrl,content);

        System.out.println(HttpUtils.extractResponseBody(response));

        assertThat(
                response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_CREATED)
        );
    }

    @AfterClass
    public static void tearDown() throws Exception {
        HttpUtils.deleteResource(token, submissionUrl);
    }
}
