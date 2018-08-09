package uk.ac.ebi.subs.utils;

import uk.ac.ebi.subs.PropertiesManager;
import uk.ac.ebi.subs.data.objects.ProcessingStatus;
import uk.ac.ebi.subs.data.objects.ValidationResult;

import java.io.IOException;
import java.util.Collection;

import static org.junit.Assert.assertNotNull;
import static uk.ac.ebi.subs.utils.TestUtils.assertNoErrorsInValidationResult;

public class SubmissionOperations {

    public static void addSample(String submissionUrl, String sampleAlias, String token, PropertiesManager pm) throws Exception {
        String sampleJson = TestJsonUtils.getSampleJson(submissionUrl, sampleAlias);
        String sampleUrl = TestUtils.createSubmittable(token, pm.getSamplesApiBaseUrl(), sampleJson);
        TestUtils.waitForValidationResults(token, sampleUrl);
        ValidationResult validationResult = TestUtils.getValidationResultForSubmittable(sampleUrl, token);
        assertNoErrorsInValidationResult(validationResult);
    }

    public static void checkAccessions(String submissionUrl, String token) throws IOException, InterruptedException {
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
