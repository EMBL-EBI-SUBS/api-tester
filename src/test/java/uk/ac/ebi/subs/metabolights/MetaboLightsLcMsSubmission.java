package uk.ac.ebi.subs.metabolights;

import org.junit.BeforeClass;
import org.junit.Test;
import uk.ac.ebi.subs.PropertiesManager;
import uk.ac.ebi.subs.data.objects.ValidationResult;
import uk.ac.ebi.subs.utils.TestUtils;

import static uk.ac.ebi.subs.utils.TestUtils.assertNoErrorsInValidationResult;

public class MetaboLightsLcMsSubmission {

    private static PropertiesManager pm = PropertiesManager.getInstance();
    private static String token;
    private static String submissionUrl;

    private static final String projectAlias = TestUtils.getRandomAlias();
    private static final String studyAlias = TestUtils.getRandomAlias();
    private static final String sampleAlias = TestUtils.getRandomAlias();
    private static final String analysisAlias = TestUtils.getRandomAlias();

    @BeforeClass
    public static void setUp() throws Exception {
        token = TestUtils.getJWTToken(pm.getAuthenticationUrl(), pm.getAapUsername(), pm.getAapPassword());
        submissionUrl = TestUtils.createSubmission(token, pm.getSubmissionsApiTemplatedUrl(), pm.getSubmitterEmail(), pm.getTeamName());
        TestUtils.createProject(token, pm.getProjectsApiBaseUrl(), submissionUrl, projectAlias);
    }

    @Test
    public void addStudy() throws Exception {
        String studyUrl = TestUtils.createMLStudy(token, pm.getStudiesApiBaseUrl(), submissionUrl, studyAlias, projectAlias, pm.getTeamName());
        TestUtils.waitForValidationResults(token, studyUrl);
        ValidationResult validationResult = TestUtils.getValidationResultForSubmittable(studyUrl, token);
        assertNoErrorsInValidationResult(validationResult);
    }

}
