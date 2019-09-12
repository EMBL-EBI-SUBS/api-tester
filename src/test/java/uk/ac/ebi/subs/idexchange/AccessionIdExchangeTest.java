package uk.ac.ebi.subs.idexchange;

import org.apache.http.HttpResponse;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.runners.MethodSorters;
import uk.ac.ebi.subs.PropertiesManager;
import uk.ac.ebi.subs.categories.DevEnv;
import uk.ac.ebi.subs.data.objects.Sample;
import uk.ac.ebi.subs.data.objects.Study;
import uk.ac.ebi.subs.utils.HttpUtils;
import uk.ac.ebi.subs.utils.TestUtils;

import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static uk.ac.ebi.subs.utils.SubmissionOperations.addSample;
import static uk.ac.ebi.subs.utils.SubmissionOperations.checkAccessions;
import static uk.ac.ebi.subs.utils.SubmissionOperations.getAccessionIdsBySubmittable;
import static uk.ac.ebi.subs.utils.TestUtils.MAXIMUM_INTERVAL_MILLIS;

@RunWith(JUnit4.class)
@Category({DevEnv.class})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AccessionIdExchangeTest {

    private static PropertiesManager pm = PropertiesManager.getInstance();
    private static String token;
    private static String submissionUrl;

    private static final String projectAlias = TestUtils.getRandomAlias();
    private static final String sampleAlias = TestUtils.getRandomAlias();

    @BeforeClass
    public static void setUp() throws Exception {
        token = TestUtils.getJWTToken(pm.getAuthenticationUrl(), pm.getAapUsername(), pm.getAapPassword());
        submissionUrl = TestUtils.createSubmission(token, pm.getSubmissionsApiTemplatedUrl(), pm.getSubmitterEmail(), pm.getTeamName());
        TestUtils.createProject(token, submissionUrl, projectAlias);
    }

    @Test
    public void A_addSample() throws Exception {
        addSample(submissionUrl, sampleAlias, token, pm);
        addSample(submissionUrl, sampleAlias + "_2", token, pm);
    }

    @Test
    public void B_submit() throws IOException, InterruptedException {
        TestUtils.waitForUpdateableSubmissionStatus(token, submissionUrl);
        TestUtils.changeSubmissionStatusToSubmitted(token, submissionUrl);
    }

    @Test
    public void C_waitForCompleteSubmission() throws IOException, InterruptedException {
        TestUtils.waitForCompletedSubmission(token, submissionUrl, TestUtils.MAXIMUM_INTERVAL_MILLIS);
    }

    @Test
    public void D_checkAccessions() throws IOException {
        checkAccessions(submissionUrl, token);
    }

    @Test
    public void E_checkAccessionIDExchangeInBioSamples() throws IOException, InterruptedException {
        Sample sampleResource = getSampleResource();

        final String bioStudiesProjectUrl = sampleResource.getExternalReferences()[0].getUrl();

        assertFalse(bioStudiesProjectUrl.isEmpty());

        HttpResponse bioStudiesResponse = HttpUtils.httpGet(null, bioStudiesProjectUrl);
        Assert.assertEquals(200, bioStudiesResponse.getStatusLine().getStatusCode());
    }

    @Test
    public void F_checkAccessionIDExchangeInBioStudies() throws IOException, InterruptedException {
        Study studyResource = getStudyResource();

        final String bioSampleAccessionId = studyResource.getSection().getLinks()[0].getUrl();

        assertFalse(bioSampleAccessionId.isEmpty());

        String bioSampleUrl = String.join("/", pm.getBioSampleJsonUrl(), bioSampleAccessionId);
        HttpResponse bioSamplesResponse = HttpUtils.httpGet(null, bioSampleUrl);
        Assert.assertEquals(200, bioSamplesResponse.getStatusLine().getStatusCode());
    }

    private Study getStudyResource() throws IOException, InterruptedException {
        long startingTimeMillis = System.currentTimeMillis();
        String archivedProjectAccessionId = getAccessionIdsBySubmittable("Project", submissionUrl, token).get(0);
        final String bioStudiesResourceUrl = String.join("/", pm.getBioStudiesJsonUrl(), archivedProjectAccessionId, archivedProjectAccessionId + ".json");

        while (System.currentTimeMillis() < startingTimeMillis + MAXIMUM_INTERVAL_MILLIS * 5) {
            Study studyResource = (Study) retrieveResource(bioStudiesResourceUrl, Study.class);

            if (studyResource == null || studyResource.getSection().getLinks() == null) {
                Thread.sleep(500);
            } else {
                return studyResource;
            }
        }

        throw new RuntimeException("Gave up waiting for study resource: " + bioStudiesResourceUrl);
    }

    private Sample getSampleResource() throws IOException, InterruptedException {
        long startingTimeMillis = System.currentTimeMillis();
        String archivedSampleAccessionId = getAccessionIdsBySubmittable("Sample", submissionUrl, token).get(0);
        final String sampleResourceUrl = String.join("/", pm.getBioSampleJsonUrl(), archivedSampleAccessionId + ".json");

        while (System.currentTimeMillis() < startingTimeMillis + MAXIMUM_INTERVAL_MILLIS) {
            Sample sampleResource = (Sample) retrieveResource(sampleResourceUrl, Sample.class);

            if (sampleResource == null || sampleResource.getExternalReferences() == null) {
                Thread.sleep(500);
            } else {
                return sampleResource;
            }
        }

        throw new RuntimeException("Gave up waiting for sample resource: " + sampleResourceUrl);
    }

    private Object retrieveResource(String resourceUrl, Class resourceClazz) throws IOException, InterruptedException {
        HttpResponse resourceJson = HttpUtils.httpGet(token, resourceUrl);

        if (resourceJson.getStatusLine().getStatusCode() == 404) {
            Thread.sleep(500);
            return null;
        }
        Assert.assertEquals(200, resourceJson.getStatusLine().getStatusCode());
        return HttpUtils.retrieveResourceFromResponse(resourceJson, resourceClazz);
    }
}
