package uk.ac.ebi.subs.ena;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runners.MethodSorters;
import uk.ac.ebi.subs.PropertiesManager;
import uk.ac.ebi.subs.categories.DevEnv;
import uk.ac.ebi.subs.data.objects.ValidationResult;
import uk.ac.ebi.subs.data.structures.Result;
import uk.ac.ebi.subs.utils.TestUtils;

@Category({DevEnv.class})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EnaReadSubmission {

    private static PropertiesManager pm = PropertiesManager.getInstance();
    private static String token;
    private static String submissionUrl;

    private static final String projectAlias = TestUtils.getRandomAlias();
    private static final String studyAlias = TestUtils.getRandomAlias();
    private static final String sampleAlias = TestUtils.getRandomAlias();
    private static final String assayAlias = TestUtils.getRandomAlias();
    private static final String assayDataAlias = TestUtils.getRandomAlias();


    @BeforeClass
    public static void setUp() throws Exception {
        token = TestUtils.getJWTToken(pm.getAuthenticationUrl(), pm.getAapUsername(), pm.getAapPassword());
        submissionUrl = TestUtils.createSubmission(token, pm.getSubmissionsApiTemplatedUrl(), pm.getSubmitterEmail(), pm.getTeamName());
        TestUtils.createProject(token, pm.getProjectsApiBaseUrl(), submissionUrl, projectAlias);
    }

    @Test
    public void A_addStudy() throws Exception {
        String studyUrl = TestUtils.createStudy(token, pm.getStudiesApiBaseUrl(), submissionUrl, studyAlias, projectAlias, pm.getTeamName());
        TestUtils.waitForValidationResults(token, studyUrl);
        ValidationResult validationResult = TestUtils.getValidationResultForSubmittable(studyUrl,token);
        for(Result[] results : validationResult.getExpectedResults().values()){
            for (Result result : results) {
                Assert.assertNotEquals("Failed",result.getValidationStatus());
            }
        }
    }

    @Test
    public void B_addSample() throws Exception {
        String sampleUrl = TestUtils.createSample(token, pm.getSamplesApiBaseUrl(), submissionUrl, sampleAlias);
        TestUtils.waitForValidationResults(token, sampleUrl);
        ValidationResult validationResult = TestUtils.getValidationResultForSubmittable(sampleUrl,token);
        for(Result[] results : validationResult.getExpectedResults().values()){
            for (Result result : results) {
                Assert.assertNotEquals("Failed",result.getValidationStatus());
            }
        }
    }

    @Test
    public void C_addAssay() throws Exception {
        String assayUrl = TestUtils.createAssay(token, pm.getAssaysApiBaseUrl(), submissionUrl, assayAlias, studyAlias, sampleAlias);
        TestUtils.waitForValidationResults(token, assayUrl);
        ValidationResult validationResult = TestUtils.getValidationResultForSubmittable(assayUrl,token);
        for(Result[] results : validationResult.getExpectedResults().values()){
            for (Result result : results) {
                Assert.assertNotEquals("Failed",result.getValidationStatus());
            }
        }
    }

    public void D_uploadFile() {

    }

    
    public void E_addAssayData() {

    }

    public void F_submit() {

    }

    public void G_checkAccessions() {

    }

    @AfterClass
    public static void tearDown() throws Exception {
        //FIXME, this won't work once the submission is submitted
    }
}
