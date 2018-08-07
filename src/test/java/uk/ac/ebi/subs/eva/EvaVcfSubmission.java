package uk.ac.ebi.subs.eva;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runners.MethodSorters;
import uk.ac.ebi.subs.PropertiesManager;
import uk.ac.ebi.subs.categories.DevEnv;
import uk.ac.ebi.subs.data.objects.ProcessingStatus;
import uk.ac.ebi.subs.data.objects.ValidationResult;
import uk.ac.ebi.subs.utils.TestJsonUtils;
import uk.ac.ebi.subs.utils.TestUtils;
import uk.ac.ebi.subs.utils.UploadUtils;

import java.io.IOException;
import java.util.Collection;

import static org.junit.Assert.assertNotNull;
import static uk.ac.ebi.subs.utils.TestUtils.assertNoErrorsInValidationResult;

@Category({DevEnv.class})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EvaVcfSubmission {

    private static PropertiesManager pm = PropertiesManager.getInstance();
    private static String token;
    private static String submissionUrl;

    private static final String projectAlias = TestUtils.getRandomAlias();
    private static final String studyAlias = TestUtils.getRandomAlias();
    private static final String sampleAlias = TestUtils.getRandomAlias();
    private static final String analysisAlias = TestUtils.getRandomAlias();
    private static final String fileName = "testFile.vcf.gz";
    private static final String fileType = "vcf";

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
    public void C_uploadFile() throws Exception {
        UploadUtils.uploadFile(token, submissionUrl, fileName);
        TestUtils.waitForFileValidationCompletion(token, submissionUrl);
    }

    @Test
    public void D_addAnalysis() throws Exception {
        String analysisJson = TestJsonUtils.getSeqVarAnalysisJson(submissionUrl, analysisAlias, studyAlias, sampleAlias, fileName, fileType);
        String analysisUrl = TestUtils.createSubmittable(token, pm.getAnalysisApiBaseUrl(), analysisJson);
        TestUtils.waitForValidationResults(token, analysisUrl);
        ValidationResult validationResult = TestUtils.getValidationResultForSubmittable(analysisUrl, token);
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
