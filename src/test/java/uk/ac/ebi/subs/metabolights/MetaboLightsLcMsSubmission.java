package uk.ac.ebi.subs.metabolights;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import uk.ac.ebi.subs.PropertiesManager;
import uk.ac.ebi.subs.data.objects.ValidationResult;
import uk.ac.ebi.subs.utils.TestUtils;

import java.util.Map;

import static uk.ac.ebi.subs.utils.TestUtils.assertNoErrorsInValidationResult;

public class MetaboLightsLcMsSubmission {

    private static PropertiesManager pm = PropertiesManager.getInstance();
    private static String token;
    private static String submissionUrl;

    private static final String projectAlias = TestUtils.getRandomAlias();
    private static final String studyAlias = TestUtils.getRandomAlias();
    private static final String sampleAlias = TestUtils.getRandomAlias();

    @BeforeClass
    public static void setUp() throws Exception {
        token = TestUtils.getJWTToken(pm.getAuthenticationUrl(), pm.getAapUsername(), pm.getAapPassword());
        submissionUrl = TestUtils.createSubmission(token, pm.getSubmissionsApiTemplatedUrl(), pm.getSubmitterEmail(), pm.getTeamName());
        TestUtils.createProject(token, submissionUrl, projectAlias);
    }

    @Test
    @Ignore
    public void A_addStudy() throws Exception {
        Map<String, String> metabolightsProtocolsRefs = TestUtils.createMLProtocols(token, "metabolightsProtocols", submissionUrl, pm.getTeamName());
        String studyUrl = TestUtils.createMLStudy(token, "metabolomicsStudies" , submissionUrl, studyAlias, projectAlias, metabolightsProtocolsRefs, pm.getTeamName());
        TestUtils.waitForValidationResults(token, studyUrl);
        ValidationResult validationResult = TestUtils.getValidationResultForSubmittable(studyUrl, token);
        assertNoErrorsInValidationResult(validationResult);
    }
}
