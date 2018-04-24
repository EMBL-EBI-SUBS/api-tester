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
import uk.ac.ebi.subs.utils.HttpUtils;
import uk.ac.ebi.subs.utils.TestJsonUtils;
import uk.ac.ebi.subs.utils.TestUtils;

import java.text.MessageFormat;
import java.util.Map;

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
    private static final String fileName = "testFile.fastq.gz";
    private static final String fileType = "fastq";

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
        ValidationResult validationResult = TestUtils.getValidationResultForSubmittable(studyUrl, token);
        assertNoErrorsInValidationResult(validationResult);
    }

    @Test
    public void B_addSample() throws Exception {
        String sampleJson = TestJsonUtils.getSampleJson(submissionUrl, sampleAlias);
        String sampleUrl = TestUtils.createSubmittable(token, pm.getSamplesApiBaseUrl(), sampleJson);
        TestUtils.waitForValidationResults(token, sampleUrl);
        ValidationResult validationResult = TestUtils.getValidationResultForSubmittable(sampleUrl, token);
        assertNoErrorsInValidationResult(validationResult);
    }

    @Test
    public void C_addAssay() throws Exception {
        String assayUrl = TestUtils.createAssay(token, pm.getAssaysApiBaseUrl(), submissionUrl, assayAlias, studyAlias, sampleAlias);
        TestUtils.waitForValidationResults(token, assayUrl);
        ValidationResult validationResult = TestUtils.getValidationResultForSubmittable(assayUrl, token);
        assertNoErrorsInValidationResult(validationResult);
    }

    public void D_uploadFile() {

    }


    @Test
    public void E_addAssayData() throws Exception {
        String assayDataUrl = TestUtils.createAssayData(
                token,
                pm.getAssayDataApiBaseUrl(),
                submissionUrl,
                assayDataAlias,
                assayAlias,
                fileName,
                fileType
        );
        TestUtils.waitForValidationResults(token, assayDataUrl);
        ValidationResult validationResult = TestUtils.getValidationResultForSubmittable(assayDataUrl, token);
        assertNoErrorsInValidationResult(validationResult);
    }

    public void F_submit() {

    }

    public void G_checkAccessions() {

    }

    @AfterClass
    public static void tearDown() throws Exception {
        //FIXME, this won't work once the submission is submitted
        HttpUtils.deleteResource(token,submissionUrl);
    }

    private void assertNoErrorsInValidationResult(ValidationResult validationResult) {
        logMessages(validationResult);
        for (Result[] results : validationResult.getExpectedResults().values()) {
            for (Result result : results) {
                Assert.assertNotEquals("Error", result.getValidationStatus());
            }
        }
    }

    private void logMessages(ValidationResult validationResult){
        for (Map.Entry<String,Result[]> entry : validationResult.getExpectedResults().entrySet()){
            String author = entry.getKey();
            Result[] results = entry.getValue();

            for (Result result : results){
                if (result.getMessage() != null){
                    String message = MessageFormat.format("Author:{0} Status:{1} Message:{2}",author,result.getValidationStatus(),result.getMessage());
                    System.out.println(message);
                }
            }
        }
    }
}