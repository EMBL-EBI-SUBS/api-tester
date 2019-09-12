package uk.ac.ebi.subs.utils;

import uk.ac.ebi.subs.PropertiesManager;
import uk.ac.ebi.subs.data.objects.ProcessingStatus;
import uk.ac.ebi.subs.data.objects.ValidationResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static uk.ac.ebi.subs.utils.TestUtils.assertNoErrorsInValidationResult;

public class SubmissionOperations {

    public static void addSample(String submissionUrl, String sampleAlias, String token, PropertiesManager pm) throws Exception {
        String sampleJson = TestJsonUtils.getSampleJson(sampleAlias);
        String sampleUrl = TestUtils.createSubmittable(token, "samples", submissionUrl, sampleJson);
        TestUtils.waitForValidationResults(token, sampleUrl);
        ValidationResult validationResult = TestUtils.getValidationResultForSubmittable(sampleUrl, token);
        assertNoErrorsInValidationResult(validationResult);
    }

    public static void addSampleWithoutValidationCheck(String submissionUrl, String sampleJson, String token) throws Exception {
        String sampleUrl = TestUtils.createSubmittable(token, "samples", submissionUrl, sampleJson);
    }

    public static void addSampleWithAccessionId(
        String submissionUrl, String sampleAlias, String accessionId, String token, PropertiesManager pm) throws Exception {
        String sampleJson = TestJsonUtils.getSampleJsonWithAccessionId(sampleAlias, accessionId);
        String sampleUrl = TestUtils.createSubmittable(token, "samples", submissionUrl, sampleJson);
        TestUtils.waitForValidationResults(token, sampleUrl);
        ValidationResult validationResult = TestUtils.getValidationResultForSubmittable(sampleUrl, token);
        assertNoErrorsInValidationResult(validationResult);
    }

    public static void checkAccessions(String submissionUrl, String token) throws IOException {
        Collection<ProcessingStatus> processingStatuses = TestUtils.fetchProcessingStatuses(token, submissionUrl);

        for (ProcessingStatus processingStatus : processingStatuses) {
            System.out.println(processingStatus);
            assertNotNull(processingStatus.getSubmittableType());
            assertNotNull(processingStatus.getAlias());
            assertNotNull(processingStatus.getStatus());
            assertNotNull(processingStatus.getArchive());
            if (!processingStatus.getStatus().equals("ArchiveDisabled")) {
                assertNotNull(processingStatus.getAccession());
            }
        }
    }

    public static List<String> getAccessionIdsBySubmittable(String submittableType, String submissionUrl, String token)
            throws IOException {
        Collection<ProcessingStatus> processingStatuses = TestUtils.fetchProcessingStatuses(token, submissionUrl);

        List<String> accessionIds = new ArrayList<>();
        for (ProcessingStatus processingStatus : processingStatuses) {
            if(processingStatus.getSubmittableType().equals(submittableType)) {
                accessionIds.add(processingStatus.getAccession());
            }
        }

        return accessionIds;
    }
}
