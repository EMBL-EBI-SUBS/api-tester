package uk.ac.ebi.subs.ena;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runners.MethodSorters;
import uk.ac.ebi.subs.PropertiesManager;
import uk.ac.ebi.subs.categories.DevEnv;
import uk.ac.ebi.subs.categories.TestEnv;
import uk.ac.ebi.subs.data.objects.ValidationResult;
import uk.ac.ebi.subs.utils.TestUtils;
import uk.ac.ebi.subs.utils.UploadUtils;

import java.io.IOException;

import static uk.ac.ebi.subs.utils.SubmissionOperations.addSample;
import static uk.ac.ebi.subs.utils.SubmissionOperations.checkAccessions;
import static uk.ac.ebi.subs.utils.SubmissionOperations.getAccessionIdsBySubmittable;
import static uk.ac.ebi.subs.utils.TestUtils.assertNoErrorsInValidationResult;

@Category({TestEnv.class, DevEnv.class})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UpdateAlreadyExistingEntitiesInENA {

    private static PropertiesManager pm = PropertiesManager.getInstance();
    private static String token;
    private static String submissionUrl;

    private static final String projectAlias = TestUtils.getRandomAlias();
    private static final String studyAlias = TestUtils.getRandomAlias();
    private static final String sampleAlias = TestUtils.getRandomAlias();
    private static final String assayAlias = TestUtils.getRandomAlias();
    private static final String assayDataAlias = TestUtils.getRandomAlias();
    private static final String fileName = "integ_test_on_file_upload_server_testFile.fastq.gz";
    private static final String fileType = "fastq";

    private static String submissionUrlForUpdate;
    private static String archivedStudyAccessionId;
    private static String archivedAssayAccessionId;

    @BeforeClass
    public static void setUp() throws Exception {
        token = TestUtils.getJWTToken(pm.getAuthenticationUrl(), pm.getAapUsername(), pm.getAapPassword());
        submissionUrl = TestUtils.createSubmission(token, pm.getSubmissionsApiTemplatedUrl(), pm.getSubmitterEmail(), pm.getTeamName());
    }

    @Test
    public void A_addProject() throws Exception {
        TestUtils.createProject(token, submissionUrl, projectAlias);
    }

    @Test
    public void B_addStudy() throws Exception {
        String studyUrl = TestUtils.createStudy(token, "Study.json", "enaStudies",
                submissionUrl, studyAlias, null, "Original title for Study",
                "Original description for Study", projectAlias, pm.getTeamName());
        TestUtils.waitForValidationResults(token, studyUrl);
        ValidationResult validationResult = TestUtils.getValidationResultForSubmittable(studyUrl, token);
        assertNoErrorsInValidationResult(validationResult);
    }

    @Test
    public void C_addSample() throws Exception {
        addSample(submissionUrl, sampleAlias, token, pm);
    }

    @Test
    public void D_addAssay() throws Exception {
        String assayUrl = TestUtils.createAssay(token, "Assay.json", "sequencingExperiments",
                submissionUrl, assayAlias, null, "Original title for Assay",
                "Original description for Assay", studyAlias, sampleAlias);
        TestUtils.waitForValidationResults(token, assayUrl);
        ValidationResult validationResult = TestUtils.getValidationResultForSubmittable(assayUrl, token);
        assertNoErrorsInValidationResult(validationResult);
    }

// @TODO when DC move completed and file upload/validation works then we should remove the @Ignore annotation

    @Ignore
    @Test
    public void E_uploadFile() throws Exception {
        UploadUtils.uploadFile(token, submissionUrl, fileName);

        TestUtils.waitForFileValidationCompletion(token, submissionUrl);
    }

    @Ignore
    @Test
    public void F_addAssayData() throws Exception {
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
    public void G_submit() throws IOException, InterruptedException {
        TestUtils.waitForUpdateableSubmissionStatus(token, submissionUrl);
        TestUtils.changeSubmissionStatusToSubmitted(token, submissionUrl);
    }

    @Test
    public void H_waitForCompleteSubmission() throws IOException, InterruptedException {
        TestUtils.waitForCompletedSubmission(token, submissionUrl, TestUtils.MAXIMUM_INTERVAL_MILLIS);
    }

    @Test
    public void I_checkAccessions() throws IOException, InterruptedException {
        checkAccessions(submissionUrl, token);
    }

    @Test
    public void J_createSubmissionToUpdateAlreadyArchivedSample() throws IOException {
        archivedStudyAccessionId = getAccessionIdsBySubmittable("Study", submissionUrl, token).get(0);
        archivedAssayAccessionId = getAccessionIdsBySubmittable("Assay", submissionUrl, token).get(0);
        submissionUrlForUpdate =
                TestUtils.createSubmission(token, pm.getSubmissionsApiTemplatedUrl(), pm.getSubmitterEmail(), pm.getTeamName());
    }

    @Test
    public void K_addUpdatedStudy() throws IOException, InterruptedException {
        String studyUrl = TestUtils.createStudy(token, "StudyWithAccessionId.json",
                "enaStudies", submissionUrlForUpdate, null, archivedStudyAccessionId,
                "Modified title for Study", "Modified description for Study", projectAlias, pm.getTeamName());
        TestUtils.waitForValidationResults(token, studyUrl);
        ValidationResult validationResult = TestUtils.getValidationResultForSubmittable(studyUrl, token);
        assertNoErrorsInValidationResult(validationResult);
    }

    @Test
    public void L_addUpdatedAssay() throws IOException, InterruptedException {
        String assayUrl = TestUtils.createAssay(token, "AssayWithAccessionId.json",
                "sequencingExperiments", submissionUrlForUpdate, null, archivedAssayAccessionId,
                "Modified title for Assay", "Modified description for Assay", studyAlias, sampleAlias);
        TestUtils.waitForValidationResults(token, assayUrl);
        ValidationResult validationResult = TestUtils.getValidationResultForSubmittable(assayUrl, token);
        assertNoErrorsInValidationResult(validationResult);
    }

    @Test
    public void M_submit() throws IOException, InterruptedException {
        TestUtils.waitForUpdateableSubmissionStatus(token, submissionUrlForUpdate);
        TestUtils.changeSubmissionStatusToSubmitted(token, submissionUrlForUpdate);
    }

    @Test
    public void N_waitForCompleteSubmission() throws IOException, InterruptedException {
        TestUtils.waitForCompletedSubmission(token, submissionUrlForUpdate, TestUtils.MAXIMUM_INTERVAL_MILLIS);
    }

    @Test
    public void O_checkAccessions() throws IOException, InterruptedException {
        checkAccessions(submissionUrlForUpdate, token);
    }
}
