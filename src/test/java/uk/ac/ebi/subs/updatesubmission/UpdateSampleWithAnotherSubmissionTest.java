package uk.ac.ebi.subs.updatesubmission;


import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.runners.MethodSorters;
import uk.ac.ebi.subs.PropertiesManager;
import uk.ac.ebi.subs.utils.TestUtils;

import java.io.IOException;

import static uk.ac.ebi.subs.utils.SubmissionOperations.addSample;
import static uk.ac.ebi.subs.utils.SubmissionOperations.addSampleWithAccessionId;
import static uk.ac.ebi.subs.utils.SubmissionOperations.checkAccessions;
import static uk.ac.ebi.subs.utils.SubmissionOperations.getAccessionIdsBySubmittable;

@RunWith(JUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UpdateSampleWithAnotherSubmissionTest {

    private static PropertiesManager pm = PropertiesManager.getInstance();
    private static String token;
    private static String submissionUrl;
    private static String submissionUrlForUpdate;
    private static String archivedSampleAccessionId;

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
    }

    @Test
    public void B_submit() throws IOException, InterruptedException {
        TestUtils.waitForUpdateableSubmissionStatus(token, submissionUrl);
        TestUtils.changeSubmissionStatusToSubmitted(token, submissionUrl);
    }

    @Test
    public void C_waitForCompleteSubmission() throws IOException, InterruptedException {
        TestUtils.waitForCompletedSubmission(token, submissionUrl);
    }

    @Test
    public void D_checkAccessions() throws IOException {
        checkAccessions(submissionUrl, token);
    }

    @Test
    public void E_createSubmissionToUpdateAlreadyArchivedSample() throws IOException {
        archivedSampleAccessionId = getAccessionIdsBySubmittable("Sample", submissionUrl, token).get(0);
        submissionUrlForUpdate =
                TestUtils.createSubmission(token, pm.getSubmissionsApiTemplatedUrl(), pm.getSubmitterEmail(), pm.getTeamName());
    }

    @Test
    public void F_addUpdatedSample() throws Exception {
        addSampleWithAccessionId(submissionUrlForUpdate, sampleAlias, archivedSampleAccessionId, token, pm);
    }

    @Test
    public void G_submitSubmissionForSampleUpdate() throws IOException, InterruptedException {
        TestUtils.waitForUpdateableSubmissionStatus(token, submissionUrlForUpdate);
        TestUtils.changeSubmissionStatusToSubmitted(token, submissionUrlForUpdate);
    }

    @Test
    public void H_waitForCompletionOfSubmissionForSampleUpdate() throws IOException, InterruptedException {
        TestUtils.waitForCompletedSubmission(token, submissionUrlForUpdate);
    }

    @Test
    public void I_checkAccessionsOfSubmissionForSampleUpdate() throws IOException {
        checkAccessions(submissionUrlForUpdate, token);
    }
}
