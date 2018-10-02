package uk.ac.ebi.subs.metabolights;

import org.junit.BeforeClass;
import org.junit.Test;
import uk.ac.ebi.subs.PropertiesManager;
import uk.ac.ebi.subs.data.objects.ValidationResult;
import uk.ac.ebi.subs.utils.TestUtils;

import static org.junit.Assert.assertEquals;
import static uk.ac.ebi.subs.utils.SubmissionOperations.addSample;
import static uk.ac.ebi.subs.utils.TestUtils.assertNoErrorsInValidationResult;

public class MetaboLightsLcMsSubmission {

    private static PropertiesManager pm = PropertiesManager.getInstance();
    private static String token;
    private static String submissionUrl;

    private static final String projectAlias = TestUtils.getRandomAlias();
    private static final String studyAlias = TestUtils.getRandomAlias();
    private static final String sampleAlias = TestUtils.getRandomAlias();
    private static final String protocolAlias = TestUtils.getRandomAlias();

    @BeforeClass
    public static void setUp() throws Exception {
        token = TestUtils.getJWTToken(pm.getAuthenticationUrl(), pm.getAapUsername(), pm.getAapPassword());
        submissionUrl = TestUtils.createSubmission(token, pm.getSubmissionsApiTemplatedUrl(), pm.getSubmitterEmail(), pm.getTeamName());
        TestUtils.createProject(token, submissionUrl, projectAlias);
    }

    @Test
    public void A_addStudy() throws Exception {
        TestUtils.createMLProtocols(token, "metabolightsProtocols" , submissionUrl, protocolAlias,  pm.getTeamName());
        String studyUrl = TestUtils.createMLStudy(token, "metabolomicsStudies" , submissionUrl, studyAlias, projectAlias, protocolAlias, pm.getTeamName());
        TestUtils.waitForValidationResults(token, studyUrl);
        ValidationResult validationResult = TestUtils.getValidationResultForSubmittable(studyUrl, token);
        assertNoErrorsInValidationResult(validationResult);
    }

    @Test
    public void B_addSample() throws Exception {
        addSample(submissionUrl, sampleAlias, token, pm);
    }

}
