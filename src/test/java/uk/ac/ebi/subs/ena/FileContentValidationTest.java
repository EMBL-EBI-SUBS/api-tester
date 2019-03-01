package uk.ac.ebi.subs.ena;

import org.apache.http.HttpResponse;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import uk.ac.ebi.subs.PropertiesManager;
import uk.ac.ebi.subs.categories.DevEnv;
import uk.ac.ebi.subs.categories.TestEnv;
import uk.ac.ebi.subs.data.objects.FileList;
import uk.ac.ebi.subs.data.objects.SubmissionContents;
import uk.ac.ebi.subs.data.objects.SubsFile;
import uk.ac.ebi.subs.data.objects.ValidationResult;
import uk.ac.ebi.subs.utils.HttpUtils;
import uk.ac.ebi.subs.utils.TestUtils;
import uk.ac.ebi.subs.utils.UploadUtils;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Category({TestEnv.class, DevEnv.class})
public class FileContentValidationTest {

    private static PropertiesManager pm = PropertiesManager.getInstance();
    private static String token;
    private static String submissionUrl;
    private static final String FILE_WITH_CONTENT_ERROR = "integ_test_on_file_upload_server_test_e1.fastq.gz";

    @BeforeClass
    public static void setUp() throws Exception {
        token = TestUtils.getJWTToken(pm.getAuthenticationUrl(), pm.getAapUsername(), pm.getAapPassword());
        submissionUrl = TestUtils.createSubmission(token, pm.getSubmissionsApiTemplatedUrl(), pm.getSubmitterEmail(), pm.getTeamName());
    }

    @Test
    public void whenValidatingAFileWithContentError_ThenResultContainsErrors() throws Exception {
        UploadUtils.uploadFile(token, submissionUrl, FILE_WITH_CONTENT_ERROR);

        SubmissionContents submissionContents = TestUtils.getSubmissionContent(token, submissionUrl);

        long maximumIntervalMillis = 120000;
        long startingTimeMillis = System.currentTimeMillis();

        String fileListUrl = submissionContents.getLinks().getFiles().getHref();

        while (System.currentTimeMillis() < startingTimeMillis + maximumIntervalMillis) {
            HttpResponse fileListResponse = HttpUtils.httpGet(token, fileListUrl);
            FileList fileListResource = HttpUtils.retrieveResourceFromResponse(fileListResponse, FileList.class);

            List<SubsFile> files = fileListResource.getContents().getFiles();

            assertEquals(1,files.size());

            SubsFile file = files.get(0);
            if (file.getEmbedded() != null && file.getEmbedded().getValidationResult() != null) {
                ValidationResult vr = file.getEmbedded().getValidationResult();

                boolean validationIsNotPending = !vr.getValidationStatus().equalsIgnoreCase("pending");

                if (validationIsNotPending) {
                    assertEquals("Error", vr.getOverallValidationOutcomeByAuthor().get("FileContent"));
                    assertNotNull(vr.getErrorMessages().get("FileContent"));
                    return;
                }
            }
            Thread.sleep(500);
        }
        throw new RuntimeException("Gave up waiting for file validation results on " + fileListUrl);
    }

    @AfterClass
    public static void tearDown() throws Exception {
        // wait 3 seconds while validation-service can handle the above request
        Thread.sleep(3000);
        HttpUtils.deleteResource(token, submissionUrl);
    }

}
