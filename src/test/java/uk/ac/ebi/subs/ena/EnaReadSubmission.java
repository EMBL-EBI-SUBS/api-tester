package uk.ac.ebi.subs.ena;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runners.MethodSorters;
import uk.ac.ebi.subs.PropertiesManager;
import uk.ac.ebi.subs.categories.DevEnv;
import uk.ac.ebi.subs.categories.TestEnv;
import uk.ac.ebi.subs.data.objects.ProcessingStatus;
import uk.ac.ebi.subs.data.objects.ValidationResult;
import uk.ac.ebi.subs.utils.TestJsonUtils;
import uk.ac.ebi.subs.utils.TestUtils;
import uk.ac.ebi.subs.utils.UploadUtils;

import java.io.IOException;
import java.util.Collection;

import static org.junit.Assert.assertNotNull;
import static uk.ac.ebi.subs.utils.TestUtils.assertNoErrorsInValidationResult;

@Category({TestEnv.class, DevEnv.class})
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

    @Test
    public void D_uploadFile() throws Exception {
        UploadUtils.uploadFile(token, submissionUrl, fileName);

        TestUtils.waitForFileValidationCompletion(token, submissionUrl);
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

    @Test
    public void F_submit() throws IOException, InterruptedException {
        TestUtils.waitForUpdateableSubmissionStatus(token, submissionUrl);
        TestUtils.changeSubmissionStatusToSubmitted(token, submissionUrl);
    }

    @Test
    public void G_waitForCompleteSubmission() throws IOException, InterruptedException {
        TestUtils.waitForCompletedSubmission(token, submissionUrl);
    }

    @Test
    public void H_checkAccessions() throws IOException, InterruptedException {
        Collection<ProcessingStatus> processingStatuses = TestUtils.fetchProcessingStatuses(token, submissionUrl);

        for (ProcessingStatus processingStatus : processingStatuses) {
            System.out.println(processingStatus);
            assertNotNull(processingStatus.getSubmittableType());
            assertNotNull(processingStatus.getAlias());
            assertNotNull(processingStatus.getStatus());
            assertNotNull(processingStatus.getArchive());
            assertNotNull(processingStatus.getAccession());
        }
    }


}
