package uk.ac.ebi.subs.samples;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.runners.MethodSorters;
import uk.ac.ebi.subs.PropertiesManager;
import uk.ac.ebi.subs.utils.TestJsonUtils;
import uk.ac.ebi.subs.utils.TestUtils;

import java.io.IOException;
import java.time.LocalDateTime;

import static uk.ac.ebi.subs.utils.SubmissionOperations.addSampleWithoutValidationCheck;
import static uk.ac.ebi.subs.utils.SubmissionOperations.checkAccessions;

@RunWith(JUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HugeNumberOfSamplesSubmissionTest {

    private static final int NUMBER_OF_SAMPLES_TO_SUBMIT = 10_000;
    private static final int WAIT_FOR_COMPLETED_SUBMISSION_IN_MILLIS = 15_000_000; // 15_000_000 -> 250 minutes
    private static final String ALIAS = "{alias.placeholder}";

    private static PropertiesManager pm = PropertiesManager.getInstance();

    private static String token;
    private static String submissionUrl;
    private static String sampleAlias = TestUtils.getRandomAlias();

    @BeforeClass
    public static void setUp() throws Exception {
        token = TestUtils.getJWTToken(pm.getAuthenticationUrl(), pm.getAapUsername(), pm.getAapPassword());
        submissionUrl = TestUtils.createSubmission(token, pm.getSubmissionsApiTemplatedUrl(), pm.getSubmitterEmail(), pm.getTeamName());
        System.out.println("Sample alias: " + sampleAlias);
    }

    @Test
    public void A_addSamples() throws Exception {
        String sampleJson = TestJsonUtils.getSampleJson(ALIAS);
        System.out.println("Sample creation started: " + LocalDateTime.now());
        String sampleAliasIndexed;
        String oldAlias = ALIAS;

        for (int index = 1; index <= NUMBER_OF_SAMPLES_TO_SUBMIT; index++) {
            sampleAliasIndexed = sampleAlias + "_" + index;
            sampleJson = sampleJson.replace(oldAlias, sampleAliasIndexed);
            addSampleWithoutValidationCheck(submissionUrl, sampleJson, token);
            oldAlias = sampleAliasIndexed;
        }

        System.out.println("Sample creation ended: " + LocalDateTime.now());
    }

    @Test
    public void B_submit() throws IOException, InterruptedException {
        token = TestUtils.getJWTToken(pm.getAuthenticationUrl(), pm.getAapUsername(), pm.getAapPassword());
        System.out.println("Wait for updateable submission status started: " + LocalDateTime.now());
        TestUtils.waitForUpdateableSubmissionStatus(token, submissionUrl);
        System.out.println("Wait for updateable submission status ended: " + LocalDateTime.now());
        TestUtils.changeSubmissionStatusToSubmitted(token, submissionUrl);
    }

    @Test
    public void C_waitForCompleteSubmission() throws IOException, InterruptedException {
        token = TestUtils.getJWTToken(pm.getAuthenticationUrl(), pm.getAapUsername(), pm.getAapPassword());
        System.out.println("Wait for completed submission started: " + LocalDateTime.now());
        TestUtils.waitForLongCompletedSubmission(token, submissionUrl, WAIT_FOR_COMPLETED_SUBMISSION_IN_MILLIS, pm);
        System.out.println("Wait for completed submission ended: " + LocalDateTime.now());
    }

    @Test
    public void D_checkAccessions() throws IOException {
        token = TestUtils.getJWTToken(pm.getAuthenticationUrl(), pm.getAapUsername(), pm.getAapPassword());
        checkAccessions(submissionUrl, token);
    }

}
