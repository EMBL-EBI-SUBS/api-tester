package uk.ac.ebi.subs.ena;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runners.MethodSorters;
import uk.ac.ebi.subs.PropertiesManager;
import uk.ac.ebi.subs.categories.DevEnv;
import uk.ac.ebi.subs.data.objects.ValidationResult;
import uk.ac.ebi.subs.utils.TestUtils;
import uk.ac.ebi.subs.utils.UploadUtils;

import java.io.IOException;

import static uk.ac.ebi.subs.utils.SubmissionOperations.addSample;
import static uk.ac.ebi.subs.utils.SubmissionOperations.checkAccessions;
import static uk.ac.ebi.subs.utils.TestUtils.assertNoErrorsInValidationResult;

@Category({DevEnv.class})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BamCramSubmission {

    private static PropertiesManager pm = PropertiesManager.getInstance();
    private static String token;
    private static String submissionUrl;

    private static final String projectAlias = TestUtils.getRandomAlias();
    private static final String studyAlias = TestUtils.getRandomAlias();
    private static final String sampleAlias = TestUtils.getRandomAlias();
    private static final String assayAlias = TestUtils.getRandomAlias();
    private static final String assayDataAlias = TestUtils.getRandomAlias();

    private static final String fileName = "integ_test_on_file_upload_server_testFile_ok.bam";
    private static final String fileType = "bam";


    @BeforeClass
    public static void setUp() throws Exception {
        token = TestUtils.getJWTToken(pm.getAuthenticationUrl(), pm.getAapUsername(), pm.getAapPassword());
        submissionUrl = TestUtils.createSubmission(token, pm.getSubmissionsApiTemplatedUrl(), pm.getSubmitterEmail(), pm.getTeamName());
        TestUtils.createProject(token, submissionUrl, projectAlias);
    }

    @Test
    public void A_addStudy() throws Exception {
        String studyUrl = TestUtils.createStudy(token, "Study.json", "enaStudies",
                submissionUrl, studyAlias, null, "Title for Study",
                "Description for Study", projectAlias, pm.getTeamName());

        TestUtils.waitForValidationResults(token, studyUrl);
        ValidationResult validationResult = TestUtils.getValidationResultForSubmittable(studyUrl, token);
        assertNoErrorsInValidationResult(validationResult);
    }

    @Test
    public void B_addSample() throws Exception {
        addSample(submissionUrl, sampleAlias, token, pm);
    }

    @Test
    public void C_addAssay() throws Exception {
        String assayUrl = TestUtils.createAssay(token, "Assay.json", "sequencingExperiments",
                submissionUrl, assayAlias, null, "Title for Assay",
                "Description for Assay", studyAlias, sampleAlias);
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
                "sequencingRuns",
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
        TestUtils.waitForCompletedSubmission(token, submissionUrl, TestUtils.MAXIMUM_INTERVAL_MILLIS);
    }

    @Test
    public void H_checkAccessions() throws IOException, InterruptedException {
        checkAccessions(submissionUrl, token);
    }
}
