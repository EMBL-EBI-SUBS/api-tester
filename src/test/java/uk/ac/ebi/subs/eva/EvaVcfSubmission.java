package uk.ac.ebi.subs.eva;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runners.MethodSorters;
import uk.ac.ebi.subs.PropertiesManager;
import uk.ac.ebi.subs.categories.DevEnv;
import uk.ac.ebi.subs.data.objects.ValidationResult;
import uk.ac.ebi.subs.utils.TestJsonUtils;
import uk.ac.ebi.subs.utils.TestUtils;
import uk.ac.ebi.subs.utils.UploadUtils;

import java.io.IOException;

import static uk.ac.ebi.subs.utils.SubmissionOperations.addSample;
import static uk.ac.ebi.subs.utils.SubmissionOperations.checkAccessions;
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
    private static final String fileName = "integ_test_on_file_upload_server_testFile.vcf.gz";
    private static final String fileType = "vcf";

    @BeforeClass
    public static void setUp() throws Exception {
        token = TestUtils.getJWTToken(pm.getAuthenticationUrl(), pm.getAapUsername(), pm.getAapPassword());
        submissionUrl = TestUtils.createSubmission(token, pm.getSubmissionsApiTemplatedUrl(), pm.getSubmitterEmail(), pm.getTeamName());
    }

    @Test
    public void A_addStudy() throws Exception {
        String studyUrl = TestUtils.createEVAStudy(token, "evaStudies", submissionUrl, studyAlias, projectAlias, pm.getTeamName());
        TestUtils.waitForValidationResults(token, studyUrl);
        ValidationResult validationResult = TestUtils.getValidationResultForSubmittable(studyUrl, token);
        assertNoErrorsInValidationResult(validationResult);
    }

    @Test
    public void B_addSample() throws Exception {
        addSample(submissionUrl, sampleAlias, token, pm);
    }

//    @Test
//    public void C_uploadFile() throws Exception {
//        UploadUtils.uploadFile(token, submissionUrl, fileName);
//        TestUtils.waitForFileValidationCompletion(token, submissionUrl);
//    }

//    @Test
//    public void D_addAnalysis() throws Exception {
//        String analysisJson = TestJsonUtils.getSeqVarAnalysisJson(analysisAlias, studyAlias, sampleAlias, fileName, fileType);
//        String analysisUrl = TestUtils.createSubmittable(token, "variantCalls", submissionUrl, analysisJson);
//        TestUtils.waitForValidationResults(token, analysisUrl);
//        ValidationResult validationResult = TestUtils.getValidationResultForSubmittable(analysisUrl, token);
//        assertNoErrorsInValidationResult(validationResult);
//    }

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
